/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.hl7parser;

import java.util.ArrayList;
import java.util.HashMap;

import plugins.hl7parser.GenericDCM.HL7GenericDCM;
import plugins.hl7parser.GenericDCM.HL7OrganizerDCM;
import plugins.hl7parser.GenericDCM.HL7ValueSetDCM;
import plugins.hl7parser.StageLRA.HL7OrganizerLRA;
import plugins.hl7parser.StageLRA.HL7StageLRA;
import plugins.hl7parser.StageLRA.HL7ValueSetLRA;

/**
 *
 * @author roankanninga
 */
interface HL7Data {

    //
    ArrayList<HL7OrganizerLRA> getHL7OrganizerLRA();
    ArrayList<HL7OrganizerDCM> getHL7OrganizerDCM();
    HashMap<String, HL7ValueSetLRA> getHashValueSetLRA();
    HashMap<String, HL7ValueSetDCM> getHashValueSetDCM();
    HL7GenericDCM getHl7GenericDCM();
    HL7StageLRA getHl7StageLRA();
    
}
