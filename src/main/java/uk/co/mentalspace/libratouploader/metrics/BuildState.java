package uk.co.mentalspace.libratouploader.metrics;

import uk.co.mentalspace.libratouploader.Metric;
import uk.co.mentalspace.utils.StringUtils;
import org.w3c.dom.Document;
import com.librato.metrics.LibratoBatch;
import java.util.Map;
import java.util.Date;

public class BuildState implements Metric {

  private String metricPrefix;
  public Metric prefix(String prefix) {
    metricPrefix = prefix;
    return this;
  }
  
  public boolean canUse(Document doc) {
    return false;
  }
  
  public void process(LibratoBatch batch, Document doc) {
    Long startTimestamp = null;
    String buildId = null;
    String result = null;
    Map<String, String> env = System.getenv();
    for (String envName : env.keySet()) {
      if ("buildStart".equals(envName)) {
        startTimestamp = StringUtils.getAsLong(env.get(envName));
      }
      if ("TRAVIS_BUILD_ID".equals(envName)) {
        buildId = env.get(envName);
      }
      if ("TRAVIS_TEST_RESULT".equals(envName)) {
        result = env.get(envName);
      }
    }
    
    long duration = 0;
    if (null != startTimestamp) {
      Long now = (new Date()).getTime();
      duration = now - startTimestamp;
    }
    System.out.println("metric [" + metricPrefix + "build-status], value [" + result + "], duration [" + duration + "], display name [Build " + buildId + "]");
  }
  
  public Type getType() {
    return Type.gauges;
  }

}
