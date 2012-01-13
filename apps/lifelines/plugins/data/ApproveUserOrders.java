package plugins.data;


import gcc.catalogue.ShoppingCart;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


import plugins.matrix.CatalogueMatrixModel;

/**
 *
 */
public class ApproveUserOrders extends PluginModel<Entity>
{
	
	private static final long serialVersionUID = -6096870458186217098L;
	private List<MolgenisUser> arrayUsers = new ArrayList<MolgenisUser>();
	private String selectedUser;
	private List<ShoppingCart> UserOrders = new ArrayList<ShoppingCart>();
	private CatalogueMatrixModel matrixModel = new CatalogueMatrixModel(null);
	private MatrixViewer matrixViewerCat = null;
	public ApproveUserOrders(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	@Override
	public String getViewName()
	{
		return "plugins_data_ApproveUserOrders";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/data/ApproveUserOrders.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception	{

		if ("showOrders".equals(request.getAction())) {
			selectedUser = request.getString("user");
			System.out.println(">>" + selectedUser);
			//arrayUsers.clear();
			
			Query<ShoppingCart> q = db.query(ShoppingCart.class);
			q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this.getLogin().getUserName()));
			q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, true));
			//q.addRules(new QueryRule(ShoppingCart.APPROVED, Operator.EQUALS, false));

			if (!q.find().isEmpty()) {
				UserOrders.addAll(q.find());
				
			} else {
				this.getModel().getMessages().add(new ScreenMessage("No orders found!", false));
			}
			
			loadMatrix(db);
		} else if ("approveOrder".equals(request.getAction())) {
				
			//ApproveOrders(db, shoppingCartList, this.getLogin().getUserName());
		}
		
	}
	
	
	@Override
	public void reload(Database db) {
		this.arrayUsers.clear();
		try {
			for (MolgenisUser u: db.find(MolgenisUser.class)) {
				this.arrayUsers.add(u);
			}
			
			
			
			
			
			
		} catch (DatabaseException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
				
	}
	
	public void loadMatrix(Database db) {
		// Load the fancy matrix with the user orders 
		
		//plugins.matrix.CatalogueMatrix.class
		matrixModel.setError(false);    //getModel().error=false;
		
		if(matrixModel.getMatrixViewerCat() != null){
			
			matrixModel.getMatrixViewerCat().setDatabase(db);
		
		} else if (matrixModel.getMatrixViewerCat() == null) {		//the matrix is completely new

			List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
			//filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, ObservationTarget.INVESTIGATION_NAME, 
			//	Operator.EQUALS, "DataShaper"));
			String userName = this.getApplicationController().getLogin().getUserName();

			Query<ShoppingCart> q = db.query(ShoppingCart.class);
			
			q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, userName));
			
			q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, true));
			
			//q.addRules(new QueryRule(ShoppingCart.APPROVED, Operator.EQUALS, true));
			
			List<String> listMeas = new ArrayList<String>();
			
			try {
				if(q.find().size() > 0)
				{
					List<ShoppingCart> shoppingCartList = q.find();
					
					for(ShoppingCart eachCart : shoppingCartList)
					{
						listMeas.addAll(eachCart.getMeasurements_Name());
					}
					
					listMeas = removeDuplication(listMeas);
					
					System.out.println("ADFADSFNADIS:F DSIFN DSAF OADS FONDS F DS::: " + listMeas.size());
					
					try {
						matrixViewerCat = new MatrixViewer(this, matrixModel.getCATMATRIX(), 
								new SliceablePhenoMatrix(ObservationTarget.class, Measurement.class), 
								true, true, true, filterRules, 
								new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, listMeas));
						matrixModel.setMatrixViewerCat(matrixViewerCat);
//						matrixModel.setMatrixViewerCat((new MatrixViewer(this, matrixModel.getCATMATRIX(), 
//								new SliceablePhenoMatrix(ObservationTarget.class, Measurement.class), 
//								true, true, true, filterRules, 
//								new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, listMeas))));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					matrixModel.getMatrixViewerCat().setDatabase(db);
				}
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	public void setArrayUsers(List<MolgenisUser> arrayUsers) {
		this.arrayUsers = arrayUsers;
	}

	public List<MolgenisUser> getArrayUsers() {
		return arrayUsers;
	}
	
	public List<ShoppingCart> getUserOrders() {
		return UserOrders;
	}
	
	public String getMatrixModel() {
		return matrixModel.getMatrixViewerIndv();
	}
}