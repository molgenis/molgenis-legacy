/* Date:        December 3, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 */

package plugins.header;

import org.molgenis.auth.DatabaseLogin;
//import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
//import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

//import plugins.emptydb.emptyDatabase;
//import app.FillMetadata;




public class BbmriHeader extends PluginModel<Entity>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5516712543692105018L;
	private String userLogin = new String();
	
	public BbmriHeader(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	@Override
	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"bbmri/css/bbmri_colors.css\">" + "\n"  ;
	}
	
	
	@Override
	public String getViewName()
	{
		return "plugins_header_BbmriHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/header/BbmriHeader.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception
	{
		if ("doLogout".equals(request.getAction())) {
				getLogin().logout(db);
		}
	}

//	private void prefillDb(Database db) {
//		try {
//			
//			// Empty DB and run generated sql scripts
//			new emptyDatabase(db, false);
//			FillMetadata.fillMetadata(db, false, "SimpleUserLoginPlugin");
//			
//			this.getMessages().add(new ScreenMessage("Your database was empty, so it was prefilled with entities needed to make bbmri application run", true));
//		} catch (Exception e) {
//			String message = "Something went wrong while trying to prefill your database";
//			if (e.getMessage() != null) {
//				message += (": " + e.getMessage());
//			}
//			this.getMessages().add(new ScreenMessage(message, false));
//			e.printStackTrace();
//		}
//	}
	
	@Override
	public void reload(Database db)
	{
//		try {
//			int nrOfUsersInDb = db.count(MolgenisUser.class);
//			if (nrOfUsersInDb == 0) { // Check if DB is filled by counting the nr. of users (should always be >= 2)
//				prefillDb(db);
//			}
//		} catch (Exception e) {
//			prefillDb(db);
//		}
		
		this.setUserLogin();
	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}

//	public void Logout() {
//		if (this.getLogin().isAuthenticated()) {
//			getLogin().logout();
//		}	
//	}
	
	public void setUserLogin() {
		if (this.getLogin().isAuthenticated()) {
			this.userLogin = "<a href='molgenis.do?__target=main&select=SimpleUserLogin'>" + "Welcome " + ((DatabaseLogin)this.getLogin()).getFullUserName() + "</a>";
			this.userLogin += "<a href='molgenis.do?__target=MolgenisHeader&select=SimpleUserLogin&__action=doLogout'>" + " | Logout " + "</a>";
		} else {
			//this.userLogin = "<a href='http://vm7.target.rug.nl/bbmri_gcc/molgenis.do?__target=main&select=UserLogin'>" + "Login" + "</a>";
			this.userLogin = "<a href='molgenis.do?__target=main&select=SimpleUserLogin'>" + "Login" + "</a>";
		}
		
	}

	public String getUserLogin() {
		
		return userLogin;
	}
}
