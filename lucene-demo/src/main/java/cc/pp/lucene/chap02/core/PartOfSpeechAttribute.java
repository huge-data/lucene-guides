package cc.pp.lucene.chap02.core;

import org.apache.lucene.util.Attribute;

/**
 * 自定义个一个属性接口
 * @author wgybzb
 *
 */
public interface PartOfSpeechAttribute extends Attribute {

	public static enum PartOfSpeech {
		Noun, Verb, Adjective, Adverb, Pronoun, Preposition, Conjunction, Article, Unknown
	}

	public void setPartOfSpeech(PartOfSpeech pos);

	public PartOfSpeech getPartOfSpeech();

}

