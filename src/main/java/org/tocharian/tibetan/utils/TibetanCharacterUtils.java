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

package org.tocharian.tibetan.utils;

/**
 * Tibetan character utility class
 * Provides character detection and text normalization functions
 */
public class TibetanCharacterUtils {
    
    /**
     * Check if a character is Tibetan text
     * Unicode ranges: 0x0F00-0x0F03 (symbols), 0x0F40-0x0FBC (letters)
     */
    public static boolean isTibetanCharacter(int codePoint) {
        return (codePoint >= 0x0F00 && codePoint <= 0x0F03) ||
               (codePoint >= 0x0F40 && codePoint <= 0x0FBC);
    }
    
    /**
     * Check if a character is Tibetan text (char version)
     */
    public static boolean isTibetanCharacter(char c) {
        return isTibetanCharacter((int) c);
    }
    
    /**
     * Check if a string contains Tibetan characters
     */
    public static boolean containsTibetanCharacters(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        for (int i = 0; i < text.length(); i++) {
            int codePoint = text.codePointAt(i);
            if (isTibetanCharacter(codePoint)) {
                return true;
            }
            if (Character.isSupplementaryCodePoint(codePoint)) {
                i++; // Skip the second char of surrogate pair
            }
        }
        return false;
    }
    
    /**
     * Count syllables in Tibetan text
     * Syllables are separated by tsheg (་) or shad (།)
     */
    public static int countSyllables(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        boolean inSyllable = false;
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (isTibetanCharacter(c) && c != '་' && c != '༌') {
                if (!inSyllable) {
                    count++;
                    inSyllable = true;
                }
            } else if (c == '་' || c == '༌') {
                inSyllable = false;
            }
        }
        
        return count;
    }
    
    /**
     * Normalize Tibetan text
     * Removes extra whitespace and normalizes separators
     */
    public static String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        
        // Trim and normalize spaces
        text = text.trim().replaceAll("\\s+", " ");
        
        // Normalize Tibetan tsheg shad (༌) to tsheg (་)
        text = text.replace('༌', '་');
        
        return text;
    }
    
    /**
     * Convert string to symbol array (handling surrogate pairs)
     */
    public static String[] getSymbols(String text) {
        int length = text.length();
        String[] symbols = new String[length];
        int index = 0;
        
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            int charCode = (int) c;
            
            // Check for surrogate pair
            if (charCode >= 0xD800 && charCode <= 0xDBFF && i + 1 < length) {
                symbols[index++] = "" + c + text.charAt(++i);
            } else {
                symbols[index++] = "" + c;
            }
        }
        
        // Trim to actual size
        String[] result = new String[index];
        System.arraycopy(symbols, 0, result, 0, index);
        return result;
    }
}

