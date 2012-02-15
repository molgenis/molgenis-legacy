
package plugins.matrix;

import gcc.catalogue.ShoppingCart;
import gcc.catalogue.ShoppingCart_Measurements;

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
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel.Show;

import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.DatabaseMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
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
public class CatalogueMatrix extends EasyPluginController<CatalogueMatrixModel>
{
	public CatalogueMatrix(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new CatalogueMatrixModel(this)); //the default model
		this.setView(new FreemarkerView("CatalogueMatrixView.ftl", getModel())); //<plugin flavor="freemarker"

	}

	public String getCustomHtmlHeaders() {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/gids.css\">";
	}

	@Override
	public void reload(Database db) throws Exception
	{	

		//		FormModel<Investigation> form = this.getParentForm(Investigation.class);
		//
		//		List<Investigation> investigationsList = form.getRecords();
		//
		//		getModel().setInvestigation(investigationsList.get(0).getName());

		

		try {
			
			getModel().error = false;
			
			List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
			//filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, ObservationTarget.INVESTIGATION_NAME, 
			//	Operator.EQUALS, "DataShaper"));
			String userName = this.getApplicationController().getLogin().getUserName();

			Query<ShoppingCart> q = db.query(ShoppingCart.class);
			
			q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, userName));
			
			q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, true));
			
			q.addRules(new QueryRule(ShoppingCart.APPROVED, Operator.EQUALS, true));
			
			List<String> listMeas = new ArrayList<String>();
			
			if(q.find().size() > 0)
			{
				List<ShoppingCart> shoppingCartList = q.find();
				
				for(ShoppingCart eachCart : shoppingCartList)
				{
					listMeas.addAll(eachCart.getMeasurements_Name());
				}
				
				listMeas = removeDuplication(listMeas);
			}
			
			if(getModel().matrixViewerCat != null){
				
				getModel().matrixViewerCat.setDatabase(db);
				
				if(listMeas.size() == 0)
				{
				
					getModel().matrixViewerCat = null;
				
				}else{
					getModel().matrixViewerCat.setColHeaderFilter(listMeas);
					
				}
				
			} else if (getModel().matrixViewerCat == null) {		//the matrix is completely new

				if(listMeas.size() > 0)
				{
					List<ShoppingCart> shoppingCartList = q.find();
					
					for(ShoppingCart eachCart : shoppingCartList)
					{
						listMeas.addAll(eachCart.getMeasurements_Name());
					}
					
					listMeas = removeDuplication(listMeas);
					
					System.out.println("ADFADSFNADIS:F DSIFN DSAF OADS FONDS F DS::: " + listMeas.size());
					
					getModel().matrixViewerCat = new MatrixViewer(this, getModel().getCATMATRIX(), 
							new SliceablePhenoMatrix(ObservationTarget.class, Measurement.class), 
							true, true, true, filterRules, 
							new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, listMeas));
					
					getModel().matrixViewerCat.setDatabase(db);
				}
			}

		}catch (Exception e) {
			logger.error(e.getMessage());
		}


	
	}

	public List<String> removeDuplication (List<String> allMeasurementList){

		List<String> temporaryList = new ArrayList<String>();

		for(String m : allMeasurementList)
		{
			if(!temporaryList.contains(m))
			{
				temporaryList.add(m);
			}
		}
		return temporaryList;
	}

	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out)
	throws HandleRequestDelegationException
	{
		
		if(getModel().matrixViewerCat != null){
			getModel().matrixViewerCat.setDatabase(db);
		}
		if (request.getAction().startsWith(getModel().matrixViewerCat.getName())) {
			getModel().matrixViewerCat.handleRequest(db, request);
		}
		return Show.SHOW_MAIN;
	}

}