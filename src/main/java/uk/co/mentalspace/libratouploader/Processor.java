package uk.co.mentalspace.libratouploader;

import java.io.File;
import uk.co.mentalspace.utils.StringUtils;

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
  
  public Processor(String prefix, String key, String secretKey) {
    metricPrefix = prefix;
    if (null == metricPrefix) {
      metricPrefix = DEFAULT_PREFIX;
    }
    libratoKey = key;
    libratoSecretKey = secretKey;
  }
  
  public Error process(String filename) {
    File file = new File(filename);
    if (!file.exists()) {
      System.err.println("Filename [" + filename + "] does not exist.");
      return Error.INVALID_FILE;
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
