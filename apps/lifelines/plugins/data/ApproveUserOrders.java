package plugins.data;


import java.util.ArrayList;
import java.util.List;

import org.molgenis.auth.MolgenisRole;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
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

	
		
		//if ("".equals(request.getAction())) {

			
		//}

		
	}
	
	@Override
	public void reload(Database db) {
		this.arrayUsers.clear();
		try {
			for (MolgenisUser u: db.find(MolgenisUser.class)) {
				this.arrayUsers.add(u);
			}
			for (MolgenisRole r: db.find(MolgenisRole.class)) {
				r.getName();
				System.out.println(">>>>" + r);
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
}