/*
 * Date: March 25, 2011 Template: PluginScreenJavaTemplateGen.java.ftl
 * generator: org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.scrum.plugins;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.Molgenis;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.scrum.Sprint;
import org.molgenis.scrum.Story;
import org.molgenis.scrum.TaskHistory;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class UpdateDatabase extends PluginModel<Entity>
{
	public UpdateDatabase(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_scrum_plugins_UpdateDatabase";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/scrum/plugins/UpdateDatabase.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		if (request.getAction().equals("updateDatabase")) {
			try {
				new Molgenis("apps/scrum/org/molgenis/scrum/scrum.properties").updateDb(true);
				this.setMessages(new ScreenMessage("Database updated successfully", true));
			} catch (Exception e) {
				e.printStackTrace();
				this.setMessages(new ScreenMessage("Database update failed: " + e.getMessage(), false));
			}
		}
	}

	@Override
	public void reload(Database db)
	{
		//
	}
}
