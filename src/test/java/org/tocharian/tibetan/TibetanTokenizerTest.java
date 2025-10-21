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

package org.tocharian.tibetan;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for TibetanTokenizer
 */
public class TibetanTokenizerTest {
    
    private TibetanTokenizer tokenizer;
    private TibetanDictionaryManager dictionary;
    
    @Before
    public void setUp() throws IOException {
        dictionary = new TibetanDictionaryManager();
        dictionary.initialize();
        tokenizer = new TibetanTokenizer(dictionary);
    }
    
    @Test
    public void testBasicTokenization() {
        String text = "བཀྲ་ཤིས་བདེ་ལེགས།";
        List<String> tokens = tokenizer.tokenize(text);
        
        assertNotNull(tokens);
        assertTrue(tokens.size() >= 2);
        System.out.println("Tokens: " + tokens);
    }
    
    @Test
    public void testEmptyString() {
        String text = "";
        List<String> tokens = tokenizer.tokenize(text);
        
        assertNotNull(tokens);
        assertEquals(0, tokens.size());
    }
    
    @Test
    public void testNullString() {
        List<String> tokens = tokenizer.tokenize(null);
        
        assertNotNull(tokens);
        assertEquals(0, tokens.size());
    }
    
    @Test
    public void testMixedTextWithPunctuation() {
        String text = "བོད་ཡིག།";
        List<String> tokens = tokenizer.tokenize(text);
        
        assertNotNull(tokens);
        assertTrue(tokens.size() >= 1);
        System.out.println("Mixed tokens: " + tokens);
    }
    
    @Test
    public void testDictionaryInitialization() {
        assertTrue(dictionary.isInitialized());
        assertNotNull(dictionary.getStatistics());
        System.out.println("Dictionary stats: " + dictionary.getStatistics());
    }
}

