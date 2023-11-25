
import org.apache.tika.Tika;
import java.io.File;
import java.io.FileInputStream;


public class TikaExtractor {

    public String extractText(File file) throws Exception {


        Tika tika = new Tika();
        // String content = tika.parseToString(stream);
        // return content;
        var bool = file.canRead();
        
        System.out.println("file from path : " + file.getAbsolutePath());
        System.out.println("file is readable:" + bool);

        String fileType = tika.detect(file);
        System.out.println("File Type: " + fileType);
        
        String content = tika.parseToString(new FileInputStream(file));
        
        return content;
    }
}

