/* Date:        October 24, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.feedback.ui;

import javax.servlet.http.HttpServletRequest;

import nl.captcha.Captcha;

import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.SimpleEmailService.EmailException;
import org.molgenis.util.Tuple;

public class Contact extends EasyPluginController<ContactModel>
{
	private static final long serialVersionUID = 1708473767804277235L;

	public Contact(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new ContactModel(this)); //the default model
	}

	public ScreenView getView()
	{
		return new FreemarkerView("Contact.ftl", getModel());
	}
	
	@Override
	public void handleRequest(Database db, Tuple request)
	{
		this.getModel().setMessages();

		try
		{
			this.getModel().setAction(request.getAction());

			if ("send".equals(this.getModel().getAction()))
			{
				if (StringUtils.isEmpty(request.getString("name")))
					throw new IllegalArgumentException("Please enter a name.");
				if (StringUtils.isEmpty(request.getString("email")))
					throw new IllegalArgumentException("Please enter an email address.");
				if (StringUtils.isEmpty(request.getString("comments")))
					throw new IllegalArgumentException("Please enter your comments.");

				// get the http request that is encapsulated inside the tuple
				HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
				HttpServletRequest httpRequest   = rt.getRequest();

				Captcha captcha                  = (Captcha) httpRequest.getSession().getAttribute(Captcha.NAME);

				if (!captcha.isCorrect(request.getString("code")))
					throw new IllegalArgumentException("Code was wrong.");
					
				String emailContents = "New comment via the contact form:\n" +
					"Name: " + request.getString("name") + "\n"+
					"Email: " + request.getString("email") + "\n" +
					"Comments: " + request.getString("comments") + "\n";

				//assuming: 'encoded' p.w. (setting deObf = true)
				this.getEmailService().email("New comment", emailContents, this.getModel().getEmailTo(), true);
				this.getModel().getMessages().add(new ScreenMessage("Your comment has been successfully sent.", true));
			}
		}
		catch (IllegalArgumentException e)
		{
			this.getModel().getMessages().add(new ScreenMessage(e.getMessage(), false));
		}
		catch (EmailException e)
		{
			this.getModel().getMessages().add(new ScreenMessage(e.getMessage(), false));
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{
		this.populateContactForm();
	}

	private void populateContactForm()
	{
		this.getModel().setContactForm(new ContactForm());
		this.getModel().getContactForm().get("select").setValue(this.getName());
		this.getModel().getContactForm().get("__target").setValue(this.getName());
	}
}
