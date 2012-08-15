package org.molgenis.lifelinesresearchportal.plugins.hl7parser;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author roankanninga
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import app.DatabaseFactory;

public class XMLReader {

	Node node = null;
	Database db = null;
	Investigation inv = null;

	public static void main(String args[]) {

		XMLReader test = new XMLReader();
		try {
			test.run();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Node nodeFunction(String xmlCode, String molgenisCode) {
		if (node.getNodeName().equals(xmlCode)) {
			if (!xmlCode.equals("")) {
				System.out.println(molgenisCode
						+ ""
						+ node.getAttributes().getNamedItem(xmlCode)
								.getNodeValue());
			}
			return node;
		}
		return node;
	}

	public void run() throws DatabaseException {

		try {

			// Get a fresh new database object
			this.db = DatabaseFactory.create();

			// Begin transaction if anything goes wrong, can always roll back
			// and the database won`t be screwed up
			db.beginTx();

			// Create a investigation for this test
			this.inv = new Investigation();

			inv.setName("HL7Test");

			if (db.find(
					Investigation.class,
					new QueryRule(Investigation.NAME, Operator.EQUALS,
							"HL7Test")).size() == 0) {
				db.add(inv);
			}

			// Load the XML file in the memory
			String path = "/Users/pc_iverson/Desktop/Input/";
			File file = new File(path + "StageCatalog.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
			Document doc = documentBuilder.parse(file);
			doc.getDocumentElement().normalize();

			// Loop through the "organizer" to collect information on the
			// protocolName and
			// MeasurementName and its fields!

			NodeList nodeLst = doc.getElementsByTagName("organizer");
			for (int i = 0; i < nodeLst.getLength(); i++) {

				// Get each "organizer" entity
				Node eachNode = nodeLst.item(i);

				// Probably the List size is 1 cos there is only one code entity
				// in the direct child of the organizer entity
				List<String> protocolNameNode = getAttributeFromEntity(
						eachNode, "code", "code", 1);

				List<Node> measurementNode = getChildNodes(eachNode,
						"observation", 2);

				transformToMeasurement(protocolNameNode.get(0), measurementNode);

			}

			// commit all the changes in the database
			db.commitTx();

		} catch (Exception e) {

			// if anything goes wrong, roll back to the previous state
			db.rollbackTx();
			e.printStackTrace();
		}

	}

	/**
	 * This method is to create the Protocol and its corresponding Measurements
	 * and add them to the db.
	 * 
	 * @param protocolName
	 * @param measurementNode
	 * @throws DatabaseException
	 */
	private void transformToMeasurement(String protocolName,
			List<Node> measurementNode) throws DatabaseException {

		List<String> listOfMeasurementName = new ArrayList<String>();

		Protocol p = new Protocol();

		p.setName(protocolName);

		p.setInvestigation(inv);

		List<Measurement> listOfMeasurements = new ArrayList<Measurement>();

		for (Node eachNode : measurementNode) {

			List<String> nameOfMeasurement = getAttributeFromEntity(eachNode,
					"code", "code", 1);

			List<String> description = getAttributeFromEntity(eachNode,
					"originalText", "", 2);

			List<String> dataType = getAttributeFromEntity(eachNode, "value",
					"xsi:type", 1);

			Measurement m = new Measurement();

			m.setName(nameOfMeasurement.get(0));

			m.setDescription(description.get(0));

			if (dataType.get(0).equals("INT")) {
				m.setDataType("int");
			} else if (dataType.get(0).equals("ST")) {
				m.setDataType("string");
			} else if (dataType.get(0).equals("TS")) {
				m.setDataType("datetime");
			}

			System.out.println(dataType.get(0));

			m.setInvestigation(inv);

			listOfMeasurements.add(m);

			listOfMeasurementName.add(m.getName());

		}

		db.update(listOfMeasurements, DatabaseAction.ADD_IGNORE_EXISTING,
				Measurement.NAME);

		listOfMeasurements = db.find(Measurement.class, new QueryRule(
				Measurement.NAME, Operator.IN, listOfMeasurementName));

		List<Integer> listOfMeasurementId = new ArrayList<Integer>();

		for (Measurement m : listOfMeasurements) {
			listOfMeasurementId.add(m.getId());
		}

		p.setFeatures_Id(listOfMeasurementId);

		if (db.find(Protocol.class,
				new QueryRule(Protocol.NAME, Operator.EQUALS, protocolName))
				.size() == 0) {

			db.add(p);

		} else {
			db.update(p);
		}
	}

	/**
	 * This method is to get the specified entities inside the current node. We
	 * can specify how many levels down we want to go to.
	 * 
	 * @param currentEntity
	 * @param subNodeName
	 * @param level
	 * @return
	 */
	public List<Node> getChildNodes(Node currentEntity, String subNodeName,
			int level) {

		List<Node> listOfSubNodes = new ArrayList<Node>();

		for (int x = 0; x < currentEntity.getChildNodes().getLength(); x++) {

			Node subNode = currentEntity.getChildNodes().item(x);

			if (level == 1) {

				if (subNode.getNodeName().equals(subNodeName)) {

					listOfSubNodes.add(subNode);
					// System.out.println(subNode.getAttributes().getNamedItem(attributeName).getNodeValue());
					// System.out.println(subNode.getTextContent());
				}

			} else {
				int nextLevel = level - 1;
				List<Node> temp = getChildNodes(subNode, subNodeName, nextLevel);
				listOfSubNodes.addAll(temp);
			}
		}

		return listOfSubNodes;
	}

	/**
	 * This method is to get the specified attribute from the specified
	 * entities. We can specify how many levels down we want to go to.
	 * 
	 * @param currentEntity
	 * @param subNodeName
	 * @param attributeName
	 * @param level
	 * @return
	 */
	public List<String> getAttributeFromEntity(Node currentEntity,
			String subNodeName, String attributeName, int level) {

		List<String> listOfSubNodes = new ArrayList<String>();

		for (int x = 0; x < currentEntity.getChildNodes().getLength(); x++) {

			Node subNode = currentEntity.getChildNodes().item(x);

			if (level == 1) {

				if (subNode.getNodeType() == Node.ELEMENT_NODE) {

					Element element = (Element) subNode;

					if (element.getNodeName().equals(subNodeName)) {

						if (attributeName.equals("")) {

							listOfSubNodes.add(element.getTextContent());

						} else if (element.hasAttribute(attributeName)) {

							listOfSubNodes.add(element
									.getAttribute(attributeName));

							// System.out.println("The attribute is " +
							// element.getAttribute(attributeName));

						}
					}
				}

			} else {
				int nextLevel = level - 1;
				List<String> temp = getAttributeFromEntity(subNode,
						subNodeName, attributeName, nextLevel);
				listOfSubNodes.addAll(temp);
			}
		}

		return listOfSubNodes;
	}
}

//
// for (int s = 0; s < nodeLst.getLength(); s++) {
//
// Node fstNode = nodeLst.item(s);
// if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
//
// for(int x = 0; x< fstNode.getChildNodes().getLength();x++){
//
//
//
//
// node = fstNode.getChildNodes().item(x);
//
// if(node.getNodeName().equals("code")){
// System.out.println("Protocols: "
// +node.getAttributes().getNamedItem("code").getNodeValue());
// }
//
//
// else if(node.getNodeName().equals("component")){
//
// for(int y = 0; y< node.getChildNodes().getLength();y++){
// node = node.getChildNodes().item(y);
//
// if(node.getNodeName().equals("observation")){
//
// for(int z = 0; z< node.getChildNodes().getLength();z++){
// node = node.getChildNodes().item(z);
// if(node.getNodeName().equals("code")){
// System.out.println("Measurements: "
// +node.getAttributes().getNamedItem("code").getNodeValue());
//
// for(int za = 0; za< node.getChildNodes().getLength();za++){
// node = node.getChildNodes().item(za);
// if(node.getNodeName().equals("originalText")){
// System.out.println("Text: " +node.getTextContent());
// }
// }
//
// }
// if(node.getNodeName().equals("value")){
// System.out.println("Datatype: "
// +node.getAttributes().getNamedItem("xsi:type").getNodeValue());
// }
// }
//
// }
// }
// }
// else{
// // System.out.println("This is wrong");
// }
//
// }
//
//
// }