/**
 * File: invengine.screen.View <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li> 2005-05-07; 1.0.0; MA Swertz; Creation.
 * <li> 2005-12-02; 1.0.0; RA Scheltema; Moved to the new structure, made the
 * method reset abstract and added documentation.
 * <li> 2006-5-14; 1.1.0; MA Swertz; refactored to separate controller and view
 * </ul>
 */

package org.molgenis.framework.ui;

// jdk
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.framework.ui.commands.CommandMenu;
import org.molgenis.framework.ui.commands.ScreenCommand;
import org.molgenis.util.EmailService;
import org.molgenis.util.FileLink;
import org.molgenis.util.SimpleTree;

// jdk

/**
 * Base-class for a screen displaying information from the invengine system to
 * the user.
 */
public abstract class SimpleModel extends SimpleTree<ScreenModel> implements ScreenModel, Serializable
{
	// member variables
	/** Logger */
	protected final transient Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	/** Bind parameter name for screen target (to be used by layout renderer) */
	public static final String INPUT_TARGET = "__target";
	/** Bind parameter name for screen action (to be used by layout renderer) */
	public static final String INPUT_ACTION = "__action";
	/** Label to show on top of the screen */
	private String label;
	/** The controller that handles requests on this screen */
	private ScreenController controller;
	/** The name of the view to be used. */
	private String viewName;
	/** Determines which of the subscreens should be shown */
	private String selectedId;
	/**
	 * Menu is a two-dimensional map: first dimension is menu's, second is the
	 * menuitems. Submenu's are not yet supported. Option: make this a class
	 * structure with special "submenu" commands to allow submenu's.
	 */
	private Map<String, CommandMenu> menubar = new LinkedHashMap<String, CommandMenu>();

	/**
	 * @param name
	 *            The name of this screen (needs to be unique in the
	 *            tree-container).
	 * @param parent
	 *            The parent of this screen.
	 */
	public SimpleModel(String name, ScreenModel parent)
	{
		super(name, parent);

		reset();
		if (parent != null)
		{
		}
	}

	/**
	 * Reset all view settings to default.
	 */
	public void reset()
	{

	}

	public UserInterface getRootScreen()
	{
		return (UserInterface) this.getRoot();
	}

	@Override
	public EmailService getEmailService()
	{
		return ((UserInterface) this.getRoot()).getEmailService();
	}

	public FileLink getTempFile() throws IOException
	{
		return getRootScreen().getTempFile();
	}

	/**
	 * Set the pretty label to show on screen.
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * @return Pretty label to show on screen
	 */
	public String getLabel()
	{
		if (label == null) return this.getName();
		return label;
	}

	/**
	 * The controller for this screen. The controller holds all the manipulation
	 * methods of the screen. (while the screen intself only contains state).
	 */
	public ScreenController getController()
	{
		return controller;
	}

	public void setController(ScreenController controller)
	{
		this.controller = controller;
	}

	@Override
	public String getViewName()
	{
		return viewName;
	}

	public void setViewMacro(String layouter)
	{
		this.viewName = layouter;
	}

	/**
	 * @return The sub-screen of this menu, which has been selected with the
	 *         http-request.
	 */
	public ScreenModel getSelected()
	{
		if (getChild(selectedId) != null)
		{
			return getChild(selectedId);
		}
		if (getChildren().size() > 0)
		{
			if (getChildren().firstElement() instanceof ScreenModel) return getChildren().firstElement();
		}
		return null;
	}

	/**
	 * @param viewid
	 */
	@Override
	public void setSelected(String viewid)
	{
		// check if the path to this is also selected
		if (this.getParent() != null)
		{
			logger.debug("call setselected on parent");
			this.getParent().setSelected(this.getName());
		}

		logger.debug("Screen " + this.getName() + " selected " + viewid);
		this.selectedId = viewid;
	}

	/**
	 * @param view
	 *            A sub-screen of this menu.
	 * @return Whether the given sub-screen has been selected.
	 */
	public boolean isSelected(ScreenModel view)
	{
		ScreenModel selected = getSelected();

		if (selected != null && view != null)
		{
			return selected.getName().equals(view.getName());
		}
		else
		{
			return false;
		}
	}

	/** COMMANDS for on the menu bar. Idea: move to screen? * */
	public <E extends ScreenCommand> void addCommand(E command)
	{
		// link the command to the screen
		command.setScreen(this);
		command.setTarget(this.getName());

		// commands must have a unique id
		if (getCommand(command.getName()) != null)
		{
			logger.warn("command with name '" + command.getName() + "' already exists; replaced");
		}

		// create new menu if not exists
		if (menubar.containsKey(command.getMenu()) == false)
		{
			menubar.put(command.getMenu(), new CommandMenu(command.getMenu(), this, command.getMenu(), "", ""));
		}

		// put the command in the menu
		menubar.get(command.getMenu()).addCommand(command);
		this.logger.debug("added action " + command.getName());
	}

	public ScreenCommand getCommand(String commandID)
	{
		for (CommandMenu menu : menubar.values())
		{
			if (menu.getCommand(commandID) != null) return menu.getCommand(commandID);
		}
		return null;
	}

	public Collection<CommandMenu> getMenus()
	{
		return menubar.values();
	}

	@Override
	public String getViewTemplate()
	{
		return null;
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		// if (this.getSelected() != null)
		// {
		// return this.getSelected().getCustomHtmlHeaders();
		// }
		// else
		// {
		String result = "";
		for (ScreenModel m : this.getChildren())
		{
			result += m.getCustomHtmlHeaders();
		}
		return result;
		// }

	}

	@Override
	public String getCustomHtmlBodyOnLoad()
	{
		String result = "";
		for (ScreenModel m : this.getChildren())
		{
			result += m.getCustomHtmlBodyOnLoad();
		}
		return result;
	}
}
