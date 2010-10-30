package org.molgenis.framework.ui.commands;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.Templateable;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.util.Entity;


public class CommandMenu extends SimpleCommand
{
	public static final transient Logger logger = Logger.getLogger(CommandMenu.class);

	/** menu items with order as entered*/
	private Map<String,ScreenCommand> menu_items = new LinkedHashMap<String,ScreenCommand>();

	public <E extends Entity>CommandMenu(String id, ScreenModel screen, String label, String icon, String action)
	{
		super( id, screen );
		this.setLabel(label);
		this.setIcon(icon);
		this.setJavaScriptAction(action);
	}

	/** for enabeling submenus **/
	public String toJavascript( Templateable s )
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Add a menu item.
	 * @param command
	 */
	public void addCommand( ScreenCommand command )
	{
		if(menu_items.containsKey(command.getName()))
		{
			logger.warn("addCommand: command with id '"+command.getName()+"' already exists; replaced");
		}
		menu_items.put(command.getName(),command);
	}
	
	/**
	 * Return the values as list.
	 *
	 */
	public Collection<ScreenCommand> getCommands()
	{
		//Logger.getLogger("test").debug("returning commands "+menu_items.values().size());
		return menu_items.values();
	}

	/**
	 * Find a specific command.
	 * @param name
	 */
	public ScreenCommand getCommand( String name )
	{
		return menu_items.get(name);
	}

	@Override
	public List<HtmlInput> getActions()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HtmlInput> getInputs() throws DatabaseException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
