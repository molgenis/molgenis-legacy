/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.hl7parser;

import java.util.ArrayList;

import plugins.hl7parser.GenericDCM.HL7OrganizerDCM;
import plugins.hl7parser.StageLRA.HL7OrganizerLRA;

/**
 *
 * @author roankanninga
 */
interface HL7Data {

    //
    ArrayList<HL7OrganizerLRA> getHL7OrganizerLRA();
    ArrayList<HL7OrganizerDCM> getHL7OrganizerDCM();
}
