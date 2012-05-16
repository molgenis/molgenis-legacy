/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Molgenis;

import java.io.File;
import javax.xml.xpath.XPathExpressionException;

/**
 *
 * @author roankanninga
 */
public class HL7Main {
    public static void main (String [] args) throws Exception{


        String file1 = "/Users/roankanninga/Work/IBDParelsnoer/HL7/StageCatalog.xml";
        String file2 = "/Users/roankanninga/Work/IBDParelsnoer/HL7/valuessets.xml";
        String file3 = "/Users/roankanninga/Work/IBDParelsnoer/HL7/StageCatalog.xml";

        HL7Data ll = new HL7LLData(file1,file2,file3);


        System.out.println("--------");

        for(HL7Organizer organizer : ll.getHL7Organizer()){
            System.out.println(organizer.getOrganizerName());
            for(Measurement meas :organizer.measurements){
                 System.out.println(" - " + meas.getMeasurementName() + "\t" + meas.getMeasurementDescription() +"\t"+meas.getMeasurementDataType() );
            }
        }
        
    }

}
