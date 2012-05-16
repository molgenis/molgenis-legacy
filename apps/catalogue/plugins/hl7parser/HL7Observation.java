/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.hl7parser;


import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Node;

/**
 *
 * @author roankanninga
 */
public class HL7Observation {

     private Node measurement;
     private XPath xpath;
     private String measurementName;
     private String measurementDescription;
     private String measurementDataType;
     private static final String OBSERVATION_NAME = "urn:hl7-org:v3:code/@code";
     private static final String OBSERVATION_DESCRIPTION = "urn:hl7-org:v3:code/urn:hl7-org:v3:originalText/text()";
     private static final String OBSERVATION_DATATYPE = "urn:hl7-org:v3:value/@*[local-name()='type']";


    public HL7Observation (Node measurement, XPath xpath) throws Exception{
        this.measurement = measurement;
        this.xpath = xpath;
        readMeasurementName();
        readMeasurementDescription();
        readMeasurementDataType();
    }

     private void readMeasurementName() throws Exception{
      
        Node nameNode = (Node) xpath.evaluate(OBSERVATION_NAME, measurement, XPathConstants.NODE);
        
        this.measurementName = nameNode.getNodeValue();
    }

     private void readMeasurementDescription()throws Exception{
        try{
        Node nameNode = (Node) xpath.evaluate(OBSERVATION_DESCRIPTION, measurement, XPathConstants.NODE);
        
        this.measurementDescription = nameNode.getNodeValue();
         }catch (Exception e){
             this.measurementDescription = "NO DESCRIPTION";
         }
     }

     private void readMeasurementDataType()throws Exception{
         try{ 
         Node nameNode = (Node) xpath.evaluate(OBSERVATION_DATATYPE, measurement, XPathConstants.NODE);
          
        this.measurementDataType = nameNode.getNodeValue();
        }catch (Exception e){
              this.measurementDataType = "NO DATATYPE";
         }
     }


     public String getMeasurementName() {
        return measurementName;
    }

     public String getMeasurementDescription() {
        return measurementDescription;
    }

     public String getMeasurementDataType(){
         return measurementDataType;
     }



}
