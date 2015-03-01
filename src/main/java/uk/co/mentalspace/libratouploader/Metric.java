package uk.co.mentalspace.libratouploader;

import org.w3c.dom.Document;
import org.json.simple.JSONArray;

public interface Metric {
  
  public enum Type { gauges, counters }
  
  public Metric prefix(String prefix);
  public boolean canUse(Document doc);
  public JSONArray process(Document doc);
  public Type getType();
}
