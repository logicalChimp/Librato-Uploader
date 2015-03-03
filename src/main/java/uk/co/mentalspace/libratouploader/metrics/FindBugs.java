package uk.co.mentalspace.libratouploader.metrics;

import uk.co.mentalspace.libratouploader.Metric;
import uk.co.mentalspace.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import com.librato.metrics.LibratoBatch;
import com.librato.metrics.SingleValueGaugeMeasurement;

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
  
  public void process(LibratoBatch batch, Document doc) {
    NodeList nodes = doc.getElementsByTagName("FindBugsSummary");
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
              p1value = StringUtils.getAsInteger(att.getNodeValue());
              batch.addMeasurement(SingleValueGaugeMeasurement
                                   .builder(metricPrefix + "findbugs-p1", p1value)
                                   .setMetricAttribute("display_name", "FindBugs Priority 1")
                                   .build());
            }
            if ("priority_2".equals(att.getNodeName())) {
              p2value = StringUtils.getAsInteger(att.getNodeValue());
              batch.addMeasurement(SingleValueGaugeMeasurement
                                   .builder(metricPrefix + "findbugs-p2", p2value)
                                   .setMetricAttribute("display_name", "FindBugs Priority 2")
                                   .build());
            }
            if ("total_bugs".equals(att.getNodeName())) {
              totalvalue = StringUtils.getAsInteger(att.getNodeValue());
              batch.addMeasurement(SingleValueGaugeMeasurement
                                   .builder(metricPrefix + "findbugs-total", totalvalue)
                                   .setMetricAttribute("display_name", "FindBugs Total")
                                   .build());
            }
          }
        default:
        //do nothing
      }
      batch.addMeasurement(SingleValueGaugeMeasurement
                           .builder(metricPrefix + "findbugs-other", (totalvalue - (p1value + p2value)))
                           .setMetricAttribute("display_name", "FindBugs (Other)")
                           .build());
    }
  }

  public Type getType() {
    return Type.gauges;
  }

}
