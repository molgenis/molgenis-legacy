/* Date:        March 7, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.administration;

import java.io.File;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.HorizontalRuler;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.SelectMultipleInput;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;


public class PrintLabelPlugin extends EasyPluginController
{
	private static final long serialVersionUID = 8416302930361487397L;
	
	private Container container;
	private DivPanel panel;
	//private SelectMultipleInput targets;
	private SelectMultipleInput features;
	private ActionInput printButton;
	private Paragraph text = null;
	private CommonService cs = CommonService.getInstance();
	private LabelGenerator labelGenerator = null;
	MatrixViewer targetMatrixViewer = null;
	static String TARGETMATRIX = "targetmatrix";
	
	public PrintLabelPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
    public Show handleRequest(Database db, Tuple request, OutputStream out)
	{
		cs.setDatabase(db);
		if (targetMatrixViewer != null) {
			targetMatrixViewer.setDatabase(db);
		}
		
		try {
			String action = request.getAction();
			
			if (action.startsWith(targetMatrixViewer.getName())) {
	    		targetMatrixViewer.handleRequest(db, request);
			}
			
			if (action.equals("Print")) {
				handlePrintRequest(db, request);
			}
		} catch(Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
		return Show.SHOW_MAIN;
	}
	
	/**
	 * When the user presses 'Print', make a pdf with labels for the desired animals and features.
	 * 
	 * @param request
	 * @throws LabelGeneratorException 
	 * @throws ParseException 
	 * @throws DatabaseException
	 * @throws MatrixException 
	 */
	private void handlePrintRequest(Database db, Tuple request) throws LabelGeneratorException, DatabaseException, ParseException, MatrixException {
		
		cs.setDatabase(db);
		
		String userName = db.getLogin().getUserName();
		
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File pdfFile = new File(tmpDir.getAbsolutePath() + File.separatorChar + "cagelabels.pdf");
		String filename = pdfFile.getName();
		
		labelGenerator.startDocument(pdfFile);
		
		List<String> investigationNames = cs.getAllUserInvestigationNames(userName);
		List<ObservationTarget> individualList = getIndividualsFromUi(db, request);
		List<String> measurementList = getMeasurementsFromUi(request);
    	String ownInvName = cs.getOwnUserInvestigationName(userName);
        
        for (ObservationTarget ind : individualList) {
        	
        	List<String> lineList = new ArrayList<String>();
        	List<String> lineLabelList = new ArrayList<String>();
        	lineLabelList.add("Name:");
        	lineList.add(ind.getName());
        	List<ObservedValue> valueList = cs.getObservedValuesByTargetAndFeatures(ind.getName(), measurementList,
        			investigationNames, ownInvName);
        	for (ObservedValue value : valueList) {
        		String actualValue;
        		if (value.getValue() != null) {
        			actualValue = value.getValue();
        		} else {
        			actualValue = value.getRelation_Name();
        		}
        		if (actualValue == null) {
        			actualValue = "NA";
        		}
        		lineLabelList.add(value.getFeature_Name());
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
		
        text = new Paragraph("pdfFilename", "<a href=\"tmpfile/" + filename + "\" target=\"blank\">Download labels as pdf</a>");
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
	 * @throws MatrixException 
	 */
	private List<ObservationTarget> getIndividualsFromUi(Database db, Tuple request) throws DatabaseException, ParseException, MatrixException {
		List<ObservationTarget> individualList = new ArrayList<ObservationTarget>();
		@SuppressWarnings("unchecked")
		List<ObservationElement> rows = (List<ObservationElement>) targetMatrixViewer.getSelection(db);
		int rowCnt = 0;
		for (ObservationElement row : rows) {
			if (request.getBool(TARGETMATRIX + "_selected_" + rowCnt) != null) {
				individualList.add(cs.getObservationTargetById(row.getId()));
			}
			rowCnt++;
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
	private List<String> getMeasurementsFromUi(Tuple request) throws DatabaseException, ParseException {
		List<String> measurementList = new ArrayList<String>();
		List<?> featureListObject = request.getList("Features");
		if (featureListObject != null) {
			for (Object o : featureListObject) {
				measurementList.add((String)o);
			}
		}
		return measurementList;
	}

	@Override
	public void reload(Database db)
	{
		cs.setDatabase(db);
		if (targetMatrixViewer != null) {
			targetMatrixViewer.setDatabase(db);
		}
		
		labelGenerator = new LabelGenerator(2);
		
		try {
			if (container == null) {
				initScreen(db);
			} else {
				// Add link to pdf to UI, if available
				if (text != null) {
					panel.remove(text);
					panel.add(text);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.setMessages(new ScreenMessage("Something went wrong: " + e.getMessage(), false));
		}
	}
	
	/**
	 * Initialize the UI.
	 * @throws Exception 
	 */
	public void initScreen(Database db) throws Exception {
		container = new Container();
		panel = new DivPanel("PrintLabelPluginDivPanel", null);
		makeTargetsSelect(db);
		makeFeaturesSelect(db);
		makePrintButton();
		container.add(panel);
	}

	public ScreenView getView() {
		MolgenisForm view = new MolgenisForm(this);
    	view.add(container);
    	return view;
	}
	
	 /** 
     * Create a select box with Individuals grabbed from the database.
	 * @throws Exception 
     */
    public void makeTargetsSelect(Database db) throws Exception {
    	List<String> investigationNames = cs.getAllUserInvestigationNames(db.getLogin().getUserName());
		List<String> measurementsToShow = new ArrayList<String>();
		measurementsToShow.add("Species");
		List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
		filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
				Operator.IN, investigationNames));
		filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
				cs.getMeasurementId("Active"), ObservedValue.VALUE, Operator.EQUALS,
				"Alive"));
		targetMatrixViewer = new MatrixViewer(this, TARGETMATRIX, 
				new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class), 
				true, 2, false, false, filterRules, 
				new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
		targetMatrixViewer.setDatabase(db);
		targetMatrixViewer.setLabel("Choose animals:");
		panel.add(targetMatrixViewer);
		panel.add(new HorizontalRuler());
		
//    	targets = new SelectMultipleInput("Targets", null);
//	    targets.setLabel("Select animal(s):");
//		try {
//			List<Integer> investigationIds = cs.getAllUserInvestigationIds(this.getLogin().getUserName());
//		    for (Integer animalId : cs.getAllObservationTargetIds("Individual", true, investigationIds)) {
//		    	targets.addOption(animalId, getTargetName(animalId));
//		    }
//		} catch(Exception e) {
//		    this.setMessages(new ScreenMessage("An error occurred while retrieving animals from the database", false));
//		}
//		panel.add(targets);
    }
    
    /**
     * Create a select box with Measurements grabbed from the database.
     */
    private void makeFeaturesSelect(Database db) {
    	features = new SelectMultipleInput("Features", null);
		features.setLabel("Select feature(s):");
    	try {
    		List<String> investigationNames = cs.getAllUserInvestigationNames(db.getLogin().getUserName());
		    for (Measurement feature : cs.getAllMeasurementsSorted(Measurement.NAME, "ASC", investigationNames)) {
		    	features.addOption(feature.getName(), feature.getName());
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
