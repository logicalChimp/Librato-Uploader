package uk.co.mentalspace.libratouploader.metrics;

import uk.co.mentalspace.libratouploader.Metric;
import uk.co.mentalspace.utils.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import com.librato.metrics.LibratoBatch;
import com.librato.metrics.SingleValueGaugeMeasurement;
import java.util.Map;
import java.util.Date;

public class BuildState implements Metric {

  private String metricPrefix;
  public Metric prefix(String prefix) {
    metricPrefix = prefix;
    return this;
  }
  
  public boolean canUse(Document doc) {
    if (null == doc) {
      return false;
    }

    NodeList nodes = doc.getElementsByTagName("buildstatus");
    return (null != nodes && nodes.getLength() > 0);
  }
  
  public void process(LibratoBatch batch, Document doc) {
    Long startTimestamp = null;
    Long endTimestamp = null;
    Long buildId = null;
    Long buildState = null;
    NodeList nodes = doc.getElementsByTagName("buildstatus");
    for (int i=0; i<nodes.getLength(); i++) {
      Node node = nodes.item(i);
      switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:
          NamedNodeMap atts = node.getAttributes();
          for (int j=0; j<atts.getLength(); j++) {
            Node att = atts.item(j);
            if ("startime".equals(att.getNodeName())) {
              startTimestamp = StringUtils.getAsLong(att.getNodeValue());
            }
            if ("endtime".equals(att.getNodeName())) {
              endTimestamp = StringUtils.getAsLong(att.getNodeValue());
            }
            if ("buildid".equals(att.getNodeName())) {
              buildId = StringUtils.getAsLong(att.getNodeValue());
            }
            if ("buildstate".equals(att.getNodeName())) {
              buildState = StringUtils.getAsLong(att.getNodeValue());
            }
          }
      }
    }

    if (null != buildState) {
      batch.addMeasurement(SingleValueGaugeMeasurement
                           .builder(metricPrefix + "build-result", buildState)
                           .setMetricAttribute("display_name", "Build Result")
                           .build());
    }

    if (null != startTimestamp && null != endTimestamp && startTimestamp <= endTimestamp) {
      Long duration = endTimestamp - startTimestamp;
      batch.addMeasurement(SingleValueGaugeMeasurement
                           .builder(metricPrefix + "build-duration", duration)
                           .setMetricAttribute("display_name", "Build Duration")
                           .build());
    }

    if (null != buildId) {
      batch.addMeasurement(SingleValueGaugeMeasurement
                           .builder(metricPrefix + "build-id", buildId)
                           .setMetricAttribute("display_name", "Build Number")
                           .build());
    }
  }
  
  public Type getType() {
    return Type.gauges;
  }

}
