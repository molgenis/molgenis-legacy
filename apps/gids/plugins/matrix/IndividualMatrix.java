
package plugins.matrix;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.Label;


import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel.Show;
import org.molgenis.gids.GidsSample;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.DatabaseMatrix;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
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
	
	public String getCustomHtmlHeaders() {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/gids.css\">";
	}
		
	@Override
	public void reload(Database db) throws Exception
	{	
		List<Protocol> listProtocols = db.query(Protocol.class).find();
		if(listProtocols.size()==0){	
			getModel().error=true;
		}
		else{
			if (!getModel().action.startsWith(getModel().INDVMATRIX)) {
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
				FormModel<Investigation> form = this.getParentForm(Investigation.class);
				List<Investigation> investigationsList = form.getRecords();
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
					if (!getModel().action.startsWith(getModel().INDVMATRIX)) {
						getModel().setCheckIfInvestchanges(false);						
					} 
				}
				
				//List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
				if (getModel().matrixViewerIndv == null && !getModel().getInvestigation().equals("Shared")) {	
					Protocol indvInfoProt = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, getModel().chosenProtocolNameI)).get(0);
					List<String> measurementsToShow = indvInfoProt.getFeatures_Name();
					getModel().setMeasExport(measurementsToShow);
					List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
					filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
							Operator.EQUALS, getModel().getInvestigation()));
					getModel().matrixViewerIndv = new MatrixViewer(this, getModel().INDVMATRIX, 
							new SliceablePhenoMatrix(Individual.class, Measurement.class), 
							true, true, false, filterRules, 
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
										
					 getModel().matrixViewerSample = new MatrixViewer(this, getModel().SAMPLEMATRIX, 
								new SliceablePhenoMatrix(GidsSample.class, Measurement.class), 
								true, true, false, showTheseIndividuals, 
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
	
	public File getAsExcelFile(String investigation, MatrixViewer matrixI) throws Exception
	   {
		
		List<Measurement> listCol = (List<Measurement>) matrixI.getMatrix().getColHeaders();
		List<String> listC = new ArrayList<String>();
		List<Individual> listRow = (List<Individual>) matrixI.getMatrix().getRowHeaders();
		List<String> listR = new ArrayList<String>();
		
		ObservedValue[][] listVal = (ObservedValue[][])matrixI.getMatrix().getValueLists();
		
		//TODO Connection is CLOSED!! WHY???????
		/*
		if(((JDBCDatabase) this.getDatabase()).getConnection().isClosed()){
			System.out.println("connection is closed");
		}else{
			System.out.println("connection is still open");
		}
		*/
		
		
		for(Measurement m : listCol){
			listC.add(m.getName());
		}
		for(Individual m : listRow){
			listR.add(m.getName());
		}		
		
//		
	
		Calendar start = Calendar.getInstance();
		String dateAndTime = start.getTime().toString();
			System.out.println(investigation + "\t"+ dateAndTime);
	       /* Create tmp file */
	       File excelFile = new File(System.getProperty("java.io.tmpdir")
	               + File.separator + (investigation+dateAndTime) + ".xls");

	       /* Create new Excel workbook and sheet */
	       WorkbookSettings ws = new WorkbookSettings();
	       ws.setLocale(new Locale("en", "EN"));
	       WritableWorkbook workbook = Workbook.createWorkbook(excelFile, ws);
	       WritableSheet s = workbook.createSheet("Sheet1", 0);

	       /* Format the fonts */
	       WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10,
	               WritableFont.BOLD);
	       WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
	       headerFormat.setWrap(false);
	       WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10,
	               WritableFont.NO_BOLD);
	       WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
	       cellFormat.setWrap(false);

	       // Write column headers 
	       for (int i = 0; i < listC.size(); i++)
	       {
	           Label l = new Label(i + 1, 0, listC.get(i), headerFormat);
	           s.addCell(l);
	       }

	       // Write row headers 
	       for (int i = 0; i < listR.size(); i++)
	       {
	           Label l = new Label(0, i + 1, listR.get(i), headerFormat);
	           s.addCell(l);
	       }

	       // Write elements 
	       for (int i = 0; i < listC.size(); i++)
	       {
	           for (int j = 0; j < listR.size(); j++)
	           {
	               if (listVal[j][i] != null)
	               {
	                   Label l = new Label(i + 1, j + 1,
	                		   listVal[j][i].toString(), cellFormat);
	                   s.addCell(l);
	               }
	               else
	               {
	                   s.addCell(new Label(i + 1, j + 1, "", cellFormat));
	               }
	           }
	       }
	       workbook.write();
	       workbook.close();
	       return excelFile;
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
				List<ObservationElement> rows = (List<ObservationElement>) getModel().matrixViewerIndv.getSelection(db);
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
			
			if (getModel().action.equals("downVisXcel")) {
				getAsExcelFile(getModel().getInvestigation(),getModel().matrixViewerIndv);	
			}
			
			
		} catch (Exception e) {
			this.setError(e.getMessage());
		}
		
		//default show
		return Show.SHOW_MAIN;
	}

}