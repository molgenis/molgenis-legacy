/**
 * File: invengine.screen.Controller <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2005-05-07; 1.0.0; MA Swertz; Creation.
 * <li>2005-12-02; 1.0.0; RA Scheltema; Moved to the new structure, made the
 * method reset abstract and added documentation.
 * <li>2006-5-14; 1.1.0; MA Swertz; refactored to separate controller and view
 * </ul>
 */

package org.molgenis.framework.ui;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.util.Tuple;




/**
 * Base-class for a screen displaying information from the invengine system to
 * the user.
 */
public abstract class SimpleController<S extends ScreenModel> implements ScreenController<S>,Serializable
{
	// member variables
	/** */
	protected ScreenModel screen;
	/** */
	protected static final transient Logger logger = Logger.getLogger(SimpleController.class);
	/** */
	static final long serialVersionUID = 5286068849305140609L;
	
	//constructor
	public SimpleController(S view)
	{
		this.screen = view;
		//logger = Logger.getLogger(this.getClass().getSimpleName() + ":" + view.getName());
	}

	// overloadable methods
	/**
	 * This method (re)loads the view, making persistant data actual again. This
	 * method needs to be called when the screen operates on, for instance, a
	 * recordset.
	 */
	public abstract void reload(Database db);

	/**
	 * This is the actual control-method, which changes the view. The view
	 * itself may delegate requests to other objects (e.g. delegate insert to
	 * the persistance layer). TODO: A superclass method can delegate all
	 * requests using reflection.
	 * 
	 * @param request The http-request in a convenient map.
	 */
	public abstract void handleRequest(Database db, Tuple request);
	
	public String getFromRequest(Tuple request, String name)
	{
		return request.getString(name);
	}

	/**
	 * This method calls the reset-function this instance and all the children
	 * of this instance. After this call the screens attached to this screen
	 * should be in a pre-defined valid state.
	 */
	public void doResetChildren()
	{
		for (ScreenModel subform : screen.getChildren())
		{
			subform.reset();
		}
	}
	
	public Templateable getScreen()
	{
		return this.screen;
	}
}
