/**
 * File: invengine.screen.MenuView <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li> 2005-05-07; 1.0.0; MA Swertz; Creation.
 * <li> 2005-12-02; 1.0.0; RA Scheltema; Moved to the new structure, made the
 * method reset abstract and added documentation.
 * <li> 2006-04-15; 1.0.0; MA Swertz; Documentation.
 * <li>2006-5-14; 1.1.0; MA Swertz; refactored to separate controller and view
 * </ul>
 */

package org.molgenis.framework.ui;

import org.molgenis.util.Entity;



// jdk

// invengine





/**
 * This class describes the functionality needed for a menu-screen. The
 * functionality of this class is used in the scripts generating the html-code
 * for this screen-variant.
 * 
 * @author MA Swertz
 * @version 1.0.0
 */
public class HtmlPluginModel<E extends Entity> extends SimpleModel
{

	/**
	 * @param name The name of this menu-screen (must be unique in the tree)
	 * @param parent The parent of this screen
	 */
	public HtmlPluginModel(String name, ScreenModel parent)
	{
		super(name, parent);	
		setController(new HtmlPluginController(this));
		setViewMacro(HtmlPluginModel.class.getSimpleName().replace("Model", "View"));
	}
	
	public String getHeader()
	{
		return "<div class=\"form_header\">"+this.getLabel()+"</div>";
	}

	public String getHtml()
	{
		return "<label>This page is under construction. A plugin will be made available here later. </label>";
	}
	
	/** */
	private static final long serialVersionUID = -1211550529820121110L;

	@Override
	public boolean isVisible()
	{
		return true;
	}

	@Override
	public String getViewTemplate()
	{
		return null;
	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub
		
	}
}
