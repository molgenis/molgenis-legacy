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
		String file1 = "/Users/roankanninga/Work/IBDParelsnoer/HL7/GenericCatalog-EX02.xml";
		String file2 = "/Users/roankanninga/Work/IBDParelsnoer/HL7/org.hl7.BodyWeight-v0.108-Template.xml-valuesets.xml";
		String file3 = "/Users/roankanninga/Work/IBDParelsnoer/HL7/org.hl7.BodyWeight-v0.108-Template+context.xml";

		//1 = genericDCM
		//2 = stageLRA
		//3 = both genericDCM as stageLRA have been created
		int i = 3;

		//Read file, fill arraylists

		HL7Data ll = new HL7LLData(file1,file2,file3, i);


		System.out.println("--------");

		Database db = DatabaseFactory.create();

		try{

			db.beginTx();

			String investigationName = "HL7LL";

			Investigation inv = null;

			if(db.find(Investigation.class, new QueryRule(Investigation.NAME, Operator.EQUALS, investigationName)).size() == 0){

				inv = new Investigation();

				inv.setName(investigationName);

				db.add(inv);

			}else{
				inv = db.find(Investigation.class, new QueryRule(Investigation.NAME, Operator.EQUALS, investigationName)).get(0);
			}
			//////
			List<Protocol> listOfProtocols = new ArrayList<Protocol>();

			List<String> listOfProtocolName = new ArrayList<String>();

			List<Integer> listOfProtocolIds = new ArrayList<Integer>();

			Protocol stageCatalogue;

			if(db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, "stageCatalogue")).size()==0){
				stageCatalogue = new Protocol();
				stageCatalogue.setName("stageCatalogue");

				stageCatalogue.setInvestigation_Name(investigationName);

				db.add(stageCatalogue);
			}else{
				stageCatalogue = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, "stageCatalogue")).get(0);
			}


			for(HL7OrganizerLRA organizer : ll.getHL7OrganizerLRA()){

				System.out.println(organizer.getHL7OrganizerNameLRA());
				Protocol protocol = new Protocol();
				protocol.setName(organizer.getHL7OrganizerNameLRA().trim());
				protocol.setInvestigation(inv);
				List<String> listOfMeasurementNames = new ArrayList<String>();
				List<Measurement> listOfMeasurements = new ArrayList<Measurement>();

				for(HL7ObservationLRA meas :organizer.measurements){
					System.out.println(" - " + meas.getMeasurementName() + "\t" + meas.getMeasurementDescription() +"\t"+meas.getMeasurementDataType() );
					Measurement m = new Measurement();
					m.setName(meas.getMeasurementName());
					m.setDescription(meas.getMeasurementDescription().replaceAll("\\\n", ""));
					m.setInvestigation(inv);

					String dataType = meas.getMeasurementDataType();

					if(dataType.equals("INT")){
						m.setDataType("int");
					}else if(dataType.equals("ST")){
						m.setDataType("string");
					}else if(dataType.equals("TS")){
						m.setDataType("datetime");
					}else{
						m.setDataType("string");
					}

					listOfMeasurementNames.add(meas.getMeasurementName());	

					listOfMeasurements.add(m);

				}

				db.update(listOfMeasurements, Database.DatabaseAction.ADD_IGNORE_EXISTING, Measurement.NAME);

				listOfMeasurements = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, listOfMeasurementNames));

				List<Integer> listOfMeasurementId = new ArrayList<Integer>();

				for(Measurement m : listOfMeasurements){
					listOfMeasurementId.add(m.getId());
				}

				protocol.setFeatures_Id(listOfMeasurementId);

				listOfProtocols.add(protocol);

				listOfProtocolName.add(protocol.getName());
			}

			db.update(listOfProtocols, Database.DatabaseAction.ADD_IGNORE_EXISTING, Protocol.NAME);
			
			listOfProtocols = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.IN, listOfProtocolName));

			for(Protocol p : listOfProtocols){
				listOfProtocolIds.add(p.getId());
			}

			stageCatalogue.setSubprotocols_Id(listOfProtocolIds);

			db.update(stageCatalogue);

			/////
			//GENERICDCM

			Protocol genericDCM;

			if(db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, "genericDCM")).size()==0){
				genericDCM = new Protocol();
				genericDCM.setName("genericDCM");

				genericDCM.setInvestigation_Name(investigationName);

				db.add(genericDCM);
			}else{
				genericDCM = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, "genericDCM")).get(0);
			}


			listOfProtocols = new ArrayList<Protocol>();

			listOfProtocolName = new ArrayList<String>();

			listOfProtocolIds = new ArrayList<Integer>();

			for(HL7OrganizerDCM organizer : ll.getHL7OrganizerDCM()){

				System.out.println(organizer.getHL7OrganizerNameDCM());
				Protocol protocol = new Protocol();
				protocol.setName(organizer.getHL7OrganizerNameDCM().trim());
				protocol.setInvestigation(inv);
				List<String> listOfMeasurementNames = new ArrayList<String>();
				List<Measurement> listOfMeasurements = new ArrayList<Measurement>();

				for(HL7MeasurementDCM meas :organizer.measurements){
					System.out.println(" - " + meas.getMeasurementName() +"\t"+meas.getMeasurementDataType() );
					Measurement m = new Measurement();
					m.setName(meas.getMeasurementName());
					//m.setDescription(meas.getMeasurementDescription());
					m.setInvestigation(inv);

					String dataType = meas.getMeasurementDataType();

					if(dataType.equals("INT")){
						m.setDataType("int");
					}else if(dataType.equals("ST")){
						m.setDataType("string");
					}else if(dataType.equals("TS")){
						m.setDataType("datetime");
					}else{
						m.setDataType("string");
					}

					listOfMeasurementNames.add(meas.getMeasurementName());	

					listOfMeasurements.add(m);
				}

				db.update(listOfMeasurements, Database.DatabaseAction.ADD_IGNORE_EXISTING, Measurement.NAME);

				listOfMeasurements = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, listOfMeasurementNames));

				List<Integer> listOfMeasurementId = new ArrayList<Integer>();

				for(Measurement m : listOfMeasurements){
					listOfMeasurementId.add(m.getId());
				}

				protocol.setFeatures_Id(listOfMeasurementId);

				listOfProtocols.add(protocol);
				
				listOfProtocolName.add(protocol.getName());
			}

			db.update(listOfProtocols, Database.DatabaseAction.ADD_IGNORE_EXISTING, Protocol.NAME);

			listOfProtocols = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.IN, listOfProtocolName));

			for(Protocol p : listOfProtocols){
				listOfProtocolIds.add(p.getId());
			}

			db.update(listOfProtocols, Database.DatabaseAction.ADD_IGNORE_EXISTING, Protocol.NAME);

			genericDCM.setSubprotocols_Id(listOfProtocolIds);

			db.update(genericDCM);

			db.commitTx();

		}catch(Exception e){
			System.out.println(e.getMessage());
			db.rollbackTx();
		}
	}

}
