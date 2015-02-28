package uk.co.mentalspace.libratouploader;

public class Processor {
  private String metricPrefix;
  private String libratoKey;
  private String libratoSecretKey;
  
  public Processor(String prefix, String key, String secretKey) {
    metricPrefix = prefix;
    if (null == metricPrefix) {
      metricPrefix = "TEST-";
    }
    libratoKey = key;
    libratoSecretKey = secretKey;
  }
  
  public int process(String filename) {
    return 0;
  }
  
  public int upload() {
    return 0;
  }
  
}
