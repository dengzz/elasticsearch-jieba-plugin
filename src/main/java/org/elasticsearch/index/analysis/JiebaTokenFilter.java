package org.elasticsearch.index.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import com.huaban.analysis.jieba.SegToken;

public final class JiebaTokenFilter extends TokenFilter {

    JiebaSegmenter segmenter;

    private Iterator<SegToken> tokenIter;
    private List<SegToken> array;
    private final SegMode segMode;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

    public JiebaTokenFilter(String segModeName, TokenStream input) {
        super(input);
        if (null == segModeName) {
            segMode = SegMode.SEARCH;
        } else {
            segMode = SegMode.valueOf(segModeName);
        }
        segmenter = new JiebaSegmenter();
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (tokenIter == null || !tokenIter.hasNext()) {
            if (input.incrementToken()) {
                array = segmenter.process(termAtt.toString(), segMode);
                tokenIter = array.iterator();
                if (!tokenIter.hasNext())
                    return false;
            } else {
                return false; // no more sentences, end of stream!
            }
        }
        // WordTokenFilter must clear attributes, as it is creating new tokens.
        clearAttributes();

        SegToken token = tokenIter.next();
        offsetAtt.setOffset(token.startOffset, token.endOffset);
        String tokenString = token.word;
        termAtt.copyBuffer(tokenString.toCharArray(), 0, tokenString.length());
        typeAtt.setType("word");
        return true;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokenIter = null;
    }

}