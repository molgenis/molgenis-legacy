/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Molgenis;

import java.util.ArrayList;
import javax.xml.xpath.XPathExpressionException;

/**
 *
 * @author roankanninga
 * based on 3 files from LifeLines
 */
public class HL7LLData implements HL7Data{

    private HL7stagecatalog hl7stagecatalog;
    
    public HL7LLData(String file1,String file2,String file3) throws Exception{

        hl7stagecatalog = new HL7stagecatalog(file1);
    }


    public ArrayList<HL7Organizer> getHL7Organizer(){
        
        return hl7stagecatalog.getHL7Organizer();
    }

}
