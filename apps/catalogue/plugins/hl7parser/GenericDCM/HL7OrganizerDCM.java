/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.hl7parser.GenericDCM;

import java.util.ArrayList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author roankanninga
 */
public class HL7OrganizerDCM {

	private Node organizer;
    private XPath xpath;
    private static final String OBSERVATION = "urn:hl7-org:v3:organizer/urn:hl7-org:v3:component";
    private static final String ORGANIZER_NAME = "comment()";
    public ArrayList<Node> allMeasurementNodes = new ArrayList<Node>();
    public String organizerName;
    public ArrayList<HL7MeasurementDCM> measurements;

    public HL7OrganizerDCM (Node organizer, XPath xpath ) throws Exception{
        this.organizer = organizer;
        this.xpath = xpath;      
        readOrganizerName();
        
        NodeList nodes = (NodeList)xpath.compile(OBSERVATION).evaluate(organizer, XPathConstants.NODESET);
        this.measurements = new ArrayList<HL7MeasurementDCM>();
        for (int i = 0; i < nodes.getLength(); i++) {
        	HL7MeasurementDCM meas = new HL7MeasurementDCM(nodes.item(i),xpath);
            measurements.add(meas);
        }
    }

    public void readOrganizerName() throws XPathExpressionException{
        
        Node nameNode = (Node) xpath.evaluate(ORGANIZER_NAME, organizer, XPathConstants.NODE);
        this.organizerName = nameNode.getNodeValue();
        
    }

    public String getHL7OrganizerNameDCM() {
        return organizerName;
    }



}
