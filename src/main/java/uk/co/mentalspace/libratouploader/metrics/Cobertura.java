package uk.co.mentalspace.libratouploader.metrics;

import uk.co.mentalspace.libratouploader.Metric;
import uk.co.mentalspace.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import com.librato.metrics.LibratoBatch;
import com.librato.metrics.SingleValueGaugeMeasurement;

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
    processPackage(batch, doc);
    processClass(batch, doc);
    processMethod(batch, doc);
    processLine(batch, doc);
    processConditional(batch, doc);
  }

  public Type getType() {
    return Type.gauges;
  }

  private void processPackage(LibratoBatch batch, Document doc) {
    processMetric(batch, doc, "package", "cobertura-packages", "Cobertura Package Coverage");
  }
  
  private void processClass(LibratoBatch batch, Document doc) {
    processMetric(batch, doc, "class", "cobertura-classes", "Cobertura Class Coverage");
  }
  
  private void processMethod(LibratoBatch batch, Document doc) {
    processMetric(batch, doc, "method", "cobertura-methods", "Cobertura Method Coverage");
  }
  
  private void processLine(LibratoBatch batch, Document doc) {
    NodeList nodes = doc.getElementsByTagName("line");
    int totalLines = 0;
    double totalLineRate = 0.0;
    for (int i=0; i<nodes.getLength(); i++) {
      Node node = nodes.item(i);
      switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:
          totalLines++;
          NamedNodeMap atts = node.getAttributes();
          for (int j=0; j<atts.getLength(); j++) {
            Node att = atts.item(j);
            if ("hits".equals(att.getNodeName())) {
              int hits = StringUtils.getAsInteger(att.getNodeValue());
              if (hits > 0) {
                //if the line is hit at all, incremenet line rate.  
                totalLineRate += 1.0;
              }
            }
          }
        default:
        //do nothing
      }
    }
    int percent = (int) ((totalLineRate / totalLines) * 100);
    batch.addMeasurement(SingleValueGaugeMeasurement
                         .builder(metricPrefix + "cobertura-lines", percent)
                         .setMetricAttribute("display_name", "Cobertura Line Coverage")
                         .build());
  }
  
  private void processConditional(LibratoBatch batch, Document doc) {
    NodeList nodes = doc.getElementsByTagName("line");
    int totalBranches = 0;
    double branchHitRate = 0.0;
    for (int i=0; i<nodes.getLength(); i++) {
      Node node = nodes.item(i);
      switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:
          NamedNodeMap atts = node.getAttributes();
          boolean isBranch = false;
          String conditionCoverage = null;
          for (int j=0; j<atts.getLength(); j++) {
            Node att = atts.item(j);
            if ("branch".equals(att.getNodeName())) {
              if ("true".equals(att.getNodeValue())) {
                totalBranches++;
                isBranch = true;
              }
            }
            if ("condition-coverage".equals(att.getNodeName())) {
              conditionCoverage = att.getNodeValue();
            }
          }
          if (isBranch) {
            branchHitRate += calculateCoverage(conditionCoverage);
          }
        default:
        //do nothing
      }
    }
    int percent = (int) ((branchHitRate / totalBranches) * 100);
    batch.addMeasurement(SingleValueGaugeMeasurement
                         .builder(metricPrefix + "cobertura-conditional", percent)
                         .setMetricAttribute("display_name", "Cobertura Conditional Coverage")
                         .build());
  }
  
  private double calculateCoverage(String str) {
    //System.out.println("Calculting coverage.  source [" + str + "]");
    String[] parts = str.substring(str.indexOf("(") + 1, str.indexOf(")")).split("/");
    double hits = StringUtils.getAsDouble(parts[0]);
    double total = StringUtils.getAsDouble(parts[1]);
    return hits / total;
  }
  
  private void processMetric(LibratoBatch batch, Document doc, String tagName, String metricName, String displayName) {
    NodeList nodes = doc.getElementsByTagName(tagName);
    int totalPackages = 0;
    double totalLineRate = 0.0;
    for (int i=0; i<nodes.getLength(); i++) {
      Node node = nodes.item(i);
      switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:
          totalPackages++;
          NamedNodeMap atts = node.getAttributes();
          for (int j=0; j<atts.getLength(); j++) {
            Node att = atts.item(j);
            if ("line-rate".equals(att.getNodeName())) {
              totalLineRate += StringUtils.getAsDouble(att.getNodeValue());
            }
          }
        default:
        //do nothing
      }
    }
    int percent = (int) ((totalLineRate / totalPackages) * 100);
    batch.addMeasurement(SingleValueGaugeMeasurement
                         .builder(metricPrefix + metricName, percent)
                         .setMetricAttribute("display_name", displayName)
                         .build());
  }
}
