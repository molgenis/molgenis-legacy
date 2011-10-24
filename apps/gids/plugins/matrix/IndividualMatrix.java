
package plugins.matrix;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel.Show;
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
public class IndividualMatrix extends EasyPluginController<IndividualMatrixModel>
{
	
	public IndividualMatrix(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new IndividualMatrixModel(this)); //the default model
		this.setView(new FreemarkerView("IndividualMatrixView.ftl", getModel())); //<plugin flavor="freemarker"
	}

	/**
	 * At each page view: reload data from database into model and/or change.
	 *
	 * Exceptions will be caught, logged and shown to the user automatically via setMessages().
	 * All db actions are within one transaction.
	 */ 
	@Override
	public void reload(Database db) throws Exception
	{	
	
		if (getModel().matrixViewerIndv == null) {
			Protocol indvInfoProt = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, getModel().chosenProtocolName)).get(0);
			List<String> measurementsToShow = indvInfoProt.getFeatures_Name();
			
			getModel().matrixViewerIndv = new MatrixViewer(this, getModel().INDVMATRIX, 
					new SliceablePhenoMatrix(this.getDatabase(), Individual.class, Measurement.class), 
					true, true, null, new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, 
							Operator.IN, measurementsToShow));
		}
		if(getModel().getListIndividuals()!=null && getModel().matrixViewerSample == null){
			Protocol sampleInfoProt = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, "Sample_info")).get(0);
			List<String> measurementsToShowSamples = sampleInfoProt.getFeatures_Name();
			
			List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
			filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.ID, 
					Operator.IN, getModel().getListIndividuals()));
			
			getModel().matrixViewerSample = new MatrixViewer(this, getModel().SAMPLEMATRIX, 
					new SliceablePhenoMatrix(this.getDatabase(), Individual.class, Measurement.class), 
					true, true, filterRules, new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, 
							Operator.IN, measurementsToShowSamples));
		}
	}
	
	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException
	{
		getModel().action = request.getString("__action");
		
		try {
			if (getModel().action.startsWith(getModel().matrixViewerIndv.getName())) {
				getModel().matrixViewerIndv.handleRequest(db, request);
				getModel().setAction("init");
			}
			//FormModel<Investigation> form = this.getParentForm(Investigation.class);			
			//List<Investigation> investigations = form.getRecords();
			//investigations.get(0).getName();

			if (getModel().action.equals("setSelection")) {
				getModel().selection = "";
				List<ObservationElement> rows = (List<ObservationElement>) getModel().matrixViewerIndv.getSelection();
				int rowCnt = 0;
				List<Integer> listIndividualIds = new ArrayList<Integer>();
				for (ObservationElement row : rows) {
					if (request.getBool(getModel().INDVMATRIX + "_selected_" + rowCnt) != null) {
						listIndividualIds.add(row.getId());
					}
					rowCnt++;
				}
				getModel().setListIndividuals(listIndividualIds);
				getModel().matrixViewerSample = null;
					
							
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