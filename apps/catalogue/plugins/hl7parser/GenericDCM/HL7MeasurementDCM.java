/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.hl7parser.GenericDCM;


import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author roankanninga
 */
public class HL7MeasurementDCM {

	private Node measurement;
    private XPath xpath;
    private String measurementName;
    private String measurementDescription;
    private String measurementDataType;
    private static final String OBSERVATION_NAME = "comment()";
    private static final String OBSERVATION_ONTOLOGY = "urn:hl7-org:v3:observation/code";
    //private static final String OBSERVATION_DATATYPE = "urn:hl7-org:v3:value/@*[local-name()='type']";
    private static final String OBSERVATION_DATATYPE = "urn:hl7-org:v3:observation/urn:hl7-org:v3:value";

   public HL7MeasurementDCM (Node measurement, XPath xpath) throws Exception{
       this.measurement = measurement;
       this.xpath = xpath;
       readMeasurementName();
       //readMeasurementDescription();
       readMeasurementDataType();
   }

    public void readMeasurementName() throws Exception{
     
       Node nameNode = (Node) xpath.evaluate(OBSERVATION_NAME, measurement, XPathConstants.NODE);
       
       this.measurementName = nameNode.getNodeValue();
   }

//    private void readMeasurementOntology()throws Exception{
//       try{
//       Node nameNode = (Node) xpath.evaluate(OBSERVATION_ONTOLOGY, measurement, XPathConstants.NODE);
//
//       this.measurementDescription = nameNode.getNodeValue();
//        }catch (Exception e){
//            this.measurementDescription = "NO DESCRIPTION";
//        }
//    }

    public void readMeasurementDataType()throws Exception{
        try{
            
       Node nameNode = (Node) xpath.evaluate(OBSERVATION_DATATYPE, measurement, XPathConstants.NODE);
       NamedNodeMap attr = nameNode.getAttributes();
       Node xsitype = attr.getNamedItem("xsi:type");
       this.measurementDataType = xsitype.getNodeValue();
       }catch (Exception e){
             this.measurementDataType = "NO DATATYPE";
        }
    }


    public String getMeasurementName() {
       return measurementName;
   }

//    public String getMeasurementDescription() {
//       return measurementDescription;
//   }

    public String getMeasurementDataType(){
        return measurementDataType;
    }



}
