/* Date:        December 3, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 */

package plugins.header;

import gcc.catalogue.Feedback;


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

//import plugins.autohidelogin.AutoHideLoginModel; 

public class catalogueHeader extends PluginModel<Entity>
{
	
	private String feedback = null;
	private static final long serialVersionUID = -4576352827620517694L;
	
	private String userLogin = new String();
	
	public catalogueHeader(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
//	@Override
//	public String getCustomHtmlHeaders()
//	{
//		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"bbmri/css/bbmri_colors.css\">" + "\n"  ;
//	}
	
	@Override
	public String getCustomHtmlHeaders() {
		return  "<script src=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.js\" language=\"javascript\"></script>\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.css\">" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.rounded.css\">" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/ctnotify/lib/jquery.ctNotify.roundedBr.css\">" +
				"<link rel=\"stylesheet\" href=\"res/css/catalogue/colors.css\">";		
	}
	
	@Override
	public String getViewName()
	{
		return "plugins_header_catalogueHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/header/catalogueHeader.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception
	{
		if ("doLogout".equals(request.getAction())) {
				getLogin().logout(db);
				
				//only for AutoHideLoginSwitchService, but harmless otherwise 
				//AutoHideLoginModel.isVisible = false; //TODO 
		}
		if ("sendFeedback".equals(request.getAction())) {
			feedback = "User: " + request.getString("name") + " (username: " + this.getLogin().getUserName() + 
					") sent:\n\n" + request.getString("feedback") + "\n\nabout: " + request.getString("plugin");
			
			// get admin email
			MolgenisUser admin = db.query(MolgenisUser.class).eq(MolgenisUser.NAME, "admin").find().get(0);
			if (StringUtils.isEmpty(admin.getEmail()))
				throw new DatabaseException("Sending feedback failed: the administrator has no email address set. Please contact your administrator about this.");
			
			
			EmailService ses = this.getEmailService();
			ses.email("New feedback on Lifelines Catalogue", feedback, admin.getEmail(), true);
			
			this.getMessages().add(new ScreenMessage(feedback, true));
			
			System.out.println("Email : " + admin.getEmail()+ "Feedback >>>"+ feedback);
			
			//save the feedback message in DB
			Feedback f = new Feedback(); 
			
			f.setFeedback(feedback);
			f.setName(name);
			
			db.add(f);
		}
		
		if ("resetFeedbackForm".equals(request.getAction())) {
			feedback = null;
		}
	}
	
	

	@Override
	public void reload(Database db)
	{
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

	public void setUserLogin() {
		
		if (this.getLogin().isAuthenticated()) {
			this.userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Welcome " + ((DatabaseLogin)this.getLogin()).getUserName() + "</a>";
			this.userLogin += "<a href='molgenis.do?__target=catalogueHeader&select=UserLogin&__action=doLogout'>" + " | Logout " + "</a>";
		} else {
			this.userLogin = "<a href='molgenis.do?__target=main&select=SimpleUserLogin'>" + "Login" + "</a>";
		}
		
	}
	
//	public void setUserLogin()
//	{
//		//if the AutoHideLoginSwitchService is enabled, use this style of redirecting in the output URLs
//		if(this.getApplicationController().getMolgenisContext().getUsedOptions().services.contains("plugins.autohidelogin.AutoHideLoginSwitchService@/autohideloginswitch"))
//		{
//			setUserLoginAutoHideService();
//		}
//		// else just use the regular one
//		// AS A FALLBACK ONLY: this header is specific for WormQTL
//		// and we want to use the auto-hide for this app
//		// but just keeping the code around doesn't hurt
//		else
//		{
//			setUserLoginRegular();
//		}
//	}
	
//	public void setUserLoginAutoHideService() {
//		if (this.getLogin().isAuthenticated()) {
//			this.userLogin = "<a href='autohideloginswitch'>" + "Logged in as: " + ((DatabaseLogin)this.getLogin()).getUserName() + "</a>";
//			this.userLogin += " | ";
//			this.userLogin += "<a href='molgenis.do?__target=MolgenisHeader&select=UserLogin&__action=doLogout'>" + "Logout " + "</a>";
//		} else {
//			this.userLogin = "<a href='autohideloginswitch'>" + "Login" + "</a>";
//		}	
//	}
//	
//	public void setUserLoginRegular() {
//		if (this.getLogin().isAuthenticated()) {
//			this.userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Welcome " + ((DatabaseLogin)this.getLogin()).getFullUserName() + "</a>";
//			this.userLogin += " | ";
//			//this.userLogin += "<a href='molgenis.do?__target=MolgenisHeader&select=UserLogin&__action=doLogout'>" + "Logout " + "</a>";
//			this.userLogin += "<a href='molgenis.do?__target=catalogueHeader&select=UserLogin&__action=doLogout'>" + " | Logout " + "</a>";
//
//		} else {
//			//this.userLogin = "<a href='molgenis.do?__target=main&select=UserLogin'>" + "Login" + "</a>";
//			this.userLogin = "<a href='molgenis.do?__target=main&select=SimpleUserLogin'>" + "Login" + "</a>";
//
//		}	
//	}
	
	public String getUserLogin() {
		
		return userLogin;
	}

	
	public String getFeedback() {
		return feedback;
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
}
