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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenModel.Show;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.util.EmailService;
import org.molgenis.util.FileLink;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tree;
import org.molgenis.util.Tuple;

/**
 * A controller changes the state of a ScreenModel.
 * 
 * <p>
 * The State and Transitions of Screens are seperated between Screen that
 * contains state of the screen, and ScreenController that implements
 * transitions. This separation prevends side-effects when generating the HTML
 * (see templates). It also eases understanding as Screen is reduced to a simple
 * Bean (that can be hold in a Session) and the Controllers that manipulate it.
 */
public interface ScreenController<MODEL extends ScreenModel> extends
		Serializable, Tree<ScreenController<?>>
{
	/**
	 * Refresh/reload the model.
	 * <p>
	 * reload() needs to be called when the screen works on external
	 * data/processes. For example:
	 * <ul>
	 * <li>a screen that works on a persistent database needs to requery the
	 * data on disk in order to show the most recent data.
	 * <li>a screen that monitors a long running process needs to retrieve the
	 * most recent progress information
	 * <li>Etc.
	 * </ul>
	 * 
	 * @throws Exception
	 * @throws Exception
	 * @throws Exception
	 */
	public void reload(Database db) throws Exception;

	/**
	 * Handle screen events.
	 * <p>
	 * handleRequest() can be called to change a screen, or any resources
	 * managed by the Screen. Such requests are passed as Tuple, typically with
	 * the {@link ScreenModel#INPUT_ACTION} that indicates to the Screen what
	 * action to execute. For example:
	 * <ul>
	 * <li>a database backed screen can receive an "delete" action to remove
	 * data from the database underlying the screen. In this case an underlying
	 * model is changed too.
	 * <li>a screen has to show data in a different way, e.g. receives a
	 * "listview" action showing it as list instead of record-by-record.
	 * <li>Etc.
	 * </ul>
	 * 
	 * @param request
	 *            a request
	 * @throws HandleRequestDelegationException 
	 * @throws Exception 
	 */
	//public void handleRequest(Database db, Tuple request) throws Exception, HandleRequestDelegationException;

	/**
	 * Handle a user request (typically implemented in the subclass).
	 * 
	 * @param db
	 * @param request
	 * @param out
	 *            additional parameter that allows you to write downloadable
	 *            output
	 * @return 
	 * @throws Exception 
	 * @throws HandleRequestDelegationException 
	 */
	public Show handleRequest(Database db, Tuple request, OutputStream out) throws HandleRequestDelegationException, Exception;

	/**
	 * Get the view
	 */
	// public Templateable getScreen();

	/**
	 * Get the model for this controller
	 */
	public MODEL getModel();

	/**
	 * Get the view for this controller
	 * 
	 * @return
	 */
	public ScreenView getView();

	/**
	 * Get access to the emailservice.
	 * 
	 * @return email service
	 */
	public EmailService getEmailService();

	/**
	 * Get an instance of the database. Big warning: please destory the
	 * instances to limit chances of unclosed database connections
	 */
	public Database getDatabase();

	/**
	 * Get the user interface this screen is part of.
	 * 
	 * @return user-interface the root screen of this user interface
	 */
	public ApplicationController getApplicationController();

	public FileLink getTempFile() throws IOException;

	String render() throws HtmlInputException;

	String getCustomHtmlHeaders();

	String getCustomHtmlBodyOnLoad();

	/**
	 * Method to select what child should be selected
	 * 
	 * @param viewid
	 */
	void setSelected(String viewid);

	@Deprecated
	String getViewTemplate();

	@Deprecated
	public String getLabel();

	ScreenModel getSelected();

	/**
	 * Retrieve the base url for this MOLGENIS. You can use this to build new
	 * urls to your app.
	 */
	String getApplicationUrl();
}
