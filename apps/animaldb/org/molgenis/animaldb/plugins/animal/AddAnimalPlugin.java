/*
 * Date: December 24, 2010 Template: PluginScreenJavaTemplateGen.java.ftl
 * generator: org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.animal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.SelectMultipleInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.TextLineInput;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Location;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Tuple;


public class AddAnimalPlugin extends GenericPlugin
{
	private static final long serialVersionUID = -4185405160313262242L;
	private CommonService ct = CommonService.getInstance();
	// Screen components:
	public StringInput researcher = null;
	public SelectInput species = null;
	public SelectInput sex = null;
	public SelectInput animaltype = null;
	public SelectInput source = null;
	public SelectInput background = null;
	public SelectMultipleInput gene = null;
	public List<SelectInput> genestateList = null;
	public DateInput birthdate = null;
	public DateInput entrydate = null;
	public SelectInput namebase = null;
	public TextLineInput<String> startnumberhelper = null;
	public StringInput newnamebase = null;
	public IntInput startnumber = null;
	public IntInput numberofmales = null;
	public IntInput numberoffemales = null;
	public IntInput numberofunknowns = null;
	public SelectInput actor = null;
	public SelectInput location = null;
	public DivPanel containingPanel = null;
	// Variables for holding form values between wizard steps:
	private int speciesId = -1;
	private int backgroundId = -1;
	private int sourceId = -1;
	private int locId = -1;
	private String birthDate = null;
	private Date entryDate = null;
	private String animalType = null;
	private String resResearcher = null;
	private List<String> genes = null;
	private List<String> genestates = null;

	public AddAnimalPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">\n" +
               "<script src=\"res/scripts/custom/addanimals.js\" type=\"text/javascript\" language=\"javascript\"></script>\n" +
               "<script src=\"res/jquery-plugins/multiselect/js/ui.multiselect.js\" type=\"text/javascript\" language=\"javascript\"></script>\n" +
               "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/multiselect/css/ui.multiselect.css\">\n";
               //"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/multiselect/css/common.css\">\n";
        	   // Moved the stuff that's really needed from common.css to ui.multiselect.css
    }

	@Override
	public void reload(Database db)
	{
		try
		{
			ct.setDatabase(db);
			ct.makeObservationTargetNameMap(this.getLogin().getUserName(), false);
			if (speciesId == -1) {
				resetAllFormValues();
				populateFirstTablePanel(db);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			String message = "Something went wrong while reloading";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.setError(message);
		}
	}

	private void resetAllFormValues() {
		backgroundId = -1;
		sourceId = -1;
		locId = -1;
		birthDate = null;
		entryDate = null;
		animalType = null;
		resResearcher = null;
		genes = null;
		genestates = null;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		ct.setDatabase(db);
		try {
			String action = request.getAction();
			containingPanel.setValuesFromRequest(request);
			
			if (action.equals("Cancel")) {
				resetAllFields();
			}
			if (action.equals("Prev1")) {
				populateFirstTablePanel(db);
			}
			if (action.equals("Cont1")) {
				handleFirstScreenRequest(db, request);
				populateSecondTablePanel(db);
			}
			if (action.equals("Prev2")) {
				populateSecondTablePanel(db);
			}
			if (action.equals("Cont2")) {
				handleSecondScreenRequest(db, request);
				if (animalType.equals("B. Transgeen dier") && genes != null) {
					populateThirdTablePanel(db);
				} else {
					// Skip third screen if animals are non-GMO or if no genes selected
					populateFourthTablePanel(db);
				}
			}
			if (action.equals("Prev3")) {
				if (animalType.equals("B. Transgeen dier") && genes != null) {
					populateThirdTablePanel(db);
				} else {
					// Skip third screen if animals are non-GMO or if no genes selected
					populateSecondTablePanel(db);
				}
			}
			if (action.equals("Cont3")) {
				handleThirdScreenRequest(db, request);
				populateFourthTablePanel(db);
			}
			if (action.equals("Save")) {
				handleAddRequest(db, request);
				resetAllFields(); 
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.setError("Error: " + e.getMessage());
			}
		}
	}
	
	private void resetAllFields() {
		speciesId = -1; // also trigger for reload() to render first screen again
		backgroundId = -1;
		sourceId = -1;
		locId = -1;
		birthDate = null;
		entryDate = null;
		animalType = null;
		resResearcher = null;
		genes = null;
		genestates = null;
	}
	
	private void handleFirstScreenRequest(Database db, Tuple request) throws Exception {
		if (species.getObject() != null) {
			speciesId = Integer.parseInt(species.getObject().toString());
		} else {
			this.setError("No species given - animal(s) not added");
		}
		if (animaltype.getObject() != null) {
			animalType = animaltype.getObject().toString();
		} else {
			throw(new Exception("No animal type given - animal(s) not added"));
		}
		if (source.getObject() != null) {
			sourceId = Integer.parseInt(source.getObject().toString());
		} else {
			throw(new Exception("No source given - animal(s) not added"));
		}
		SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
		// Birth date (String)
		if (!birthdate.getValue().equals("")) {
			birthDate = birthdate.getValue();
		}
		// Entry date (Date)
		if (!entrydate.getValue().equals("")) {
			String entryDateString = entrydate.getValue();
			entryDate = dateOnlyFormat.parse(entryDateString);
		} else {
			throw(new Exception("No entry date given - animal(s) not added"));
		}
		// Researcher
		if (researcher.getObject() != null) {
			resResearcher = researcher.getValue();
		}
		// Location
		if (location.getObject() != null) {
			locId = Integer.parseInt(location.getObject().toString());
		}
	}
	
	private void handleSecondScreenRequest(Database db, Tuple request) throws Exception {
		
		if (background.getObject() != null) {
			backgroundId = Integer.parseInt(background.getObject().toString());
		} else {
			throw(new Exception("No background given - animal(s) not added"));
		}
		
		if (animalType.equals("B. Transgeen dier")) {
			genes = gene.getObject();
		}
	}
	
	private void handleThirdScreenRequest(Database db, Tuple request) throws Exception {
		
		genestates = new ArrayList<String>();
		for (SelectInput genestateBox : genestateList) {
			genestates.add((String) genestateBox.getObject());
		}
	}
	
	private void handleAddRequest(Database db, Tuple request) throws Exception {
		
		ct.setDatabase(db);
			
		// Get name from last form
		String nameBase = "";
		int startNumber = -1;
		if (namebase.getObject() != null) {
			nameBase = namebase.getObject().toString();
			if (nameBase.equals("New")) {
				if (newnamebase.getObject() != null) {
					nameBase = newnamebase.getObject().toString();
				} else {
					nameBase = "";
				}
			}
		} else {
			 nameBase = "";
		}
		if (startnumber.getObject() != null) {
			// TODO: Find out why HtmlInput<E>'s getObject() returns a String object and not an
			// Integer one, as expected!
			startNumber = Integer.parseInt(startnumber.getValue());
		} else {
			startNumber = 1; // standard start at 1
		}
		// Get numbers of animals from last form
		int nrOfMales = 0;
		int nrOfFemales = 0;
		int nrOfUnknowns = 0;
		if (numberofmales.getObject() != null) {
			nrOfMales = Integer.parseInt(numberofmales.getValue());
		}
		if (numberoffemales.getObject() != null) {
			nrOfFemales = Integer.parseInt(numberoffemales.getValue());
		}
		if (numberofunknowns.getObject() != null) {
			nrOfUnknowns = Integer.parseInt(numberofunknowns.getValue());
		}
		int nrOfAnimals = nrOfMales + nrOfFemales + nrOfUnknowns;
		if (nrOfAnimals == 0) {
			throw(new Exception("No number(s) given - animal(s) not added"));
		}
		
		// Investigation
		int userId = this.getLogin().getUserId();
		int invid = ct.getOwnUserInvestigationIds(userId).get(0);
		
		// Init lists that we can later add to the DB at once
		List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
		List<Individual> animalsToAddList = new ArrayList<Individual>();
		List<ProtocolApplication> appsToAddList = new ArrayList<ProtocolApplication>();
		
		// Make all animals
		for (int i = 0; i < nrOfAnimals; i++) {
			// Make and add animal
			String nrPart = "" + (startNumber + i);
			nrPart = ct.prependZeros(nrPart, 6);
			Individual newAnimal = ct.createIndividual(invid, nameBase + nrPart, 
					this.getLogin().getUserId());
			animalsToAddList.add(newAnimal);
		}
		db.add(animalsToAddList);
		
		// Make or update name prefix entry
		ct.updatePrefix("animal", nameBase, startNumber + nrOfAnimals - 1);
		
		// Make all protocol applications
		List<Integer> protocolIdList = new ArrayList<Integer>();
		protocolIdList.add(ct.getProtocolId("SetActive"));
		protocolIdList.add(ct.getProtocolId("SetSpecies"));
		protocolIdList.add(ct.getProtocolId("SetSex"));
		protocolIdList.add(ct.getProtocolId("SetAnimalType"));
		protocolIdList.add(ct.getProtocolId("SetSource"));
		protocolIdList.add(ct.getProtocolId("SetBackground"));
		protocolIdList.add(ct.getProtocolId("SetGenotype"));
		protocolIdList.add(ct.getProtocolId("SetDateOfBirth"));
		protocolIdList.add(ct.getProtocolId("SetResponsibleResearcher"));
		protocolIdList.add(ct.getProtocolId("SetLocation"));
		for (int j = 0; j < 10; j++) {
			ProtocolApplication newApp = ct.createProtocolApplication(invid, protocolIdList.get(j));
			appsToAddList.add(newApp);
		}
		db.add(appsToAddList);
		// Get all measurements
		List<Integer> featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("Active"));
		featureIdList.add(ct.getMeasurementId("Species"));
		featureIdList.add(ct.getMeasurementId("Sex"));
		featureIdList.add(ct.getMeasurementId("AnimalType"));
		featureIdList.add(ct.getMeasurementId("Source"));
		featureIdList.add(ct.getMeasurementId("Background"));
		featureIdList.add(ct.getMeasurementId("GeneModification"));
		featureIdList.add(ct.getMeasurementId("GeneState"));
		featureIdList.add(ct.getMeasurementId("DateOfBirth"));
		featureIdList.add(ct.getMeasurementId("ResponsibleResearcher"));
		featureIdList.add(ct.getMeasurementId("Location"));
		// Get ID's for sexes
		int maleId = ct.getObservationTargetId("Male");
		int femaleId = ct.getObservationTargetId("Female");
		int unknownId = ct.getObservationTargetId("UnknownSex");
		// Make all values
		int animalCnt = 0;
		for (Individual animal : animalsToAddList) {
			int animalid = animal.getId();
			// Set Active, with (start)time = entrydate and endtime = null
			ProtocolApplication app = appsToAddList.get(0);
	 		valuesToAddList.add(ct.createObservedValue(invid, app.getId(), entryDate, null, 
	 				featureIdList.get(0), animalid, "Alive", 0));
			// Set species
	 		app = appsToAddList.get(1);
	 		valuesToAddList.add(ct.createObservedValue(invid, app.getId(), entryDate, null, 
	 				featureIdList.get(1), animalid, null, speciesId));
			// Set sex
	 		int sexId;
	 		if (animalCnt < nrOfMales) {
	 			sexId = maleId;
	 		} else if (animalCnt < nrOfMales + nrOfFemales) {
	 			sexId = femaleId;
	 		} else {
	 			sexId = unknownId;
	 		}
	 		app = appsToAddList.get(2);
	 		valuesToAddList.add(ct.createObservedValue(invid, app.getId(), entryDate, null, 
	 				featureIdList.get(2), animalid, null, sexId));
			// Set animaltype
	 		app = appsToAddList.get(3);
			valuesToAddList.add(ct.createObservedValue(invid, app.getId(), entryDate,  null, 
					featureIdList.get(3), animalid, animalType, 0));
			// Set source
			app = appsToAddList.get(4);
	 		valuesToAddList.add(ct.createObservedValue(invid, app.getId(), entryDate, null, 
	 				featureIdList.get(4), animalid, null, sourceId));
			// Set background
			if (backgroundId > 0) {
				app = appsToAddList.get(5);
				valuesToAddList.add(ct.createObservedValue(invid, app.getId(), entryDate, null, 
						featureIdList.get(5), animalid, null, backgroundId));
			}
			// Set genotype(s)
			int index = 0;
			if (genes != null) {
				for (String gene : genes) {
					String geneState = genestates.get(index);
					// Make protocol application
					app = appsToAddList.get(6);
					valuesToAddList.add(ct.createObservedValue(invid, app.getId(), entryDate, null, 
							featureIdList.get(6), animalid, gene, 0));
					valuesToAddList.add(ct.createObservedValue(invid, app.getId(), entryDate, null, 
							featureIdList.get(7), animalid, geneState, 0));
					index++;
				}
			}
			// Set birthdate
			if (birthDate != null) {
				app = appsToAddList.get(7);
				valuesToAddList.add(ct.createObservedValue(invid, app.getId(), entryDate, null, 
						featureIdList.get(8), animalid, birthDate, 0));
			}
			// Set responsible researcher
			if (resResearcher != null) {
				app = appsToAddList.get(8);
				valuesToAddList.add(ct.createObservedValue(invid, app.getId(), entryDate, null, 
						featureIdList.get(9), animalid, resResearcher, 0));
			}
			// Set location
			if (locId != -1) {
				app = appsToAddList.get(9);
				valuesToAddList.add(ct.createObservedValue(invid, app.getId(), entryDate, null, 
						featureIdList.get(10), animalid, null, locId));
			}
			animalCnt++;
		}	
		db.add(valuesToAddList);
		
		// Update custom label map now new animals have been added
		ct.makeObservationTargetNameMap(this.getLogin().getUserName(), true);
		
		this.setSuccess(animalsToAddList.size() + " animal(s) successfully added");
	}
	
	private void populateFirstTablePanel(Database db) throws DatabaseException, ParseException {
		
		ct.setDatabase(db);
		List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserName());
		SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
		
		// panel for all elements
		containingPanel = new DivPanel(this.getName() + "panel", "");
		
		// Populate animal species list
		species = new SelectInput("species");
		species.setLabel("Species:");
		species.addOption("","");
		for (ObservationTarget s : ct.getAllMarkedPanels("Species", investigationIds)) {
			species.addOption(s.getId(), s.getName());
		}
		species.setNillable(false);
		species.setDescription("Give the species.");
		species.setTooltip("Give the species.");
		species.setId("species");
		species.setOnchange("updateNamePrefixBox();");
		if (speciesId != -1) {
			species.setValue(speciesId);
		}

		// Populate source list
		// All source types except "Eigen fok binnen uw organisatorische werkeenheid",
		// which is taken care of in the Breeding Module
		source = new SelectInput("source");
		source.setLabel("Source:");
		source.addOption("","");
		List<ObservationTarget> tmpSourceList = ct.getAllMarkedPanels("Source", investigationIds);
		for (ObservationTarget tmpSource : tmpSourceList)
		{
			List<ObservedValue> sourceTypeValueList = db.query(ObservedValue.class).
					eq(ObservedValue.TARGET, tmpSource.getId()).eq(ObservedValue.FEATURE_NAME, "SourceType").find();
			if (sourceTypeValueList.size() > 0) {
				String sourcetype = sourceTypeValueList.get(0).getValue();
				if (!sourcetype.equals("Eigen fok binnen uw organisatorische werkeenheid")) {
					source.addOption(tmpSource.getId(), tmpSource.getName());
				}
			}
		}
		source.setDescription("Give the source from which the new animal(s) originate(s).");
		source.setTooltip("Give the source from which the new animal(s) originate(s).");
		source.setNillable(false);
		if (sourceId != -1) {
			source.setValue(sourceId);
		}
		
		// Populate animal type list
		animaltype = new SelectInput("animaltype");
		animaltype.setLabel("Animal type:");
		animaltype.addOption("","");
		for (Category c : ct.getAllCodesForFeature("AnimalType")) {
			animaltype.addOption(c.getDescription(), c.getDescription());
		}
		animaltype.setDescription("Give the type of the new animal(s). Select type 2 (GMO) to modify the genotype of the animal(s).");
		animaltype.setOnchange("showHideGenotypeDiv(this.value);");
		animaltype.setNillable(false);
		if (animalType != null) {
			animaltype.setValue(animalType);
		}

		birthdate = new DateInput("birthdate");
		birthdate.setLabel("Date of birth (if known):");
		birthdate.setValue(null);
		birthdate.setDescription("The date of birth of the animal(s).");
		if (birthDate != null) {
			birthdate.setValue(dateOnlyFormat.parse(birthDate));
		}

		entrydate = new DateInput("entrydate");
		entrydate.setLabel("Date of entry:");
		entrydate.setValue(new Date());
		entrydate.setNillable(false);
		entrydate.setDescription("The date of arrival of these animals in the animal facility. This date will be used as start date to count the presence of animals in the yearly report.");
		if (entryDate != null) {
			entrydate.setValue(entryDate);
		}
		
		researcher = new StringInput("researcher");
		researcher.setLabel("Responsible researcher:");
		researcher.setNillable(true);
		researcher.setDescription("Give the responsible researcher.");
		researcher.setTooltip("Give the responsible researcher.");
		if (resResearcher != null) {
			researcher.setValue(resResearcher);
		}
		
		// Populate locations list
		location = new SelectInput("location");
		location.setLabel("Location (optional):");
		location.addOption("","");
		for (Location l : ct.getAllLocations()) {
			location.addOption(l.getId(), l.getName());
		}
		location.setDescription("Give the location of the new animal(s).");
		location.setTooltip("Give the location of the new animal(s).");
		location.setNillable(true);
		if (locId != -1) {
			location.setValue(locId);
		}
		
		DivPanel buttonPanel = new DivPanel("buttonPanel1", "", false);
		ActionInput contbutton = new ActionInput("Cont1", "", "Next");
		buttonPanel.add(contbutton);

		// add everything to the panel
		containingPanel.add(new Paragraph("<h2>Bring in animals: set general info</h2>"));
		containingPanel.add(species);
		containingPanel.add(source);
		containingPanel.add(animaltype);
		containingPanel.add(birthdate);
		containingPanel.add(entrydate);
		containingPanel.add(researcher);
		containingPanel.add(location);
		containingPanel.add(buttonPanel);
	}
	
	private void populateSecondTablePanel(Database db) throws DatabaseException, ParseException {
		
		ct.setDatabase(db);
		
		List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserName());
		
		containingPanel = new DivPanel(this.getName() + "panel", "");
		containingPanel.add(new Paragraph("<h2>Bring in animals: set genotype info</h2>"));
		
		background = new SelectInput("background");
		background.setLabel("Background:");
		background.setDescription("Give the genetic background of the animal.");
		background.setTooltip("Give the genetic background of the animal.");
		background.addOption("","");
		background.addOption("0", "no background");
		for (ObservationTarget b : ct.getAllMarkedPanels("Background", investigationIds)) {
			// Only show if background belongs to chosen species
			if (ct.getMostRecentValueAsXref(b.getId(), "Species") == speciesId) {
				background.addOption(b.getId(), b.getName());
			}
		}
		background.setNillable(false);
		if (backgroundId != -1) {
			background.setValue(backgroundId);
		}
		containingPanel.add(background);
		
		if (animalType.equals("B. Transgeen dier")) {
			gene = new SelectMultipleInput("gene");
			gene.setUseJqueryMultiplePlugin(true);
			gene.setNillable(false); // to avoid the empty option from showing up
			gene.setLabel("Gene(s):");
			for (String option : ct.getAllCodesForFeatureAsStrings("GeneModification")) {
				gene.addOption(option, option);
			}
			if (genes != null) {
				gene.setValue(genes);
			}
			containingPanel.add(gene);
		}
		
		DivPanel buttonPanel = new DivPanel("buttonPanel2", "", false);
		ActionInput cancelbutton = new ActionInput("Cancel", "", "Cancel");
		ActionInput prevbutton = new ActionInput("Prev1", "", "Previous");
		ActionInput contbutton = new ActionInput("Cont2", "", "Next");
		buttonPanel.add(cancelbutton);
		buttonPanel.add(prevbutton);
		buttonPanel.add(contbutton);
		containingPanel.add(buttonPanel);
	}
	
	private void populateThirdTablePanel(Database db) throws DatabaseException, ParseException {
		
		ct.setDatabase(db);
		
		containingPanel = new DivPanel(this.getName() + "panel", "");
		containingPanel.add(new Paragraph("<h2>Bring in animals: set genotype info (II)</h2>"));
		
		genestateList = new ArrayList<SelectInput>();
		List<String> geneList = gene.getObject();
		int geneNr = 0;
		for (String geneName : geneList) {
			SelectInput genestateBox = new SelectInput("genestate_" + geneName);
			genestateBox.setLabel("State for " + geneName + ":");
			for (String option : ct.getAllCodesForFeatureAsStrings("GeneState")) {
				genestateBox.addOption(option, option);
			}
			if (genestates != null && genestates.get(geneNr) != null) {
				genestateBox.setValue(genestates.get(geneNr));
			}
			containingPanel.add(genestateBox);
			genestateList.add(genestateBox);
			geneNr++;
		}
		
		DivPanel buttonPanel = new DivPanel("buttonPanel3", "", false);
		ActionInput cancelbutton = new ActionInput("Cancel", "", "Cancel");
		ActionInput prevbutton = new ActionInput("Prev2", "", "Previous");
		ActionInput contbutton = new ActionInput("Cont3", "", "Next");
		buttonPanel.add(cancelbutton);
		buttonPanel.add(prevbutton);
		buttonPanel.add(contbutton);
		containingPanel.add(buttonPanel);
	}
	
	private void populateFourthTablePanel(Database db) throws DatabaseException, ParseException {
		
		ct.setDatabase(db);
		
		containingPanel = new DivPanel(this.getName() + "panel", "");
		containingPanel.add(new Paragraph("<h2>Bring in animals: set names and numbers</h2>"));
		
		String defaultPrefix = "";
		// TODO: put this hardcoded info in the database (NamePrefix table)
		if (ct.getObservationTargetLabel(speciesId).equals("House mouse")) {
			defaultPrefix = "mm_";
		}
		if (ct.getObservationTargetLabel(speciesId).equals("Brown rat")) {
			defaultPrefix = "rn_";
		}
		if (ct.getObservationTargetLabel(speciesId).equals("Common vole")) {
			defaultPrefix = "mar_";
		}
		if (ct.getObservationTargetLabel(speciesId).equals("Tundra vole")) {
			defaultPrefix = "mo_";
		}
		if (ct.getObservationTargetLabel(speciesId).equals("Syrian hamster")) {
			defaultPrefix = "ma_";
		}
		if (ct.getObservationTargetLabel(speciesId).equals("European groundsquirrel")) {
			defaultPrefix = "sc_";
		}
		if (ct.getObservationTargetLabel(speciesId).equals("Siberian hamster")) {
			defaultPrefix = "ps_";
		}
		if (ct.getObservationTargetLabel(speciesId).equals("Domestic guinea pig")) {
			defaultPrefix = "cp_";
		}
		if (ct.getObservationTargetLabel(speciesId).equals("Fat-tailed dunnart")) {
			defaultPrefix = "sg_";
		}
		
		List<String> bases = ct.getPrefixes("animal");
		namebase = new SelectInput("namebase");
		namebase.setLabel("Name prefix (may be empty):");
		namebase.setId("namebase");
		namebase.setDescription("The default prefix string that will be put in front of your name.");
		namebase.addOption("New", "New (specify below)");
		for (String base : bases) {
			if (!base.equals("")) {
				namebase.addOption(base, base);
			}
		}
		namebase.setValue(defaultPrefix);
		namebase.setOnchange("updateStartNumberAndNewNameBase(this.value)");
		
		startnumberhelper = new TextLineInput<String>("startnumberhelper");
		startnumberhelper.setLabel("");
		String helperContents = "1"; // start number for new base
		for (String base : bases) {
			if (!base.equals("")) {
				helperContents += (";" + (ct.getHighestNumberForPrefix(base) + 1));
			}
		}
		helperContents += (";" + (ct.getHighestNumberForPrefix("") + 1)); // start number for empty base (comes last in jQuery select box)
		startnumberhelper.setValue(helperContents);
		startnumberhelper.setHidden(true);
		
		newnamebase = new StringInput("newnamebase");
		newnamebase.setLabel("New name prefix:");
		DivPanel newnamebasePanel = new DivPanel("Namebase", "");
		newnamebasePanel.add(newnamebase);
		newnamebasePanel.setId("newnamebasePanel");
		newnamebasePanel.setHidden(true);
		
		startnumber = new IntInput("startnumber");
		startnumber.setLabel("Start numbering at:");
		startnumber.setId("startnumber");
		startnumber.setValue(ct.getHighestNumberForPrefix(defaultPrefix) + 1); // start with highest number for default prefix
		startnumber.setDescription("Set the inital number to increment the name with. The correct number is automatically set when a name prefix is selected.");
		startnumber.setReadonly(true);
		
		numberofmales = new IntInput("numberofmales");
		numberofmales.setLabel("Number of males:");
		numberofmales.setValue(0);
		numberofmales.setNillable(false);
		numberofmales.setDescription("Give the number of male animals to add to the database.");
		
		numberoffemales = new IntInput("numberoffemales");
		numberoffemales.setLabel("Number of females:");
		numberoffemales.setValue(0);
		numberoffemales.setNillable(false);
		numberoffemales.setDescription("Give the number of female animals to add to the database.");
		
		numberofunknowns = new IntInput("numberofunknowns");
		numberofunknowns.setLabel("Number of animals of unknown sex:");
		numberofunknowns.setValue(0);
		numberofunknowns.setNillable(false);
		numberofunknowns.setDescription("Give the number of animals of unkowon sex to add to the database.");

		DivPanel buttonPanel = new DivPanel("buttonPanel4", "", false);
		ActionInput cancelbutton = new ActionInput("Cancel", "", "Cancel");
		ActionInput prevbutton = new ActionInput("Prev3", "", "Previous");
		ActionInput contbutton = new ActionInput("Save", "", "Save");
		buttonPanel.add(cancelbutton);
		buttonPanel.add(prevbutton);
		buttonPanel.add(contbutton);
		
		containingPanel.add(namebase);
		containingPanel.add(startnumberhelper);
		containingPanel.add(newnamebasePanel);
		containingPanel.add(startnumber);
		containingPanel.add(numberofmales);
		containingPanel.add(numberoffemales);
		containingPanel.add(numberofunknowns);
		containingPanel.add(buttonPanel);
	}
	
	public String render()
	{
		return this.containingPanel.toHtml();
	}
	
}
