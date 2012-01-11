package plugins.data;

import gcc.catalogue.ShoppingCart;
import gcc.catalogue.UserMeasurements;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.Measurement;
import org.molgenis.util.Entity;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

public class OldPlacedOrders extends PluginModel<Entity>{


	private static final long serialVersionUID = -8140222842047905408L;
	private ShoppingCart shoppingCart = null;
	private List<ShoppingCart> shoppingCartList = null;
	
	public OldPlacedOrders(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_data_OldPlacedOrders";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/data/OldPlacedOrders.ftl";
	}
	
	public String getCustomHtmlHeaders()
    {
        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/shopping_cart.css\">";
    }

	
	@Override
	public void handleRequest(Database db, Tuple request) throws HandleRequestDelegationException, Exception {
		
		if ("DeleteOldOrders".equals(request.getAction())) {
	
			UserMeasurements userMeasurement = db.find(UserMeasurements.class, new QueryRule(UserMeasurements.USERID, Operator.EQUALS, this.getLogin().getUserName())).get(0);
			db.remove(userMeasurement);
			this.DeleteOldOrders(db);
			this.reload(db);
		}
		
	}

	public void DeleteOldOrders(Database db) {
		//empty db table: actually delete the ones that have checkedOut='false' 
		List<ShoppingCart> resshoppingCart  = new ArrayList<ShoppingCart>();
		Query<ShoppingCart> q = db.query(ShoppingCart.class);
		q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this.getLogin().getUserName()));
		q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, true));
		try {
			resshoppingCart = q.find();
			db.remove(resshoppingCart);

		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		this.reload(db);
		this.getModel().getMessages().add(new ScreenMessage("Your old orders have been deleted", true));
	}
	
	@Override
	public void reload(Database db) {
		try {
			Query<ShoppingCart> q = db.query(ShoppingCart.class);
			q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this.getLogin().getUserName()));
			q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, true));

			if (!q.find().isEmpty()) {
				shoppingCart = q.find().get(0);
				//Same user could order multiple times
				shoppingCartList = new ArrayList<ShoppingCart>();
				for(ShoppingCart order : q.find()){
					shoppingCartList.add(order);
				}
				
				fakeProveMethod(db, shoppingCartList, this.getLogin().getUserName());
				
			} else {
				shoppingCart = null;
				shoppingCartList = null;
			}

		} catch (Exception e) {
			this.getModel().getMessages().add(new ScreenMessage("No old orders available", false));
			e.printStackTrace();
		}

	}

//	public ShoppingCart getshoppingCart() {
//		return shoppingCart;
//	}
	public List<ShoppingCart> getshoppingCart(){
		return shoppingCartList;
	}
	
	public void fakeProveMethod(Database db, List<ShoppingCart> shoppingCartList, String userName) throws DatabaseException{
		
		UserMeasurements newUserWithMeasurements;
		
		if(db.find(UserMeasurements.class, new QueryRule(UserMeasurements.USERID, Operator.EQUALS, userName)).size() > 0){
			
			newUserWithMeasurements = db.find(UserMeasurements.class, new QueryRule(UserMeasurements.USERID, Operator.EQUALS, userName)).get(0);
			
			List<Integer> allMeasurementList = newUserWithMeasurements.getMeasurements_Id();
			
			for(ShoppingCart eachOrder : shoppingCartList){
				allMeasurementList.addAll(eachOrder.getMeasurements_Id());
			}
			
			allMeasurementList = removeDuplication(allMeasurementList);
			
			newUserWithMeasurements.setMeasurements_Id(allMeasurementList);
			
			db.update(newUserWithMeasurements);
			
		}else{
			
			newUserWithMeasurements = new UserMeasurements();
			
			newUserWithMeasurements.setUserID(userName);
			
			List<Integer> allMeasurementList = new ArrayList<Integer>();
			
			for(ShoppingCart eachOrder : shoppingCartList){
				allMeasurementList.addAll(eachOrder.getMeasurements_Id());
			}
			
			allMeasurementList = removeDuplication(allMeasurementList);
			
			newUserWithMeasurements.setMeasurements_Id(allMeasurementList);
			
			db.add(newUserWithMeasurements);
		}
		
	}
	
	public List<Integer> removeDuplication (List<Integer> allMeasurementList){
		
		List<Integer> temporaryList = new ArrayList<Integer>();
		
		for(Integer m : allMeasurementList)
		{
			if(!temporaryList.contains(m))
			{
				temporaryList.add(m);
			}
		}
		return temporaryList;
	}
}
