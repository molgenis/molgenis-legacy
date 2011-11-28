/* Date:        November 11, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.header;

import mx4j.log.Logger;

import org.molgenis.auth.DatabaseLogin;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import app.FillMetadata;

import plugins.emptydb.emptyDatabase;
import plugins.fillanimaldb.FillAnimalDB;

import commonservice.CommonService;


/**
 * A simple plugin to create the header of the MOLGENIS application. This
 * includes the header logo as well as the top level menu items for
 * documentation, services etc (replaces the hardcoded header).
 * 
 * @author Morris Swertz
 */
public class AnimalDBHeader extends PluginModel<Entity>
{

	private static final long serialVersionUID = 4701628601897969977L;
	
	public AnimalDBHeader(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_header_AnimalDBHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/header/AnimalDBHeader.ftl";
	}

	@Override
	public void reload(Database db)
	{
		try {
			int nrOfUsersInDb = db.count(MolgenisUser.class);
			if (nrOfUsersInDb == 0) { // Check if DB is filled by counting the nr. of users (should always be >= 2)
				prefillDb(db);
			}
		} catch (Exception e) {
			prefillDb(db);
		}
	}
	
	private void prefillDb(Database db) {
		try {
			// Empty DB and run generated sql scripts
			new emptyDatabase(db, false);
			FillMetadata.fillMetadata(db, false);
			
			// Populate db with targets, features, values etc. needed to make AnimalDB run
			FillAnimalDB myFillAnimalDB = new FillAnimalDB(db);
			myFillAnimalDB.populateDB(this.getLogin());
			
			logger.info("Your database was empty, so it was prefilled with entities needed to make AnimalDB run");
		} catch (Exception e) {
			String message = "Something went wrong while trying to prefill your database";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			logger.info(message);
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleRequest(Database db, Tuple request) throws Exception
	{
		if ("doLogout".equals(request.getAction())) {

				getLogin().logout(db);
		}
	}
	
	public String getUserLogin() {
		String userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Login" + "</a>";
		if (this.getLogin().isAuthenticated()) {
			userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Logged in as: " + ((DatabaseLogin)this.getLogin()).getFullUserName() + "</a>";
			userLogin += "<span style=\"color:black\">&nbsp;|&nbsp;</span>";
			userLogin += "<a href='molgenis.do?__target=MolgenisHeader&select=UserLogin&__action=doLogout'>" + "Logout " + "</a>";
		}
		return userLogin;
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}
	
}
