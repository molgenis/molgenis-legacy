/* Date:        October 5, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.xqtl.minigui;

import java.util.List;

import org.molgenis.auth.DatabaseLogin;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.core.RuntimeProperty;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.render.LinkoutRenderDecorator;
import org.molgenis.framework.ui.html.render.RenderDecorator;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;

import filehandling.storage.StorageHandler;

public class CleanHome extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5307970595544892186L;

	private boolean userIsAdminAndDatabaseIsEmpty;
	private String validpath;
	private boolean loggedIn;
	private RenderDecorator linkouter;
	private StorageHandler sh;
	private Boolean hideLoginButtons;
	public static final String XQTL_HOMESCREEN_HIDELOGINBUTTONS = "xqtl_homescreen_hideloginbuttons";

	public RenderDecorator getLinkouter()
	{
		return linkouter;
	}

	public boolean isLoggedIn()
	{
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn)
	{
		this.loggedIn = loggedIn;
	}

	public String getValidpath()
	{
		return validpath;
	}

	public Boolean getHideLoginButtons()
	{
		return hideLoginButtons;
	}

	public void setValidpath(String validpath)
	{
		this.validpath = validpath;
	}

	public boolean isUserIsAdminAndDatabaseIsEmpty()
	{
		return userIsAdminAndDatabaseIsEmpty;
	}

	public void setUserIsAdminAndDatabaseIsEmpty(boolean userIsAdminAndDatabaseIsEmpty)
	{
		this.userIsAdminAndDatabaseIsEmpty = userIsAdminAndDatabaseIsEmpty;
	}

	public CleanHome(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_xqtl_minigui_CleanHome";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/xqtl/minigui/CleanHome.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
	}

	private void queryHideLoginSetting(Database db)
	{

		try
		{
			List<RuntimeProperty> rp = db.find(RuntimeProperty.class, new QueryRule(RuntimeProperty.NAME,
					Operator.EQUALS, XQTL_HOMESCREEN_HIDELOGINBUTTONS));

			if (rp.size() == 1 && rp.get(0).getValue().equals("false"))
			{
				this.hideLoginButtons = false;
			}
			else if (rp.size() == 1 && rp.get(0).getValue().equals("true"))
			{
				this.hideLoginButtons = true;
			}
			else
			{
				this.hideLoginButtons = false;
			}
		}
		catch (DatabaseException e)
		{
			this.setMessages(new ScreenMessage("Could not query runtime propery: " + e.getMessage(), false));
		}
	}

	@Override
	public void reload(Database db)
	{
		if (linkouter == null)
		{
			linkouter = new LinkoutRenderDecorator();
		}

		queryHideLoginSetting(db);

		sh = new StorageHandler(db);

		// HtmlSettings.uiToolkit = UiToolkit.ORIGINAL;

		if (this.getLogin().isAuthenticated())
		{
			this.setLoggedIn(true);
		}
		else
		{
			this.setLoggedIn(false);
		}

		if (this.getLogin() instanceof DatabaseLogin)
		{
			try
			{
				// fails when there is no table 'MolgenisUser', or no
				// MolgenisUser named 'admin'
				// assume database has not been setup yet
				db.find(MolgenisUser.class, new QueryRule("name", Operator.EQUALS, "admin")).get(0);
			}
			catch (Exception e)
			{
				// setup database and report back
				String report = ResetXgapDb.reset(this.getDatabase(), true);
				if (report.endsWith("SUCCESS"))
				{
					this.setMessages(new ScreenMessage("Database setup success!", true));
				}
				else
				{
					this.setMessages(new ScreenMessage("Database setup fail! Review report: " + report, false));
				}
			}

			try
			{
				// show special dataloader box for admin when the database has
				// no investigations
				if (this.getLogin().getUserName().equals("admin"))
				{
					List<Investigation> invList = db.find(Investigation.class);
					if (invList.size() == 0)
					{

						// flip bool to enable box
						setUserIsAdminAndDatabaseIsEmpty(true);

						// since we're now showing the special box,
						// find out if there is a validated path and save this
						// info
						if (sh.hasValidFileStorage(db))
						{
							this.setValidpath(sh.getFileStorage(true, db).getAbsolutePath());
						}
						else
						{
							this.setValidpath(null);
						}

					}
					else
					{
						setUserIsAdminAndDatabaseIsEmpty(false);
					}
				}
				else
				{
					setUserIsAdminAndDatabaseIsEmpty(false);
				}
			}
			catch (Exception e)
			{
				// something went wrong, set boolean to false for safety
				setUserIsAdminAndDatabaseIsEmpty(false);
			}

		}
		else
		{
			// for simplelogin, just check if there are investigations present
			try
			{
				List<Investigation> invList = db.find(Investigation.class);
				if (invList.size() == 0)
				{

					// flip bool to enable box
					setUserIsAdminAndDatabaseIsEmpty(true);

					// since we're now showing the special box,
					// find out if there is a validated path and save this info
					if (sh.hasValidFileStorage(db))
					{
						this.setValidpath(sh.getFileStorage(true, db).getAbsolutePath());
					}
					else
					{
						this.setValidpath(null);
					}
				}
				else
				{
					setUserIsAdminAndDatabaseIsEmpty(false);
				}

			}
			catch (Exception e)
			{
				// something went wrong, set boolean to false for safety
				setUserIsAdminAndDatabaseIsEmpty(false);
			}

		}
	}

	@Override
	public boolean isVisible()
	{
		// you can use this to hide this plugin, e.g. based on user rights.
		// e.g.
		// if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
}
