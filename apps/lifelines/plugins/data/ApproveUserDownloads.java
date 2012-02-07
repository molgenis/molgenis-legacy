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
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 *
 */
public class ApproveUserDownloads extends PluginModel<Entity>
{
	
	private static final long serialVersionUID = -6096870458186217098L;
	private List<MolgenisUser> arrayUsers = new ArrayList<MolgenisUser>();
	private String selectedUser;
	private List<ShoppingCart> UserDownloads = new ArrayList<ShoppingCart>();
	private List<Integer> shoppingCartIds = new ArrayList<Integer>();
	public ApproveUserDownloads(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	@Override
	public String getViewName()
	{
		return "plugins_data_ApproveUserDownloads";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/data/ApproveUserDownloads.ftl";
	}
	
	public String getCustomHtmlHeaders()
    {
        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/shopping_cart.css\">";
    }

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception	{

		if ("showDownloads".equals(request.getAction())) {
			UserDownloads.clear();
			selectedUser = request.getString("user");
			System.out.println(">>" + selectedUser);
			//arrayUsers.clear();
			
			Query<ShoppingCart> q = db.query(ShoppingCart.class);
			q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this.getLogin().getUserName()));
			q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, true));
			q.addRules(new QueryRule(ShoppingCart.APPROVED, Operator.EQUALS, false));

			if (!q.find().isEmpty()) {
				UserDownloads.addAll(q.find());
				for (int i=0; i<UserDownloads.size(); i++) 
					shoppingCartIds.add(UserDownloads.get(i).getId());
				
			} else {
				this.getModel().getMessages().add(new ScreenMessage("No Downloads found!", false));
			}
			
		} else if ("ApproveSelectedDownloads".equals(request.getAction())) {
			System.out.println("the user has pressed approve , now let's retrieve the selected items ");
			String selecteditems =  request.getString("approvedItems");
			
			String[] temp;
			String delimiter = ",";
			temp = selecteditems.split(delimiter);
			for(int i =0; i < temp.length ; i++) {
				temp[i] = temp[i].replace("[","").replace("]", "").trim();
			    System.out.println("-->"+ temp[i]);
			}
			
			for (int i=0; i< temp.length; i++) {
				
				ShoppingCart sc = db.query(ShoppingCart.class).eq(ShoppingCart.ID, temp[i]).find().get(0);
				sc.setApproved(true);
				db.update(sc);
			}
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

	public List<MolgenisUser> getArrayUsers() {
		return arrayUsers;
	}
	
	public List<ShoppingCart> getUserDownloads() {
		return UserDownloads;
	}

	public List<Integer> getShoppingCartIds() {
		return shoppingCartIds;
	}
}