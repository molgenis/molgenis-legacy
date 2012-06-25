package org.molgenis.feedback;

import org.apache.commons.lang.StringUtils;
import org.molgenis.auth.DatabaseLogin;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.EmailService;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class FeedbackForm extends PluginModel<Entity>
{
	private String feedback = null;
	private static final long serialVersionUID = 4701628601897969977L;
	
	public FeedbackForm(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_feedback_FeedbackForm";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/feedback/FeedbackForm.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{	
			String appName = this.getController().getApplicationController().getMolgenisContext().getVariant();
			if ("sendFeedback".equals(request.getAction())) {
				feedback = "User: " + request.getString("name") + " (username: " + this.getLogin().getUserName() + 
						") sent:\n\n" + request.getString("feedback") + "\n\nabout: " + request.getString("plugin") + " in app '" + appName +"'";
				
				// get admin email
				MolgenisUser admin = db.query(MolgenisUser.class).eq(MolgenisUser.NAME, "admin").find().get(0);
				if (StringUtils.isEmpty(admin.getEmail()))
					throw new DatabaseException("Sending feedback failed: the administrator has no email address set. Please contact your administrator about this.");
				
				EmailService ses = this.getEmailService();
				ses.email("New feedback on "+appName+"", feedback, admin.getEmail(), true);
				
				this.getMessages().add(new ScreenMessage(feedback, true));
			}
			
			if ("resetFeedbackForm".equals(request.getAction())) {
				feedback = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			//this.setError(e.getMessage());
		}
	}
	
	public String getActivePlugin() {
		if (this.getParent().getSelected() == null) {
			return "";
		}
		ScreenModel model = this.getParent().getSelected();
		while (model.getSelected() != null) {
			model = model.getSelected();
		}
		return model.getLabel();
	}

	public String getFullUserName() {
		
		if (this.getLogin().isAuthenticated()) {
			return ((DatabaseLogin)this.getLogin()).getFullUserName();
		}
		return null;
	}
	
	public String getFeedback() {
		return feedback;
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}

	@Override
	public void reload(Database db) {
		// TODO Auto-generated method stub
		
	}

}
