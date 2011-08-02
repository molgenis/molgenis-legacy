/* Date:        May 6, 2011
 * Template:	NewPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.NewPluginModelGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.feedback.ui;

import org.molgenis.framework.ui.EasyPluginModel;

/**
 * ContactModel takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class ContactModel extends EasyPluginModel
{
	//a system veriable that is needed by tomcat
	private static final long serialVersionUID = 1L;
	private String emailTo;
	private String action = "init";
	private String text   = "";
	private ContactForm contactForm;
	
	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ContactForm getContactForm() {
		return contactForm;
	}

	public void setContactForm(ContactForm contactForm) {
		this.contactForm = contactForm;
	}

	public ContactModel(Contact controller)
	{
		//each Model can access the controller to notify it when needed.
		super(controller);
	}
	
	
}
