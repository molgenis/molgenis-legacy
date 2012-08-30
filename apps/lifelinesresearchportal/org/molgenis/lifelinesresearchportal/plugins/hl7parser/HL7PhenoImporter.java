package org.molgenis.lifelinesresearchportal.plugins.hl7parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.core.Ontology;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.lifelinesresearchportal.plugins.catalogue.StageLRA.HL7ObservationLRA;
import org.molgenis.lifelinesresearchportal.plugins.catalogue.StageLRA.HL7OrganizerLRA;
import org.molgenis.lifelinesresearchportal.plugins.catalogue.StageLRA.HL7ValueSetAnswerLRA;
import org.molgenis.lifelinesresearchportal.plugins.catalogue.StageLRA.HL7ValueSetLRA;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;

public class HL7PhenoImporter
{

	public void start(HL7Data ll, Database db) throws Exception
	{
		// TODO Auto-generated method stub

		try
		{

			db.beginTx();

			String investigationName = "LifeLines";

			Investigation inv = null;

			if (db.find(Investigation.class, new QueryRule(Investigation.NAME, Operator.EQUALS, investigationName))
					.size() == 0)
			{

				inv = new Investigation();

				inv.setName(investigationName);

				db.add(inv);

			}
			else
			{
				inv = db.find(Investigation.class,
						new QueryRule(Investigation.NAME, Operator.EQUALS, investigationName)).get(0);
			}

			Protocol stageCatalogue;

			if (db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, "stageCatalogue")).size() == 0)
			{
				stageCatalogue = new Protocol();
				stageCatalogue.setName("stageCatalogue");

				stageCatalogue.setInvestigation_Name(investigationName);

				db.add(stageCatalogue);
			}
			else
			{
				stageCatalogue = db.find(Protocol.class,
						new QueryRule(Protocol.NAME, Operator.EQUALS, "stageCatalogue")).get(0);
			}

			HashMap<String, HL7ValueSetLRA> hashValueSetLRA = ll.getHashValueSetLRA();

			List<Integer> listOfProtocolIds = new ArrayList<Integer>();

			List<String> uniqueListOfMeasurementNames = new ArrayList<String>();
			List<Measurement> uniqueListOfMeasurements = new ArrayList<Measurement>();
			List<Category> uniqueListOfCategory = new ArrayList<Category>();
			List<String> uniqueCategoryName = new ArrayList<String>();
			List<Protocol> uniqueProtocol = new ArrayList<Protocol>();
			List<String> uniqueListOfProtocolName = new ArrayList<String>();

			for (HL7OrganizerLRA organizer : ll.getHL7OrganizerLRA())
			{

				System.out.println(organizer.getHL7OrganizerNameLRA());
				String protocolName = organizer.getHL7OrganizerNameLRA().trim();
				Protocol protocol = new Protocol();
				protocol.setName(protocolName);
				protocol.setInvestigation(inv);

				List<String> protocolFeature = new ArrayList<String>();

				for (HL7ObservationLRA meas : organizer.measurements)
				{

					List<String> measurementCategory = new ArrayList<String>();

					if (db.find(Measurement.class,
							new QueryRule(Measurement.NAME, Operator.EQUALS, meas.getMeasurementName())).size() > 0)
					{
						Measurement m = db.find(Measurement.class,
								new QueryRule(Measurement.NAME, Operator.EQUALS, meas.getMeasurementName())).get(0);

						m.setDescription(meas.getMeasurementLabel());
						m.setInvestigation(inv);

						protocolFeature.add(m.getName());

						if (hashValueSetLRA.containsKey(protocolName + "." + meas.getMeasurementName().trim()))
						{

							HL7ValueSetLRA valueSetLRA = hashValueSetLRA.get(protocolName + "."
									+ meas.getMeasurementName());

							for (HL7ValueSetAnswerLRA eachAnswer : valueSetLRA.getListOFAnswers())
							{

								String codeValue = eachAnswer.getCodeValue();
								String categoryName = eachAnswer.getName().trim().toLowerCase();

								if (!uniqueCategoryName.contains(categoryName))
								{
									uniqueCategoryName.add(categoryName);
									Category c = new Category();
									String indentifier = codeValue + categoryName.replaceAll("[^(a-zA-Z0-9)]", "");
									c.setName(indentifier.trim().toLowerCase());
									c.setCode_String(codeValue);
									c.setDescription(categoryName);
									c.setInvestigation(inv);
									uniqueListOfCategory.add(c);
								}
								String indentifier = codeValue + categoryName.replaceAll("[^(a-zA-Z0-9)]", "");
								measurementCategory.add(indentifier.trim().toLowerCase());
							}
						}
						m.setCategories_Name(measurementCategory);

						String dataType = meas.getMeasurementDataType();

						if (dataType.equals("INT"))
						{
							m.setDataType("int");
						}
						else if (dataType.equals("ST"))
						{
							m.setDataType("string");
						}
						else if (dataType.equals("CO"))
						{
							m.setDataType("categorical");
						}
						else if (dataType.equals("CD"))
						{
							m.setDataType("code");
						}
						else if (dataType.equals("PQ"))
						{
							m.setDataType("decimal");
						}
						else if (dataType.equals("TS"))
						{
							m.setDataType("datetime");
						}
						else if (dataType.equals("REAL"))
						{
							m.setDataType("decimal");
						}
						else if (dataType.equals("BL"))
						{
							m.setDataType("bool");
						}

						if (!uniqueListOfMeasurementNames.contains(m.getName()))
						{
							uniqueListOfMeasurementNames.add(m.getName());
							uniqueListOfMeasurements.add(m);
						}
					}
					protocol.setFeatures_Name(protocolFeature);
					if (protocol.getFeatures_Name().size() > 0)
					{
						uniqueProtocol.add(protocol);
						uniqueListOfProtocolName.add(protocolName);
					}
				}
			}

			for (Category c : uniqueListOfCategory)
			{
				System.out.println("-------------." + c.getName());
			}

			db.update(uniqueListOfCategory, Database.DatabaseAction.ADD_IGNORE_EXISTING, Category.NAME);

			for (Measurement m : uniqueListOfMeasurements)
			{

				if (m.getCategories_Name().size() > 0)
				{

					List<Category> listOfCategory = db.find(Category.class,
							new QueryRule(Category.NAME, Operator.IN, m.getCategories_Name()));

					List<Integer> listOfCategoryID = new ArrayList<Integer>();

					for (Category c : listOfCategory)
					{
						listOfCategoryID.add(c.getId());
					}
					m.setCategories_Id(listOfCategoryID);
				}
			}
			db.update(uniqueListOfMeasurements);

			for (Protocol p : uniqueProtocol)
			{

				if (p.getFeatures_Name().size() > 0)
				{

					List<Measurement> listOfMeasurement = db.find(Measurement.class, new QueryRule(Measurement.NAME,
							Operator.IN, p.getFeatures_Name()));

					List<Integer> listOfMeasurementID = new ArrayList<Integer>();

					for (Measurement m : listOfMeasurement)
					{
						listOfMeasurementID.add(m.getId());
					}
					p.setFeatures_Id(listOfMeasurementID);
				}
			}

			db.update(uniqueProtocol, Database.DatabaseAction.ADD_IGNORE_EXISTING, Protocol.NAME);

			uniqueProtocol = db.find(Protocol.class,
					new QueryRule(Protocol.NAME, Operator.IN, uniqueListOfProtocolName));

			for (Protocol p : uniqueProtocol)
			{
				listOfProtocolIds.add(p.getId());
			}

			Protocol otherProtocol = new Protocol();
			otherProtocol.setName("NotClassified");
			otherProtocol.setInvestigation_Name(investigationName);
			List<Integer> listOfFeaturesID = new ArrayList<Integer>();
			for (Measurement m : db.find(Measurement.class, new QueryRule(Measurement.INVESTIGATION_NAME,
					Operator.EQUALS, investigationName)))
			{

				if (!uniqueListOfMeasurementNames.contains(m.getName()))
				{
					listOfFeaturesID.add(m.getId());
				}

			}
			if (listOfFeaturesID.size() > 0)
			{
				otherProtocol.setFeatures_Id(listOfFeaturesID);

				db.add(otherProtocol);
				listOfProtocolIds.add(otherProtocol.getId());
			}

			stageCatalogue.setSubprotocols_Id(listOfProtocolIds);

			db.update(stageCatalogue);

			// ##################################################################
			// GENERICDCM

			// Protocol genericDCM;
			//
			// if (db.find(Protocol.class,
			// new QueryRule(Protocol.NAME, Operator.EQUALS, "generic"))
			// .size() == 0) {
			// genericDCM = new Protocol();
			// genericDCM.setName("generic");
			//
			// genericDCM.setInvestigation_Name(investigationName);
			//
			// db.add(genericDCM);
			// } else {
			// genericDCM = db
			// .find(Protocol.class,
			// new QueryRule(Protocol.NAME, Operator.EQUALS,
			// "generic")).get(0);
			// }
			//
			// uniqueListOfMeasurementNames.clear();
			// uniqueListOfMeasurements.clear();
			// uniqueListOfCategory.clear();
			// uniqueCategoryName = new ArrayList<String>();
			//
			// uniqueProtocol = new ArrayList<Protocol>();
			//
			// uniqueListOfProtocolName = new ArrayList<String>();
			//
			// listOfProtocolIds = new ArrayList<Integer>();
			//
			// HashMap<String, HL7ValueSetDCM> hashValueSetDCM = ll
			// .getHashValueSetDCM();
			//
			// if (ll.getHl7GenericDCM() != null) {
			//
			// for (HL7OrganizerDCM organizer : ll.getHL7OrganizerDCM()) {
			//
			// System.out.println(organizer.getHL7OrganizerNameDCM());
			//
			// Protocol protocol = new Protocol();
			// protocol.setName(organizer.getHL7OrganizerNameDCM().trim());
			// protocol.setInvestigation(inv);
			//
			// List<String> protocolFeature = new ArrayList<String>();
			//
			// List<Integer> listOfOntologyTermForProtocolIDs =
			// addingOntologyTerm(
			// organizer.getHl7OntologyTerms(), db);
			//
			// protocol.setOntologyReference_Id(listOfOntologyTermForProtocolIDs);
			//
			// for (HL7ObservationDCM meas : organizer.measurements) {
			//
			// System.out.println(" - " + meas.getDisplayName() + "\t"
			// + meas.getOriginalText());
			// Measurement m = new Measurement();
			// m.setName(meas.getDisplayName());
			// // m.setDescription(meas.getMeasurementDescription());
			// m.setInvestigation(inv);
			//
			// List<String> measurementCategory = new ArrayList<String>();
			//
			// String dataType = meas.getValue();
			//
			// if (dataType.equals("INT")) {
			// m.setDataType("int");
			// } else if (dataType.equals("ST")) {
			// m.setDataType("string");
			// } else if (dataType.equals("CO")) {
			// m.setDataType("categorical");
			// } else if (dataType.equals("CD")) {
			// m.setDataType("code");
			// } else if (dataType.equals("PQ")) {
			// m.setDataType("decimal");
			// } else if (dataType.equals("TS")) {
			// m.setDataType("datetime");
			// } else if (dataType.equals("REAL")) {
			// m.setDataType("decimal");
			// } else if (dataType.equals("BL")) {
			// m.setDataType("bool");
			// }
			//
			// List<Integer> listOfOntologyTermIDs = addingOntologyTerm(
			// meas.getHl7OntologyTerms(), db);
			//
			// m.setOntologyReference_Id(listOfOntologyTermIDs);
			//
			// if (!uniqueListOfMeasurementNames.contains(m.getName())) {
			// uniqueListOfMeasurementNames.add(m.getName());
			// uniqueListOfMeasurements.add(m);
			// }
			//
			// if (hashValueSetDCM.containsKey(meas.getDisplayName())) {
			//
			// HL7ValueSetDCM valueSet = hashValueSetDCM.get(meas
			// .getDisplayName());
			//
			// for (HL7ValueSetAnswerDCM eachAnswer : valueSet
			// .getListOFAnswers()) {
			//
			// String categoryName = eachAnswer.getName();
			// HL7OntologyTerm ont = eachAnswer.getOnt();
			// if (!uniqueCategoryName.contains(categoryName)) {
			// Category c = new Category();
			// c.setName(categoryName);
			// c.setCode_String("");
			// c.setDescription(categoryName);
			// c.setInvestigation(inv);
			// ArrayList<HL7OntologyTerm> list = new
			// ArrayList<HL7OntologyTerm>();
			// list.add(ont);
			// List<Integer> ontologyTermIDsFoCategory = addingOntologyTerm(
			// list, db);
			// c.setOntologyReference_Id(ontologyTermIDsFoCategory);
			// uniqueListOfCategory.add(c);
			// }
			// measurementCategory.add(categoryName);
			// }
			// }
			//
			// m.setCategories_Name(measurementCategory);
			//
			// protocolFeature.add(m.getName());
			// }
			// protocol.setFeatures_Name(protocolFeature);
			//
			// if (!uniqueListOfProtocolName.contains(protocol.getName())) {
			// uniqueListOfProtocolName.add(protocol.getName());
			// uniqueProtocol.add(protocol);
			// }
			// }
			//
			// db.update(uniqueListOfCategory,
			// Database.DatabaseAction.ADD_IGNORE_EXISTING,
			// Category.NAME);
			//
			// for (Measurement m : uniqueListOfMeasurements) {
			//
			// if (m.getCategories_Name().size() > 0) {
			// List<Category> categories = db.find(
			// Category.class,
			// new QueryRule(Category.NAME, Operator.IN, m
			// .getCategories_Name()));
			// List<Integer> categoryIDs = new ArrayList<Integer>();
			// for (Category c : categories) {
			// categoryIDs.add(c.getId());
			// }
			// m.setCategories_Id(categoryIDs);
			// }
			// }
			//
			// db.update(uniqueListOfMeasurements,
			// Database.DatabaseAction.ADD_IGNORE_EXISTING,
			// Measurement.NAME);
			//
			// for (Protocol p : uniqueProtocol) {
			//
			// List<String> listOfFeatures = p.getFeatures_Name();
			//
			// List<Measurement> listOfMeasurements = db.find(
			// Measurement.class, new QueryRule(Measurement.NAME,
			// Operator.IN, listOfFeatures));
			//
			// List<Integer> listOfFeaturesID = new ArrayList<Integer>();
			//
			// for (Measurement m : listOfMeasurements) {
			// listOfFeaturesID.add(m.getId());
			// }
			//
			// p.setFeatures_Id(listOfFeaturesID);
			// }
			//
			// db.update(uniqueProtocol,
			// Database.DatabaseAction.ADD_IGNORE_EXISTING,
			// Protocol.NAME);
			//
			// uniqueProtocol = db.find(Protocol.class, new QueryRule(
			// Protocol.NAME, Operator.IN, uniqueListOfProtocolName));
			//
			// for (Protocol p : uniqueProtocol) {
			// listOfProtocolIds.add(p.getId());
			// }
			//
			// genericDCM.setSubprotocols_Id(listOfProtocolIds);
			//
			// db.update(genericDCM);
			//
			// }

			db.commitTx();

		}
		catch (Exception e)
		{
			e.printStackTrace();
			db.rollbackTx();
		}
	}

	public List<Integer> addingOntologyTerm(List<HL7OntologyTerm> listOfHL7OntologyTerms, Database db) throws Exception
	{

		List<Integer> listOfOntologyTermIDs = new ArrayList<Integer>();

		for (HL7OntologyTerm t : listOfHL7OntologyTerms)
		{

			String codeSystemName = t.getCodeSystemName();

			if (t.getCodeSystemName().toLowerCase().startsWith("snomed")
					|| t.getCodeSystemName().equalsIgnoreCase("sct"))
			{
				codeSystemName = "SCT";
			}

			Ontology ot = new Ontology();

			if (db.find(Ontology.class, new QueryRule(Ontology.ONTOLOGYACCESSION, Operator.EQUALS, t.getCodeSystem()))
					.size() == 0)
			{

				ot.setName(codeSystemName);
				ot.setOntologyAccession(t.getCodeSystem());
				db.add(ot);

			}
			else
			{
				ot = db.find(Ontology.class,
						new QueryRule(Ontology.ONTOLOGYACCESSION, Operator.EQUALS, t.getCodeSystem())).get(0);
			}

			Query<OntologyTerm> q = db.query(OntologyTerm.class);
			q.addRules(new QueryRule(OntologyTerm.TERMACCESSION, Operator.EQUALS, t.getCode()));
			q.addRules(new QueryRule(OntologyTerm.ONTOLOGY_NAME, Operator.EQUALS, codeSystemName));

			OntologyTerm ont = new OntologyTerm();

			if (q.find().size() == 0)
			{

				ont.setOntology_Id(ot.getId());
				ont.setName(t.getDisplayName());
				ont.setTermAccession(t.getCode());
				db.add(ont);

			}
			else
			{
				ont = q.find().get(0);
			}

			listOfOntologyTermIDs.add(ont.getId());

			System.out.println("The mapped ontology term is " + t.getDisplayName() + "\t" + t.getCode());
		}

		return listOfOntologyTermIDs;
	}

}
