/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.hl7parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.core.Ontology;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;

import plugins.hl7parser.GenericDCM.HL7ObservationDCM;
import plugins.hl7parser.GenericDCM.HL7OrganizerDCM;
import plugins.hl7parser.StageLRA.HL7ObservationLRA;
import plugins.hl7parser.StageLRA.HL7OrganizerLRA;
import plugins.hl7parser.StageLRA.HL7ValueSetAnswerLRA;
import plugins.hl7parser.StageLRA.HL7ValueSetLRA;
import app.DatabaseFactory;

/**
 *
 * @author roankanninga
 */
public class HL7Main {
	public static void main(String [] args) throws Exception{ 

		//		String file1 = "/Users/pc_iverson/Desktop/input/Catalog-EX03.xml";
		String file1 = "/Users/roankanninga/Work/IBDParelsnoer/HL7/Catalog-EX04.xml";
		//		String file2 = "/Users/pc_iverson/Desktop/input/Catalog-EX03-valuesets.xml";
		String file2 = "/Users/roankanninga/Work/IBDParelsnoer/HL7/Catalog-EX04-valuesets.xml";

		//Read file, fill arraylists

		HL7Data ll = new HL7LLData(file1,file2);

		Database db = DatabaseFactory.create();

		HL7PhenoImporter importer = new HL7PhenoImporter();
		
		importer.start(ll, db);
	}

}
