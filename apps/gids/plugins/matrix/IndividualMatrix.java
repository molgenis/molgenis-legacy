
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
import org.molgenis.framework.ui.ScreenView;
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
public class IndividualMatrix extends EasyPluginController<IndividualMatrixModel>
{
	private static final long serialVersionUID = 4513824752877779376L;


	public IndividualMatrix(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new IndividualMatrixModel(this)); //the default model
	}
	
	public ScreenView getView()
	{
		return new FreemarkerView("IndividualMatrixView.ftl", getModel());
	}
	
	public String getCustomHtmlHeaders() {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/gids.css\">";
	}
		
	@Override
	public void reload(Database db) throws Exception
	{	
		FormModel<Investigation> form = this.getParentForm(Investigation.class);
		List<Investigation> investigationsList = form.getRecords();
		getModel().setInvestigation(investigationsList.get(0).getName());
		
		List<Protocol> listProtocols = db.query(Protocol.class).find();
		if(listProtocols.size()==0){	
			getModel().error=true;
		}
		else{
			if (!getModel().action.startsWith(IndividualMatrixModel.INDVMATRIX)) {
				getModel().setCheckForPaging(false);
			}		
			if (getModel().getCheckForPaging()==false){
				if(getModel().selectedScreenI==1){
					getModel().matrixViewerIndv = null;
					getModel().setChosenProtocolNameI("Individual_info");
					getModel().individualNavClass="nav1";
				}else{getModel().individualNavClass="nav";}
						
				if(getModel().selectedScreenI==2){
					getModel().setChosenProtocolNameI("Personal_info");
					getModel().matrixViewerIndv = null;
					getModel().personalNavClass="nav1";
				}else{getModel().personalNavClass="nav";}
	
				if(getModel().selectedScreenI==3){
					getModel().setChosenProtocolNameI("Medical_info");
					getModel().matrixViewerIndv = null;
					getModel().medicalNavClass="nav1";
				}else{getModel().medicalNavClass="nav";}

				if(!getModel().getInvestigation().equals("Shared")){
					if(getModel().selectedScreenI==4){
						getModel().setChosenProtocolNameI(getModel().getInvestigation());
						getModel().matrixViewerIndv = null;
						getModel().projectSpecificNavClass="nav1";
					}else{getModel().projectSpecificNavClass="nav";}
				}
		
				if (getModel().selectedScreenS==1) {
					getModel().setChosenProtocolNameS("Sample_info");	
					getModel().matrixViewerSample = null;
					getModel().sampleNavClass="nav1";	
				}else{getModel().sampleNavClass="nav";}
			
				if (getModel().selectedScreenS==2) {
					getModel().setChosenProtocolNameS("DNA");
					getModel().matrixViewerSample = null;
					getModel().dnaNavClass="nav1";
				}else{getModel().dnaNavClass="nav";}
				
				if (getModel().selectedScreenS==3) {
					getModel().setChosenProtocolNameS("RNA");
					getModel().matrixViewerSample = null;
					getModel().rnaNavClass="nav1";	
				}else{getModel().rnaNavClass="nav";}
				
				if (getModel().selectedScreenS==4) {
					getModel().setChosenProtocolNameS("Serum");
					getModel().matrixViewerSample = null;
					getModel().serumNavClass="nav1";
				}else{getModel().serumNavClass="nav";}
				
				if (getModel().selectedScreenS==5) {
					getModel().setChosenProtocolNameS("Plasma");
					getModel().matrixViewerSample = null;
					getModel().plasmaNavClass="nav1";	
				}else{getModel().plasmaNavClass="nav";}
				
				if (getModel().selectedScreenS==6) {
					getModel().setChosenProtocolNameS("Biopsies");
					getModel().matrixViewerSample = null;
					getModel().biopsiesNavClass="nav1";
					}else{getModel().biopsiesNavClass="nav";}
				
				if (getModel().selectedScreenS==7) {
					getModel().setChosenProtocolNameS("HLA_Typing");
					getModel().matrixViewerSample = null;
					getModel().hlaNavClass="nav1";
				}else{getModel().hlaNavClass="nav";}
			}

			try {
				getModel().error=false;
				//FormModel<Investigation> form = this.getParentForm(Investigation.class);
				//List<Investigation> investigationsList = form.getRecords();
				getModel().setInvestigation(investigationsList.get(0).getName());
				if(getModel().getInvestigation().equals("Shared")){
					getModel().setProjectShared(true);
				}
				else{
					getModel().setProjectShared(false);
				}
				if(!getModel().getLastInvest().equals(getModel().getInvestigation())){
					getModel().setCheckIfInvestchanges(true);
				}
				else{
					if (!getModel().action.startsWith(IndividualMatrixModel.INDVMATRIX)) {
						getModel().setCheckIfInvestchanges(false);						
					} 
				}
				
				if (getModel().matrixViewerIndv == null && !getModel().getInvestigation().equals("Shared")) {	
					Protocol indvInfoProt = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, getModel().chosenProtocolNameI)).get(0);
					List<String> measurementsToShow = indvInfoProt.getFeatures_Name();
					getModel().setMeasExport(measurementsToShow);
					List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
					filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
							Operator.EQUALS, getModel().getInvestigation()));
					getModel().matrixViewerIndv = new MatrixViewer(this, IndividualMatrixModel.INDVMATRIX, 
							new SliceablePhenoMatrix<Individual, Measurement>(Individual.class, Measurement.class), 
							true, 2, true, false, filterRules, 
							new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
				}
				if(getModel().getListIndividuals()!=null && getModel().matrixViewerSample == null){
					Protocol sampleInfoProt = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, getModel().chosenProtocolNameS)).get(0);
					List<String> measurementsToShowSamples = sampleInfoProt.getFeatures_Name();
					List<MatrixQueryRule> showTheseIndividuals = new ArrayList<MatrixQueryRule>();

					List<Integer> sampleidList = new ArrayList<Integer>();
					for (Integer indiId : getModel().getListIndividuals()){
						Individual individual = db.findById(Individual.class, indiId);
						sampleidList.add(individual.getId());
					}
					 
					 showTheseIndividuals.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, GidsSample.INDIVIDUALID, 
								Operator.IN, sampleidList));
										
					getModel().matrixViewerSample = new MatrixViewer(this, IndividualMatrixModel.SAMPLEMATRIX, 
								new SliceablePhenoMatrix<GidsSample, Measurement>(GidsSample.class, Measurement.class), 
								true, 2, true, false, showTheseIndividuals, 
								new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShowSamples));
				}	
				getModel().setLastInvest(getModel().getInvestigation());
				
			}catch (Exception e) {
				logger.error(e.getMessage());
			}
			
		}
		if(getModel().matrixViewerIndv != null){
			getModel().matrixViewerIndv.setDatabase(db);
			if (getModel().action.startsWith(getModel().matrixViewerIndv.getName())) {
				getModel().setCheckForPaging(false);
			}
		}
		if(getModel().matrixViewerSample != null){
			getModel().matrixViewerSample.setDatabase(db);
			
		}
		
	}
	
	
	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException
		{	
		 if(request.getInt("selectedScreenI")!=null){
			getModel().setSelectedScreenI(request.getInt("selectedScreenI"));
		 }
		 if(request.getInt("selectedScreenS")!=null){
				getModel().setSelectedScreenS(request.getInt("selectedScreenS"));
			 }
		
		getModel().action = request.getString("__action");
		try {
			
			if (getModel().action.startsWith(getModel().matrixViewerIndv.getName())) {
				getModel().setCheckForPaging(true);	
				getModel().matrixViewerIndv.handleRequest(db, request);
			}
			else{
				getModel().setCheckForPaging(false);
			}

			if (getModel().action.equals("setSelection")) {
				getModel().selection = "";
				@SuppressWarnings("unchecked")
				List<ObservationElement> rows = (List<ObservationElement>) getModel().matrixViewerIndv.getSelection(db);
				int rowCnt = 0;
				List<Integer> listIndividualIds = new ArrayList<Integer>();
				for (ObservationElement row : rows) {

					if (request.getBool(IndividualMatrixModel.INDVMATRIX + "_selected_" + rowCnt) != null) {
						listIndividualIds.add(row.getId());

					}
					rowCnt++;
				}
				
				getModel().setListIndividuals(listIndividualIds);
				getModel().matrixViewerSample = null;						
			}
						
			
		} catch (Exception e) {
			this.setError(e.getMessage());
		}
		
		//default show
		return Show.SHOW_MAIN;
	}

}