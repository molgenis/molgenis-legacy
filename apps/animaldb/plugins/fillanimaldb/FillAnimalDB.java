package plugins.fillanimaldb;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.core.MolgenisEntity;
import org.molgenis.core.Ontology;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.security.Login;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservedValue;

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
	
	public void populateDB(Login login) throws DatabaseException, ParseException, IOException {
		
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
		inv.setOwns(login.getUserId());
		db.add(inv);
		int invid = inv.getId();
		
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
		ct.makeMeasurement(invid, "Line", targetlinkUnitId, panel, "Link", false, "xref", "To link a parentgroup to a breeding line.", login.getUserId());
		ct.makeMeasurement(invid, "SourceType", stringUnitId, null, null, false, "string", "To set the type of an animal source (used in VWA Report 4).", login.getUserId());
		ct.makeMeasurement(invid, "SourceTypeSubproject", stringUnitId, null, null, false, "string", "To set the animal's source type, when it enters a DEC subproject (used in VWA Report 5).", login.getUserId());
		ct.makeMeasurement(invid, "ParticipantGroup", stringUnitId, null, null, false, "string", "To set the participant group an animal is considered part of.", login.getUserId());
		ct.makeMeasurement(invid, "OldAnimalDBRemarks", stringUnitId, null, null, false, "string", "To store remarks about the animal in the animal table, from the old version of AnimalDB.", login.getUserId());
		ct.makeMeasurement(invid, "Remark", stringUnitId, null, null, false, "string", "To store remarks about the animal.", login.getUserId());
		ct.makeMeasurement(invid, "Litter", targetlinkUnitId, panel, "Litter", false, "xref", "To link an animal to a litter.", login.getUserId());
		ct.makeMeasurement(invid, "ExperimentNr", stringUnitId, null, null, false, "string", "To set a (sub)experiment's number.", login.getUserId());
		ct.makeMeasurement(invid, "DecSubprojectApplicationPdf", stringUnitId, null, null, false, "string", "To set a link to a PDF file with the (sub)experiment's DEC application.", login.getUserId());
		ct.makeMeasurement(invid, "DecApplicationPdf", stringUnitId, null, null, false, "string", "To set a link to a PDF file with the DEC application.", login.getUserId());
		ct.makeMeasurement(invid, "DecApprovalPdf", stringUnitId, null, null, false, "string", "To set a link to a PDF file with the DEC approval.", login.getUserId());
		ct.makeMeasurement(invid, "DecApplication", targetlinkUnitId, panel, "DecApplication", false, "xref", "To link a DEC subproject (experiment) to a DEC application.", login.getUserId());
		ct.makeMeasurement(invid, "DecNr", stringUnitId, null, null, false, "string", "To set a DEC application's DEC number.", login.getUserId());
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
		ct.makeMeasurement(invid, "GeneName", stringUnitId, null, null, false, "string", "The name of a gene that may or may not be present in an animal.", login.getUserId());
		ct.makeMeasurement(invid, "GeneState", stringUnitId, null, null, false, "string", "To indicate whether an animal is homo- or heterozygous for a gene.", login.getUserId());
		ct.makeMeasurement(invid, "VWASpecies", stringUnitId, null, null, false, "string", "To give a species the name the VWA uses for it.", login.getUserId());
		ct.makeMeasurement(invid, "LatinSpecies", stringUnitId, null, null, false, "string", "To give a species its scientific (Latin) name.", login.getUserId());
		ct.makeMeasurement(invid, "StartDate", datetimeUnitId, null, null, true, "datetime", "To set a (sub)project's start date.", login.getUserId());
		ct.makeMeasurement(invid, "EndDate", datetimeUnitId, null, null, true, "datetime", "To set a (sub)project's end date.", login.getUserId());
		ct.makeMeasurement(invid, "Removal", stringUnitId, null, null, false, "string", "To register an animal's removal.", login.getUserId());
		ct.makeMeasurement(invid, "Article", numberUnitId, null, null, false, "int", "To set an actor's Article status according to the Law, e.g. Article 9.", login.getUserId());
		ct.makeMeasurement(invid, "MolgenisUserId", numberUnitId, null, null, false, "int", "To set an actor's corresponding MolgenisUser ID.", login.getUserId());
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
		ct.makeMeasurement(invid, "OldUliDbKuerzel", stringUnitId, null, null, false, "string", "To set an animal's 'Kürzel' in the old Uli Eisel DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldUliDbAktenzeichen", stringUnitId, null, null, false, "string", "To set an animal's 'Aktenzeichen' in the old Uli Eisel DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldUliDbExperimentator", stringUnitId, null, null, false, "string", "To set an animal's experimenter in the old Uli Eisel DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldUliDbTierschutzrecht", stringUnitId, null, null, false, "string", "To set an animal's 'Tierschutzrecht' (~ DEC subproject Goal) in the old Uli Eisel DB.", login.getUserId());
		ct.makeMeasurement(invid, "Color", stringUnitId, null, null, false, "string", "To set an animal's color.", login.getUserId());
		ct.makeMeasurement(invid, "Earmark", stringUnitId, null, null, false, "string", "To set an animal's earmark.", login.getUserId());
		ct.makeMeasurement(invid, "OldUliDbMotherInfo", stringUnitId, null, null, false, "string", "To set an animal's mother info in the old Uli Eisel DB.", login.getUserId());
		ct.makeMeasurement(invid, "OldUliDbFatherInfo", stringUnitId, null, null, false, "string", "To set an animal's father info in the old Uli Eisel DB.", login.getUserId());
		
		logger.info("Add codes");
		// Codes for SourceType
		ct.makeCode("1-1", "Eigen fok binnen uw organisatorische werkeenheid", "SourceType");
		ct.makeCode("1-2", "Andere organisatorische werkeenheid vd instelling", "SourceType");
		ct.makeCode("1-3", "Geregistreerde fok/aflevering in Nederland", "SourceType");
		ct.makeCode("2", "Van EU-lid-staten", "SourceType");
		ct.makeCode("3", "Niet-geregistreerde fok/afl in Nederland", "SourceType");
		ct.makeCode("4", "Niet-geregistreerde fok/afl in andere EU-lid-staat", "SourceType");
		ct.makeCode("5", "Andere herkomst", "SourceType");
		// Codes for SourceTypeSubproject
		ct.makeCode("1", "Geregistreerde fok/aflevering in Nederland", "SourceTypeSubproject");
		ct.makeCode("2", "Van EU-lid-staten", "SourceTypeSubproject");
		ct.makeCode("3", "Niet-geregistreerde fok/afl in Nederland", "SourceTypeSubproject");
		ct.makeCode("4", "Niet-geregistreerde fok/afl in andere EU-lid-staat", "SourceTypeSubproject");
		ct.makeCode("5", "Andere herkomst", "SourceTypeSubproject");
		ct.makeCode("6", "Hergebruik eerste maal in het registratiejaar", "SourceTypeSubproject");
		ct.makeCode("7", "Hergebruik tweede, derde enz. maal in het registratiejaar", "SourceTypeSubproject");
		// Codes for ParticipantGroup
		ct.makeCode("04", "Chrono- en gedragsbiologie", "ParticipantGroup");
		ct.makeCode("06", "Plantenbiologie", "ParticipantGroup");
		ct.makeCode("07", "Dierfysiologie", "ParticipantGroup");
		ct.makeCode("Klinische Farmacologie (no code yet)", "Klinische Farmacologie", "ParticipantGroup");
		// Codes for Anaestheasia
		ct.makeCode("1", "A. Is niet toegepast (geen aanleiding)", "Anaesthesia");
		ct.makeCode("2", "B. Is niet toegepast (onverenigbaar proef)", "Anaesthesia");
		ct.makeCode("3", "C. Is niet toegepast (praktisch onuitvoerbaar)", "Anaesthesia");
		ct.makeCode("4", "D. Is wel toegepast", "Anaesthesia");
		// Codes for PainManagement
		ct.makeCode("1", "A. Is niet toegepast (geen aanleiding)", "PainManagement");
		ct.makeCode("2", "B. Is niet toegepast (onverenigbaar proef)", "PainManagement");
		ct.makeCode("3", "C. Is niet toegepast (praktisch onuitvoerbaar)", "PainManagement");
		ct.makeCode("4", "D. Is wel toegepast", "PainManagement");
		// Codes for AnimalEndStatus
		ct.makeCode("1", "A. Dood in het kader van de proef", "AnimalEndStatus");
		ct.makeCode("2", "B. Gedood na beeindiging van de proef", "AnimalEndStatus");
		ct.makeCode("3", "C. Na einde proef in leven gelaten", "AnimalEndStatus");
		// Codes for ExpectedAnimalEndStatus
		ct.makeCode("1", "A. Dood in het kader van de proef", "ExpectedAnimalEndStatus");
		ct.makeCode("2", "B. Gedood na beeindiging van de proef", "ExpectedAnimalEndStatus");
		ct.makeCode("3", "C. Na einde proef in leven gelaten", "ExpectedAnimalEndStatus");
		// Codes for ActualAnimalEndStatus
		ct.makeCode("1", "A. Dood in het kader van de proef", "ActualAnimalEndStatus");
		ct.makeCode("2", "B. Gedood na beeindiging van de proef", "ActualAnimalEndStatus");
		ct.makeCode("3", "C. Na einde proef in leven gelaten", "ActualAnimalEndStatus");
		// Codes for LawDef
		ct.makeCode("1", "A. Geen wettelijke bepaling", "LawDef");
		ct.makeCode("2", "B. Uitsluitend Nederland", "LawDef");
		ct.makeCode("3", "C. Uitsluitend EU-lidstaten", "LawDef");
		ct.makeCode("4", "D. Uitsluitend Lidstaten Raad v. Eur.", "LawDef");
		ct.makeCode("5", "E. Uitsluitend Europese landen", "LawDef");
		ct.makeCode("6", "F. Ander wettelijke bepaling", "LawDef");
		ct.makeCode("7", "G. Combinatie van B. C. D. E. en F", "LawDef");
		// Codes for ToxRes
		ct.makeCode("01", "A. Geen toxicologisch onderzoek", "ToxRes");
		ct.makeCode("02", "B. Acuut tox. met letaliteit", "ToxRes");
		ct.makeCode("03", "C. Acuut tox. LD50/LC50", "ToxRes");
		ct.makeCode("04", "D. Overige acuut tox. (geen letaliteit)", "ToxRes");
		ct.makeCode("05", "E. Sub-acuut tox.", "ToxRes");
		ct.makeCode("06", "F. Sub-chronisch en chronische tox.", "ToxRes");
		ct.makeCode("07", "G. Carcinogeniteitsonderzoek", "ToxRes");
		ct.makeCode("08", "H. Mutageniteitsonderzoek", "ToxRes");
		ct.makeCode("09", "I. Teratogeniteitsonderz. (segment II)", "ToxRes");
		ct.makeCode("10", "J. Reproductie-onderzoek (segment 1 en III)", "ToxRes");
		ct.makeCode("11", "K. Overige toxiciteitsonderzoek", "ToxRes");
		// Codes for SpecialTechn
		ct.makeCode("01", "A. Geen van deze technieken/ingrepen", "SpecialTechn");
		ct.makeCode("02", "B. Doden zonder voorafgaande handelingen", "SpecialTechn");
		ct.makeCode("03", "C. Curare-achtige stoffen zonder anesthesie", "SpecialTechn");
		ct.makeCode("04", "D. Technieken/ingrepen verkrijgen transgene dieren", "SpecialTechn");
		ct.makeCode("05", "E. Toedienen van mogelijk irriterende stoffen via luchtwegen", "SpecialTechn");
		ct.makeCode("06", "E. Toedienen van mogelijk irriterende stoffen op het oog", "SpecialTechn");
		ct.makeCode("07", "E. Toedienen van mogelijk irriterende stoffen op andere slijmvliezen of huid", "SpecialTechn");
		ct.makeCode("08", "F. Huidsensibilisaties", "SpecialTechn");
		ct.makeCode("09", "G. Bestraling, met schadelijke effecten", "SpecialTechn");
		ct.makeCode("10", "H. Traumatiserende fysische/chemische prikkels (CZ)", "SpecialTechn");
		ct.makeCode("11", "I. Traumatiserende psychische prikkels", "SpecialTechn");
		ct.makeCode("12", "J. Technieken/ingrepen anders dan C t/m H, gericht: opwekken van ontstekingen/infecties", "SpecialTechn");
		ct.makeCode("13", "J. Technieken/ingrepen anders dan C t/m H, gericht: opwekken van verbrand./fract. of letsel (traum.)", "SpecialTechn");
		ct.makeCode("14", "J. Technieken/ingrepen anders dan C t/m H, gericht: opwekken van poly- en monoclonale antistoffen", "SpecialTechn");
		ct.makeCode("15", "J. Technieken/ingrepen anders dan C t/m H, gericht: produceren van monoclonale antistoffen", "SpecialTechn");
		ct.makeCode("16", "K. Meer dan een onder G t/m J vermelde mogelijkheden", "SpecialTechn");
		ct.makeCode("17", "L. Gefokt met ongerief", "SpecialTechn");
		// Codes for Concern
		ct.makeCode("1", "A. Gezondheid/voed. ja", "Concern");
		ct.makeCode("2", "B. Gezondheid/voed. nee", "Concern");
		// Codes for Goal
		ct.makeCode("1", "A. Onderzoek m.b.t. de mens: ontw. sera vaccins/biol.produkten", "Goal");
		ct.makeCode("2", "A. Onderzoek m.b.t. de mens: prod./contr./ijking sera vaccins/biol. producten", "Goal");
		ct.makeCode("3", "A. Onderzoek m.b.t. de mens: ontw. geneesmiddelen", "Goal");
		ct.makeCode("4", "A. Onderzoek m.b.t. de mens: prod./contr./ijking geneesmiddelen", "Goal");
		ct.makeCode("5", "A. Onderzoek m.b.t. de mens: ontw. med. hulpmiddelen/ toepassingen", "Goal");
		ct.makeCode("6", "A. Onderzoek m.b.t. de mens: prod./contr./ijking med.hulpm./toepassingen", "Goal");
		ct.makeCode("7", "A. Onderzoek m.b.t. de mens: and. ijkingen", "Goal");
		ct.makeCode("8", "A. Onderzoek m.b.t. het dier: ontw. sera vaccins/biol.produkten", "Goal");
		ct.makeCode("9", "A. Onderzoek m.b.t. het dier: prod./contr./ijking sera vaccins/biol. producten", "Goal");
		ct.makeCode("10", "A. Onderzoek m.b.t. het dier: ontw. geneesmiddelen", "Goal");
		ct.makeCode("11", "A. Onderzoek m.b.t. het dier: prod./contr./ijking geneesmiddelen", "Goal");
		ct.makeCode("12", "A. Onderzoek m.b.t. het dier: ontw. med. hulpmiddelen/ toepassingen", "Goal");
		ct.makeCode("13", "A. Onderzoek m.b.t. het dier: prod./contr./ijking med.hulpm./toepassingen", "Goal");
		ct.makeCode("14", "A. Onderzoek m.b.t. het dier: and. ijkingen", "Goal");
		ct.makeCode("15", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: agrarische sector", "Goal");
		ct.makeCode("16", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: industrie", "Goal");
		ct.makeCode("17", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: huishouden", "Goal");
		ct.makeCode("18", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: cosm./toiletartikelen", "Goal");
		ct.makeCode("19", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: voed.midd.mens.cons.", "Goal");
		ct.makeCode("20", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: voed.midd.dier.cons.", "Goal");
		ct.makeCode("21", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: tabak/and.rookwaren", "Goal");
		ct.makeCode("22", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: stoffen schad.voor milieu", "Goal");
		ct.makeCode("23", "B. Onderzoek m.b.t. schadelijkheid van stoffen voor: ander", "Goal");
		ct.makeCode("24", "C. Opsporen van/ uivoeren van diagnostiek: ziekten bij mensen", "Goal");
		ct.makeCode("25", "C. Opsporen van/ uivoeren van diagnostiek: and.lich.kenmerken bij mensen", "Goal");
		ct.makeCode("26", "C. Opsporen van/ uivoeren van diagnostiek: ziekten bij dieren", "Goal");
		ct.makeCode("27", "C. Opsporen van/ uivoeren van diagnostiek: and. lich.kenmerken bij dieren", "Goal");
		ct.makeCode("28", "C. Opsporen van/ uivoeren van diagnostiek: ziekten/kenmerken bij planten", "Goal");
		ct.makeCode("29", "D. Onderwijs/Training", "Goal");
		ct.makeCode("30", "E. Wetensch.vraag m.b.t.: kanker (excl.carcinogene stoffen) bij mensen", "Goal");
		ct.makeCode("31", "E. Wetensch.vraag m.b.t.: hart-en vaatziekten bij mensen", "Goal");
		ct.makeCode("32", "E. Wetensch.vraag m.b.t.: geestesz./zenuwz.  bij mensen", "Goal");
		ct.makeCode("33", "E. Wetensch.vraag m.b.t.: and. ziekten bij mensen", "Goal");
		ct.makeCode("34", "E. Wetensch.vraag m.b.t.: and. lich. kenmerken bij mensen", "Goal");
		ct.makeCode("35", "E. Wetensch.vraag m.b.t.: gedrag van dieren", "Goal");
		ct.makeCode("36", "E. Wetensch.vraag m.b.t.: ziekten bij dieren", "Goal");
		ct.makeCode("37", "E. Wetensch.vraag m.b.t.: and. wetenschappelijke vraag", "Goal");
		// Codes for ExpectedDiscomfort
		ct.makeCode("1", "A. Gering", "ExpectedDiscomfort");
		ct.makeCode("2", "B. Gering/matig", "ExpectedDiscomfort");
		ct.makeCode("3", "C. Matig", "ExpectedDiscomfort");
		ct.makeCode("4", "D. Matig/ernstig", "ExpectedDiscomfort");
		ct.makeCode("5", "E. Ernstig", "ExpectedDiscomfort");
		ct.makeCode("6", "F. Zeer ernstig", "ExpectedDiscomfort");
		// Codes for ActualDiscomfort
		ct.makeCode("1", "A. Gering", "ActualDiscomfort");
		ct.makeCode("2", "B. Gering/matig", "ActualDiscomfort");
		ct.makeCode("3", "C. Matig", "ActualDiscomfort");
		ct.makeCode("4", "D. Matig/ernstig", "ActualDiscomfort");
		ct.makeCode("5", "E. Ernstig", "ActualDiscomfort");
		ct.makeCode("6", "F. Zeer ernstig", "ActualDiscomfort");
		// Codes for AnimalType
		ct.makeCode("1", "A. Gewoon dier", "AnimalType");
		ct.makeCode("2", "B. Transgeen dier", "AnimalType");
		ct.makeCode("3", "C. Wildvang", "AnimalType");
		ct.makeCode("4", "D. Biotoop", "AnimalType");
		// Codes for GeneName
		ct.makeCode("Cry1", "Cry1", "GeneName");
		ct.makeCode("Cry2", "Cry2", "GeneName");
		ct.makeCode("Per1", "Per1", "GeneName");
		ct.makeCode("Per2", "Per2", "GeneName");
		// Codes for GeneState
		ct.makeCode("0", "-/-", "GeneState");
		ct.makeCode("1", "+/-", "GeneState");
		ct.makeCode("2", "+/+", "GeneState");
		ct.makeCode("3", "ntg", "GeneState");
		ct.makeCode("4", "WT", "GeneState");
		ct.makeCode("5", "-/- -/-", "GeneState");
		ct.makeCode("6", "-/- +/-", "GeneState");
		ct.makeCode("7", "-/- +/+", "GeneState");
		ct.makeCode("8", "-/- ntg", "GeneState");
		ct.makeCode("9", "+/- -/-", "GeneState");
		ct.makeCode("10", "+/- +/-", "GeneState");
		ct.makeCode("11", "+/- +/+", "GeneState");
		ct.makeCode("12", "+/- ntg", "GeneState");
		ct.makeCode("13", "+/+ -/-", "GeneState");
		ct.makeCode("14", "+/+ +/-", "GeneState");
		ct.makeCode("15", "+/+ +/+", "GeneState");
		ct.makeCode("16", "+/+ ntg", "GeneState");
		ct.makeCode("17", "ntg ntg", "GeneState");
		ct.makeCode("18", "ntg -/-", "GeneState");
		ct.makeCode("19", "ntg +/-", "GeneState");
		ct.makeCode("20", "ntg +/+", "GeneState");
		ct.makeCode("21", "unknown", "GeneState");
		ct.makeCode("22", "transgenic", "GeneState");
		// Codes for VWASpecies
		ct.makeCode("01", "Muizen", "VWASpecies");
		ct.makeCode("02", "Ratten", "VWASpecies");
		ct.makeCode("03", "Hamsters", "VWASpecies");
		ct.makeCode("04", "Cavia's", "VWASpecies");
		ct.makeCode("09", "And. Knaagdieren", "VWASpecies");
		ct.makeCode("11", "Konijnen", "VWASpecies");
		ct.makeCode("21", "Honden", "VWASpecies");
		ct.makeCode("22", "Katten", "VWASpecies");
		ct.makeCode("23", "Fretten", "VWASpecies");
		ct.makeCode("29", "And. Vleeseters", "VWASpecies");
		ct.makeCode("31", "Prosimians", "VWASpecies");
		ct.makeCode("32", "Nieuwe wereld apen", "VWASpecies");
		ct.makeCode("33", "Oude wereld apen", "VWASpecies");
		ct.makeCode("34", "Mensapen", "VWASpecies");
		ct.makeCode("41", "Paarden", "VWASpecies");
		ct.makeCode("42", "Varkens", "VWASpecies");
		ct.makeCode("43", "Geiten", "VWASpecies");
		ct.makeCode("44", "Schapen", "VWASpecies");
		ct.makeCode("45", "Runderen", "VWASpecies");
		ct.makeCode("49", "And. Zoogdieren", "VWASpecies");
		ct.makeCode("51", "Kippen", "VWASpecies");
		ct.makeCode("52", "Kwartels", "VWASpecies");
		ct.makeCode("59", "And.Vogels", "VWASpecies");
		ct.makeCode("69", "Reptielen", "VWASpecies");
		ct.makeCode("79", "Amfibieen", "VWASpecies");
		ct.makeCode("89", "Vissen", "VWASpecies");
		ct.makeCode("91", "Cyclostoma", "VWASpecies");
		// Codes for Removal
		ct.makeCode("0", "dood", "Removal");
		ct.makeCode("1", "levend afgevoerd andere organisatorische eenheid RuG", "Removal");
		ct.makeCode("2", "levend afgevoerd gereg. onderzoeksinstelling NL", "Removal");
		ct.makeCode("3", "levend afgevoerd gereg. onderzoeksinstelling EU", "Removal");
		ct.makeCode("4", "levend afgevoerd andere bestemming", "Removal");
		// Codes for Earmark
		ct.makeCode("1 l", "one left", "Earmark");
		ct.makeCode("1 r", "one right", "Earmark");
		ct.makeCode("1 r 1 l", "one right, one left", "Earmark");
		ct.makeCode("1 r 2 l", "one right, two left", "Earmark");
		ct.makeCode("2 l", "two left", "Earmark");
		ct.makeCode("2 l 1 r", "two left, one right", "Earmark");
		ct.makeCode("2 r", "two right", "Earmark");
		ct.makeCode("2 r 1 l", "two right, one left", "Earmark");
		ct.makeCode("2 r 2 l", "two right, two left", "Earmark");
		ct.makeCode("O", "none", "Earmark");
		ct.makeCode("x", "", "Earmark");
		
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
		ct.makeProtocol(invid, "SetLitterSpecs", "To set the specifications of a litter.", featureIdList);
		// Protocol SetAddress
		featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("Street"));
		featureIdList.add(ct.getMeasurementId("Housenumber"));
		featureIdList.add(ct.getMeasurementId("City"));
		ct.makeProtocol(invid, "SetAddress", "To set an address.", featureIdList);
		// Protocol SetDecProjectSpecs
		featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("DecNr"));
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
		featureIdList.add(ct.getMeasurementId("GeneName"));
		featureIdList.add(ct.getMeasurementId("GeneState"));
		ct.makeProtocol(invid, "SetGenotype", "To set part (one gene) of an animal's genotype.", featureIdList);
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
		int vwaMeasurementId = ct.getMeasurementId("VWASpecies");
		int latinMeasurementId = ct.getMeasurementId("LatinSpecies");
		groupId = ct.makePanel(invid, "Syrian hamster", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "Hamsters", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Mesocricetus auratus", 0));
		groupId = ct.makePanel(invid, "European groundsquirrel", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "And. knaagdieren", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Spermophilus citellus", 0));
		groupId = ct.makePanel(invid, "House mouse", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "Muizen", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Mus musculus", 0));
		groupId = ct.makePanel(invid, "Siberian hamster", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "Hamsters", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Phodopus sungorus", 0));
		groupId = ct.makePanel(invid, "Gray mouse lemur", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "Prosimians", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Microcebus murinus", 0));
		groupId = ct.makePanel(invid, "Brown rat", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Species", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				vwaProtocolId, vwaMeasurementId, groupId, "Ratten", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				latinProtocolId, latinMeasurementId, groupId, "Rattus norvegicus", 0));
		
		// Groups -> Background
		groupId = ct.makePanel(invid, "CD1", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Background", 0));
		groupId = ct.makePanel(invid, "C57black6J", login.getUserId());
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
		groupId = ct.makePanel(invid, "UliEisel51and52", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Van EU-lid-staten", 0));
		groupId = ct.makePanel(invid, "UliEisel55", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Andere herkomst", 0));
		groupId = ct.makePanel(invid, "Kweek moleculaire neurobiologie", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Eigen fok binnen uw organisatorische werkeenheid", 0));
		// Sources for demo purposes:
		groupId = ct.makePanel(invid, "Max-Planck-Institut fuer Verhaltensfysiologie", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Van EU-lid-staten", 0));
		groupId = ct.makePanel(invid, "Unknown source UK", login.getUserId());
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				protocolId, measurementId, groupId, "Source", 0));
		valuesToAddList.add(ct.createObservedValueWithProtocolApplication(invid, now, null, 
				sourceProtocolId, sourceMeasurementId, groupId, "Niet-geregistreerde fok/afl in andere EU-lid-staat", 0));
		
		// Add everything to DB
		db.add(valuesToAddList);
		
		logger.info("AnimalDB database updated successfully!");
		
		login.logout();
		login.login(db, "anonymous", "anonymous");
	}
}
