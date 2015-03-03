package uk.co.mentalspace.libratouploader;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStreamWriter;
import uk.co.mentalspace.utils.StringUtils;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException; 
import org.w3c.dom.Document;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import uk.co.mentalspace.libratouploader.metrics.*;
import com.librato.metrics.HttpPoster;
import com.librato.metrics.NingHttpPoster;
import com.librato.metrics.Sanitizer;
import com.librato.metrics.LibratoBatch;
import com.librato.metrics.BatchResult;
import com.librato.metrics.PostResult;

public class Processor {
  
  public static enum Error {NO_ERROR(0, "No Error (normal termination)"), 
                            INVALID_FILE(1, "Specified file does not exist"),
                            MISSING_LIBRATO_KEY(2, "No Librato Key specified"),
                            MISSING_LIBRATO_SECRET(3, "No Librato Secret specified");
  
    private int code;
    private String description;
    private Error(int i, String desc) {
      code = i;
      description = desc;
    }
    public int getCode() {
      return code;
    }
    public String getDesciption() {
      return description;
    }
    public String formatForOutput() {
      return getCodeFormatted() + ": " + description;
    }
    private String getCodeFormatted() {
      if (code < 100) {
        if (code < 10) {
          return "  " + code;
        }
        return " " + code;
      }
      return "" + code;
    }
  }
  
  private static final String DEFAULT_PREFIX = "TEST-";
  
  private String libratoKey;
  private String libratoSecretKey;
  private String source;
  private LibratoBatch batch = null;
  private List<Metric> metrics = new ArrayList<Metric>();
  
  public Processor(String prefix, String sourceName,  String key, String secretKey) {
    if (null == prefix) {
      prefix = DEFAULT_PREFIX;
    }
    libratoKey = key;
    libratoSecretKey = secretKey;
    source = sourceName;
    
    metrics.add((new Checkstyle()).prefix(prefix));
    metrics.add((new Cobertura()).prefix(prefix));
    metrics.add((new FindBugs()).prefix(prefix));
    metrics.add((new Junit()).prefix(prefix));
    metrics.add((new BuildState()).prefix(prefix));
    
    String apiUrl = "https://metrics-api.librato.com/v1/metrics";
    HttpPoster poster = NingHttpPoster.newPoster(libratoKey, libratoSecretKey, apiUrl);

    int batchSize = 300;
    long timeout = 10L;
    TimeUnit timeoutUnit = TimeUnit.SECONDS;
    String agent = "librato-uploader";
    batch = new LibratoBatch(batchSize, Sanitizer.LAST_PASS, timeout, timeoutUnit, agent, poster);
  }
  
  public Error process(String filename) {
    File file = new File(filename);
    if (!file.exists()) {
      System.err.println("Filename [" + filename + "] does not exist.");
      return Error.INVALID_FILE;
    }

    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder(); 
	   OutputStreamWriter errorWriter = new OutputStreamWriter(System.err, "UTF-8");
      db.setErrorHandler(new ErrorHandler (new PrintWriter(errorWriter, true)));
      Document doc = db.parse(file);
      
      for (Metric metric : metrics) {
        if (metric.canUse(doc)) {
          metric.process(batch, doc);
        }
      }
      
    } catch (ParserConfigurationException pce) {
      System.err.println(pce);
    } catch (UnsupportedEncodingException uee) {
      System.err.println(uee);
    } catch (SAXException se) {
      System.err.println(se);
    } catch (IOException ioe) {
      System.err.println(ioe);
    }
    
    
    return Error.NO_ERROR;
  }
  
  public Error upload() {
    if (StringUtils.isEmpty(libratoKey)) {
      return Error.MISSING_LIBRATO_KEY;
    }
    if (StringUtils.isEmpty(libratoSecretKey)) {
      return Error.MISSING_LIBRATO_SECRET;
    }

    long epoch = System.currentTimeMillis() / 1000;
    BatchResult result = batch.post(source, epoch);
    if (!result.success()) {
      for (PostResult post : result.getFailedPosts()) {
        System.err.println("Could not POST to Librato: " + post.toString());
      }
    }    
    return Error.NO_ERROR;
  }
  
}
