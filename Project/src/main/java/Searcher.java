import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.Analyzer;




public class Searcher {
    
    private int hitsPerPage = 10;
    private IndexReader indexReader;
    private IndexSearcher searcher;
    private String querriesPath;
    private Analyzer analyzer;
    
    public Searcher(Analyzer analyzer,Directory indexDirectory, String querriesPath) throws IOException{
        
        this.indexReader = DirectoryReader.open(indexDirectory);
        this.searcher = new IndexSearcher(indexReader);
        this.querriesPath = querriesPath;
        this.analyzer= analyzer;
    }

    public void searchDocuments() throws IOException, ParseException{

        List<Query> queries =  getQuerries();
        
        // iterate over queries and search in documents
        for(Query q : queries){
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;
            
            // display results
            System.out.println("For query " + q + " found  " + hits.length + " hits.");
            for(int i=0; i<hits.length; i++) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". " +" In document with id= " + d.get("id") + " and path= " + d.get("path")+ "\t");// + d.get("content"));
            }
        }
        
        indexReader.close();
    }

    private List<Query> getQuerries() throws ParseException, FileNotFoundException{

        List<Query> querries = new ArrayList<>();
        File qfile = new File(querriesPath);

        // Create a Scanner to read from the file
        Scanner scanner = new Scanner(qfile);
        
        // Read queries from the file
        while (scanner.hasNextLine()) {

            String qLine = scanner.nextLine();
            System.out.println("Query:" + qLine);
            // Process each query
            Query q = new QueryParser("content", analyzer).parse(qLine);
            
            System.out.println("Final query: " + q);
            querries.add(q);
        }
        scanner.close();


        return querries;
    }
}
