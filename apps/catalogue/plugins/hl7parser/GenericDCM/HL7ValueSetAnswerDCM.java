package plugins.hl7parser.GenericDCM;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Node;

import plugins.hl7parser.HL7OntologyTerm;



public class HL7ValueSetAnswerDCM {
	
	private static final String DISPLAYNAME = "@displayName";
	private String name = "";
	private HL7OntologyTerm ont;

	public HL7ValueSetAnswerDCM(Node node, XPath xpath) throws Exception {
		
		Node nameNode = (Node) xpath.compile(DISPLAYNAME).evaluate(node, XPathConstants.NODE);
		name = nameNode.getNodeValue().trim();
		ont = new HL7OntologyTerm(node, xpath);
	}

	public String getName() {
		return name;
	}
	public HL7OntologyTerm getOnt() {
		return ont;
	}
}
