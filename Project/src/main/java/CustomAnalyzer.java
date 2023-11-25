
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.FilteringTokenFilter;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.ext.RomanianStemmer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;

public class CustomAnalyzer extends Analyzer {

    private CharArraySet stopWords;

    Map<Character, Character> diacriticMap;

    public Map<Character, Character> DiacriticMapper() {

        Map<Character, Character> d_map =  new HashMap<>();

        d_map.put('ă', 'a');
        d_map.put('â', 'a');
        d_map.put('î', 'i');
        d_map.put('ş', 's');
        d_map.put('ţ', 't');

        return d_map;
    }

    public CustomAnalyzer() {
        this.diacriticMap = DiacriticMapper();
        this.stopWords = this.getFullStopSet();
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        
        Tokenizer source = new StandardTokenizer();
        
        TokenStream result = new LowerCaseFilter(source);

        // Uniform diacritics in order to be recognized by other filters from RomanianAnalyzer.
        result = new ReplaceDiacriticsFilter(result);
        
        //Removing stop words
        result = new StopFilter(result, stopWords);
        
        //Applying stemming
        result = new SnowballFilter((TokenStream)result, new RomanianStemmer());

        //Remove diacritics
        result = new ASCIIFoldingFilter(result);


        return new Analyzer.TokenStreamComponents(source, result);
    }

    private class ReplaceDiacriticsFilter extends FilteringTokenFilter {

        private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

        protected ReplaceDiacriticsFilter(TokenStream in) {
            super(in);
        }

        @Override
        protected boolean accept() throws IOException {
            char[] buffer = termAtt.buffer();
            int length = termAtt.length();

            // Replace diacritics in the term buffer
            for (int i = 0; i < length; i++) {
                switch (buffer[i]) {
                    case 'ș':
                        buffer[i] = 'ş';
                        break;
                    case 'ț':
                        buffer[i] = 'ţ';
                        break;
                    // Add more cases for other diacritics as needed
                }
            }

            return true;
        }
    }

    protected CharArraySet getFullStopSet() {

        CharArraySet defaultStopSet = RomanianAnalyzer.getDefaultStopSet();
        HashSet<char[]> stopwords_hashset = new HashSet<>();

        // Add stopwords without diacritics to the set
        for (Object stopword : defaultStopSet) {

            char[] c_stopword = (char []) stopword;
            stopwords_hashset.add(c_stopword);

            char[] stopwordWithoutDiacritics = removeDiacritics(c_stopword);
            if( !stopwords_hashset.contains(stopwordWithoutDiacritics))
                stopwords_hashset.add(stopwordWithoutDiacritics);

            // fullStopSet.add(stopwordWithoutDiacritics);
        }

        System.out.println("List of all stopwords : ");
        var fullStopSet = new CharArraySet(stopwords_hashset, true);
        // System.out.println(fullStopSet);

        return fullStopSet;
    }

    private char[] removeDiacritics(char[] input) {

        var inp_copy = input.clone();
        var d = 0;

        for (int i = 0; i < input.length; i++) {
            char currentChar = input[i];
            if (diacriticMap.containsKey(currentChar)) {
                d=1;
                inp_copy[i] = diacriticMap.get(currentChar);
            }
        }

        if (d ==1) {
            return inp_copy;
        }
        return input;
    }
}
