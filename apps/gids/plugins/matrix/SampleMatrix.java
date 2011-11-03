
package plugins.matrix;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel.Show;
import org.molgenis.gids.GidsSample;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

/**
 * GidsMatrixController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>GidsMatrixModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>GidsMatrixView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class SampleMatrix extends EasyPluginController<SampleMatrixModel>
{

	
	public SampleMatrix(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new SampleMatrixModel(this)); //the default model
		this.setView(new FreemarkerView("SampleMatrixView.ftl", getModel())); //<plugin flavor="freemarker"
	}

	/**
	 * At each page view: reload data from database into model and/or change.
	 *
	 * Exceptions will be caught, logged and shown to the user automatically via setMessages().
	 * All db actions are within one transaction.
	 */ 
	
	public String getCustomHtmlHeaders() {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/gids.css\">";
	}
	
	@Override
	public void reload(Database db) throws Exception
	{	
		
		List<Protocol> listt = db.query(Protocol.class).find();
		if(listt.size()==0){
			getModel().error =true;
		}
		else{
			if (getModel().action.equals("setSample")) {
				getModel().sampleNavClass="nav1";
				
			}
			else{
				getModel().sampleNavClass="nav";
			}
		
			if (getModel().action.equals("setDNA")) {
				getModel().dnaNavClass="nav1";
				
			}
			else{
				getModel().dnaNavClass="nav";
			}
			if (getModel().action.equals("setRNA")) {
				getModel().rnaNavClass="nav1";
				
			}
			else{
				getModel().rnaNavClass="nav";

			}
			if (getModel().action.equals("setSerum")) {
				getModel().serumNavClass="nav1";
				
			}
			else{
				getModel().serumNavClass="nav";
			}
			if (getModel().action.equals("setPlasma")) {
				getModel().plasmaNavClass="nav1";
				
			}
			else{
				getModel().plasmaNavClass="nav";
			}
			if (getModel().action.equals("setBiopsies")) {
				getModel().biopsiesNavClass="nav1";
				
			}
			else{
				getModel().biopsiesNavClass="nav";
			}
			
			if (getModel().action.equals("setHLA")) {
				getModel().hlaNavClass="nav1";
			}
			else{
				getModel().hlaNavClass="nav";
			}
			
			try{
			getModel().error=false;
			//Show sampleMatrix, with chosenProtocol name to be shown
				if (getModel().matrixViewerSample == null) {
					Protocol sampleInfoProt = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, getModel().chosenProtocolName)).get(0);
					List<String> measurementsToShow = sampleInfoProt.getFeatures_Name();
					
					
					getModel().matrixViewerSample = new MatrixViewer(this, getModel().SAMPLEMATRIXS, 
							new SliceablePhenoMatrix(this.getDatabase(), GidsSample.class, Measurement.class), 
							true, true, null, new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, 
									Operator.IN, measurementsToShow));
				}
				// if samples are chosen, individualmatrix will be filled with chosenProtocol
				if(getModel().getListSamples()!=null && getModel().matrixViewerIndv == null){
					Protocol indvInfoProt = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, getModel().chosenProtocolName)).get(0);
					List<String> measurementsToShowIndividuals = indvInfoProt.getFeatures_Name();
				
					
					
					List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
					List<Integer> individIdList = new ArrayList<Integer>();
					//filling list with the selected individuals
					for (Integer sampleId : getModel().getListSamples()){
						GidsSample sample = db.findById(GidsSample.class, sampleId);
						individIdList.add(sample.getIndividualID_Id());
					}
					
					filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.ID, 
							Operator.IN, individIdList));
					getModel().matrixViewerIndv = new MatrixViewer(this, getModel().INDVMATRIXS, 
							new SliceablePhenoMatrix(this.getDatabase(), Individual.class, Measurement.class), 
							true, true, filterRules, new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, 
									Operator.IN, measurementsToShowIndividuals));
				}
			}
			catch (Exception e) {
				logger.error(e.getMessage());
				
				//e.printStackTrace();
			}
		}
	}
	
	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException
	{
		getModel().action = request.getString("__action");
			
			try {
				if (getModel().action.startsWith(getModel().matrixViewerSample.getName())) {
					getModel().matrixViewerSample.handleRequest(db, request);
					getModel().setAction("init");
				}
				//FormModel<Investigation> form = this.getParentForm(Investigation.class);			
				//List<Investigation> investigations = form.getRecords();
				//investigations.get(0).getName();
	
				if (getModel().action.equals("setSelection")) {
					getModel().selection = "";
					List<ObservationElement> rows = (List<ObservationElement>) getModel().matrixViewerSample.getSelection();
					int rowCnt = 0;
					List<Integer> listSampleIds = new ArrayList<Integer>();
					for (ObservationElement row : rows) {
						if (request.getBool(getModel().SAMPLEMATRIXS + "_selected_" + rowCnt) != null) {
							listSampleIds.add(row.getId());
						}
						rowCnt++;
					}
					getModel().setListSamples(listSampleIds);
					getModel().matrixViewerIndv = null;
						
								
				}
				if (getModel().action.equals("setSample")) {
					getModel().matrixViewerSample = null;
					getModel().setChosenProtocolName("Sample_info");
					
				}

				if (getModel().action.equals("setDNA")) {
					getModel().matrixViewerSample = null;
					getModel().setChosenProtocolName("DNA");
					
				}
				if (getModel().action.equals("setRNA")) {
					getModel().matrixViewerSample = null;
					getModel().setChosenProtocolName("RNA");
					
				}
				if (getModel().action.equals("setSerum")) {
					getModel().matrixViewerSample = null;
					getModel().setChosenProtocolName("Serum");
					
				}
				if (getModel().action.equals("setPlasma")) {
					getModel().matrixViewerSample = null;
					getModel().setChosenProtocolName("Plasma");
					
				}
				if (getModel().action.equals("setBiopsies")) {
					getModel().matrixViewerSample = null;
					getModel().setChosenProtocolName("Biopsies");
					
				}
				if (getModel().action.equals("setHLA")) {
					getModel().matrixViewerSample = null;
					getModel().setChosenProtocolName("HLA_Typing");
					
				}
				if (getModel().action.equals("setMedical")) {
					getModel().matrixViewerIndv = null;
					getModel().setChosenProtocolName("Medical_info");
					
				}
				if (getModel().action.equals("setPersonal")) {
					getModel().matrixViewerIndv = null;
					getModel().setChosenProtocolName("Personal_info");
					
				}
				if (getModel().action.equals("setIndividual")) {
					getModel().matrixViewerIndv = null;
					getModel().setChosenProtocolName("Individual_info");
					
				}
				
				
			} catch (Exception e) {
				this.setError(e.getMessage());
				e.printStackTrace();
			}
			
		 
		
		//default show
		return Show.SHOW_MAIN;
	}	
}