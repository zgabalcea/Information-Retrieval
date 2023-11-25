
import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;

public class Indexer {

    public Analyzer analyzer;
    public IndexWriterConfig config;
    public IndexWriter writer;

    public Indexer(Analyzer analyzer, Directory indexDirectory) throws IOException{

        this.analyzer = analyzer;
        this.config = new IndexWriterConfig(analyzer);
        this.writer = new IndexWriter(indexDirectory, config);
    }

    
    public void indexDocuments(String docs_path) throws IOException {

        File folder = new File(docs_path);
        var TikaExt = new TikaExtractor();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            
            var id = 0;
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()){// && file.getName().endsWith(".txt")) {

                        System.out.println(file.getAbsolutePath());
                        // Read the content of the text file
                        try {
                            String content = TikaExt.extractText(file);

                            var docID = String.format("%d",id);
                            
                            // Create a new Lucene document
                            Document doc = new Document();
                            doc.add(new StringField("id", docID, Field.Store.YES));
                            doc.add(new TextField("content", content, Field.Store.YES));
                            doc.add(new StringField("path", file.getAbsolutePath(), Field.Store.YES));
                            
                            // System.out.println("content");
                            // System.out.println(doc.get("content"));
                            
                            // Add the document to the index
                            writer.updateDocument(new Term("id", docID), doc);
                            id++;
                        } catch (Exception e) {
                            e.printStackTrace();
                            writer.close();
                        }
                    }
                }
            }
        }

        writer.close();
    }
}
