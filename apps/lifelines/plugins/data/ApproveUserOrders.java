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
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 *
 */
public class ApproveUserOrders extends PluginModel<Entity>
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6096870458186217098L;
	private List<MolgenisUser> arrayUsers = new ArrayList<MolgenisUser>();
	private String selectedUser;
	private List<ShoppingCart> UserOrders = new ArrayList<ShoppingCart>();
	
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
			arrayUsers.clear();
		} else if ("approveOrder".equals(request.getAction())) {
			
			Query<ShoppingCart> q = db.query(ShoppingCart.class);
			q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this.getLogin().getUserName()));
			q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, true));
			q.addRules(new QueryRule(ShoppingCart.APPROVED, Operator.EQUALS, false));

			if (!q.find().isEmpty()) {
				UserOrders.addAll(q.find());
				
			}
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
		}
		
				
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
}