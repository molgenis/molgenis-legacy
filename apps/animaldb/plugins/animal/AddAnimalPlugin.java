/*
 * Date: December 24, 2010 Template: PluginScreenJavaTemplateGen.java.ftl
 * generator: org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.animal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.RepeatingPanel;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.SelectMultipleInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.TextLineInput;
import org.molgenis.framework.ui.html.Newline;

import org.molgenis.pheno.Code;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class AddAnimalPlugin extends GenericPlugin
{
	private static final long serialVersionUID = -4185405160313262242L;
	private CommonService ct = CommonService.getInstance();
	
	private List<String> bases = null;

	// inputs
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
	public IntInput numberofanimals = null;
	public SelectInput actor = null;
	public ActionInput addbutton = null;
	public ActionInput savebutton = null;
	private boolean genesSaved = false;
	
	// container that renders whole form as divs (left labels, right inputs)
	public DivPanel containingPanel = null;
	// subpanel to conditionally show the genetic modification questions (background, genotype)
	public DivPanel gmoPanel = null;
	// sub-subpanel to conditionally show the genetic modification questions (gene, genestate)
	public DivPanel genePanel = null;
	// subpanel to conditionally show the custom name questions (base, start number)
	public DivPanel namePanel = null;
	public DivPanel newnamebasePanel = null;

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
			int userId = this.getLogin().getUserId();
			
			ct.setDatabase(db);
			ct.makeObservationTargetNameMap(userId, false);
			
			bases = ct.getPrefixes(userId, "animal");
			
			if (!genesSaved) {
				populateTablePanel(db);
			}
			
			genesSaved = false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.getMessages().clear();
			String message = "Something went wrong while reloading";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
		}
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		ct.setDatabase(db);
		try {
			String action = request.getAction();
			containingPanel.setValuesFromRequest(request);
			if (action.equals("Add")) {
				handleAddRequest(db, request);
			}
			if (action.equals("Save")) {
				handleSaveRequest(db, request);
			}
		} catch (Exception e) {
			try {
				db.rollbackTx();
			} catch (DatabaseException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}
	
	private void handleSaveRequest(Database db, Tuple request) throws Exception {
		genesSaved = true;
		genestateList = new ArrayList<SelectInput>();
		List<String> geneList = (List<String>) gene.getObject();
		for (String geneName : geneList) {
			SelectInput genestateBox = new SelectInput("genestate_" + geneName);
			genestateBox.setLabel("State for gene " + geneName + ":");
			for (String option : ct.getAllCodesForFeatureAsStrings("GeneState")) {
				genestateBox.addOption(option, option);
			}
			gmoPanel.add(genestateBox);
			genestateList.add(genestateBox);
		}
		gmoPanel.setHidden(false);
	}
	
	private void handleAddRequest(Database db, Tuple request) throws Exception {
		int speciesId = 0;
		if (species.getObject() != null) {
			speciesId = Integer.parseInt(species.getObject().toString());
		} else {
			throw(new Exception("No species given - animal(s) not added"));
		}
		
		int backgroundId = 0;
		if (background.getObject() != null) {
			Integer.parseInt(background.getObject().toString());
		} else {
			throw(new Exception("No background given - animal(s) not added"));
		}
		
		int sexId = 0;
		if (sex.getObject() != null) {
			sexId = Integer.parseInt(sex.getObject().toString());
		} else {
			throw(new Exception("No sex given - animal(s) not added"));
		}
		
		int sourceId = 0;
		if (source.getObject() != null) {
			sourceId = Integer.parseInt(source.getObject().toString());
		} else {
			throw(new Exception("No source given - animal(s) not added"));
		}
		
		String animalType = null;
		if (animaltype.getObject() != null) {
			animalType = animaltype.getObject().toString();
		} else {
			throw(new Exception("No animal type given - animal(s) not added"));
		}
		
		// GMO info
		List<String> genes = new ArrayList<String>();
		List<String> genestates = new ArrayList<String>();
		// User may have filled in fields and then switched the AnimalType from GMO again,
		// so we need to check first what the AnimalType is.
		if (animalType.equals("B. Transgeen dier")) {
			// GMO panel already made visible through JavaScript, now make permanent
			gmoPanel.setHidden(false);
			// Get gene name/state pairs
			genes = (List<String>) gene.getObject();
			for (SelectInput genestateBox : genestateList) {
				genestates.add((String) genestateBox.getObject());
			}
		} else {
			// GMO panel already made invisible through JavaScript, now make permanent
			gmoPanel.setHidden(true);
		}
		
		SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
		
		// Birth date
		String birthDate = null;
		if (!birthdate.getValue().equals("")) {
			birthDate = birthdate.getValue();
		}
		
		// Entry date
		Date entryDate = null;
		if (!entrydate.getValue().equals("")) {
			String entryDateString = entrydate.getValue();
			entryDate = dateOnlyFormat.parse(entryDateString);
		} else {
			throw(new Exception("No entry date given - animal(s) not added"));
		}
						
		// Name
		String nameBase = null;
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
		
		// Number of animals
		int nrOfAnimals = 1;
		if (numberofanimals.getObject() != null) {
			// TODO: Find out why HtmlInput<E>'s getObject() returns a String object and not an
			// Integer one, as expected!
			nrOfAnimals = Integer.parseInt(numberofanimals.getValue());
		} else {
			throw(new Exception("No number given - animal(s) not added"));
		}
		
		// Investigation
		int userId = this.getLogin().getUserId();
		int invid = ct.getOwnUserInvestigationIds(userId).get(0);
		
		// Init lists that we can later add to the DB at once
		List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
		List<ObservationTarget> animalsToAddList = new ArrayList<ObservationTarget>();
		List<ProtocolApplication> appsToAddList = new ArrayList<ProtocolApplication>();
		
		// Make all animals
		for (int i = 0; i < nrOfAnimals; i++) {
			// Make and add animal
			String nrPart = "" + (startNumber + i);
			nrPart = ct.prependZeros(nrPart, 6);
			ObservationTarget newAnimal = ct.createIndividual(invid, nameBase + nrPart, 
					this.getLogin().getUserId());
			animalsToAddList.add(newAnimal);
		}
		db.add(animalsToAddList);
		
		// Make or update name prefix entry
		ct.updatePrefix(userId, "animal", nameBase, startNumber + nrOfAnimals - 1);
		
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
		for (int j = 0; j < 8; j++) {
			ProtocolApplication newApp = ct.createProtocolApplication(invid, protocolIdList.get(j));
			appsToAddList.add(newApp);
		}
		db.add(appsToAddList);
		
		// Make all values
		List<Integer> featureIdList = new ArrayList<Integer>();
		featureIdList.add(ct.getMeasurementId("Active"));
		featureIdList.add(ct.getMeasurementId("Species"));
		featureIdList.add(ct.getMeasurementId("Sex"));
		featureIdList.add(ct.getMeasurementId("AnimalType"));
		featureIdList.add(ct.getMeasurementId("Source"));
		featureIdList.add(ct.getMeasurementId("Background"));
		featureIdList.add(ct.getMeasurementId("GeneName"));
		featureIdList.add(ct.getMeasurementId("GeneState"));
		featureIdList.add(ct.getMeasurementId("DateOfBirth"));
		
		for (ObservationTarget animal : animalsToAddList) {
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
			if (backgroundId != 0) {
				app = appsToAddList.get(5);
				valuesToAddList.add(ct.createObservedValue(invid, app.getId(), entryDate, null, 
						featureIdList.get(5), animalid, null, backgroundId));
			}
			// Set genotype(s)
			int index = 0;
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
			// Set birthdate
			if (birthDate != null) {
				app = appsToAddList.get(7);
				valuesToAddList.add(ct.createObservedValue(invid, app.getId(), entryDate, null, 
						featureIdList.get(8), animalid, birthDate, 0));
			}
			
		}	
		db.add(valuesToAddList);
		
		// Update custom label map now new animals have been added
		ct.makeObservationTargetNameMap(this.getLogin().getUserId(), true);
		
		// Add success message to the screen
		this.getMessages().clear();
		this.getMessages().add(new ScreenMessage(animalsToAddList.size() + " animal(s) successfully added", true));
	}
	
	private void populateTablePanel(Database db) throws DatabaseException, ParseException {
		
		List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserId());
		
		// panel for all elements
		containingPanel = new DivPanel(this.getName() + "panel", "");

		// Populate animal species list
		species = new SelectInput("species");
		species.setLabel("Species:");
		//species.setOptions(ct.getAllMarkedPanels("Species", investigationIds), "id", "name");
		species.addOption("","");
		for (ObservationTarget s : ct.getAllMarkedPanels("Species", investigationIds)) {
			species.addOption(s.getId(), s.getName());
		}
		species.setNillable(false);
		species.setDescription("Give the species.");
		species.setTooltip("Give the species.");
		
		background = new SelectInput("background");
		background.setLabel("Background:");
		//background.setOptions(ct.getAllMarkedPanels("Background", investigationIds), "id", "name");
		background.setDescription("Give the genetic background of the animal, for instance C57black6/j, if applicable.");
		background.setTooltip("Give the genetic background of the animal, for instance C57black6/j, if applicable.");
		background.addOption("","");
		background.addOption("0", "no background");
		ArrayList<ObservationTarget> backgroundslist= new ArrayList<ObservationTarget>(ct.getAllMarkedPanels("Background", investigationIds));
		for (ObservationTarget each : backgroundslist) {
			background.addOption(each.getId(), each.getName());
		}
		background.setNillable(false);
		
		// Populate sexes list
		sex = new SelectInput("sex");
		sex.setLabel("Sex:");
		sex.addOption("","");
		//sex.setOptions(ct.getAllMarkedPanels("Sex", investigationIds), "id", "name");
		for (ObservationTarget s : ct.getAllMarkedPanels("Sex", investigationIds)) {
			sex.addOption(s.getId(), s.getName());
		}
		sex.setDescription("Give the sex of the new animal(s).");
		sex.setTooltip("Give the sex of the new animal(s).");
		sex.setNillable(false);

		// Populate source list
		// All source types except "Eigen fok binnen uw organisatorische werkeenheid",
		// which is taken care of in the Breeding Module
		source = new SelectInput("source");
		source.setLabel("Source:");
		source.addOption("","");
		List<ObservationTarget> tmpSourceList = ct.getAllMarkedPanels("Source", investigationIds);
		for (ObservationTarget tmpSource : tmpSourceList)
		{
			int featureId = ct.getMeasurementId("SourceType");
			List<ObservedValue> sourceTypeValueList = db.query(ObservedValue.class).eq(ObservedValue.TARGET,
					tmpSource.getId()).eq(ObservedValue.FEATURE, featureId).find();
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
		
		// Populate animal type list
		animaltype = new SelectInput("animaltype");
		animaltype.setLabel("Animal type:");
		animaltype.addOption("","");
		for (Code c : ct.getAllCodesForFeature("AnimalType")) {
			animaltype.addOption(c.getDescription(), c.getCode_String() + " ("
					+ c.getDescription() + ")");
		}
		animaltype.setDescription("Give the type of the new animal(s). Select type 2 (GMO) to modify the genotype of the animal(s).");
		animaltype.setOnchange("showHideGenotypeDiv(this.value);");
		animaltype.setNillable(false);

		// gene and genestate are CONDITIONAL on animaltype = B. Transgeen dier
		gmoPanel = new DivPanel("GMO", "Genotype:");
		gmoPanel.setId("GMO");
		gene = new SelectMultipleInput("gene");
		gene.setUseJqueryMultiplePlugin(true);
		gene.setNillable(false); // to avoid the empty option from showing up
		gene.setLabel("Gene(s):");
		for (String option : ct.getAllCodesForFeatureAsStrings("GeneName")) {
			gene.addOption(option, option);
		}
		gmoPanel.add(gene);
		savebutton = new ActionInput("Save", "", "Save gene(s)");
		gmoPanel.add(savebutton);
		gmoPanel.setHidden(true);

		birthdate = new DateInput("birthdate");
		birthdate.setLabel("Date of birth (if known):");
		birthdate.setValue(null);
		birthdate.setDescription("The date of birth of the animal(s).");

		entrydate = new DateInput("entrydate");
		entrydate.setLabel("Date of entry:");
		entrydate.setValue(new Date());
		entrydate.setNillable(false);
		entrydate.setDescription("The date of arrival of these animals in the animal facility. This date will be used as start date to count the presence of animals in the yearly report.");
		
		namePanel = new DivPanel("Name", "Name:");
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
		namebase.setValue(""); // default empty prefix
		namebase.setOnchange("updateStartNumberAndNewNameBase(this.value)");
		namePanel.add(namebase);
		startnumberhelper = new TextLineInput<String>("startnumberhelper");
		startnumberhelper.setLabel("");
		String helperContents = ((ct.getHighestNumberForPrefix("") + 1) + ";"); // start number for empty base (comes first in jQuery select box because default)
		helperContents += "1"; // start number for new base
		for (String base : bases) {
			if (!base.equals("")) {
				helperContents += (";" + (ct.getHighestNumberForPrefix(base) + 1));
			}
		}
		startnumberhelper.setValue(helperContents);
		startnumberhelper.setHidden(true);
		namePanel.add(startnumberhelper);
		newnamebase = new StringInput("newnamebase");
		newnamebase.setLabel("New name prefix:");
		newnamebasePanel = new DivPanel("Namebase", "");
		newnamebasePanel.add(newnamebase);
		newnamebasePanel.setId("newnamebasePanel");
		newnamebasePanel.setHidden(true);
		namePanel.add(newnamebasePanel);
		startnumber = new IntInput("startnumber");
		startnumber.setLabel("Start numbering at:");
		startnumber.setId("startnumber");
		startnumber.setValue(ct.getHighestNumberForPrefix("") + 1); // start with highest number for empty prefix (default selected)
		startnumber.setDescription("Set the inital number to increment the name with. The correct number is automatically set when a name prefix is selected.");
		namePanel.add(startnumber);
		
		numberofanimals = new IntInput("numberofanimals");
		numberofanimals.setLabel("Number of animals to add:");
		numberofanimals.setValue(1);
		numberofanimals.setNillable(false);
		numberofanimals.setDescription("Give the number of animals to add to the database.");

		addbutton = new ActionInput("Add", "", "Add animal(s)");

		// add everything to the panel
		containingPanel.add(species);
		containingPanel.add(background);
		containingPanel.add(sex);
		containingPanel.add(source);
		containingPanel.add(animaltype);
		containingPanel.add(gmoPanel);
		containingPanel.add(birthdate);
		containingPanel.add(entrydate);
		containingPanel.add(namePanel);
		
		Newline newline = new Newline();
		Newline newline2 = new Newline();
		containingPanel.add(newline);
		containingPanel.add(newline2);
		containingPanel.add(numberofanimals);
		containingPanel.add(addbutton);
	}
	
	public String render()
	{
		return this.containingPanel.toHtml();
	}

	public List<String> getBases() {
		return bases;
	}

	public void setBases(List<String> bases) {
		this.bases = bases;
	}
	
}
