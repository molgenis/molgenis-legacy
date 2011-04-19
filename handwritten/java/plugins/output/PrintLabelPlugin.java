/* Date:        March 7, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.SelectMultipleInput;
import org.molgenis.framework.ui.html.TextParagraph;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import commonservice.CommonService;

public class PrintLabelPlugin extends GenericPlugin
{
	private static final long serialVersionUID = 8416302930361487397L;
	
	private Container container;
	private DivPanel panel;
	private SelectMultipleInput targets;
	private SelectMultipleInput features;
	private ActionInput printButton;
	private TextParagraph text = null;
	private CommonService cs = CommonService.getInstance();
	private Map<Integer, String> targetMap;

	public PrintLabelPlugin(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			String action = request.getString("__action");
			
			if( action.equals("Print") )
			{
				handlePrintRequest(request);
			}
		} catch(Exception e)
		{
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}

	private void handlePrintRequest(Tuple request) throws DatabaseException, ParseException, FileNotFoundException, DocumentException {
		List<Individual> individualList = getIndividualsFromUi(request);
		List<Integer> measurementIdList = getMeasurementsFromUi(request);
		
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File pdfFile = new File(tmpDir.getAbsolutePath() + File.separatorChar + "cagelabels.pdf");
		Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();
		
        PdfPTable table = new PdfPTable(2);
        
        for (Individual ind : individualList) {
        	PdfPCell newCell = new PdfPCell();
        	newCell.addElement(new Paragraph("Database ID: " + ind.getId().toString()));
        	newCell.addElement(new Paragraph("Database name: " + ind.getName()));
        	
        	List<ObservedValue> valueList = cs.getObservedValueByTargetAndFeatures(ind.getId(), measurementIdList);
        	for (ObservedValue value : valueList) {
        		String featName = cs.getMeasurementById(value.getFeature()).getName();
        		String actualValue;
        		if (value.getValue() != null) {
        			actualValue = value.getValue();
        		} else {
        			actualValue = value.getRelation_Name();
        		}
        		newCell.addElement(new Paragraph(featName + ": " + actualValue));
        	}
        	
        	table.addCell(newCell);
        }
        if (individualList.size() % 2 != 0) {
        	// In case of uneven number of animals, add empty cell to make row full
        	table.addCell("");
        }
        
        document.add(table);
	     
        document.close();
        
        text = new TextParagraph("pdfFilename", "<a href=\"tmpfile/" + pdfFile.getName() + "\">Download pdf</a>");
		text.setLabel("");
		// text is added to panel on reload()
	}
	
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
	
	private List<Integer> getMeasurementsFromUi(Tuple request) throws DatabaseException, ParseException {
		List<Integer> measurementList = new ArrayList<Integer>();
		List<?> featureListObject = request.getList("Features");
		if (featureListObject != null) {
			for (Object o : featureListObject) {
				measurementList.add(Integer.parseInt((String)o));
			}
		}
		return measurementList;
	}

	@Override
	public void reload(Database db)
	{
		cs.setDatabase(db);
		
		try {
		    List<Integer> allTargetIdList = cs.getAllObservationTargetIds("Individual", true);
		    targetMap = cs.getObservationTargetNames(allTargetIdList);
		} catch (Exception e) {
		    // Something went wrong, targetMap will remain null but getTargetName() can handle this
		}
		
		initScreen();
	}
	
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
	
	@Override
	public boolean isVisible()
	{
		if (this.getLogin().isAuthenticated()){
			return true;
		} else {
			return false;
		}
	}
	
	 /** Create a select box with ObservationTargets grabbed from the database
     * 
     */
    public void makeTargetsSelect() {
		try {
			targets = new SelectMultipleInput("Targets", null);
		    targets.setLabel("Select animal(s):");
		    for (Integer animalId : cs.getAllObservationTargetIds("Individual", true)) {
		    	targets.addOption(animalId, getTargetName(animalId));
		    }
		    panel.add(targets);
	
		} catch(Exception e) {
		    logger.error("An error occurred while retrieving targets from the database", e);
		}
    }
    
    private void makeFeaturesSelect() {
    	try {
			features = new SelectMultipleInput("Features", null);
			features.setLabel("Select feature(s):");
		    for (Measurement feature : cs.getAllMeasurementsSorted(Measurement.NAME, "ASC")) {
		    	features.addOption(feature.getId(), feature.getName());
		    }
		    panel.add(features);
	
		} catch(Exception e) {
		    logger.error("An error occurred while retrieving features from the database", e);
		}
	}
    
    private void makePrintButton() {
		printButton = new ActionInput("Print", "", "Print");
		panel.add(printButton);
	}
    
    /**
     * Get the custom label (if available) or name for the ObservationTarget with id 'id'
     * 
     * @param id
     * @return
     */
    public String getTargetName(Integer id) {
		if (targetMap != null && targetMap.get(id) != null) {
		    return targetMap.get(id);
		} else {
		    return id.toString();
		}
    }
}
