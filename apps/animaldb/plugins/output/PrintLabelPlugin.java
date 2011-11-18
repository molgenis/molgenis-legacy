/* Date:        March 7, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.output;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.SelectMultipleInput;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class PrintLabelPlugin extends GenericPlugin
{
	private static final long serialVersionUID = 8416302930361487397L;
	
	private Container container;
	private DivPanel panel;
	private SelectMultipleInput targets;
	private SelectMultipleInput features;
	private ActionInput printButton;
	private Paragraph text = null;
	private CommonService cs = CommonService.getInstance();
	private LabelGenerator labelGenerator = null;
	
	public PrintLabelPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		cs.setDatabase(db);
		try {
			String action = request.getString("__action");
			
			if (action.equals("Print")) {
				handlePrintRequest(request);
			}
		} catch(Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}
	
	/**
	 * When the user presses 'Print', make a pdf with labels for the desired animals and features.
	 * 
	 * @param request
	 * @throws LabelGeneratorException 
	 * @throws ParseException 
	 * @throws DatabaseException
	 */
	private void handlePrintRequest(Tuple request) throws LabelGeneratorException, DatabaseException, ParseException {
		
		int userId = this.getLogin().getUserId();
		
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File pdfFile = new File(tmpDir.getAbsolutePath() + File.separatorChar + "cagelabels.pdf");
		String filename = pdfFile.getName();
		
		labelGenerator.startDocument(pdfFile);
		
		List<Integer> investigationIds = cs.getAllUserInvestigationIds(userId);
		List<Individual> individualList = getIndividualsFromUi(request);
		List<Measurement> measurementList = getMeasurementsFromUi(request);
    	int ownInvId = cs.getOwnUserInvestigationId(userId);
        
        for (Individual ind : individualList) {
        	
        	List<String> lineList = new ArrayList<String>();
        	List<String> lineLabelList = new ArrayList<String>();
        	
        	lineLabelList.add("Database ID:");
        	lineList.add(ind.getId().toString());
        	lineLabelList.add("Name:");
        	lineList.add(ind.getName());
        	List<ObservedValue> valueList = cs.getObservedValuesByTargetAndFeatures(ind.getId(), measurementList,
        			investigationIds, ownInvId);
        	for (ObservedValue value : valueList) {
        		String featName = cs.getMeasurementById(value.getFeature_Id()).getName();
        		String actualValue;
        		if (value.getValue() != null) {
        			actualValue = value.getValue();
        		} else {
        			actualValue = value.getRelation_Name();
        		}
        		lineLabelList.add(featName);
        		lineList.add(actualValue);
        	}
        	
        	labelGenerator.addLabelToDocument(lineLabelList, lineList);
        }
        if (individualList.size() % 2 != 0) {
        	// In case of uneven number of animals, add empty label to make row full
        	List<String> lineLabelList = new ArrayList<String>();
        	List<String> lineList = new ArrayList<String>();
        	labelGenerator.addLabelToDocument(lineLabelList, lineList);
        }
		
		labelGenerator.finishDocument();
		
        text = new Paragraph("pdfFilename", "<a href=\"tmpfile/" + filename + "\">Download pdf</a>");
		text.setLabel("");
		// text is added to panel on reload()
	}
	
	/**
	 * Get the animals (Individuals) selected by the user.
	 * 
	 * @param request
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	private List<Individual> getIndividualsFromUi(Tuple request) throws DatabaseException, ParseException {
		List<Individual> individualList = new ArrayList<Individual>();
		List<?> targetListObject = request.getList("Targets");
		if (targetListObject != null) {
			for (Object o : targetListObject) {
				String tmpString = (String)o;
				individualList.add(cs.getIndividualById(Integer.parseInt(tmpString)));
			}
		}
		return individualList;
	}
	
	/**
	 * Get the features (Measurements) selected by the user.
	 * 
	 * @param request
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	private List<Measurement> getMeasurementsFromUi(Tuple request) throws DatabaseException, ParseException {
		List<Measurement> measurementList = new ArrayList<Measurement>();
		List<?> featureListObject = request.getList("Features");
		if (featureListObject != null) {
			for (Object o : featureListObject) {
				int measurementId = Integer.parseInt((String)o);
				measurementList.add(cs.getMeasurementById(measurementId));
			}
		}
		return measurementList;
	}

	@Override
	public void reload(Database db)
	{
		int userId = this.getLogin().getUserId();
		
		cs.setDatabase(db);
		cs.makeObservationTargetNameMap(userId, false);
		
		labelGenerator = new LabelGenerator(2);
		
		initScreen();
	}
	
	/**
	 * Initialize the UI. If already a pdf has been generated, show a link to that.
	 */
	public void initScreen() {
		container = new Container();
		panel = new DivPanel("PrintLabelPluginDivPanel", null);
		
		makeTargetsSelect();
		
		makeFeaturesSelect();
		
		makePrintButton();
		
		if (text != null) {
			panel.add(text);
		}
		
		container.add(panel);
	}

	public String render() {
		return container.toHtml();
	}
	
	 /** 
     * Create a select box with Individuals grabbed from the database.
     */
    public void makeTargetsSelect() {
    	targets = new SelectMultipleInput("Targets", null);
	    targets.setLabel("Select animal(s):");
		try {
			List<Integer> investigationIds = cs.getAllUserInvestigationIds(this.getLogin().getUserId());
		    for (Integer animalId : cs.getAllObservationTargetIds("Individual", true, investigationIds)) {
		    	targets.addOption(animalId, getTargetName(animalId));
		    }
		} catch(Exception e) {
		    this.setMessages(new ScreenMessage("An error occurred while retrieving animals from the database", false));
		}
		panel.add(targets);
    }
    
    /**
     * Create a select box with Measurements grabbed from the database.
     */
    private void makeFeaturesSelect() {
    	features = new SelectMultipleInput("Features", null);
		features.setLabel("Select feature(s):");
    	try {
    		List<Integer> investigationIds = cs.getAllUserInvestigationIds(this.getLogin().getUserId());
		    for (Measurement feature : cs.getAllMeasurementsSorted(Measurement.NAME, "ASC", investigationIds)) {
		    	features.addOption(feature.getId(), feature.getName());
		    }
		} catch(Exception e) {
			this.setMessages(new ScreenMessage("An error occurred while retrieving features from the database", false));
		}
		panel.add(features);
	}
    
    /**
     * Create the Print button.
     */
    private void makePrintButton() {
		printButton = new ActionInput("Print", "", "Print");
		panel.add(printButton);
	}
    
    /**
     * Get the custom label (if available) or name for the ObservationTarget with id 'id'.
     * 
     * @param id
     * @return
     */
    public String getTargetName(Integer id) {
    	try {
			return cs.getObservationTargetLabel(id);
		} catch (Exception e) {
			return id.toString();
		}
    }
}
