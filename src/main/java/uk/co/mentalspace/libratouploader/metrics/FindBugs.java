package uk.co.mentalspace.libratouploader.metrics;

import uk.co.mentalspace.libratouploader.Metric;
import uk.co.mentalspace.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class FindBugs implements Metric {

  private String metricPrefix;
  public Metric prefix(String prefix) {
    metricPrefix = prefix;
    return this;
  }
  
  public boolean canUse(Document doc) {
    if (null == doc) {
      return false;
    }

    NodeList nodes = doc.getElementsByTagName("BugCollection");
    return (null != nodes && nodes.getLength() > 0);
  }
  
  @SuppressWarnings("unchecked")
  public JSONArray process(Document doc) {
    NodeList nodes = doc.getElementsByTagName("FindBugsSummary");
    JSONArray metrics = new JSONArray();
    for (int i=0; i<nodes.getLength(); i++) {
      Node node = nodes.item(i);
      int p1value = 0;
      int p2value = 0;
      int totalvalue = 0;
      switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:
          NamedNodeMap atts = node.getAttributes();
          for (int j=0; j<atts.getLength(); j++) {
            Node att = atts.item(j);
            if ("priority_1".equals(att.getNodeName())) {
              JSONObject p1 = new JSONObject();
              p1.put("name", metricPrefix + "findbugs-p1");
              p1value = StringUtils.getAsInteger(att.getNodeValue());
              p1.put("value", p1value);
              p1.put("display_name", "FindBugs Priority 1");
              metrics.add(p1);
            }
            if ("priority_2".equals(att.getNodeName())) {
              JSONObject p2 = new JSONObject();
              p2.put("name", metricPrefix + "findbugs-p2");
              p2value = StringUtils.getAsInteger(att.getNodeValue());
              p2.put("value", p2value);
              p2.put("display_name", "FindBugs Priority 2");
              metrics.add(p2);
            }
            if ("total".equals(att.getNodeName())) {
              JSONObject total = new JSONObject();
              total.put("name", metricPrefix + "findbugs-total");
              totalvalue = StringUtils.getAsInteger(att.getNodeValue());
              total.put("value", totalvalue);
              total.put("display_name", "FindBugs Total");
              metrics.add(total);
            }
          }
        default:
        //do nothing
      }
      JSONObject other = new JSONObject();
      other.put("name", metricPrefix + "findbugs-other");
      other.put("value", (totalvalue - (p1value + p2value)));
      other.put("display_name", "FindBugs (Other)");
      metrics.add(other);      
    }
    
    return metrics;
  }

  public Type getType() {
    return Type.gauges;
  }

}
