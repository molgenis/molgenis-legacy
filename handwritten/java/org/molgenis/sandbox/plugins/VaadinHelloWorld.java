package org.molgenis.sandbox.plugins;

import javax.servlet.http.HttpSession;

import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.UserInterface;
import org.molgenis.organization.Investigation;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class VaadinHelloWorld extends Application
{

	private static final long serialVersionUID = 7013203779959795804L;

	private boolean showAddressbook = false;

	@Override
	public void init()
	{

		this.setTheme("molgenis");
		initHello();
	}
	

	private void initHello()
	{
		//todo: to really integrate we must have access to MOLGENIS user interface elements.
		WebApplicationContext context = (WebApplicationContext) getContext();
		HttpSession session = context.getHttpSession();
		UserInterface molgenis = (UserInterface<?>) session.getAttribute("application");
		Investigation currentInvestigation = ((FormModel<Investigation>)molgenis.get("Investigations")).getCurrent();
		

		Window mainWindow = new Window("My first Vaadin Application");
		this.setMainWindow(mainWindow);

		final Label myLabel = new Label();
		myLabel.setValue("Hello world");
		mainWindow.addComponent(myLabel);

		final TextField myText = new TextField();
		myText.setCaption("Hello ...investigation="+currentInvestigation.getName());
		mainWindow.addComponent(myText);
		myText.setStyleName("myspacing");

		final Button changeButton = new Button();
		changeButton.setCaption("Change hello");
		changeButton.addListener(new ClickListener()
		{
			public void buttonClick(ClickEvent event)
			{
				myLabel.setValue("Hello " + myText.getValue());
			}
		});
		mainWindow.addComponent(changeButton);
	}

}
