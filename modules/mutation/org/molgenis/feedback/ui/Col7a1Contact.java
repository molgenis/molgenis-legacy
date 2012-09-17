
package org.molgenis.feedback.ui;

import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;

/**
 * Chd7ContactController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>Chd7ContactModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>Chd7ContactView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class Col7a1Contact extends Contact
{
	private static final long serialVersionUID = 1L;

	public Col7a1Contact(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new ContactModel(this)); //the default model
		this.getModel().setEmailTo("p.c.van.den.akker@medgen.umcg.nl");
		this.getModel().setText("If you have any comments, questions or suggestions to improve the DEB patient registry, please do not hesitate to contact us. Please enter your name, a valid email address and your message and press \"submit\". We will reply shortly.");
	}
	
	public ScreenView getView()
	{
		return new FreemarkerView("Contact.ftl", getModel());
	}
}