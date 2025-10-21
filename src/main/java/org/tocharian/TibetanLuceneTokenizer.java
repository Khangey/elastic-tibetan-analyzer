/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.tocharian;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.tocharian.tibetan.TibetanTokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Lucene Tokenizer for Tibetan text
 * Uses TibetanTokenizer to perform word segmentation
 */
public class TibetanLuceneTokenizer extends Tokenizer {
    
    private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAttr = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncrAttr = addAttribute(PositionIncrementAttribute.class);
    
    private final TibetanTokenizer tibetanTokenizer;
    private List<String> tokens;
    private int tokenIndex;
    private String inputText;
    private int currentOffset;
    
    public TibetanLuceneTokenizer(TibetanTokenizer tibetanTokenizer) {
        this.tibetanTokenizer = tibetanTokenizer;
    }
    
    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        
        // Initialize on first call
        if (tokens == null) {
            // Read entire input
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[8192];
            int numRead;
            Reader inputReader = input;
            while ((numRead = inputReader.read(buffer)) != -1) {
                sb.append(buffer, 0, numRead);
            }
            inputText = sb.toString();
            
            // Tokenize using Tibetan tokenizer
            tokens = tibetanTokenizer.tokenize(inputText);
            tokenIndex = 0;
            currentOffset = 0;
        }
        
        // Return next token
        if (tokenIndex < tokens.size()) {
            String token = tokens.get(tokenIndex);
            
            // Find token position in original text
            int startOffset = inputText.indexOf(token, currentOffset);
            if (startOffset == -1) {
                startOffset = currentOffset;
            }
            int endOffset = startOffset + token.length();
            
            termAttr.append(token);
            offsetAttr.setOffset(correctOffset(startOffset), correctOffset(endOffset));
            posIncrAttr.setPositionIncrement(1);
            
            currentOffset = endOffset;
            tokenIndex++;
            return true;
        }
        
        return false;
    }
    
    @Override
    public void reset() throws IOException {
        super.reset();
        tokens = null;
        tokenIndex = 0;
        currentOffset = 0;
        inputText = null;
    }
}

