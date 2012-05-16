/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.hl7parser;

import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Node;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author roankanninga
 */
public class HL7stagecatalog {

    ArrayList<HL7Organizer> hl7organizer;
    ArrayList<Measurement> measurement;
    XPath xpath;

    private static final String ORGANIZER = "//urn:hl7-org:v3:organizer";

    public HL7stagecatalog(String fileName) throws Exception{

        String xpathExpres = ORGANIZER;
        ArrayList<Node> allOrganizerNodes = new ArrayList<Node>();
        XPathFactory factory = XPathFactory.newInstance();
        this.xpath = factory.newXPath();

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(fileName);
        NodeList nodes = (NodeList)xpath.compile(xpathExpres).evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < nodes.getLength(); i++) {
            allOrganizerNodes.add(nodes.item(i));           
        }

        hl7organizer = new ArrayList<HL7Organizer>(allOrganizerNodes.size());


        for(Node y : allOrganizerNodes){
            HL7Organizer hl7org = new HL7Organizer(y,xpath);
            hl7organizer.add(hl7org);
        }
    }

    public ArrayList<HL7Organizer> getHL7Organizer(){
          return hl7organizer;
    }

}
