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

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.tocharian.tibetan.TibetanTokenizer;

import java.io.IOException;
import java.util.List;

/**
 * Tibetan token filter
 * Processes tokens from input stream and applies Tibetan tokenization
 */
public class TibetanTokenFilter extends TokenFilter {
    
    private final CharTermAttribute termAttr = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAttr = addAttribute(OffsetAttribute.class);
    private final TibetanTokenizer tibetanTokenizer;
    
    private List<String> currentTokens;
    private int currentTokenIndex;
    private int currentStartOffset;
    private int currentEndOffset;
    
    public TibetanTokenFilter(TokenStream input, TibetanTokenizer tokenizer) {
        super(input);
        this.tibetanTokenizer = tokenizer;
    }
    
    @Override
    public final boolean incrementToken() throws IOException {
        // If we have tokens from previous segmentation, output them
        if (currentTokens != null && currentTokenIndex < currentTokens.size()) {
            clearAttributes();
            String token = currentTokens.get(currentTokenIndex);
            termAttr.append(token);
            
            // Calculate offsets (approximate)
            int tokenLength = token.length();
            offsetAttr.setOffset(currentStartOffset, currentStartOffset + tokenLength);
            currentStartOffset += tokenLength;
            
            currentTokenIndex++;
            return true;
        }
        
        // Get next token from input
        if (input.incrementToken()) {
            String inputToken = termAttr.toString();
            int startOffset = offsetAttr.startOffset();
            int endOffset = offsetAttr.endOffset();
            
            // Tokenize using Tibetan tokenizer
            List<String> tokens = tibetanTokenizer.tokenize(inputToken);
            
            if (tokens != null && !tokens.isEmpty()) {
                if (tokens.size() == 1) {
                    // Single token, just pass through
                    return true;
                } else {
                    // Multiple tokens, save for incremental output
                    currentTokens = tokens;
                    currentTokenIndex = 0;
                    currentStartOffset = startOffset;
                    currentEndOffset = endOffset;
                    
                    // Output first token
                    return incrementToken();
                }
            } else {
                // No tokens produced, pass through original
                return true;
            }
        }
        
        // No more input tokens
        return false;
    }
    
    @Override
    public void reset() throws IOException {
        super.reset();
        currentTokens = null;
        currentTokenIndex = 0;
        currentStartOffset = 0;
        currentEndOffset = 0;
    }
}

