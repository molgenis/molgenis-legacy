package org.molgenis.framework.ui.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.Templateable;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.util.Tuple;

/**
 * A command defines a button on a screen, including the logic when the command
 * is executed. In architecture it behaves the same as any screen, i.e. it is
 * Templateable to allow for customized layouts. However, the default layout is
 * usually sufficient listing inputs (from getInputs) and actions (from
 * getActions).
 * <ul>
 * <li>handleRequest() defines how the action should be processed and how the
 * result should be shown</li>
 * <li>getInputs() lists the inputs to be shown</li>
 * <li>getActions() lists the pushbuttons to be shown</li>
 * </ul>
 */
public interface ScreenCommand extends Templateable, Serializable
{
	/**
	 * Retrieve the name of the icon to be shown. No icon will be shown if null.
	 * 
	 * @return path to the icon
	 */
	public String getIcon();

	/**
	 * Set the name of the icon to be shown. No icon will be if null.
	 * 
	 * @param icon
	 *            name
	 */
	public void setIcon(String icon);

	/**
	 * @return descriptive label of this command
	 */
	public String getLabel();

	/**
	 * @param label
	 *            descriptive label of this command
	 */
	public void setLabel(String label);

	/**
	 * Override the default javascript for this command by your own (usally not
	 * necessary, @see 'isDialog').
	 * 
	 * @param action
	 *            a string with javascript that should be executed onClick.
	 */
	public void setJavaScriptAction(String action);

	/**
	 * Returns the javascript that is run when this action is clicked.
	 * 
	 * @return the javascript that should be executed onClick.
	 */
	public String getJavaScriptAction();

	/**
	 * @param name
	 *            unique action name of this command (unique within the parent
	 *            screen)
	 */
	public void setName(String name);

	/**
	 * @return unique action name of this command (unique within one screen)
	 */
	public String getName();

	/**
	 * @return Unique name of the target screen that should handle this command.
	 *         Default this equals getScreen(), i.e. the target is the same as
	 *         the screen this command is part of.
	 */
	public String getTarget();

	/**
	 * @param target
	 *            Unique name of the target screen that should handle this
	 *            command
	 */
	public void setTarget(String target);

	/**
	 * @return The screen this command is a part of
	 */
	public ScreenModel getScreen();

	/**
	 * Helper method to reduce casting
	 */
	public FormModel getFormScreen();

	/**
	 * @param screen
	 *            The screen this command belongs to
	 */
	public void setScreen(ScreenModel screen);

	/**
	 * @return true if this command should be shown as a dialog. @see #setDialog
	 */
	public boolean isDialog();

	/**
	 * @param dialog
	 *            set to true if MOLGENIS should show this command via a dialog.
	 *            This results in javascript that pop-ups a dialog when pushed.
	 */
	public void setDialog(boolean dialog);

	/**
	 * @return the unique name of the menu this command is part of. Null
	 *         indicates it will not be shown on the menu.
	 */
	public String getMenu();

	/**
	 * @param menu
	 *            unique name of the screenmenu this command is part of
	 *            (typically shown on top of each screen). Not to be confused
	 *            with the parent ScreenMenu.
	 */
	public void setMenu(String menu);

	/**
	 * 
	 * @return a list of input boxes to show
	 * @throws DatabaseException
	 */
	public List<HtmlInput> getInputs() throws DatabaseException;

	/**
	 * Return a list of buttons to show.
	 * 
	 * @return list of HtmlInput that should be shown in the action area
	 */
	public List<HtmlInput> getActions();

	/**
	 * Handle the request thrown by this command.
	 * 
	 * @param db
	 *            provides access to the database
	 * @param request
	 *            provides access to the filled in inputs and actions
	 * @param downloadStream
	 *            this the request can use to write results as download (in
	 *            combination with Show.VIEW_DOWNLOAD
	 * @return a Show.XYZ that indicates if next action should be shown as
	 *         popup, back to main screen or as download. @see ScreenModel.Show
	 * @throws IOException
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public ScreenModel.Show handleRequest(Database db, Tuple request, PrintWriter downloadStream)
			throws ParseException, DatabaseException, IOException;

	/**
	 * @return boolean whether this action should be treated as a download
	 *         button.
	 */
	public boolean isDownload();

	/**
	 * @param download
	 *            true indicates that this action should be treated as a
	 *            download action. @see handleRequest() where the download
	 *            target is passed as stream and ScreenModel.Show can return
	 *            Show.VIEW_DOWNLOAD.
	 */
	public void setDownload(boolean download);

	/**
	 * @return whether this command should be shown on the command menu.
	 */
	public boolean isToolbar();

	/**
	 * @param showOnToolbar
	 *            indicates whether this command should be shown on the command
	 *            menu.
	 */
	public void setToolbar(boolean showOnToolbar);

	/**
	 * @return the layout macro to be used if this command has a dialog.
	 *         Defaults to the default layout showing inputs and actions.
	 */
	public String getViewName();

	/**
	 * @return the layout macro to be used if this command has a dialog.
	 *         Defaults to the default layout template for commands,
	 *         ScreenCommand.ftl
	 */
	public String getViewTemplate();

	/**
	 * 
	 * @return false if this command should be hidden
	 */
	public boolean isVisible();

}
