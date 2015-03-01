package uk.co.mentalspace.libratouploader;

import org.w3c.dom.Document;
import com.librato.metrics.LibratoBatch;

public interface Metric {
  
  public enum Type { gauges, counters }
  
  public Metric prefix(String prefix);
  public boolean canUse(Document doc);
  public void process(LibratoBatch batch, Document doc);
  public Type getType();
}
