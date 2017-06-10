package org.elasticsearch.index.analysis;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexSettings;

import com.huaban.analysis.jieba.WordDictionary;

public class JiebaTokenFilterFactory extends AbstractTokenFilterFactory {
    private String segMode;

	@Inject
	public JiebaTokenFilterFactory(IndexSettings indexSettings, Environment env,
								   String name, Settings settings) {
		super(indexSettings, name, settings);
        WordDictionary.getInstance().init(env.pluginsFile().resolve("jieba/dic"));
	}

	@Override
	public TokenStream create(TokenStream input) {
		return new JiebaTokenFilter(segMode, input);
	}

    public String getSegMode() {
        return segMode;
    }

    public void setSegMode(String segMode) {
        this.segMode = segMode;
    }

	public static TokenFilterFactory getJiebaSearchTokenFilterFactory(IndexSettings indexSettings,
																  Environment environment,
																  String s,
																  Settings settings) {
		JiebaTokenFilterFactory jiebaTokenFilterFactory = new JiebaTokenFilterFactory(indexSettings,
				environment,
				s,
				settings);
		jiebaTokenFilterFactory.setSegMode(JiebaSegmenter.SegMode.SEARCH.name());
		return jiebaTokenFilterFactory;
	}

	public static TokenFilterFactory getJiebaIndexTokenFilterFactory(IndexSettings indexSettings,
																 Environment environment,
																 String s,
																 Settings settings) {
		JiebaTokenFilterFactory jiebaTokenFilterFactory = new JiebaTokenFilterFactory(indexSettings,
				environment,
				s,
				settings);
		jiebaTokenFilterFactory.setSegMode(JiebaSegmenter.SegMode.INDEX.name());
		return jiebaTokenFilterFactory;
	}
}