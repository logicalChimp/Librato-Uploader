package uk.co.mentalspace.libratouploader.metrics;

import uk.co.mentalspace.libratouploader.Metric;
import uk.co.mentalspace.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import com.librato.metrics.LibratoBatch;
import com.librato.metrics.SingleValueGaugeMeasurement;

public class Junit implements Metric {
  
  private String metricPrefix;
  public Metric prefix(String prefix) {
    metricPrefix = prefix;
    return this;
  }
  
  public boolean canUse(Document doc) {
    if (null == doc) {
      return false;
    }

    NodeList nodes = doc.getElementsByTagName("testsuites");
    return (null != nodes && nodes.getLength() > 0);
  }
  
  public void process(LibratoBatch batch, Document doc) {
    NodeList nodes = doc.getElementsByTagName("testsuite");
    int errors = 0;
    int failures = 0;
    int total = 0;

    for (int i=0; i<nodes.getLength(); i++) {
      Node node = nodes.item(i);
      switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:
          NamedNodeMap atts = node.getAttributes();
          for (int j=0; j<atts.getLength(); j++) {
            Node att = atts.item(j);
            if ("errors".equals(att.getNodeName())) {
              errors += StringUtils.getAsInteger(att.getNodeValue());
            }
            if ("failures".equals(att.getNodeName())) {
              failures += StringUtils.getAsInteger(att.getNodeValue());
            }
            if ("tests".equals(att.getNodeName())) {
              total += StringUtils.getAsInteger(att.getNodeValue());
            }
          }
        default:
        //do nothing
      }
    }
    batch.addMeasurement(SingleValueGaugeMeasurement
                         .builder(metricPrefix + "junit-errors", errors)
                         .setMetricAttribute("display_name", "JUnit Errors")
                         .build());
    batch.addMeasurement(SingleValueGaugeMeasurement
                         .builder(metricPrefix + "junit-failures", failures)
                         .setMetricAttribute("display_name", "JUnit Failures")
                         .build());
    batch.addMeasurement(SingleValueGaugeMeasurement
                         .builder(metricPrefix + "junit-passed", total - (errors + failures))
                         .setMetricAttribute("display_name", "JUnit Passed")
                         .build());
    batch.addMeasurement(SingleValueGaugeMeasurement
                         .builder(metricPrefix + "junit-total", total)
                         .setMetricAttribute("display_name", "JUnit Total")
                         .build());
  }

  public Type getType() {
    return Type.gauges;
  }

}
