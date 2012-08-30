package org.molgenis.lifelinesresearchportal.plugins.hl7parser;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.HashMap;

import org.molgenis.lifelinesresearchportal.plugins.catalogue.GenericDCM.HL7GenericDCM;
import org.molgenis.lifelinesresearchportal.plugins.catalogue.GenericDCM.HL7OrganizerDCM;
import org.molgenis.lifelinesresearchportal.plugins.catalogue.GenericDCM.HL7ValueSetDCM;
import org.molgenis.lifelinesresearchportal.plugins.catalogue.StageLRA.HL7OrganizerLRA;
import org.molgenis.lifelinesresearchportal.plugins.catalogue.StageLRA.HL7StageLRA;
import org.molgenis.lifelinesresearchportal.plugins.catalogue.StageLRA.HL7ValueSetLRA;

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
