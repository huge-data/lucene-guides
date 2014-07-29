查询相关目录
========================
* `搜索基础`
* `所有查询类`
* `评分介绍`
* `评分基础`
* `修改评分规则`
* `附录: 搜索算法`

# 搜索基础 #

LuceneConstant.LUCENE_VERSION
Analyzer analyzer = new StandardAnalyzer(LuceneConstant.LUCENE_VERSION);
IndexWriterConfig conf = new IndexWriterConfig(LuceneConstant.LUCENE_VERSION, analyzer);
IndexWriter writer = new IndexWriter(dir, conf);

IndexReader reader = DirectoryReader.open(dir);
IndexSearcher searcher = new IndexSearcher(reader);

[Lucene] 提供了多种`Query`实现类，大部分都是在<org.apache.lucene.search>包或者它的子包（`spans`、`payloads`），或者其他查询模块中。可以通过组合这些查询实现更为复杂的查询能力。下面将会介绍一些比较重要的查询类，当然可以通过`Custom Queries`的介绍实现自定义的查询功能。
搜索的时候，通过使用<IndexSearcher.search(Query,int)>或者<IndexSearcher.search(Query,Filter,int)>来实现。
一旦创建和提交查询给<IndexSearcher>后，评分过程将进行。

# 所有查询类 #

## TermQuery ##

Of the various implementations of Query, the TermQuery is the easiest to understand and the most often used in applications. A TermQuery matches all the documents that contain the specified Term, which is a word that occurs in a certain Field. Thus, a TermQuery identifies and scores all Documents that have a Field with the specified string in it. Constructing a TermQuery is as simple as:

        TermQuery tq = new TermQuery(new Term("fieldName", "term"));
    In this example, the Query identifies all Documents that have the Field named "fieldName" containing the word "term".

## BooleanQuery ##

Things start to get interesting when one combines multiple TermQuery instances into a BooleanQuery. A BooleanQuery contains multiple BooleanClauses, where each clause contains a sub-query (Query instance) and an operator (from BooleanClause.Occur) describing how that sub-query is combined with the other clauses:

* SHOULD — Use this operator when a clause can occur in the result set, but is not required. If a query is made up of all SHOULD clauses, then every document in the result set matches at least one of these clauses.

* MUST — Use this operator when a clause is required to occur in the result set. Every document in the result set will match all such clauses.

* MUST NOT — Use this operator when a clause must not occur in the result set. No document in the result set will match any such clauses.

Boolean queries are constructed by adding two or more BooleanClause instances. If too many clauses are added, a TooManyClauses exception will be thrown during searching. This most often occurs when a Query is rewritten into a BooleanQuery with many TermQuery clauses, for example by WildcardQuery. The default setting for the maximum number of clauses 1024, but this can be changed via the static method BooleanQuery.setMaxClauseCount(int).

## Phrases ##

Another common search is to find documents containing certain phrases. This is handled three different ways:

* PhraseQuery — Matches a sequence of Terms. PhraseQuery uses a slop factor to determine how many positions may occur between any two terms in the phrase and still be considered a match. The slop is 0 by default, meaning the phrase must match exactly.

* MultiPhraseQuery — A more general form of PhraseQuery that accepts multiple Terms for a position in the phrase. For example, this can be used to perform phrase queries that also incorporate synonyms.

* SpanNearQuery — Matches a sequence of other SpanQuery instances. SpanNearQuery allows for much more complicated phrase queries since it is constructed from other SpanQuery instances, instead of only TermQuery instances.

## TermRangeQuery ##

The TermRangeQuery matches all documents that occur in the exclusive range of a lower Term and an upper Term according to TermsEnum.getComparator(). It is not intended for numerical ranges; use NumericRangeQuery instead. For example, one could find all documents that have terms beginning with the letters a through c.

## NumericRangeQuery ##

The NumericRangeQuery matches all documents that occur in a numeric range. For NumericRangeQuery to work, you must index the values using a one of the numeric fields (IntField, LongField, FloatField, or DoubleField).

## PrefixQuery, WildcardQuery, RegexpQuery ##

While the PrefixQuery has a different implementation, it is essentially a special case of the WildcardQuery. The PrefixQuery allows an application to identify all documents with terms that begin with a certain string. The WildcardQuery generalizes this by allowing for the use of * (matches 0 or more characters) and ? (matches exactly one character) wildcards. Note that the WildcardQuery can be quite slow. Also note that WildcardQuery should not start with * and ?, as these are extremely slow. Some QueryParsers may not allow this by default, but provide a setAllowLeadingWildcard method to remove that protection. The RegexpQuery is even more general than WildcardQuery, allowing an application to identify all documents with terms that match a regular expression pattern.

## FuzzyQuery ##

A FuzzyQuery matches documents that contain terms similar to the specified term. Similarity is determined using Levenshtein (edit) distance. This type of query can be useful when accounting for spelling variations in the collection.

# 评分介绍 #

Lucene scoring is the heart of why we all love Lucene. It is blazingly fast and it hides almost all of the complexity from the user. In a nutshell, it works. At least, that is, until it doesn't work, or doesn't work as one would expect it to work. Then we are left digging into Lucene internals or asking for help on java-user@lucene.apache.org to figure out why a document with five of our query terms scores lower than a different document with only one of the query terms.

While this document won't answer your specific scoring issues, it will, hopefully, point you to the places that can help you figure out the what and why of Lucene scoring.

Lucene scoring supports a number of pluggable information retrieval models, including:

* Vector Space Model (VSM)
* Probablistic Models such as Okapi BM25 and DFR
* Language models

These models can be plugged in via the Similarity API, and offer extension hooks and parameters for tuning. In general, Lucene first finds the documents that need to be scored based on boolean logic in the Query specification, and then ranks this subset of matching documents via the retrieval model. For some valuable references on VSM and IR in general refer to Lucene Wiki IR references.
The rest of this document will cover Scoring basics and explain how to change your Similarity. Next, it will cover ways you can customize the lucene internals in Custom Queries -- Expert Level, which gives details on implementing your own Query class and related functionality. Finally, we will finish up with some reference material in the Appendix.

# 评分基础 #

Scoring is very much dependent on the way documents are indexed, so it is important to understand indexing. (see Lucene overview before continuing on with this section) Be sure to use the useful IndexSearcher.explain(Query, doc) to understand how the score for a certain matching document was computed.

Generally, the Query determines which documents match (a binary decision), while the Similarity determines how to assign scores to the matching documents.

## Fields and Documents ##

In Lucene, the objects we are scoring are Documents. A Document is a collection of Fields. Each Field has semantics about how it is created and stored (tokenized, stored, etc). It is important to note that Lucene scoring works on Fields and then combines the results to return Documents. This is important because two Documents with the exact same content, but one having the content in two Fields and the other in one Field may return different scores for the same query due to length normalization.

## Score Boosting ##

Lucene allows influencing search results by "boosting" at different times:

* Index-time boost by calling Field.setBoost() before a document is added to the index.
* Query-time boost by setting a boost on a query clause, calling Query.setBoost().

Indexing time boosts are pre-processed for storage efficiency and written to storage for a field as follows:

All boosts of that field (i.e. all boosts under the same field name in that doc) are multiplied.
The boost is then encoded into a normalization value by the Similarity object at index-time: computeNorm(). The actual encoding depends upon the Similarity implementation, but note that most use a lossy encoding (such as multiplying the boost with document length or similar, packed into a single byte!).
Decoding of any index-time normalization values and integration into the document's score is also performed at search time by the Similarity.

# 修改评分规则 — 根据相似度 #

Changing Similarity is an easy way to influence scoring, this is done at index-time with IndexWriterConfig.setSimilarity(Similarity) and at query-time with IndexSearcher.setSimilarity(Similarity). Be sure to use the same Similarity at query-time as at index-time (so that norms are encoded/decoded correctly); Lucene makes no effort to verify this.

You can influence scoring by configuring a different built-in Similarity implementation, or by tweaking its parameters, subclassing it to override behavior. Some implementations also offer a modular API which you can extend by plugging in a different component (e.g. term frequency normalizer).

Finally, you can extend the low level Similarity directly to implement a new retrieval model, or to use external scoring factors particular to your application. For example, a custom Similarity can access per-document values via FieldCache or NumericDocValues and integrate them into the score.

See the org.apache.lucene.search.similarities package documentation for information on the built-in available scoring models and extending or changing Similarity.

# 自定义Query #

Custom queries are an expert level task, so tread carefully and be prepared to share your code if you want help.

With the warning out of the way, it is possible to change a lot more than just the Similarity when it comes to matching and scoring in Lucene. Lucene's search is a complex mechanism that is grounded by three main classes:

* `Query` — The abstract object representation of the user's information need.
* `Weight` — The internal interface representation of the user's Query, so that Query objects may be reused. This is global (across all segments of the index) and generally will require global statistics (such as docFreq for a given term across all segments).
* `Scorer` — An abstract class containing common functionality for scoring. Provides both scoring and explanation capabilities. This is created per-segment.
Details on each of these classes, and their children, can be found in the subsections below.

## The Query Class ## 

In some sense, the Query class is where it all begins. Without a Query, there would be nothing to score. Furthermore, the Query class is the catalyst for the other scoring classes as it is often responsible for creating them or coordinating the functionality between them. The Query class has several methods that are important for derived classes:

* createWeight(IndexSearcher searcher) — A Weight is the internal representation of the Query, so each Query implementation must provide an implementation of Weight. See the subsection on The Weight Interface below for details on implementing the Weight interface.
* rewrite(IndexReader reader) — Rewrites queries into primitive queries. Primitive queries are: TermQuery, BooleanQuery, and other queries that implement createWeight(IndexSearcher searcher)

## The Weight Interface ## 

The Weight interface provides an internal representation of the Query so that it can be reused. Any IndexSearcher dependent state should be stored in the Weight implementation, not in the Query class. The interface defines five methods that must be implemented:

* getQuery() — Pointer to the Query that this Weight represents.
* getValueForNormalization() — A weight can return a floating point value to indicate its magnitude for query normalization. Typically a weight such as TermWeight that scores via a Similarity will just defer to the Similarity's implementation: SimWeight#getValueForNormalization(). For example, with Lucene's classic vector-space formula, this is implemented as the sum of squared weights: (idf * boost)2
* normalize(float norm, float topLevelBoost) — Performs query normalization:
topLevelBoost: A query-boost factor from any wrapping queries that should be multiplied into every document's score. For example, a TermQuery that is wrapped within a BooleanQuery with a boost of 5 would receive this value at this time. This allows the TermQuery (the leaf node in this case) to compute this up-front a single time (e.g. by multiplying into the IDF), rather than for every document.
norm: Passes in a a normalization factor which may allow for comparing scores between queries.
Typically a weight such as TermWeight that scores via a Similarity will just defer to the Similarity's implementation: SimWeight#normalize(float,float).
* scorer(AtomicReaderContext context, boolean scoresDocsInOrder, boolean topScorer, Bits acceptDocs) — Construct a new Scorer for this Weight. See The Scorer Class below for help defining a Scorer. As the name implies, the Scorer is responsible for doing the actual scoring of documents given the Query.
* explain(AtomicReaderContext context, int doc) — Provide a means for explaining why a given document was scored the way it was. Typically a weight such as TermWeight that scores via a Similarity will make use of the Similarity's implementation: SimScorer#explain(int doc, Explanation freq).

## The Scorer Class ## 

The Scorer abstract class provides common scoring functionality for all Scorer implementations and is the heart of the Lucene scoring process. The Scorer defines the following abstract (some of them are not yet abstract, but will be in future versions and should be considered as such now) methods which must be implemented (some of them inherited from DocIdSetIterator):

* nextDoc() — Advances to the next document that matches this Query, returning true if and only if there is another document that matches.
* docID() — Returns the id of the Document that contains the match.
* score() — Return the score of the current document. This value can be determined in any appropriate way for an application. For instance, the TermScorer simply defers to the configured Similarity: SimScorer.score(int doc, float freq).
* freq() — Returns the number of matches for the current document. This value can be determined in any appropriate way for an application. For instance, the TermScorer simply defers to the term frequency from the inverted index: DocsEnum.freq().
* advance() — Skip ahead in the document matches to the document whose id is greater than or equal to the passed in value. In many instances, advance can be implemented more efficiently than simply looping through all the matching documents until the target document is identified.
* getChildren() — Returns any child subscorers underneath this scorer. This allows for users to navigate the scorer hierarchy and receive more fine-grained details on the scoring process.
Why would I want to add my own Query?

In a nutshell, you want to add your own custom Query implementation when you think that Lucene's aren't appropriate for the task that you want to do. You might be doing some cutting edge research or you need more information back out of Lucene (similar to Doug adding SpanQuery functionality).

# 附录: 搜索算法 #

This section is mostly notes on stepping through the Scoring process and serves as fertilizer for the earlier sections.

In the typical search application, a Query is passed to the IndexSearcher, beginning the scoring process.

Once inside the IndexSearcher, a Collector is used for the scoring and sorting of the search results. These important objects are involved in a search:

* The Weight object of the Query. The Weight object is an internal representation of the Query that allows the Query to be reused by the IndexSearcher.
* The IndexSearcher that initiated the call.
* A Filter for limiting the result set. Note, the Filter may be null.
* A Sort object for specifying how to sort the results if the standard score-based sort method is not desired.
Assuming we are not sorting (since sorting doesn't affect the raw Lucene score), we call one of the search methods of the IndexSearcher, passing in the Weight object created by IndexSearcher.createNormalizedWeight(Query), Filter and the number of results we want. This method returns a TopDocs object, which is an internal collection of search results. The IndexSearcher creates a TopScoreDocCollector and passes it along with the Weight, Filter to another expert search method (for more on the Collector mechanism, see IndexSearcher). The TopScoreDocCollector uses a PriorityQueue to collect the top results for the search.

If a Filter is being used, some initial setup is done to determine which docs to include. Otherwise, we ask the Weight for a Scorer for each IndexReader segment and proceed by calling Scorer.score().

At last, we are actually going to score some documents. The score method takes in the Collector (most likely the TopScoreDocCollector or TopFieldCollector) and does its business.Of course, here is where things get involved. The Scorer that is returned by the Weight object depends on what type of Query was submitted. In most real world applications with multiple query terms, the Scorer is going to be a BooleanScorer2 created from BooleanWeight (see the section on custom queries for info on changing this).

Assuming a BooleanScorer2, we first initialize the Coordinator, which is used to apply the coord() factor. We then get a internal Scorer based on the required, optional and prohibited parts of the query. Using this internal Scorer, the BooleanScorer2 then proceeds into a while loop based on the Scorer.nextDoc() method. The nextDoc() method advances to the next document matching the query. This is an abstract method in the Scorer class and is thus overridden by all derived implementations. If you have a simple OR query your internal Scorer is most likely a DisjunctionSumScorer, which essentially combines the scorers from the sub scorers of the OR'd terms.
