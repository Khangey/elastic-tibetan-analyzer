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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.tocharian.tibetan.TibetanDictionaryManager;
import org.tocharian.tibetan.TibetanTokenizer;

import java.io.IOException;

/**
 * Tibetan analyzer for Elasticsearch
 * Provides Tibetan word segmentation based on longest-match algorithm
 */
public class TibetanAnalyzer extends Analyzer {
    
    private final TibetanDictionaryManager dictionaryManager;
    private final TibetanTokenizer tibetanTokenizer;
    
    public TibetanAnalyzer() {
        this.dictionaryManager = new TibetanDictionaryManager();
        
        try {
            this.dictionaryManager.initialize();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Tibetan dictionary: " + e.getMessage(), e);
        }
        
        this.tibetanTokenizer = new TibetanTokenizer(dictionaryManager);
    }
    
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new TibetanLuceneTokenizer(tibetanTokenizer);
        return new TokenStreamComponents(tokenizer);
    }
    
    /**
     * Get dictionary statistics
     */
    public String getStatistics() {
        if (dictionaryManager != null && dictionaryManager.isInitialized()) {
            return dictionaryManager.getStatistics().toString();
        }
        return "Dictionary not initialized";
    }
}

