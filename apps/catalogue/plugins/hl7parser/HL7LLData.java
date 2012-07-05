/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.hl7parser;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;


import javax.xml.xpath.XPath;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import plugins.hl7parser.GenericDCM.HL7GenericDCM;
import plugins.hl7parser.GenericDCM.HL7OrganizerDCM;
import plugins.hl7parser.StageLRA.HL7OrganizerLRA;
import plugins.hl7parser.StageLRA.HL7StageLRA;

/**
 *
 * @author roankanninga
 * based on 3 files from LifeLines
 */
public class HL7LLData implements HL7Data{

	
	private static final String ORGANIZER = "/urn:hl7-org:v3:genericCatalog/urn:hl7-org:v3:component/urn:hl7-org:v3:organizer/urn:hl7-org:v3:code";
    XPath xpath;
    public HL7GenericDCM hl7GenericDCM;
    public HL7StageLRA hl7StageLRA;
    
    public HL7LLData(String file1,String file2,String file3, int j) throws Exception{
    	String xpathExpres = ORGANIZER;
        ArrayList<Node> allOrganizerNodes = new ArrayList<Node>();
        XPathFactory factory = XPathFactory.newInstance();
        this.xpath = factory.newXPath();

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);

        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse(file1);
        NodeList nodes = (NodeList)xpath.compile(xpathExpres).evaluate(doc, XPathConstants.NODESET);

//        NodeList nodesCode = (NodeList)xpath.compile(xpathExpres+"").evaluate(doc, XPathConstants.NODESET);

        //genericDCM 1 en 3
        //stageLRA 2 en 3
        for (int i = 0; i < nodes.getLength(); i++) {
            if(nodes.item(i).getAttributes().getNamedItem("code").getNodeValue().equals("GenericDCM") && j!=2){
                hl7GenericDCM = new HL7GenericDCM (nodes.item(i).getParentNode(),xpath);
                
            }
            else if(nodes.item(i).getAttributes().getNamedItem("code").getNodeValue().equals("StageLRA")&& j!=1){
            	
            	hl7StageLRA = new HL7StageLRA (nodes.item(i).getParentNode(),xpath);
            }
            else{
                System.out.println("Error");
            }
            allOrganizerNodes.add(nodes.item(i));
        }
    }


    public ArrayList<HL7OrganizerLRA> getHL7OrganizerLRA(){
        
        return hl7StageLRA.getHL7OrganizerLRA();
    }
    public ArrayList<HL7OrganizerDCM> getHL7OrganizerDCM(){
        
        return hl7GenericDCM.getHL7OrganizerDCM();
    }

}
