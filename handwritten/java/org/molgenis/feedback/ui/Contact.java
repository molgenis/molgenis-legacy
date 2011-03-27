/* Date:        October 24, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.feedback.ui;

import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.mutation.service.MutationEmailService;
import org.molgenis.util.EmailService;
import org.molgenis.util.SimpleEmailService.EmailException;
import org.molgenis.util.Tuple;


public class Contact extends PluginModel
{
	private static String EMAIL_TO  = "p.c.van.den.akker@medgen.umcg.nl"; // TODO: read from MolgenisOptions
	private String action           = "init";
	private ContactForm contactForm = new ContactForm();

	public Contact(String name, ScreenModel parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_feedback_ui_Contact";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/feedback/ui/Contact.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		this.setMessages();

		try
		{
			this.action = request.getAction();

			if ("send".equals(this.action))
			{
				if (StringUtils.isEmpty(request.getString("name")))
					throw new IllegalArgumentException("Please enter a name.");
				if (StringUtils.isEmpty(request.getString("email")))
					throw new IllegalArgumentException("Please enter an email address.");
				if (StringUtils.isEmpty(request.getString("comments")))
					throw new IllegalArgumentException("Please enter your comments.");

				EmailService service = new MutationEmailService();
					
				String emailContents = "New comment via the contact form:\n" +
					"Name: " + request.getString("name") + "\n"+
					"Email: " + request.getString("email") + "\n" +
					"Comments: " + request.getString("comments") + "\n";

				service.email("New comment on COL7A1", emailContents, EMAIL_TO);
				this.getMessages().add(new ScreenMessage("Your comment has been successfully sent.", true));
			}
		}
		catch (IllegalArgumentException e)
		{
			this.getMessages().add(new ScreenMessage(e.getMessage(), false));
		}
		catch (EmailException e)
		{
			this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{
		this.populateContactForm();
	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
	
	public String getAction()
	{
		return this.action;
	}

	public Container getForm()
	{
		return this.contactForm;
	}

	private void populateContactForm()
	{
		this.contactForm.get("__target").setValue(this.getScreen().getName());
	}
}
