/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package plugins.hl7parser;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;

import plugins.hl7parser.GenericDCM.HL7MeasurementDCM;
import plugins.hl7parser.GenericDCM.HL7OrganizerDCM;
import plugins.hl7parser.StageLRA.HL7ObservationLRA;
import plugins.hl7parser.StageLRA.HL7OrganizerLRA;

import app.DatabaseFactory;

/**
 *
 * @author roankanninga
 */
public class HL7Main {
	public static void main(String [] args) throws Exception{ 


		//String file1 = "/Users/pc_iverson/Desktop/input/StageCatalog.xml";
		String file1 = "/Users/roankanninga/Work/IBDParelsnoer/HL7/StageCatalog.xml";
		String file2 = "/Users/roankanninga/Work/IBDParelsnoer/HL7/org.hl7.BodyWeight-v0.108-Template.xml-valuesets.xml";
		String file3 = "/Users/roankanninga/Work/IBDParelsnoer/HL7/org.hl7.BodyWeight-v0.108-Template+context.xml";

		HL7Data ll = new HL7LLData(file1,file2,file3);


		System.out.println("--------");
		
		for(HL7OrganizerDCM organizer : ll.getHL7OrganizerDCM()){
            System.out.println("This is the name of the protocol: " + organizer.getHL7OrganizerNameDCM());
            for(HL7MeasurementDCM m :organizer.measurements){
                System.out.println("\t"+m.getMeasurementName() + "\t"+ m.getMeasurementDataType());
            }
            
        }
		
//
//		Database db = DatabaseFactory.create();
//
//		try{
//
//			db.beginTx();
//
//			String investigationName = "HL7LL";
//
//			Investigation inv = null;
//
//			if(db.find(Investigation.class, new QueryRule(Investigation.NAME, Operator.EQUALS, investigationName)).size() == 0){
//
//				inv = new Investigation();
//
//				inv.setName(investigationName);
//
//				db.add(inv);
//
//			}else{
//				inv = db.find(Investigation.class, new QueryRule(Investigation.NAME, Operator.EQUALS, investigationName)).get(0);
//			}
//
//			List<Protocol> listOfProtocols = new ArrayList<Protocol>();
//
//			for(HL7OrganizerLRA organizer : ll.getHL7OrganizerLRA()){
//
//				System.out.println(organizer.getOrganizerName());
//				Protocol protocol = new Protocol();
//				protocol.setName(organizer.getOrganizerName());
//				protocol.setInvestigation(inv);
//				List<String> listOfMeasurementNames = new ArrayList<String>();
//				List<Measurement> listOfMeasurements = new ArrayList<Measurement>();
//
//				for(HL7ObservationLRA meas :organizer.measurements){
//					System.out.println(" - " + meas.getMeasurementName() + "\t" + meas.getMeasurementDescription() +"\t"+meas.getMeasurementDataType() );
//					Measurement m = new Measurement();
//					m.setName(meas.getMeasurementName());
//					m.setDescription(meas.getMeasurementDescription());
//					m.setInvestigation(inv);
//
//					String dataType = meas.getMeasurementDataType();
//
//					if(dataType.equals("INT")){
//						m.setDataType("int");
//					}else if(dataType.equals("ST")){
//						m.setDataType("string");
//					}else if(dataType.equals("TS")){
//						m.setDataType("datetime");
//					}
//
//					listOfMeasurementNames.add(meas.getMeasurementName());	
//					
//					listOfMeasurements.add(m);
//				}
//
//				db.update(listOfMeasurements, Database.DatabaseAction.ADD_IGNORE_EXISTING, Measurement.NAME);
//
//				listOfMeasurements = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, listOfMeasurementNames));
//
//				List<Integer> listOfMeasurementId = new ArrayList<Integer>();
//
//				for(Measurement m : listOfMeasurements){
//					listOfMeasurementId.add(m.getId());
//				}
//
//				protocol.setFeatures_Id(listOfMeasurementId);
//
//				listOfProtocols.add(protocol);
//			}
//
//			db.update(listOfProtocols, Database.DatabaseAction.ADD_IGNORE_EXISTING, Protocol.NAME);
//
//			db.commitTx();
//
//		}catch(Exception e){
//			db.rollbackTx();
//		}
	}

}
