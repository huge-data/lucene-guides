package cc.pp.chap08and09.tools;

import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.messages.MessageImpl;
import org.apache.lucene.queryParser.core.QueryNodeException;
import org.apache.lucene.queryParser.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryParser.core.nodes.FieldQueryNode;
import org.apache.lucene.queryParser.core.nodes.FuzzyQueryNode;
import org.apache.lucene.queryParser.core.nodes.QueryNode;
import org.apache.lucene.queryParser.core.nodes.SlopQueryNode;
import org.apache.lucene.queryParser.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryParser.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryParser.core.processors.QueryNodeProcessorPipeline;
import org.apache.lucene.queryParser.standard.StandardQueryParser;
import org.apache.lucene.queryParser.standard.builders.StandardQueryBuilder;
import org.apache.lucene.queryParser.standard.nodes.WildcardQueryNode;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

public class CustomFlexibleQueryParser extends StandardQueryParser {

	/**
	 * 重载slop查询
	 * @author wanggang
	 *
	 */
	public class SlopQueryNodeBuilder implements StandardQueryBuilder {

		@Override
		public Query build(QueryNode queryNode) {

			SlopQueryNode phraseSlopNode = (SlopQueryNode) queryNode;
			Query query = (Query) phraseSlopNode.getChild().getTag(//
					QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);

			if (query instanceof PhraseQuery) {
				((PhraseQuery) query).setSlop(phraseSlopNode.getValue());
			} else if (query instanceof MultiPhraseQuery) {
				((MultiPhraseQuery) query).setSlop(phraseSlopNode.getValue());
			}

			return query;
		}
	}

	private final class NoFuzzyOrWildcardQueryProcessor extends //
	QueryNodeProcessorImpl {

		@Override
		protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
			return node;
		}

		/**
		 * 屏蔽通配符查询和模糊查询
		 */
		@Override
		protected QueryNode preProcessNode(QueryNode node) throws QueryNodeException {
			if (node instanceof FuzzyQueryNode || node instanceof WildcardQueryNode) {
				throw new QueryNodeException(new MessageImpl("no"));
			}
			return node;
		}

		@Override
		protected List<QueryNode> setChildrenOrder(List<QueryNode> children) {
			return children;
		}

	}

	private class SpanNearPhraseQueryBuilder implements StandardQueryBuilder {

		@Override
		public Query build(QueryNode queryNode) throws QueryNodeException {

			TokenizedPhraseQueryNode phraseNode = (TokenizedPhraseQueryNode) queryNode;
			PhraseQuery phraseQuery = new PhraseQuery();

			// 从phrase解析器中拉取所有term项
			List<QueryNode> children = phraseNode.getChildren();

			SpanTermQuery[] clauses;
			if (children != null) {
				int numTerms = children.size();
				clauses = new SpanTermQuery[numTerms];
				for (int i = 0; i < numTerms; i++) {
					FieldQueryNode termNode = (FieldQueryNode) children.get(i);
					TermQuery termQuery = (TermQuery) termNode.//
							getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
					clauses[i] = new SpanTermQuery(termQuery.getTerm());
				}
			} else {
				clauses = new SpanTermQuery[0];
			}

			// 创建邻近查询，并返回
			return new SpanNearQuery(clauses, phraseQuery.getSlop(), true);
		}

	}

	public CustomFlexibleQueryParser(Analyzer analyzer) {

		super(analyzer);
		QueryNodeProcessorPipeline processors = (QueryNodeProcessorPipeline) //
				getQueryNodeProcessor();
		processors.addProcessor(new NoFuzzyOrWildcardQueryProcessor()); // 安装custom节点处理器
		QueryTreeBuilder builders = (QueryTreeBuilder) getQueryBuilder();

		// 安装两个custom查询构建器
		builders.setBuilder(TokenizedPhraseQueryNode.class, //
				new SpanNearPhraseQueryBuilder());
		builders.setBuilder(SlopQueryNode.class, new SlopQueryNodeBuilder());
	}

}
