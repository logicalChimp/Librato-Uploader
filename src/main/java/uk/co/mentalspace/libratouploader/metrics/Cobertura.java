package uk.co.mentalspace.libratouploader.metrics;

import uk.co.mentalspace.libratouploader.Metric;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import com.librato.metrics.LibratoBatch;

public class Cobertura implements Metric {
  
  private String metricPrefix;
  public Metric prefix(String prefix) {
    metricPrefix = prefix;
    return this;
  }
  
  public boolean canUse(Document doc) {
    if (null == doc) {
      return false;
    }

    NodeList nodes = doc.getElementsByTagName("coverage");
    return (null != nodes && nodes.getLength() > 0);
  }
  
  public void process(LibratoBatch batch, Document doc) {
  }

  public Type getType() {
    return Type.gauges;
  }

}
