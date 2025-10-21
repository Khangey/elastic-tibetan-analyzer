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

import org.tocharian.TibetanDictionaryLoader;

import java.io.IOException;
import java.util.*;

/**
 * Tibetan dictionary manager
 * Manages all dictionary data and provides lookup functions
 */
public class TibetanDictionaryManager {
    
    private Set<String> particles;
    private Set<String> verbs;
    private Map<Integer, Set<String>> wordsBySyllable;  // Indexed by syllable count
    private Set<String> verbsAshung;
    private Set<String> wordsAshung;
    private Set<String> customWords;
    private List<String> suffixes;
    private Set<String> secondSuffixes;
    
    private boolean initialized = false;
    
    public TibetanDictionaryManager() {
        wordsBySyllable = new HashMap<>();
        for (int i = 1; i <= 4; i++) {
            wordsBySyllable.put(i, new HashSet<>());
        }
    }
    
    /**
     * Initialize and load all dictionaries
     */
    public void initialize() throws IOException {
        if (initialized) {
            return;
        }
        
        System.out.println("Loading Tibetan dictionaries...");
        
        // Load particles
        particles = TibetanDictionaryLoader.loadDictionary("particles.txt");
        System.out.println("Loaded particles: " + particles.size());
        
        // Load verbs
        verbs = TibetanDictionaryLoader.loadDictionary("verbs.txt");
        System.out.println("Loaded verbs: " + verbs.size());
        
        // Load verbs ashung
        verbsAshung = TibetanDictionaryLoader.loadDictionary("verbs_ashung.txt");
        System.out.println("Loaded verbs ashung: " + verbsAshung.size());
        
        // Load words by syllable count
        wordsBySyllable.put(1, TibetanDictionaryLoader.loadDictionary("words_1_syllable.txt"));
        System.out.println("Loaded 1-syllable words: " + wordsBySyllable.get(1).size());
        
        wordsBySyllable.put(2, TibetanDictionaryLoader.loadDictionary("words_2_syllable.txt"));
        System.out.println("Loaded 2-syllable words: " + wordsBySyllable.get(2).size());
        
        wordsBySyllable.put(3, TibetanDictionaryLoader.loadDictionary("words_3_syllable.txt"));
        System.out.println("Loaded 3-syllable words: " + wordsBySyllable.get(3).size());
        
        wordsBySyllable.put(4, TibetanDictionaryLoader.loadDictionary("words_4_syllable.txt"));
        System.out.println("Loaded 4-syllable words: " + wordsBySyllable.get(4).size());
        
        // Load words ashung
        wordsAshung = TibetanDictionaryLoader.loadDictionary("words_ashung.txt");
        System.out.println("Loaded words ashung: " + wordsAshung.size());
        
        // Load custom dictionary (may be empty)
        try {
            customWords = TibetanDictionaryLoader.loadDictionary("custom_dictionary.txt");
            System.out.println("Loaded custom words: " + customWords.size());
        } catch (IOException e) {
            customWords = new HashSet<>();
            System.out.println("No custom dictionary found, using empty set");
        }
        
        // Load suffixes (order matters)
        suffixes = TibetanDictionaryLoader.loadDictionaryAsList("suffixes.txt");
        System.out.println("Loaded suffixes: " + suffixes.size());
        
        // Load second suffixes
        secondSuffixes = TibetanDictionaryLoader.loadDictionary("second_suffixes.txt");
        System.out.println("Loaded second suffixes: " + secondSuffixes.size());
        
        initialized = true;
        System.out.println("Tibetan dictionary manager initialized successfully");
    }
    
    /**
     * Check if a word exists in particles
     */
    public boolean isParticle(String word) {
        return particles != null && particles.contains(word);
    }
    
    /**
     * Check if a word exists in verbs
     */
    public boolean isVerb(String word) {
        return verbs != null && verbs.contains(word);
    }
    
    /**
     * Check if a word exists in the dictionary with specified syllable count
     */
    public boolean containsWord(String word, int syllableCount) {
        if (wordsBySyllable == null || !wordsBySyllable.containsKey(syllableCount)) {
            return false;
        }
        return wordsBySyllable.get(syllableCount).contains(word);
    }
    
    /**
     * Check if a word is in custom dictionary
     */
    public boolean isCustomWord(String word) {
        return customWords != null && customWords.contains(word);
    }
    
    /**
     * Check if a word is an ashung verb
     */
    public boolean isVerbAshung(String word) {
        return verbsAshung != null && verbsAshung.contains(word);
    }
    
    /**
     * Check if a word is an ashung word
     */
    public boolean isWordAshung(String word) {
        return wordsAshung != null && wordsAshung.contains(word);
    }
    
    /**
     * Get all suffixes
     */
    public List<String> getSuffixes() {
        return suffixes;
    }
    
    /**
     * Check if a suffix is a second suffix
     */
    public boolean isSecondSuffix(String suffix) {
        return secondSuffixes != null && secondSuffixes.contains(suffix);
    }
    
    /**
     * Check if initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Get statistics
     */
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("particles", particles != null ? particles.size() : 0);
        stats.put("verbs", verbs != null ? verbs.size() : 0);
        stats.put("verbs_ashung", verbsAshung != null ? verbsAshung.size() : 0);
        stats.put("words_1_syllable", wordsBySyllable.get(1) != null ? wordsBySyllable.get(1).size() : 0);
        stats.put("words_2_syllable", wordsBySyllable.get(2) != null ? wordsBySyllable.get(2).size() : 0);
        stats.put("words_3_syllable", wordsBySyllable.get(3) != null ? wordsBySyllable.get(3).size() : 0);
        stats.put("words_4_syllable", wordsBySyllable.get(4) != null ? wordsBySyllable.get(4).size() : 0);
        stats.put("words_ashung", wordsAshung != null ? wordsAshung.size() : 0);
        stats.put("custom_words", customWords != null ? customWords.size() : 0);
        stats.put("suffixes", suffixes != null ? suffixes.size() : 0);
        stats.put("second_suffixes", secondSuffixes != null ? secondSuffixes.size() : 0);
        return stats;
    }
}

