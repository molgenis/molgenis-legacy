package plugins.fillanimaldb;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.auth.MolgenisGroup;
import org.molgenis.core.MolgenisEntity;
import org.molgenis.core.Ontology;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.security.Login;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.HandleRequestDelegationException;

import app.DatabaseFactory;

import commonservice.CommonService;

public class FillAnimalDB {

	private Database db;
	private CommonService ct;
	
	public FillAnimalDB() throws Exception {
		db = DatabaseFactory.create("handwritten/apps/org/molgenis/animaldb/animaldb.properties");
		ct = CommonService.getInstance();
		ct.setDatabase(db);
	}
	
	public FillAnimalDB(Database db) throws Exception {
		this.db = db;
		ct = CommonService.getInstance();
		ct.setDatabase(this.db);
	}
	
	public void populateDB(Login login) throws Exception, HandleRequestDelegationException,DatabaseException, ParseException, IOException {
		
		// Login as admin to have enough rights to do this
		login.login(db, "admin", "admin");
		
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		
		Logger logger = Logger.getLogger("FillAnimalDB");
		logger.info("Start filling the database with factory defaults for AnimalDB.");
		
		// Make investigation
		logger.info("Create investigation");
		Investigation inv = new Investigation();
		inv.setName("System");
		inv.setOwns_Id(login.getUserId());
		inv.setCanRead_Name("AllUsers");
		int invid = db.add(inv);
		
		// Make ontology 'Units'
		logger.info("Add ontology entries");
		Ontology ont = new Ontology();
		ont.setName("Units");
		db.add(ont);
		Query<Ontology> q = db.query(Ontology.class);
		q.eq(Ontology.NAME, "Units");
		List<Ontology> ontList = q.find();
		int ontid = ontList.get(0).getId();
		// Make ontology term entries
		int targetlinkUnitId = ct.makeOntologyTerm("TargetLink", ontid, "Link to an entry in one of the ObservationTarget tables.");
		int gramUnitId = ct.makeOntologyTerm("Gram", ontid, "SI unit for mass.");
		int booleanUnitId = ct.makeOntologyTerm("Boolean", ontid, "True (1) or false (0).");
		int datetimeUnitId = ct.makeOntologyTerm("Datetime", ontid, "Timestamp.");
		int numberUnitId = ct.makeOntologyTerm("Number", ontid, "A discrete number greater than 0.");
		int stringUnitId = ct.makeOntologyTerm("String", ontid, "Short piece of text.");
		
		logger.info("Create measurements");
		MolgenisEntity individual = db.query(MolgenisEntity.class).equals(MolgenisEntity.NAME, "Individual").find().get(0);
		MolgenisEntity panel = db.query(MolgenisEntity.class).equals(MolgenisEntity.NAME, "Panel").find().get(0);
		MolgenisEntity location = db.query(MolgenisEntity.class).equals(MolgenisEntity.NAME, "Location").find().get(0);
		// Make features
		// Because of the MeasurementDecorator a basic protocol with the name Set<MeasurementName> will be auto-generated
		ct.makeMeasurement(invid, "TypeOfGroup", stringUnitId, null, null, false, "string", "To label a group of targets.", login.getUserId());
		ct.makeMeasurement(invid, "Species", targetlinkUnitId, panel, "Species", false, "xref", "To set the species of an animal.", login.getUserId());
		ct.makeMeasurement(invid, "Sex", targetlinkUnitId, panel, "Sex", false, "xref", "To set the sex of an animal.", login.getUserId());
		ct.makeMeasurement(invid, "Location", targetlinkUnitId, location, null, false, "xref", "To set the location of a target.", login.getUserId());
		ct.makeMeasurement(invid, "Weight", gramUnitId, null, null, true, "decimal", "To set the weight of a target.", login.getUserId());
		ct.makeMeasurement(invid, "Father", targetlinkUnitId, individual, null, false, "xref", "To link a parent-group to an animal that may be a father.", login.getUserId());
		ct.makeMeasurement(invid, "Mother", targetlinkUnitId, individual, null, false, "xref", "To link a parent-group to an animal that may be a mother.", login.getUserId());
		ct.makeMeasurement(invid, "Certain", booleanUnitId, null, null, false, "bool", "To indicate whether the parenthood of an animal regarding a parent-group is certain.", login.getUserId());
		ct.makeMeasurement(invid, "Group", targetlinkUnitId, panel, null, false, "xref", "To add a target to a panel.", login.getUserId());
		ct.makeMeasurement(invid, "Parentgroup", targetlinkUnitId, panel, "Parentgroup", false, "xref", "To link a litter to a parent-group.", login.getUserId());
		ct.makeMeasurement(invid, "DateOfBirth", datetimeUnitId, null, null, true, "datetime", "To set a target's or a litter's date of birth.", login.getUserId());
		ct.makeMeasurement(invid, "Size", numberUnitId, null, null, true, "int", "To set the size of a target-group, for instance a litter.", login.getUserId());
		ct.makeMeasurement(invid, "WeanSize", numberUnitId, null, null, true, "int", "To set the wean size of a litter.", login.getUserId());
		ct.makeMeasurement(invid, "WeanSizeFemale", numberUnitId, null, null, true, "int", "To set the number of females in a litter when weaning.", login.getUserId());
		ct.makeMeasurement(invid, "WeanSizeMale", numberUnitId, null, null, true, "int", "To set the number of males in a litter when weaning.", login.getUserId());
		ct.makeMeasurement(invid, "WeanSizeUnknown", numberUnitId, null, null, true, "int", "To set the number of animals of unknown sex in a litter when weaning.", login.getUserId());
		ct.makeMeasurement(invid, "Street", stringUnitId, null, null, false, "string", "To set the street part of an address.", login.getUserId());
		ct.makeMeasurement(invid, "Housenumber", numberUnitId, null, null, false, "int", "To set the house-number part of an address.", login.getUserId());
		ct.makeMeasurement(invid, "City", stringUnitId, null, null, false, "string", "To set the city part of an address.", login.getUserId());
		ct.makeMeasurement(invid, "CustomID", stringUnitId, null, null, false, "string", "To set a target's custom ID.", login.getUserId());
		ct.makeMeasurement(invid, "WeanDate", datetimeUnitId, null, null, true, "datetime", "To set a litter's or target's date of weaning.", login.getUserId());
		ct.makeMeasurement(invid, "GenotypeDate", datetimeUnitId, null, null, true, "datetime", "To set a litter's date of genotyping.", login.getUserId());
		ct.makeMeasurement(invid, "CageCleanDate", datetimeUnitId, null, null, true, "datetime", "To set a target's date of cage cleaning.", login.getUserId());
		ct.makeMeasurement(invid, "DeathDate", datetimeUnitId, null, null, true, "datetime", "To set a target's date of death.", login.getUserId());
		ct.makeMeasurement(invid, "Active", stringUnitId, null, null, false, "string", "To register a target's activity span.", login.getUserId());
		ct.makeMeasurement(invid, "Background", targetlinkUnitId, panel, "Background", false, "xref", "To set an animal's genotypic background.", login.getUserId());
		ct.makeMeasurement(invid, "Source", targetlinkUnitId, panel, "Source", false, "xref", "To link an animal or a breeding line to a source.", login.getUserId());
		ct.makeMeasurement(invid, "Line", targetlinkUnitId, panel, "Line", false, "xref", "To link a parentgroup to a breeding line.", login.getUserId());
		ct.makeMeasurement(invid, "LineInfoLink", stringUnitId, null, null, false, "string", "To provide a link to a website with information about this line.", login.getUserId());
		ct.makeMeasurement(invid, "LineJAXName", stringUnitId, null, null, false, "string", "To provide the full line name according to JAX mouse nomenclature", login.getUserId());
		ct.makeMeasurement(invid, "SourceType", stringUnitId, null, null, false, "string", "To set the type of an animal source (used in VWA Report 4).", login.getUserId());
		ct.makeMeasurement(invid, "SourceTypeSubproject", stringUnitId, null, null, false, "string", "To set the animal's source type, when it enters a DEC subproject (used in VWA Report 5).", login.getUserId());
		ct.makeMeasurement(invid, "ParticipantGroup", stringUnitId, null, null, false, "string", "To set the participant group an animal is considered part of.", login.getUserId());
		ct.makeMeasurement(invid, "OldAnimalDBRemarks", stringUnitId, null, null, false, "string", "To store remarks about the animal in the animal table, from the old version of AnimalDB.", login.getUserId());
		ct.makeMeasurement(invid, "Remark", stringUnitId, null, null, false, "string", "To store remarks about the animal.", login.getUserId());
		ct.makeMeasurement(invid, "Litter", targetlinkUnitId, panel, "Litter", false, "xref", "To link an animal to a litter.", login.getUserId());
		ct.makeMeasurement(invid, "ExperimentNr", stringUnitId, null, null, false, "string", "To set a (sub)experiment's number.", login.getUserId());
		ct.makeMeasurement(invid, "ExperimentTitle", stringUnitId, null, null, false, "string", "To set a (sub)experiment's title.", login.getUserId());
		ct.makeMeasurement(invid, "DecSubprojectApplicationPdf", stringUnitId, null, null, false, "string", "To set a link to a PDF file with the (sub)experiment's DEC application.", login.getUserId());
		ct.makeMeasurement(invid, "DecApplicationPdf", stringUnitId, null, null, false, "string", "To set a link to a PDF file with the DEC application.", login.getUserId());
		ct.makeMeasurement(invid, "DecApprovalPdf", stringUnitId, null, null, false, "string", "To set a link to a PDF file with the DEC approval.", login.getUserId());
		ct.makeMeasurement(invid, "DecApplication", targetlinkUnitId, panel, "DecApplication", false, "xref", "To link a DEC subproject (experiment) to a DEC application.", login.getUserId());
		ct.makeMeasurement(invid, "DecNr", stringUnitId, null, null, false, "string", "To set a DEC application's DEC number.", login.getUserId());
		ct.makeMeasurement(invid, "DecTitle", stringUnitId, null, null, false, "string", "To set the title of a DEC project.", login.getUserId());
		ct.makeMeasurement(invid, "DecApplicantId", stringUnitId, null, null, false, "string", "To link a DEC application to a user with this ID in the old version of AnimalDB.", login.getUserId());
		ct.makeMeasurement(invid, "Anaesthesia", stringUnitId, null, null, false, "string", "To set the Anaesthesia value of (an animal in) an experiment.", login.getUserId());
		ct.makeMeasurement(invid, "PainManagement", stringUnitId, null, null, false, "string", "To set the PainManagement value of (an animal in) an experiment.", login.getUserId());
		ct.makeMeasurement(invid, "AnimalEndStatus", stringUnitId, null, null, false, "string", "To set the AnimalEndStatus value of an experiment.", login.getUserId());
		ct.makeMeasurement(invid, "LawDef", stringUnitId, null, null, false, "string", "To set the Lawdef value of an experiment.", login.getUserId());
		ct.makeMeasurement(invid, "ToxRes", stringUnitId, null, null, false, "string", "To set the ToxRes value of an experiment.", login.getUserId());
		ct.makeMeasurement(invid, "SpecialTechn", stringUnitId, null, null, false, "string", "To set the SpecialTechn value of an experiment.", login.getUserId());
		ct.makeMeasurement(invid, "Goal", stringUnitId, null, null, false, "string", "To set the Goal of an experiment.", login.getUserId());
		ct.makeMeasurement(invid, "Concern", stringUnitId, null, null, false, "string", "To set the Concern value of an experiment.", login.getUserId());
		ct.makeMeasurement(invid, "FieldBiology", booleanUnitId, null, null, false, "bool", "To indicate whether a DEC application is related to field biology.", login.getUserId());
		ct.makeMeasurement(invid, "ExpectedDiscomfort", stringUnitId, null, null, false, "string", "To set the expected discomfort of an animal in an experiment.", login.getUserId());
		ct.makeMeasurement(invid, "ActualDiscomfort", stringUnitId, null, null, false, "string", "To set the actual discomfort of an animal in an experiment.", login.getUserId());
		ct.makeMeasurement(invid, "AnimalType", stringUnitId, null, null, false, "string", "To set the animal type.", login.getUserId());
		ct.makeMeasurement(invid, "ExpectedAnimalEndStatus", stringUnitId, null, null, false, "string", "To set the expected end status of an animal in an experiment.", login.getUserId());
		ct.makeMeasurement(invid, "ActualAnimalEndStatus", stringUnitId, null, null, false, "string", "To set the actual end status of an animal in an experiment.", login.getUserId());
		ct.makeMeasurement(invid, "Experiment", targetlinkUnitId, panel, "Experiment", false, "xref", "To link an animal to a DEC subproject (experiment).", login.getUserId());
		ct.makeMeasurement(invid, "FromExperiment", targetlinkUnitId, panel, "Experiment", false, "xref", "To remove an animal from a DEC subproject (experiment).", login.getUserId());
		ct.makeMeasurement(invid, "GeneModification", stringUnitId, null, null, false, "string", "A genetic modification to a gene in an animal.", login.getUserId());
		ct.makeMeasurement(invid, "GeneState", stringUnitId, null, null, false, "string", "To indicate whether an animal is homo- or heterozygous for a gene modification.", login.getUserId());
		ct.makeMeasurement(invid, "VWASpecies", stringUnitId, null, null, false, "string", "To give a species the name the VWA uses for it.", login.getUserId());
		ct.makeMeasurement(invid, "LatinSpecies", stringUnitId, null, null, false, "string", "To give a species its scientific (Latin) name.", login.getUserId());
		ct.makeMeasurement(invid, "DutchSpecies", stringUnitId, null, null, false, "string", "To give a species its Dutch name.", login.getUserId());
		ct.makeMeasurement(invid, "StartDate", datetimeUnitId, null, null, true, "datetime", "To set a (sub)project's start date.", login.getUserId());
		ct.makeMeasurement(invid, "EndDate", datetimeUnitId, null, null, true, "datetime", "To set a (sub)project's end date.", login.getUserId());
		ct.makeMeasurement(invid, "Removal", stringUnitId, null, null, false, "string", "To register an animal's removal.", login.getUserId());
		ct.makeMeasurement(invid, "Article", numberUnitId, null, null, false, "int", "To set an actor's Article status according to the Law, e.g. Article 9.", login.getUserId());
		ct.makeMeasurement(invid, "MolgenisUserId", numberUnitId, null, null, false, "int", "To set an actor's corresponding MolgenisUser ID.", login.getUserId());
		ct.makeMeasurement(invid, "TransponderId", stringUnitId, null, null, false, "string", "To set the ID of an animal's transponder.", login.getUserId());
		ct.makeMeasurement(invid, "Color", stringUnitId, null, null, false, "string", "To set an animal's color.", login.getUserId());
		ct.makeMeasurement(invid, "Earmark", stringUnitId, null, null, false, "string", "To set an animal's earmark.", login.getUserId());
		ct.makeMeasurement(invid, "ResponsibleResearcher", stringUnitId, null, null, false, "string", "To set the researcher responsible for this animal.", login.getUserId());
		// For importing old AnimalDB
		ct.makeMeasurement(invid, "OldAnimalDBAnimalID", stringUnitId, null, null, false, "string", "To set an animal's ID in the old version of AnimalDB.", login.getUserId());
		ct.makeMeasurement(invid, "OldAnimalDBAnimalCustomID", stringUnitId, null, null, false, "string", "To set an animal's Custom ID in the old version of AnimalDB.", login.getUserId());
		ct.makeMeasurement(invid, "OldAnimalDBLocationID", stringUnitId, null, null, false, "string", "To set a location's ID in the old version of AnimalDB.", login.getUserId());
		ct.makeMeasurement(invid, "OldAnimalDBLitterID", stringUnitId, null, null, false, "string", "To link an animal to a litter with this ID in the old version of AnimalDB.", login.getUserId());
		ct.makeMeasurement(invid, "OldAnimalDBExperimentID", stringUnitId, null, null, false, "string", "To set an experiment's ID in the old version of AnimalDB.", login.getUserId());
		ct.makeMeasurement(invid, "OldAnimalDBDecApplicationID", stringUnitId, null, null, false, "string", "To link an experiment to a DEC application with this ID in the old version of AnimalDB.", login.getUserId());
		ct.makeMeasurement(invid, "OldAnimalDBBroughtinDate", datetimeUnitId, null, null, true, "datetime", "To set a target's date of arrival in the system/ on the location in the old version of AnimalDB.", login.getUserId());
		ct.makeMeasurement(invid, "OldAnimalDBExperimentalManipulationRemark", stringUnitId, null, null, false, "string", "To store Experiment remarks about the animal, from the Experimental manipulation event, from the old version of AnimalDB.", login.getUserId());
		ct.makeMeasurement(invid, "OldAnimalDBPresetID", stringUnitId, null, null, false, "string", "To link a targetgroup to a preset this ID in the old version of AnimalDB.", login.getUserId());
		// For importing old Uli Eisel DB
		ct.makeMeasurement(invid, "OldUliDbId", stringUnitId, null, null, false, "string", "To set an animal's ID in the old Uli Eisel DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldUliDbMotherInfo", stringUnitId, null, null, false, "string", "To set an animal's mother info in the old Uli Eisel DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldUliDbFatherInfo", stringUnitId, null, null, false, "string", "To set an animal's father info in the old Uli Eisel DB.", login.getUserId());
		// For importing old Roelof Hut DB
		ct.makeMeasurement(invid, "OldRhutDbAnimalId", stringUnitId, null, null, false, "string", "To set an animal's ID in Roelof Hut's old DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldRhutDbLitterId", stringUnitId, null, null, false, "string", "To link an animal to a litter with this ID in the old version of Roelof Hut's DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldRhutDbSampleDate", datetimeUnitId, null, null, false, "datetime", "To set the date that an animal was sampled in the old version of Roelof Hut's DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldRhutDbSampleNr", numberUnitId, null, null, false, "int", "To set the sample number in the old version of Roelof Hut's DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldRhutDbExperimentId", numberUnitId, null, null, false, "int", "To set the experiment's ID in the old version of Roelof Hut's DB.", login.getUserId());
		
		logger.info("Add codes");
		// Codes for Subprojects
		ct.makeCategory("A", "A", "ExperimentNr");
		ct.makeCategory("B", "B", "ExperimentNr");
		ct.makeCategory("C", "C", "ExperimentNr");
		ct.makeCategory("D", "D", "ExperimentNr");
		ct.makeCategory("E", "E", "ExperimentNr");
		ct.makeCategory("F", "F", "ExperimentNr");
		ct.makeCategory("G", "G", "ExperimentNr");
		ct.makeCategory("H", "H", "ExperimentNr");
		ct.makeCategory("I", "I", "ExperimentNr");
		ct.makeCategory("J", "J", "ExperimentNr");
		ct.makeCategory("K", "K", "ExperimentNr");
		ct.makeCategory("L", "L", "ExperimentNr");
		ct.makeCategory("M", "M", "ExperimentNr");
		ct.makeCategory("N", "N", "ExperimentNr");
		ct.makeCategory("O", "O", "ExperimentNr");
		ct.makeCategory("P", "P", "ExperimentNr");
		ct.makeCategory("Q", "Q", "ExperimentNr");
		ct.makeCategory("R", "R", "ExperimentNr");
		ct.makeCategory("S", "S", "ExperimentNr");
		ct.makeCategory("T", "T", "ExperimentNr");
		ct.makeCategory("U", "U", "ExperimentNr");
		ct.makeCategory("V", "V", "ExperimentNr");
		ct.makeCategory("W", "W", "ExperimentNr");
		ct.makeCategory("X", "X", "ExperimentNr");
		ct.makeCategory("Y", "Y", "ExperimentNr");
		ct.makeCategory("Z", "Z", "ExperimentNr");
		
		// Codes for SourceType
		ct.makeCategory("1-1", "Eigen fok binnen uw organisatorische werkeenheid", "SourceType");
		ct.makeCategory("1-2", "Andere organisatorische werkeenheid vd instelling", "SourceType");
		ct.makeCategory("1-3", "Geregistreerde fok/aflevering in Nederland", "SourceType");
		ct.makeCategory("2", "Van EU-lid-staten", "SourceType");
		ct.makeCategory("3", "Niet-geregistreerde fok/afl in Nederland", "SourceType");
		ct.makeCategory("4", "Niet-geregistreerde fok/afl in andere EU-lid-staat", "SourceType");
		ct.makeCategory("5", "Andere herkomst", "SourceType");
		// Codes for SourceTypeSubproject
		ct.makeCategory("1", "Geregistreerde fok/aflevering in Nederland", "SourceTypeSubproject");
		ct.makeCategory("2", "Van EU-lid-staten", "SourceTypeSubproject");
		ct.makeCategory("3", "Niet-geregistreerde fok/afl in Nederland", "SourceTypeSubproject");
		ct.makeCategory("4", "Niet-geregistreerde fok/afl in andere EU-lid-staat", "SourceTypeSubproject");
		ct.makeCategory("5", "Andere herkomst", "SourceTypeSubproject");
		ct.makeCategory("6", "Hergebruik eerste maal in het registratiejaar", "SourceTypeSubproject");
		ct.makeCategory("7", "Hergebruik tweede, derde enz. maal in het registratiejaar", "SourceTypeSubproject");
		// Codes for ParticipantGroup
		ct.makeCategory("04", "Chrono- en gedragsbiologie", "ParticipantGroup");
		ct.makeCategory("06", "Plantenbiologie", "ParticipantGroup");
		ct.makeCategory("07", "Dierfysiologie", "ParticipantGroup");
		ct.makeCategory("Klinische Farmacologie (no code yet)", "Klinische Farmacologie", "ParticipantGroup");
		// Codes for Anaestheasia
		ct.makeCategory("1", "A. Is niet toegepast (geen aanleiding)", "Anaesthesia");
		ct.makeCategory("2", "B. Is niet toegepast (onverenigbaar proef)", "Anaesthesia");
		ct.makeCategory("3", "C. Is niet toegepast (praktisch onuitvoerbaar)", "Anaesthesia");
		ct.makeCategory("4", "D. Is wel toegepast", "Anaesthesia");
		// Codes for PainManagement
		ct.makeCategory("1", "A. Is niet toegepast (geen aanleiding)", "PainManagement");
		ct.makeCategory("2", "B. Is niet toegepast (onverenigbaar proef)", "PainManagement");
		ct.makeCategory("3", "C. Is niet toegepast (praktisch onuitvoerbaar)", "PainManagement");
		ct.makeCategory("4", "D. Is wel toegepast", "PainManagement");
		// Codes for AnimalEndStatus
		ct.makeCategory("1", "A. Dood in het kader van de proef", "AnimalEndStatus");
		ct.makeCategory("2", "B. Gedood na beeindiging van de proef", "AnimalEndStatus");
		ct.makeCategory("3", "C. Na einde proef in leven gelaten", "AnimalEndStatus");
		// Codes for ExpectedAnimalEndStatus
		ct.makeCategory("1", "A. Dood in het kader van de proef", "ExpectedAnimalEndStatus");
		ct.makeCategory("2", "B. Gedood na beeindiging van de proef", "ExpectedAnimalEndStatus");
		ct.makeCategory("3", "C. Na einde proef in leven gelaten", "ExpectedAnimalEndStatus");
		// Codes for ActualAnimalEndStatus
		ct.makeCategory("1", "A. Dood in het kader van de proef", "ActualAnimalEndStatus");
		ct.makeCategory("2", "B. Gedood na beeindiging van de proef", "ActualAnimalEndStatus");
		ct.makeCategory("3", "C. Na einde proef in leven gelaten", "ActualAnimalEndStatus");
		// Codes for LawDef
		ct.makeCategory("1", "A. Geen wettelijke bepaling", "LawDef");
		ct.makeCategory("2", "B. Uitsluitend Nederland", "LawDef");
		ct.makeCategory("3", "C. Uitsluitend EU-lidstaten", "LawDef");
		ct.makeCategory("4", "D. Uitsluitend Lidstaten Raad v. Eur.", "LawDef");
		ct.makeCategory("5", "E. Uitsluitend Europese landen", "LawDef");
		ct.makeCategory("6", "F. Ander wettelijke bepaling", "LawDef");
		ct.makeCategory("7", "G. Combinatie van B. C. D. E. en F", "LawDef");
		// Codes for ToxRes
		ct.makeCategory("01", "A. Geen toxicologisch onderzoek", "ToxRes");
		ct.makeCategory("02", "B. Acuut tox. met letaliteit", "ToxRes");
		ct.makeCategory("03", "C. Acuut tox. LD50/LC50", "ToxRes");
		ct.makeCategory("04", "D. Overige acuut tox. (geen letaliteit)", "ToxRes");
		ct.makeCategory("05", "E. Sub-acuut tox.", "ToxRes");
		ct.makeCategory("06", "F. Sub-chronisch en chronische tox.", "ToxRes");
		ct.makeCategory("07", "G. Carcinogeniteitsonderzoek", "ToxRes");
		ct.makeCategory("08", "H. Mutageniteitsonderzoek", "ToxRes");
		ct.makeCategory("09", "I. Teratogeniteitsonderz. (segment II)", "ToxRes");
		ct.makeCategory("10", "J. Reproductie-onderzoek (segment 1 en III)", "ToxRes");
		ct.makeCategory("11", "K. Overige toxiciteitsonderzoek", "ToxRes");
		// Codes for SpecialTechn
		ct.makeCategory("01", "A. Geen van deze technieken/ingrepen", "SpecialTechn");
		ct.makeCategory("02", "B. Doden zonder voorafgaande handelingen", "SpecialTechn");
		ct.makeCategory("03", "C. Curare-achtige stoffen zonder anesthesie", "SpecialTechn");
		ct.makeCategory("04", "D. Technieken/ingrepen verkrijgen transgene dieren", "SpecialTechn");
		ct.makeCategory("05", "E. Toedienen van mogelijk irriterende stoffen via luchtwegen", "SpecialTechn");
		ct.makeCategory("06", "E. Toedienen van mogelijk irriterende stoffen op het oog", "SpecialTechn");
		ct.makeCategory("07", "E. Toedienen van mogelijk irriterende stoffen op andere slijmvliezen of huid", "SpecialTechn");
		ct.makeCategory("08", "F. Huidsensibilisaties", "SpecialTechn");
		ct.makeCategory("09", "G. Bestraling, met schadelijke effecten", "SpecialTechn");
		ct.makeCategory("10", "H. Traumatiserende fysische/chemische prikkels (CZ)", "SpecialTechn");
		ct.makeCategory("11", "I. Traumatiserende psychische prikkels", "SpecialTechn");
		ct.makeCategory("12", "J. Technieken/ingrepen anders dan C t/m H, gericht: opwekken van ontstekingen/infecties", "SpecialTechn");
		ct.makeCategory("13", "J. Technieken/ingrepen anders dan C t/m H, gericht: opwekken van verbrand./fract. of letsel (traum.)", "SpecialTechn");
		ct.makeCategory("14", "J. Technieken/ingrepen anders dan C t/m H, gericht: opwekken van poly- en monoclonale antistoffen", "SpecialTechn");
		ct.makeCategory("15", "J. Technieken/ingrepen anders dan C t/m H, gericht: produceren van monoclonale antistoffen", "SpecialTechn");
		ct.makeCategory("16", "K. Meer dan een onder G t/m J vermelde mogelijkheden", "SpecialTechn");
		ct.makeCategory("17", "L. Gefokt met ongerief", "SpecialTechn");
		// Codes for Concern
		ct.makeCategory("1", "A. Gezondheid/voed. ja", "Concern");
		ct.makeCategory("2", "B. Gezondheid/voed. nee", "Concern");
		// Codes for Goal
		ct.makeCategory("1", "A. Onderzoek m.b.t. de mens: ontw. sera vaccins/biol.produkten", "Goal");
		ct.makeCategory("2", "A. Onderzoek m.b.t. de mens: prod./contr./ijking sera vaccins/biol. producten", "Goal");
		ct.makeCategory("3", "A. Onderzoek m.b.t. de mens: ontw. geneesmiddelen", "Goal");
		ct.makeCategory("4", "A. Onderzoek m.b.t. de mens: prod./contr./ijking geneesmiddelen", "Goal");
		ct.makeCategory("5", "A. Onderzoek m.b.t. de mens: ontw. med. hulpmiddelen/ toepassingen", "Goal");
		ct.makeCategory("6", "A. Onderzoek m.b.t. de mens: prod./contr./ijking med.hulpm./toepassingen", "Goal");
		ct.makeCategory("7", "A. Onderzoek m.b.t. de mens: and. ijkingen", "Goal");
		ct.makeCategory("8", "A. Onderzoek m.b.t. het dier: ontw. sera vaccins/biol.produkten", "Goal");
		ct.makeCategory("9", "A. Onderzoek m.b.t. het dier: prod./contr./ijking sera vaccins/biol. producten", "Goal");
		ct.makeCategory("10", "A. Onderzoek m.b.t. het dier: ontw. geneesmiddelen", "Goal");
		ct.makeCategory("11", "A. Onderzoek m.b.t. het dier: prod./contr./ijking geneesmiddelen", "Goal");
		ct.makeCategory("12", "A. Onderzoek m.b.t. het dier: ontw. med. hulpmiddelen/ toepassingen", "Goal");
		ct.makeCategory("13", "A. Onderzoek m.b.t. het dier: prod./contr./ijking med.hulpm./toepassingen", "Goal");
		ct.makeCategory("14", "A. Onderzoek m.b.t. het dier: and. ijkingen", "Goal");
		ct.makeCategory("15", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: agrarische sector", "Goal");
		ct.makeCategory("16", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: industrie", "Goal");
		ct.makeCategory("17", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: huishouden", "Goal");
		ct.makeCategory("18", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: cosm./toiletartikelen", "Goal");
		ct.makeCategory("19", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: voed.midd.mens.cons.", "Goal");
		ct.makeCategory("20", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: voed.midd.dier.cons.", "Goal");
		ct.makeCategory("21", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: tabak/and.rookwaren", "Goal");
		ct.makeCategory("22", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: stoffen schad.voor milieu", "Goal");
		ct.makeCategory("23", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: ander", "Goal");
		ct.makeCategory("24", "C. Opsporen van/ uivoeren van diagnostiek: ziekten bij mensen", "Goal");
		ct.makeCategory("25", "C. Opsporen van/ uivoeren van diagnostiek: and.lich.kenmerken bij mensen", "Goal");
		ct.makeCategory("26", "C. Opsporen van/ uivoeren van diagnostiek: ziekten bij dieren", "Goal");
		ct.makeCategory("27", "C. Opsporen van/ uivoeren van diagnostiek: and. lich.kenmerken bij dieren", "Goal");
		ct.makeCategory("28", "C. Opsporen van/ uivoeren van diagnostiek: ziekten/kenmerken bij planten", "Goal");
		ct.makeCategory("29", "D. Onderwijs/Training", "Goal");
		ct.makeCategory("30", "E. Wetensch.vraag m.b.t.: kanker (excl.carcinogene stoffen) bij mensen", "Goal");
		ct.makeCategory("31", "E. Wetensch.vraag m.b.t.: hart-en vaatziekten bij mensen", "Goal");
		ct.makeCategory("32", "E. Wetensch.vraag m.b.t.: geestesz./zenuwz.  bij mensen", "Goal");
		ct.makeCategory("33", "E. Wetensch.vraag m.b.t.: and. ziekten bij mensen", "Goal");
		ct.makeCategory("34", "E. Wetensch.vraag m.b.t.: and. lich. kenmerken bij mensen", "Goal");
		ct.makeCategory("35", "E. Wetensch.vraag m.b.t.: gedrag van dieren", "Goal");
		ct.makeCategory("36", "E. Wetensch.vraag m.b.t.: ziekten bij dieren", "Goal");
		ct.makeCategory("37", "E. Wetensch.vraag m.b.t.: and. wetenschappelijke vraag", "Goal");
		// Codes for ExpectedDiscomfort
		ct.makeCategory("1", "A. Gering", "ExpectedDiscomfort");
		ct.makeCategory("2", "B. Gering/matig", "ExpectedDiscomfort");
		ct.makeCategory("3", "C. Matig", "ExpectedDiscomfort");
		ct.makeCategory("4", "D. Matig/ernstig", "ExpectedDiscomfort");
		ct.makeCategory("5", "E. Ernstig", "ExpectedDiscomfort");
		ct.makeCategory("6", "F. Zeer ernstig", "ExpectedDiscomfort");
		// Codes for ActualDiscomfort
		ct.makeCategory("1", "A. Gering", "ActualDiscomfort");
		ct.makeCategory("2", "B. Gering/matig", "ActualDiscomfort");
		ct.makeCategory("3", "C. Matig", "ActualDiscomfort");
		ct.makeCategory("4", "D. Matig/ernstig", "ActualDiscomfort");
		ct.makeCategory("5", "E. Ernstig", "ActualDiscomfort");
		ct.makeCategory("6", "F. Zeer ernstig", "ActualDiscomfort");
		// Codes for AnimalType
		ct.makeCategory("1", "A. Gewoon dier", "AnimalType");
		ct.makeCategory("2", "B. Transgeen dier", "AnimalType");
		ct.makeCategory("3", "C. Wildvang", "AnimalType");
		ct.makeCategory("4", "D. Biotoop", "AnimalType");
		// Codes for GeneModification
		// TODO: find a way to make the KO/KI part mandatory
		// Maybe turn Gene into a Panel on which you have to set the ModificationType measurement
		ct.makeCategory("Cry1 KO", "Cry1 KO", "GeneModification");
		ct.makeCategory("Cry2 KO", "Cry2 KO", "GeneModification");
		ct.makeCategory("Per1 KO", "Per1 KO", "GeneModification");
		ct.makeCategory("Per2 KO", "Per2 KO", "GeneModification");
		ct.makeCategory("Per2::Luc KI", "Per2::Luc KI", "GeneModification");
		// Codes for GeneState
		ct.makeCategory("0", "-/-", "GeneState");
		ct.makeCategory("1", "+/-", "GeneState");
		ct.makeCategory("2", "+/+", "GeneState");
		ct.makeCategory("3", "ntg", "GeneState");
		ct.makeCategory("4", "wt", "GeneState");
		ct.makeCategory("5", "unknown", "GeneState");
		ct.makeCategory("6", "transgenic", "GeneState");
		// Codes for VWASpecies
		ct.makeCategory("01", "Muizen", "VWASpecies");
		ct.makeCategory("02", "Ratten", "VWASpecies");
		ct.makeCategory("03", "Hamsters", "VWASpecies");
		ct.makeCategory("04", "Cavia's", "VWASpecies");
		ct.makeCategory("09", "And. Knaagdieren", "VWASpecies");
		ct.makeCategory("11", "Konijnen", "VWASpecies");
		ct.makeCategory("21", "Honden", "VWASpecies");
		ct.makeCategory("22", "Katten", "VWASpecies");
		ct.makeCategory("23", "Fretten", "VWASpecies");
		ct.makeCategory("29", "And. Vleeseters", "VWASpecies");
		ct.makeCategory("31", "Prosimians", "VWASpecies");
		ct.makeCategory("32", "Nieuwe wereld apen", "VWASpecies");
		ct.makeCategory("33", "Oude wereld apen", "VWASpecies");
		ct.makeCategory("34", "Mensapen", "VWASpecies");
		ct.makeCategory("41", "Paarden", "VWASpecies");
		ct.makeCategory("42", "Varkens", "VWASpecies");
		ct.makeCategory("43", "Geiten", "VWASpecies");
		ct.makeCategory("44", "Schapen", "VWASpecies");
		ct.makeCategory("45", "Runderen", "VWASpecies");
		ct.makeCategory("49", "And. Zoogdieren", "VWASpecies");
		ct.makeCategory("51", "Kippen", "VWASpecies");
		ct.makeCategory("52", "Kwartels", "VWASpecies");
		ct.makeCategory("59", "And.Vogels", "VWASpecies");
		ct.makeCategory("69", "Reptielen", "VWASpecies");
		ct.makeCategory("79", "Amfibieen", "VWASpecies");
		ct.makeCategory("89", "Vissen", "VWASpecies");
		ct.makeCategory("91", "Cyclostoma", "VWASpecies");
		// Codes for Removal
		ct.makeCategory("0", "dood", "Removal");
		ct.makeCategory("1", "levend afgevoerd andere organisatorische eenheid RuG", "Removal");
		ct.makeCategory("2", "levend afgevoerd gereg. onderzoeksinstelling NL", "Removal");
		ct.makeCategory("3", "levend afgevoerd gereg. onderzoeksinstelling EU", "Removal");
		ct.makeCategory("4", "levend afgevoerd andere bestemming", "Removal");
		// Codes for Earmark
		ct.makeCategory("1 l", "one left", "Earmark");
		ct.makeCategory("1 r", "one right", "Earmark");
		ct.makeCategory("1 r 1 l", "one right, one left", "Earmark");
		ct.makeCategory("1 r 2 l", "one right, two left", "Earmark");
		ct.makeCategory("2 l", "two left", "Earmark");
		ct.makeCategory("2 l 1 r", "two left, one right", "Earmark");
		ct.makeCategory("2 r", "two right", "Earmark");
		ct.makeCategory("2 r 1 l", "two right, one left", "Earmark");
		ct.makeCategory("2 r 2 l", "two right, two left", "Earmark");
		ct.makeCategory("O", "none", "Earmark");
		ct.makeCategory("x", "", "Earmark");
		// Codes for Color
		ct.makeCategory("beige", "beige", "Color");
		ct.makeCategory("brown", "brown", "Color");
		ct.makeCategory("yellow", "yellow", "Color");
		ct.makeCategory("gray", "gray", "Color");
		ct.makeCategory("gray-brown", "gray-brown", "Color");
		ct.makeCategory("red-brown", "red-brown", "Color");
		ct.makeCategory("black", "black", "Color");
		ct.makeCategory("black-brown", "black-brown", "Color");
		ct.makeCategory("black-gray", "black-gray", "Color");
		ct.makeCategory("white", "white", "Color");
		ct.makeCategory("cinnamon", "cinnamon", "Color");
		
		logger.info("Create Protocols");
		// Protocol for Location plugin: SetSublocationOf (feature: Location)
		List<Integer> featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("Location"));
		ct.makeProtocol(invid, "SetSublocationOf", "To set one location as the sublocation of another.", featureIdList);	
		// Protocol for Breeding module: SetLitterSpecs
		featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("Parentgroup"));
		featureIdList.add(ct.getMeasurementId("DateOfBirth"));
		featureIdList.add(ct.getMeasurementId("Size"));
		featureIdList.add(ct.getMeasurementId("Certain"));
		featureIdList.add(ct.getMeasurementId("Active"));
		ct.makeProtocol(invid, "SetLitterSpecs", "To set the specifications of a litter.", featureIdList);
		
		// Protocol for Breeding module: SetBreedingLineSpecs
		featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("Line"));
		featureIdList.add(ct.getMeasurementId("LineJAXName"));
		featureIdList.add(ct.getMeasurementId("Species"));
		featureIdList.add(ct.getMeasurementId("GeneModification"));
		featureIdList.add(ct.getMeasurementId("LineInfoLink"));
		featureIdList.add(ct.getMeasurementId("Remark"));
		featureIdList.add(ct.getMeasurementId("Active"));
		featureIdList.add(ct.getMeasurementId("SourceType"));
		ct.makeProtocol(invid, "SetBreedingLineSpecs", "To set the specifications of a Breedingline.", featureIdList);
		
		// Protocol SetAddress
		featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("Street"));
		featureIdList.add(ct.getMeasurementId("Housenumber"));
		featureIdList.add(ct.getMeasurementId("City"));
		ct.makeProtocol(invid, "SetAddress", "To set an address.", featureIdList);
		// Protocol SetDecProjectSpecs
		featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("DecNr"));
		featureIdList.add(ct.getMeasurementId("DecTitle"));
		featureIdList.add(ct.getMeasurementId("DecApplicantId"));
		featureIdList.add(ct.getMeasurementId("DecApplicationPdf"));
		featureIdList.add(ct.getMeasurementId("DecApprovalPdf"));
		featureIdList.add(ct.getMeasurementId("StartDate"));
		featureIdList.add(ct.getMeasurementId("EndDate"));
		ct.makeProtocol(invid, "SetDecProjectSpecs", "To set the specifications of a DEC project.", featureIdList);
		// Protocol SetDecSubprojectSpecs
		featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("ExperimentNr"));
		featureIdList.add(ct.getMeasurementId("DecSubprojectApplicationPdf"));
		featureIdList.add(ct.getMeasurementId("Concern"));
		featureIdList.add(ct.getMeasurementId("Goal"));
		featureIdList.add(ct.getMeasurementId("SpecialTechn"));
		featureIdList.add(ct.getMeasurementId("LawDef"));
		featureIdList.add(ct.getMeasurementId("ToxRes"));
		featureIdList.add(ct.getMeasurementId("Anaesthesia"));
		featureIdList.add(ct.getMeasurementId("PainManagement"));
		featureIdList.add(ct.getMeasurementId("AnimalEndStatus"));
		featureIdList.add(ct.getMeasurementId("OldAnimalDBRemarks"));
		featureIdList.add(ct.getMeasurementId("DecApplication"));
		featureIdList.add(ct.getMeasurementId("StartDate"));
		featureIdList.add(ct.getMeasurementId("EndDate"));
		ct.makeProtocol(invid, "SetDecSubprojectSpecs", "To set the specifications of a DEC subproject.", featureIdList);
		// Protocol AnimalInSubproject
		featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("Experiment"));
		featureIdList.add(ct.getMeasurementId("ExperimentTitle"));
		featureIdList.add(ct.getMeasurementId("SourceTypeSubproject"));
		featureIdList.add(ct.getMeasurementId("PainManagement"));
		featureIdList.add(ct.getMeasurementId("Anaesthesia"));
		featureIdList.add(ct.getMeasurementId("ExpectedDiscomfort"));
		featureIdList.add(ct.getMeasurementId("ExpectedAnimalEndStatus"));
		ct.makeProtocol(invid, "AnimalInSubproject", "To add an animal to an experiment.", featureIdList);
		// Protocol AnimalFromSubproject
		featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("FromExperiment"));
		featureIdList.add(ct.getMeasurementId("ActualDiscomfort"));
		featureIdList.add(ct.getMeasurementId("ActualAnimalEndStatus"));
		ct.makeProtocol(invid, "AnimalFromSubproject", "To remove an animal from an experiment.", featureIdList);
		// Protocol SetGenotype
		featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("GeneModification"));
		featureIdList.add(ct.getMeasurementId("GeneState"));
		ct.makeProtocol(invid, "SetGenotype", "To set part (one gene modification) of an animal's genotype.", featureIdList);
		// Protocol Wean
		// Discussion: for now we leave out the custom label feature, because that is flexible (set by user).
		// Discussion: for now we leave out the Genotype features. Genotype is set a few weeks after weaning,
		// when the PCR results come in. So we'd probably better use a separate set of protocols for that
		// (Background + X times Genotype protocol).
		featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("Litter"));
		featureIdList.add(ct.getMeasurementId("Sex"));
		featureIdList.add(ct.getMeasurementId("WeanDate"));
		featureIdList.add(ct.getMeasurementId("Active"));
		featureIdList.add(ct.getMeasurementId("DateOfBirth"));
		featureIdList.add(ct.getMeasurementId("Species"));
		featureIdList.add(ct.getMeasurementId("AnimalType"));
		featureIdList.add(ct.getMeasurementId("Source"));
		featureIdList.add(ct.getMeasurementId("Color"));
		featureIdList.add(ct.getMeasurementId("Earmark"));
		featureIdList.add(ct.getMeasurementId("Sex"));
		ct.makeProtocol(invid, "Wean", "To wean an animal.", featureIdList);
		
		// Init lists that we can later add to the DB at once
		List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
		
		// Find MolgenisUsers, create corresponding AnimalDB Actors and link them using a value
		// Obsolete since we will not use Actors anymore, only MolgenisUsers
		/*
		logger.info("Find MolgenisUsers and create corresponding Actors");
		int protocolId = ct.getProtocolId("SetMolgenisUserId");
		int measurementId = ct.getMeasurementId("MolgenisUserId");
		int adminActorId = 0;
		List<MolgenisUser> userList = db.find(MolgenisUser.class);
		for (MolgenisUser user : userList) {
			String userName = user.getName();
			int animaldbUserId = ct.makeActor(invid, userName);
			// Link Actor to MolgenisUser
			valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
					protocolId, measurementId, animaldbUserId, Integer.toString(user.getId()), 0));
			// Keep admin's id for further use
			if (userName.equals("admin")) {
				adminActorId = animaldbUserId;
			}
		}
		
		// Give admin Actor the Article 9 status
		protocolId = ct.getProtocolId("SetArticle");
		measurementId = ct.getMeasurementId("Article");
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, adminActorId, "9", 0));
		*/
		
		int protocolId = ct.getProtocolId("SetTypeOfGroup");
		int measurementId = ct.getMeasurementId("TypeOfGroup");
		
		logger.info("Create Groups");
		// Groups -> sex
		int groupId = ct.makePanel(invid, "Male", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Sex", 0));
		groupId = ct.makePanel(invid, "Female", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Sex", 0));
		groupId = ct.makePanel(invid, "UnknownSex", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Sex", 0));
		
		// Groups -> species
		int vwaProtocolId = ct.getProtocolId("SetVWASpecies");
		int latinProtocolId = ct.getProtocolId("SetVWASpecies");
		int dutchProtocolId = ct.getProtocolId("SetDutchSpecies");
		int vwaMeasurementId = ct.getMeasurementId("VWASpecies");
		int latinMeasurementId = ct.getMeasurementId("LatinSpecies");
		int dutchMeasurementId = ct.getMeasurementId("DutchSpecies");
		
		groupId = ct.makePanel(invid, "House mouse", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "Muizen", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Mus musculus", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				dutchProtocolId, dutchMeasurementId, groupId, "Huismuis", 0));
		
		groupId = ct.makePanel(invid, "Brown rat", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "Ratten", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Rattus norvegicus", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				dutchProtocolId, dutchMeasurementId, groupId, "Bruine rat", 0));
		
		groupId = ct.makePanel(invid, "Common vole", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "And. knaagdieren", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Microtus arvalis", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				dutchProtocolId, dutchMeasurementId, groupId, "Veldmuis", 0));
		
		groupId = ct.makePanel(invid, "Tundra vole", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "And. knaagdieren", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Microtus oeconomus", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				dutchProtocolId, dutchMeasurementId, groupId, "Noordse woelmuis", 0));
		
		//http://en.wikipedia.org/wiki/Common_Vole
		
		groupId = ct.makePanel(invid, "Syrian hamster", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "Hamsters", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Mesocricetus auratus", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				dutchProtocolId, dutchMeasurementId, groupId, "Goudhamster", 0));
		
		groupId = ct.makePanel(invid, "European groundsquirrel", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "And. knaagdieren", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Spermophilus citellus", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				dutchProtocolId, dutchMeasurementId, groupId, "Europese grondeekhoorn", 0));
		
		groupId = ct.makePanel(invid, "Siberian hamster", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "Hamsters", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Phodopus sungorus", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				dutchProtocolId, dutchMeasurementId, groupId, "Siberische hamster", 0));
		
		groupId = ct.makePanel(invid, "Domestic guinea pig", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "Cavia's", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Cavia porcellus", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				dutchProtocolId, dutchMeasurementId, groupId, "Cavia", 0));
		
		groupId = ct.makePanel(invid, "Fat-tailed dunnart", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "And. Zoogdieren", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Sminthopsis granulipes", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				dutchProtocolId, dutchMeasurementId, groupId, "Dikstaartsmalvoetbuidelmuis", 0));
		
		// Groups -> Backgrounds
		groupId = ct.makePanel(invid, "129S", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Background", 0));
		groupId = ct.makePanel(invid, "C57BL/6j", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Background", 0));
		groupId = ct.makePanel(invid, "C57BL/10j", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Background", 0));
		groupId = ct.makePanel(invid, "CBA/j", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Background", 0));
		groupId = ct.makePanel(invid, "BALB/c", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Background", 0));
		groupId = ct.makePanel(invid, "CD1", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Background", 0));
		groupId = ct.makePanel(invid, "CBA/CaJ", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Background", 0));
		
		// Groups -> Source
		int sourceProtocolId = ct.getProtocolId("SetSourceType");
		int sourceMeasurementId = ct.getMeasurementId("SourceType");
		groupId = ct.makePanel(invid, "Harlan", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Geregistreerde fok/aflevering in Nederland", 0));
		groupId = ct.makePanel(invid, "Kweek chronobiologie", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Eigen fok binnen uw organisatorische werkeenheid", 0));
		groupId = ct.makePanel(invid, "Kweek gedragsbiologie", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Eigen fok binnen uw organisatorische werkeenheid", 0));
		groupId = ct.makePanel(invid, "Kweek dierfysiologie", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Eigen fok binnen uw organisatorische werkeenheid", 0));
		groupId = ct.makePanel(invid, "Wilde fauna", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Niet-geregistreerde fok/afl in Nederland", 0));
		// Sources for Uli Eisel:
		groupId = ct.makePanel(invid, "Stuttgart (Uli Eisel)", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Van EU-lid-staten", 0));
		/*
		groupId = ct.makePanel(invid, "UliEisel51en52", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Van EU-lid-staten", 0));
		groupId = ct.makePanel(invid, "UliEisel55", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Andere herkomst", 0));
		*/
		groupId = ct.makePanel(invid, "Kweek moleculaire neurobiologie", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Eigen fok binnen uw organisatorische werkeenheid", 0));
		// Sources for Roelof Hut:
		groupId = ct.makePanel(invid, "ErasmusMC", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Geregistreerde fok/aflevering in Nederland", 0));
		groupId = ct.makePanel(invid, "JacksonCharlesRiver", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Andere herkomst", 0));
		// Sources for demo purposes:
		/*groupId = ct.makePanel(invid, "Max-Planck-Institut fuer Verhaltensfysiologie", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Van EU-lid-staten", 0));
		groupId = ct.makePanel(invid, "Unknown source UK", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Niet-geregistreerde fok/afl in andere EU-lid-staat", 0));
		*/
		
		// Add everything to DB
		db.add(valuesToAddList);
		
		logger.info("AnimalDB database updated successfully!");
		
		login.logout(db);
		login.login(db, "anonymous", "anonymous");
	}
}
