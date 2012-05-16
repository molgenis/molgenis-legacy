/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Molgenis;

import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author roankanninga
 */
public class HL7Organizer {

    private Node organizer;
    private XPath xpath;
    private static final String OBSERVATION = "urn:hl7-org:v3:component/urn:hl7-org:v3:observation";
    private static final String ORGANIZER_NAME = "urn:hl7-org:v3:code/@code";
    ArrayList<Node> allMeasurementNodes = new ArrayList<Node>();
    private String organizerName;
     ArrayList<Measurement> measurements;

    public HL7Organizer (Node organizer, XPath xpath ) throws Exception{
        this.organizer = organizer;
        this.xpath = xpath;      
        readOrganizerName();
        
        NodeList nodes = (NodeList)xpath.compile(OBSERVATION).evaluate(organizer, XPathConstants.NODESET);
        this.measurements = new ArrayList<Measurement>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Measurement meas = new Measurement(nodes.item(i),xpath);
            measurements.add(meas);
        }
    }

    private void readOrganizerName() throws XPathExpressionException{
        
        Node nameNode = (Node) xpath.evaluate(ORGANIZER_NAME, organizer, XPathConstants.NODE);
        this.organizerName = nameNode.getNodeValue();
        
    }

    public String getOrganizerName() {
        return organizerName;
    }


}
