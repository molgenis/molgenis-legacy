/**
 * File: invengine.screen.MenuController <br>
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

import java.io.PrintWriter;

import org.molgenis.framework.db.Database;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;




public class HtmlPluginController<E extends Entity> extends SimpleController<E, HtmlPluginModel<E>>
{

	// member variables
	/** */
	private HtmlPluginModel<E> screen;

	/** */
	//private static final transient Logger logger = Logger.getLogger(HtmlPluginController.class);
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HtmlPluginController(HtmlPluginModel<E> view)
	{
		super(view);
		this.screen = view;
	}

	// Controller overrides
	@Override
	public void handleRequest(Database db, Tuple request)
	{
		this.doSelect(request);
	}

	@Override
	public void reload(Database db)
	{
		// refresh self (empty)

		// update children (done automatically)
		// TODO: support more than one object? MS: Maybe, but is complex.
		for (ScreenModel<?> v : screen.getChildren())
		{
			v.getController().reload(db);
		}
	}

	// access methods
	/**
	 * Request to choose which subscreen is whosed.
	 * 
	 * @param request Treemap containing all the data from the http-request.
	 */
	public boolean doSelect(Tuple request)
	{
		if (request.getString("select") != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public void handleRequest(Database db, Tuple request, PrintWriter out) {
		this.handleRequest(db, request);
		
	}


}
