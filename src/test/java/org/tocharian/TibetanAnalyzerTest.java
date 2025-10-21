package org.tocharian;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TibetanAnalyzerTest {
    
    private TibetanAnalyzer analyzer;
    
    @Before
    public void setUp() {
        analyzer = new TibetanAnalyzer();
    }
    
    @Test
    public void testBasicGreeting() throws Exception {
        String text = "བཀྲ་ཤིས་བདེ་ལེགས།";
        List<String> tokens = tokenize(text);
        
        System.out.println("Test: " + text);
        System.out.println("Tokens: " + tokens);
        
        assertFalse("Should produce tokens", tokens.isEmpty());
        
        // Expected: བཀྲ་ཤིས་, བདེ་ལེགས, །
        // At minimum should not split by tsek
        assertTrue("Should not have single syllables only", 
            tokens.stream().anyMatch(t -> t.length() > 3));
    }
    
    @Test
    public void testSentenceWithParticles() throws Exception {
        String text = "ང་ཚོས་སློབ་གྲྭར་འགྲོ་དགོས།";
        List<String> tokens = tokenize(text);
        
        System.out.println("Test: " + text);
        System.out.println("Tokens: " + tokens);
        
        assertFalse("Should produce tokens", tokens.isEmpty());
    }
    
    @Test
    public void testDictionaryLoaded() {
        String stats = analyzer.getStatistics();
        System.out.println("Dictionary statistics: " + stats);
        
        assertNotNull("Statistics should not be null", stats);
        assertFalse("Dictionary should be initialized", 
            stats.contains("not initialized"));
    }
    
    private List<String> tokenize(String text) throws Exception {
        List<String> result = new ArrayList<>();
        TokenStream ts = analyzer.tokenStream("test", new StringReader(text));
        CharTermAttribute termAttr = ts.addAttribute(CharTermAttribute.class);
        
        try {
            ts.reset();
            while (ts.incrementToken()) {
                result.add(termAttr.toString());
            }
            ts.end();
        } finally {
            ts.close();
        }
        
        return result;
    }
}

