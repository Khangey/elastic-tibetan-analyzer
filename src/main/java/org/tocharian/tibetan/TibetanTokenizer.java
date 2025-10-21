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

import org.tocharian.tibetan.utils.TibetanCharacterUtils;

import java.util.*;

/**
 * Tibetan tokenizer
 * Implements longest-match algorithm for Tibetan word segmentation
 */
public class TibetanTokenizer {
    
    private final TibetanDictionaryManager dictionary;
    private final boolean debug = false;
    
    // Characters to insert between tokens
    private static final String SPACE_MARKER = " ";
    private static final String PARTICLE_MARKER = "";  // Particles don't get space before them
    
    public TibetanTokenizer(TibetanDictionaryManager dictionary) {
        this.dictionary = dictionary;
    }
    
    /**
     * Tokenize Tibetan text
     * @param text Input Tibetan text
     * @return List of tokens
     */
    public List<String> tokenize(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        text = TibetanCharacterUtils.normalizeText(text);
        String[] symbols = TibetanCharacterUtils.getSymbols(text);
        List<String> result = new ArrayList<>();
        
        int beginIndex = 0;
        
        while (beginIndex < symbols.length) {
            // Skip non-Tibetan characters
            while (beginIndex < symbols.length && !isTibetanText(symbols[beginIndex])) {
                if (!symbols[beginIndex].trim().isEmpty()) {
                    result.add(symbols[beginIndex]);
                }
                beginIndex++;
            }
            
            if (beginIndex >= symbols.length) {
                break;
            }
            
            // Get next syllables (up to 4)
            SyllableInfo syllables = getNextSyllables(symbols, beginIndex, 4);
            int nbSyllables = syllables.nbSyllables;
            
            if (nbSyllables < 1) {
                beginIndex++;
                continue;
            }
            
            // Longest match algorithm
            boolean found = false;
            while (nbSyllables >= 1) {
                LookupResult lookupResult = lookupStr(nbSyllables, syllables.str);
                
                if (lookupResult != null) {
                    // Found a match
                    String token = syllables.str;
                    
                    // Handle suffix reconstruction for ashung words
                    if (lookupResult.ashung && lookupResult.suffixLength > 0) {
                        // Reconstruct with འ prefix
                        int splitPoint = token.length() - lookupResult.suffixLength;
                        String root = token.substring(0, splitPoint);
                        String suffix = token.substring(splitPoint);
                        result.add(root + "འ");
                        result.add(suffix);
                    } else if (lookupResult.suffixLength > 0) {
                        // Split root and suffix
                        int splitPoint = token.length() - lookupResult.suffixLength;
                        String root = token.substring(0, splitPoint);
                        String suffix = token.substring(splitPoint);
                        result.add(root);
                        result.add(suffix);
                    } else {
                        result.add(token);
                    }
                    
                    beginIndex += syllables.strLen;
                    
                    // Skip tsheg after the word
                    if (beginIndex < symbols.length && 
                        (symbols[beginIndex].equals("་") || symbols[beginIndex].equals("༌"))) {
                        beginIndex++;
                    }
                    
                    found = true;
                    break;
                } else {
                    // No match, try fewer syllables
                    nbSyllables--;
                    if (nbSyllables > 0) {
                        syllables = getNextSyllables(symbols, beginIndex, nbSyllables);
                    }
                }
            }
            
            // If no match found, treat as unknown
            if (!found) {
                syllables = getNextSyllables(symbols, beginIndex, 1);
                if (syllables.nbSyllables > 0) {
                    result.add(syllables.str);
                    beginIndex += syllables.strLen;
                    
                    // Skip tsheg
                    if (beginIndex < symbols.length && 
                        (symbols[beginIndex].equals("་") || symbols[beginIndex].equals("༌"))) {
                        beginIndex++;
                    }
                } else {
                    beginIndex++;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Lookup a string in the dictionary
     * Returns null if not found, otherwise returns lookup result with suffix info
     */
    private LookupResult lookupStr(int nbSyllables, String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        
        // Priority order: particles > verbs > words > custom
        if (dictionary.isParticle(str)) {
            return new LookupResult("particle", 0, false);
        }
        
        if (dictionary.isVerb(str)) {
            return new LookupResult("verb", 0, false);
        }
        
        if (dictionary.containsWord(str, nbSyllables)) {
            return new LookupResult("word", 0, false);
        }
        
        if (dictionary.isCustomWord(str)) {
            return new LookupResult("custom", 0, false);
        }
        
        // Try removing suffix
        int suffixLength = getSuffixLength(str);
        if (suffixLength > 0) {
            String root = str.substring(0, str.length() - suffixLength);
            
            if (root.isEmpty()) {
                return null;
            }
            
            // Check ashung forms first
            if (dictionary.isVerbAshung(root)) {
                return new LookupResult("verb", suffixLength, true);
            }
            
            if (dictionary.isWordAshung(root)) {
                return new LookupResult("word", suffixLength, true);
            }
            
            // Check regular forms
            if (dictionary.isVerb(root)) {
                return new LookupResult("verb", suffixLength, false);
            }
            
            if (dictionary.isParticle(root)) {
                return new LookupResult("particle", suffixLength, false);
            }
            
            if (dictionary.containsWord(root, nbSyllables)) {
                return new LookupResult("word", suffixLength, false);
            }
            
            if (dictionary.isCustomWord(root)) {
                return new LookupResult("custom", suffixLength, false);
            }
        }
        
        return null;
    }
    
    /**
     * Get suffix length of a string
     */
    private int getSuffixLength(String str) {
        List<String> suffixes = dictionary.getSuffixes();
        
        for (String suffix : suffixes) {
            if (str.endsWith(suffix)) {
                // Special handling for ས when it's a second suffix
                if (suffix.equals("ས") && str.length() > 2) {
                    String lastTwo = str.substring(str.length() - 2);
                    // Check if this is a second suffix (གས, ངས, བས, མས)
                    if (dictionary.isSecondSuffix(lastTwo) && 
                        str.length() > 2 && 
                        str.substring(str.length() - 3, str.length() - 2).indexOf('་') == -1) {
                        // This is a second suffix, don't treat as grammatical suffix
                        continue;
                    }
                }
                return suffix.length();
            }
        }
        
        return 0;
    }
    
    /**
     * Get next N syllables from symbol array
     */
    private SyllableInfo getNextSyllables(String[] symbols, int beginIndex, int nbSyllables) {
        int resNbSyllables = 1;
        StringBuilder resStr = new StringBuilder();
        int resStrLen = 0;
        boolean lastIsTsheg = false;
        
        while (beginIndex < symbols.length && 
               (isTibetanText(symbols[beginIndex]) || 
                symbols[beginIndex].equals("་") || 
                symbols[beginIndex].equals("༌"))) {
            
            if (lastIsTsheg) {
                resStrLen++;
                resStr.append("་");
            }
            
            if (symbols[beginIndex].equals("་") || symbols[beginIndex].equals("༌")) {
                lastIsTsheg = true;
                if (resNbSyllables >= nbSyllables) {
                    break;
                }
                resNbSyllables++;
            } else {
                lastIsTsheg = false;
                resStrLen++;
                resStr.append(symbols[beginIndex]);
            }
            
            beginIndex++;
        }
        
        return new SyllableInfo(resStr.toString(), resNbSyllables, resStrLen);
    }
    
    /**
     * Check if a symbol is Tibetan text
     */
    private boolean isTibetanText(String symbol) {
        if (symbol == null || symbol.isEmpty()) {
            return false;
        }
        
        int codePoint = symbol.codePointAt(0);
        return TibetanCharacterUtils.isTibetanCharacter(codePoint);
    }
    
    /**
     * Inner class to hold syllable information
     */
    private static class SyllableInfo {
        String str;
        int nbSyllables;
        int strLen;
        
        SyllableInfo(String str, int nbSyllables, int strLen) {
            this.str = str;
            this.nbSyllables = nbSyllables;
            this.strLen = strLen;
        }
    }
    
    /**
     * Inner class to hold lookup result
     */
    private static class LookupResult {
        String type;
        int suffixLength;
        boolean ashung;
        
        LookupResult(String type, int suffixLength, boolean ashung) {
            this.type = type;
            this.suffixLength = suffixLength;
            this.ashung = ashung;
        }
    }
}

