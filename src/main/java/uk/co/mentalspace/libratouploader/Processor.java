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
import uk.co.mentalspace.libratouploader.metrics.*;

public class Processor {
  
  public static enum Error {NO_ERROR(0, "No Error (normal termination)"), 
                            INVALID_FILE(1, "Specified file does not exist"),
                            MISSING_LIBRATO_KEY(2, "No Librato Key specified"),
                            MISSING_LIBRATO_SECRET(3, "No Librato Secret specified");
  
    private int code;
    private String description;
    private Error(int i, String desc) {
      code = i;
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
  
  private String metricPrefix;
  private String libratoKey;
  private String libratoSecretKey;
  
  private List<Metric> metrics = new ArrayList<Metric>();
  
  public Processor(String prefix, String key, String secretKey) {
    metricPrefix = prefix;
    if (null == metricPrefix) {
      metricPrefix = DEFAULT_PREFIX;
    }
    libratoKey = key;
    libratoSecretKey = secretKey;
    
    metrics.add(new Checkstyle());
    metrics.add(new Cobertura());
    metrics.add(new FindBugs());
    metrics.add(new Junit());
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
          metric.process(doc);
        }
      }
      
    } catch (ParserConfigurationException pce) {
    } catch (UnsupportedEncodingException uee) {
    } catch (SAXException se) {
    } catch (IOException ioe) {
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
    return Error.NO_ERROR;
  }
  
}
