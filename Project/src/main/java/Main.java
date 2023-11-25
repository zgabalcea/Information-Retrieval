
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.Analyzer;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        
        String docs_path = "src\\main\\aux_files\\docs";
        String indexPath = "src\\main\\aux_files\\indexer";
        String queriesPath = "src\\main\\aux_files\\queries.txt";
        
        // 0. Specify the analyzer for tokenizing text.
        Analyzer analyzer = new CustomAnalyzer();
        
        // 1. create the index
        Directory index = FSDirectory.open(Paths.get(indexPath));

        try{

            Indexer indexer = new Indexer(analyzer, index);
            indexer.indexDocuments(docs_path);
            IndexReader indexReader = DirectoryReader.open(index);
    
            // Get the number of documents in the index
            int numDocuments = indexReader.numDocs();
            System.out.println("number of documents");
            System.out.println(numDocuments);
    
            // 2. Search in documents
            var searcher = new Searcher(analyzer, index, queriesPath);
            searcher.searchDocuments();

        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
