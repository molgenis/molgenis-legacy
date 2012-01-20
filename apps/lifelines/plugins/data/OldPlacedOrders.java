package plugins.data;

import gcc.catalogue.ShoppingCart;

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
	private List<ShoppingCart> shoppingCartList = new ArrayList<ShoppingCart>();
	private boolean ApprovedordersChoice; /*false is for All orders, true for approved*/
	private String selectedOrdersChoice = null;

	
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
		
		if ("showOrders".equals(request.getAction())){
			selectedOrdersChoice = request.getString("ordersChoice");
			System.out.println("selected choice : "+request.getString("ordersChoice"));
			if (selectedOrdersChoice.equals("AllPlacedOrders")) setApprovedordersChoice(false);
			else if (selectedOrdersChoice.equals("ApprovedOrders")) setApprovedordersChoice(true);
			this.reload(db);
		} else if ("DeleteOldOrders".equals(request.getAction())) {
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

			if (!isApprovedordersChoice()) {
					shoppingCartList.clear();
					System.out.println(">>>>these are all the orders");
					//get all the items
					Query<ShoppingCart> q = db.query(ShoppingCart.class);
					q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this.getLogin().getUserName()));
					q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, true));
					if (!q.find().isEmpty()) {
						shoppingCart = q.find().get(0);
						//Same user could order multiple times
						for(ShoppingCart order : q.find()){
							shoppingCartList.add(order);
						}
					} else {
						shoppingCart = null;
						shoppingCartList.clear();
					}
			} else if (isApprovedordersChoice()){
				System.out.println(">>>>these are only the approved all the orders");
				shoppingCartList.clear();
				//get only the approved shopping cart 
				Query<ShoppingCart> aq = db.query(ShoppingCart.class);
				aq.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this.getLogin().getUserName()));
				aq.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, true));
				aq.addRules(new QueryRule(ShoppingCart.APPROVED, Operator.EQUALS, true));
	
				if (!aq.find().isEmpty()) {
					shoppingCart = aq.find().get(0);
					System.out.println("Gia na douem ti exei mesa >>>"+shoppingCart);

					//Same user could order multiple times
					for(ShoppingCart order : aq.find()){
						shoppingCartList.add(order);
					}
					
				} else {
					shoppingCart = null;
					shoppingCartList.clear();
				}
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
	
	public void setApprovedordersChoice(boolean approvedordersChoice) {
		ApprovedordersChoice = approvedordersChoice;
	}

	public boolean isApprovedordersChoice() {
		return ApprovedordersChoice;
	}
	
	public String getselectedOrdersChoice() {
		return selectedOrdersChoice;
	}

	
	
}
