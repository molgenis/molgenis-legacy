/**
 * File: invengine.screen.FormView <br>
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

// jdk
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.CsvToDatabase;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.paging.DatabasePager;
import org.molgenis.framework.security.Login;
import org.molgenis.framework.security.SimpleLogin;
import org.molgenis.framework.ui.commands.AddBatchCommand;
import org.molgenis.framework.ui.commands.AddCommand;
import org.molgenis.framework.ui.commands.ChangeListLimitCommand;
import org.molgenis.framework.ui.commands.CommandMenu;
import org.molgenis.framework.ui.commands.DownloadAllCommand;
import org.molgenis.framework.ui.commands.DownloadSelectedCommand;
import org.molgenis.framework.ui.commands.DownloadVisibleCommand;
import org.molgenis.framework.ui.commands.EditSelectedCommand;
import org.molgenis.framework.ui.commands.RemoveSelectedCommand;
import org.molgenis.framework.ui.commands.ScreenCommand;
import org.molgenis.framework.ui.commands.ViewEditViewCommand;
import org.molgenis.framework.ui.commands.ViewListViewCommand;
import org.molgenis.framework.ui.html.FileInput;
import org.molgenis.framework.ui.html.HtmlForm;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * 
 * 
 * @param
 */
public abstract class FormModel<E extends Entity> extends SimpleModel<E>
{
	/**
	 * Parameters to enable links between parent forms and subforms using
	 * foreign key relationships (aka master-detail views)
	 * 
	 * @author Morris Swertz
	 */
	public static class ParentFilter
	{
		/** for filtering based on parentform */
		private String parentForm;
		private String parentId;
		private List<String> parentLabels;
		private String xrefToParent;

		public ParentFilter(String parentForm, String parentId, List<String> parentLabel, String xrefToParent)
		{
			this.parentForm = parentForm;
			this.parentId = parentId;
			this.parentLabels = parentLabel;
			this.xrefToParent = xrefToParent;
		}

		public String getParentForm()
		{
			return parentForm;
		}

		public String getParentId()
		{
			return parentId;
		}

		public String getXrefToParent()
		{
			return xrefToParent;
		}

		public void setParentForm(String parentForm)
		{
			this.parentForm = parentForm;
		}

		public void setParentId(String parentId)
		{
			this.parentId = parentId;
		}

		public void setXrefToParent(String xrefToParent)
		{
			this.xrefToParent = xrefToParent;
		}

		public List<String> getParentLabels()
		{
			return parentLabels;
		}

		public void setParentLabels(List<String> parentLabel)
		{
			this.parentLabels = parentLabel;
		}
	}

	// ABSTRACT METHODS, varied per entity
	/**
	 * Abstract method to build the inputs for each row. The result will be a
	 * set of inputs that can be put on the form screen.
	 * 
	 * @param entity
	 * @param newrecord
	 * @throws ParseException
	 */
	public abstract HtmlForm getInputs(E entity, boolean newrecord);

	public List<HtmlInput> getInputs()
	{
		return getInputs((E) create(), true).getInputs();
	}

	public List<HtmlInput> getInputs(E entity)
	{
		return getInputs(entity, false).getInputs();
	}

	/**
	 * Provides the class of the entitites managed by this form. Note: Java
	 * erases the specific type of E, therefore we cannot say E.newInstance();
	 */
	public abstract Class<E> getEntityClass();

	/**
	 * Default settings for hidden columns
	 */
	public abstract void resetSystemHiddenColumns();

	/**
	 * Default settings for compact columns
	 */
	public abstract void resetCompactView();

	/**
	 * Helper function that translates xref field name into its label (for
	 * showing that in the UI).
	 */
	public abstract String getSearchField(String fieldName);

	/** Alternative view modes */
	public static enum Mode
	{
		LIST_VIEW("listview"), EDIT_VIEW("editview");
		// RECORD_VIEW("recordview")
		// , EMBEDDED_VIEW("embeddedview"),;

		private Mode(String tag)
		{
			this.tag = tag;
		}

		public final String tag;

		public String toString()
		{
			return tag;
		}
	}

	// CONSTANTS
	public static final String INPUT_SHOW = "__show";
	public static final String INPUT_OFFSET = "__offset";
	public static final String INPUT_DOWNLOADFILENAME = "__filename";
	public static final String INPUT_BATCHADD = "__batchadd";
	public static final String INPUT_SELECTED = "massUpdate";
	public static final String ACTION_DOWNLOAD = "download";

	// PROPERTIES (default initialization is done in reset!)
	private static final transient Logger logger = Logger.getLogger(FormModel.class);

	/** List of actions of this screen */
	private Map<String, ScreenCommand<E>> commands = new LinkedHashMap<String, ScreenCommand<E>>();

	/** entity csv reader */
	private CsvToDatabase<E> csvReader;

	/** currently known offset */
	private int offset;

	/** how many pages to show */
	private int limit;

	/** number of entities */
	private int count;

	/** column to sort by */
	private String sortby = "";

	/** either ASC or DESC */
	private Operator sortMode;

	/** how the form should be shown */
	Mode viewMode;

	/** remember the current record we are editing */
	//private E current = null;

	/** cache of currently viewable records */
	private List<E> records = new ArrayList<E>();

	/** whether this form can be edited */
	private boolean readonly;

	/** query filters set by user */
	private List<QueryRule> userRules;

	/** query filter set by system (invisible to user) */
	List<QueryRule> systemRules;

	/** Active command (in case of a command with dialog) */
	private ScreenCommand<E> currentCommand = null;

	/** columns that are invisible to the user */
	protected Vector<String> systemHiddenColumns = new Vector<String>();
	protected Vector<String> userHiddenColumns = new Vector<String>();

	/** Helper object that takes care of database paging */
	//private DatabasePager<E> pager;

	/** Here the currently selected is are stored */
	private List<?> selectedIds;
	/** Filter of parent form filtering */
	// TODO make this infer automatically
	private List<ParentFilter> parentFilters = new ArrayList<ParentFilter>();

	/* which fields are shown in compact view */
	protected List<String> compactView = new ArrayList<String>();

	/** */
	private static final long serialVersionUID = 8048540994925740038L;

	// constructors
	/**
	 * TODO: make gateway part of inversion of control structure. TODO: make
	 * parent_rules integral part of the system rules of this view.
	 * 
	 * @param parent
	 * @throws DatabaseException
	 */
	public FormModel(String name, ScreenModel<?> parent)
	{
		super(name, parent);

		// set defaults
		setViewMacro(FormModel.class.getSimpleName().replace("Model", "View"));
		setUserRules(new ArrayList<QueryRule>());
		setSystemRules(new ArrayList<QueryRule>());
		setLimit(5);
		setOffset(0);
		setCount(0);

		// default sort on id
		setSort(this.getIdField());
		setSortMode(Operator.SORTASC);

		setMode(Mode.LIST_VIEW);
		setMessages(new Vector<ScreenMessage>());
		setReadonly(false);
		resetSystemHiddenColumns();
		resetUserHiddenColumns();

		setController(new FormController<E>(this));

		// add all actions
		// menu FILE

 		// File:Download visible
		super.addCommand(new DownloadVisibleCommand<E>("download_visible", this));

		// File:Download Selected
		super.addCommand(new DownloadSelectedCommand<E>("download_selected", this));

		// FILE:Download all
		super.addCommand(new DownloadAllCommand<E>("download_all", this));

		// File: Add batch
		super.addCommand(new AddBatchCommand<E>("upload_csv", this));

		// EDIT MENU
		// EDIT: Add new record
		super.addCommand(new AddCommand<E>("edit_new", this));

		// EDIT: Update selected
		super.addCommand(new EditSelectedCommand<E>("edit_update_selected", this));

		// EDIT: Remove selected
		super.addCommand(new RemoveSelectedCommand<E>("edit_remove_selected", this));

		// menu VIEW
		// ScreenCommand v2 = new ViewRecordViewCommand("recordview",
		// this);
		// v2.setToolbar(true);
		// super.addCommand("View", v2);

		// v3.setToolbar(true);
		super.addCommand(new ViewEditViewCommand<E>("editview", this));
		super.addCommand(new ViewListViewCommand<E>("listview", this));

		ChangeListLimitCommand<E> view_5 = new ChangeListLimitCommand<E>("view_5show5", this);
		view_5.setLimit(5);
		super.addCommand(view_5);

		ChangeListLimitCommand<E> view_10 = new ChangeListLimitCommand<E>("view_6show10", this);
		view_5.setLimit(10);
		super.addCommand(view_10);

		ChangeListLimitCommand<E> view_20 = new ChangeListLimitCommand<E>("view_7show20", this);
		view_20.setLimit(20);
		super.addCommand(view_20);

		ChangeListLimitCommand<E> view_50 = new ChangeListLimitCommand<E>("view_8show50", this);
		view_50.setLimit(50);
		super.addCommand(view_50);

		ChangeListLimitCommand<E> view_100 = new ChangeListLimitCommand<E>("view_9show100", this);
		view_100.setLimit(100);
		super.addCommand(view_100);

		ChangeListLimitCommand<E> view_500 = new ChangeListLimitCommand<E>("view_10show500", this);
		view_500.setLimit(500);
		super.addCommand(view_500);

		// add the plugged-in actions
		for (ScreenCommand<E> command : this.commands.values())
		{
			super.addCommand(command);
		}
	}

	@Override
	public boolean isVisible()
	{
		try
		{
			return this.getSecurity().canRead(this);
		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Create the toolbar
	 * 
	 * @return vector of commands to be shown on the toolbar
	 */
	public Vector<ScreenCommand<E>> getToolbar()
	{
		Vector<ScreenCommand<E>> toolbar = new Vector<ScreenCommand<E>>();

		for (CommandMenu<E> menu : this.getMenus())
		{
			for (ScreenCommand<E> c : menu.getCommands())
			{
				if (c.isToolbar() && c.isVisible())
				{
					toolbar.add(c);
				}
			}
		}

		return toolbar;
	}

	/**
	 * A convenience method that merges system and user rules.
	 * 
	 * @return an array of all rules
	 */
	public QueryRule[] getRules()
	{
		return this.getRules(true);
	}

	/**
	 * A convenience method that merges system and user rules, excluding limit
	 * and offset (i.e., if you want 'all').
	 * 
	 * @return an array of all rules
	 */
	public QueryRule[] getRulesExclLimitOffset()
	{
		return this.getRules(false);
	}

	/**
	 * A convenience method that merges system and user rules.
	 * 
	 * @return an array of all rules
	 */
	public QueryRule[] getRules(boolean includingLimitOffset)
	{
		List<QueryRule> rules = new ArrayList<QueryRule>();
		rules.addAll(this.getSystemRules());
		rules.addAll(this.getUserRules());
		if (includingLimitOffset)
		{
			if (this.limit > 0) rules.add(new QueryRule(QueryRule.Operator.LIMIT, this.limit));
			if (this.offset > 0) rules.add(new QueryRule(QueryRule.Operator.OFFSET, this.offset));
		}
		if (this.sortby != "") rules.add(new QueryRule(this.sortMode, this.sortby));
		logger.debug("rules.size: " + rules.size() + "=" + this.getSystemRules().size() + "+"
				+ this.getUserRules().size());
		return rules.toArray(new QueryRule[rules.size()]);
	}

	/**
	 * Create a new instance of the entity
	 */
	public E create()
	{
		try
		{
			E entity = getEntityClass().newInstance();

			// set defaults for xrefs
			for (ParentFilter pf : this.getParentFilters())
			{
				FormModel<?> parent = (FormModel<?>) this.get(pf.getParentForm());
				List<?> records = parent.getRecords();
				if (records.size() > 0)
				{
					// xref only
					Object value = ((Entity) records.get(0)).get(pf.getParentId());
					if (!(value instanceof List<?>) && value != null)
					{
						entity.set(pf.getXrefToParent(), value);
					}

					for (String labelName : pf.getParentLabels())
					{
						Object label = ((Entity) records.get(0)).get(labelName);
						entity.set(pf.getXrefToParent() + "_" + labelName, label);
					}
				}
			}

			return entity;
		}
		catch (Exception e)
		{
			// should never happen
			logger.error("failed to create class " + getEntityClass() + ": " + e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Column headers of the form
	 * 
	 * @return vector of column label strings.
	 */
	public Vector<String> getHeaders()
	{
		Vector<String> headers = new Vector<String>();
		for (HtmlInput input : getInputs(this.create(), true).getInputs())
		{
			headers.add(input.getLabel());
		}
		return headers;
	}

	/**
	 * Create the inputs for a new record.
	 * 
	 * @throws DatabaseException
	 */
	public HtmlForm getNewRecordForm() throws DatabaseException
	{
		E entity = this.create();
		//if (current != null) entity = current;
		return getInputs(entity, true);
	}

	/**
	 * Create for each record a form, with each record modeled as a list of
	 * HtmlInput elements.
	 * 
	 * @return a vector of vectors, each vector containing the inputs for one
	 *         row on screen.
	 */
	public Vector<HtmlForm> getRecordInputs()
	{
		Vector<HtmlForm> records = new Vector<HtmlForm>();

		try
		{
			for (E entity : getRecords())
			{
				HtmlForm record = getInputs(entity, false);
				record.setReadonly(!getSecurity().canWrite(entity));
				records.add(record);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return records;
	}

	/**
	 * This function is used by the user interface template to show rules on the
	 * screen.
	 * 
	 * @return a list of query rules that can be managed by the user.
	 * @throws DatabaseException
	 */
	public Vector<String> getFilters() throws DatabaseException
	{
		Vector<String> filters = new Vector<String>();
		Map<String, String> nameLabelMap = new TreeMap<String, String>();

		for (HtmlInput input : this.getNewRecordForm().getInputs())
		{
			// getSearchFields maps xref and mref field to their label
			String fieldName = getSearchField(input.getName());
			nameLabelMap.put(fieldName, input.getLabel());
		}

		for (QueryRule rule : this.getUserRules())
		{
			String field = rule.getField();
			String label = "";
			if (field != null) label = nameLabelMap.get(field);
			filters.add(label + " " + rule.getOperator().toString() + " " + rule.getValue());
		}

		return filters;
	}

	// SIMPLE GETTERS AND SETTERS BELOW
	/**
	 * Limit the number of retrieved records. 0 means all records.
	 * 
	 * @param limit
	 */
	public void setLimit(int limit)
	{
		this.limit = limit;
	}

	/**
	 * @return Limit the number of retrieved records. 0 means all records.
	 */
	public int getLimit()
	{
		if (this.viewMode.equals(Mode.EDIT_VIEW)) return 1;
		return limit;
	}

	/**
	 * Total number of records available to this form (given filter rules).
	 * 
	 * @param count
	 */
	public void setCount(int count)
	{
		this.count = count;
	}

	/**
	 * @return Total number of records available to this form (given filter
	 *         rules).
	 */
	public int getCount()
	{
		return count;
	}

	/**
	 * @return The column name this form is sorted by.
	 */
	public String getSort()
	{
		return sortby;
	}

	/**
	 * @param sort
	 *            the column name this form should be sorted by.
	 */
	public void setSort(String sort)
	{
		this.sortby = sort;
	}

	/**
	 * @return Operator indicating whether this form should be sorted ASC or
	 *         DESC
	 */
	public Operator getSortMode()
	{
		return sortMode;
	}

	/**
	 * 
	 * @param sortMode
	 */
	public void setSortMode(Operator sortMode)
	{
		this.sortMode = sortMode;
	}

	/**
	 * Number of records that should be skipped before retrieving records. 0
	 * means first record.
	 * 
	 * @param offset
	 */
	public void setOffset(int offset)
	{
		this.offset = offset;
	}

	/**
	 * @return Number of records that should be skipped before retrieving
	 *         records. 0 means first record.
	 */
	public int getOffset()
	{
		return offset;
	}

	/**
	 * Switch between alternative view modes.
	 * 
	 * @param viewmode
	 */
	public void setMode(Mode viewmode)
	{
		this.viewMode = viewmode;
	}

	/**
	 * @return currently selected view mode.
	 */
	public Mode getMode()
	{
		return viewMode;
	}

	/**
	 * Cache of the current set of records as viewed by this FormScreen.
	 * 
	 * @param recordlist
	 */
	public void setRecords(List<E> recordlist)
	{
		this.records = recordlist;
	}

	/**
	 * Current list of records.
	 */
	public List<E> getRecords()
	{
		return records;
	}

	/**
	 * User defined filtering rules. For example, filtering by a field such as
	 * date.
	 * 
	 * @see QueryRule
	 * @param rules
	 */
	public void setUserRules(List<QueryRule> rules)
	{
		this.userRules = rules;
	}

	/**
	 * @return User defined filtering rules
	 */
	public List<QueryRule> getUserRules()
	{
		return userRules;
	}

	/**
	 * System defined filtering rules. For example, filtering by a "parent"
	 * field of a related record.
	 */
	public List<QueryRule> getSystemRules()
	{
		List<QueryRule> rules = new ArrayList<QueryRule>();

		// parent filters
		if (this.getParentFilters() != null && this.getParentFilters().size() > 0)
		{
			List<QueryRule> xref_filters = new ArrayList<QueryRule>();

			for (ParentFilter pf : this.getParentFilters())
			{
				FormModel<?> parent = (FormModel<?>) this.get(pf.getParentForm());
				List<?> records = parent.getRecords();

				// add filters for xref or mref relationships (if any)
				// if multiple xrefs apply then the filters are union
				// (so xref1 OR xref2 OR etc)
				if (records.size() > 0)
				{

					Object parentValue = ((Entity) records.get(0)).get(pf.getParentId());
					// mref?
					if (parentValue instanceof List<?>)
					{
						List<?> values = (List<?>) parentValue;
						if (values.size() > 0)
						{
							for (int i = 0; i < values.size(); i++)
							{
								QueryRule rule = new QueryRule(pf.getXrefToParent(), QueryRule.Operator.EQUALS, values
										.get(i));
								if(xref_filters.size() > 0) xref_filters.add(new QueryRule(Operator.OR));
								xref_filters.add(rule);
							}
						}
						// if no filters then prohibit to get ALL data by adding
						// impossible condition
						else
						{
							QueryRule rule = new QueryRule(pf.getXrefToParent(), QueryRule.Operator.EQUALS,
									Integer.MIN_VALUE);
							if(xref_filters.size() > 0) xref_filters.add(new QueryRule(Operator.OR));
							xref_filters.add(rule);
							
						}
					}
					// xref
					else if (parentValue != null)
					{
						QueryRule rule = new QueryRule(pf.getXrefToParent(), QueryRule.Operator.EQUALS, parentValue);
						if (xref_filters.size() > 0) xref_filters.add(new QueryRule(Operator.OR));
						xref_filters.add(rule);

					}
//					else
//					{
//						QueryRule rule = new QueryRule(pf.getXrefToParent(), QueryRule.Operator.EQUALS,
//								Integer.MIN_VALUE);
//						if (xref_filters.size() > 0) xref_filters.add(new QueryRule(Operator.OR));
//						xref_filters.add(rule);
//
//					}
				}
			}
			// put it all in one clause, e.g. (xref1 = 0 OR xref1 = 1 OR xref2=3
			// ....)
			if(rules.size()>0) rules.add(new QueryRule(Operator.OR));
			rules.add(new QueryRule(xref_filters));
			
		}

		return rules;

	}

	/**
	 * @param systemRules
	 *            System defined filtering rules.
	 */
	public void setSystemRules(List<QueryRule> systemRules)
	{
		this.systemRules = systemRules;
	}

	/**
	 * Determine whether the form can handle data manipulation events.
	 * 
	 * @param readonly
	 */
	public void setReadonly(boolean readonly)
	{
		this.readonly = readonly;
	}

	/**
	 * @return whether the form handles data manipulation events.
	 */
	public boolean isReadonly()
	{
		// If no "real" auth is used, return value from xml-ui
		if (this.getSecurity() instanceof SimpleLogin)
			return this.readonly;

		// Otherwise dynamically return whether form is read-only
		try
		{
			return !this.getSecurity().canWrite(this.create().getClass());
		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Override getChildren to only return selected elements.
	 */
	@Override
	// FIXME: this may be problematic?
	public Vector<ScreenModel<?>> getChildren()
	{
		if (viewMode.equals(Mode.EDIT_VIEW))
		{
			return super.getChildren();
		}
		else
		{
			return new Vector<ScreenModel<?>>(); // empty set.
		}
	}

	@Deprecated
	public File getDownloadFile(Database db, Tuple requestTuple)
	{
		String file = db.getFilesource() + requestTuple.getString(FileInput.INPUT_CURRENT_DOWNLOAD);
		logger.error("file to download: " + file);
		return new File(file);
	}

	public void resetUserHiddenColumns()
	{
		this.userHiddenColumns = new Vector<String>();
		this.userHiddenColumns.addAll(this.getSystemHiddenColumns());
	}

	public Vector<String> getSystemHiddenColumns()
	{
		return systemHiddenColumns;
	}

	public Vector<String> getUserHiddenColumns()
	{
		return userHiddenColumns;
	}

	public void setUserHiddenColumns(Vector<String> userHiddenColumns)
	{
		this.userHiddenColumns = userHiddenColumns;
	}

	public Login getSecurity()
	{
		return getRootScreen().getLogin();
	}

	public CsvToDatabase<E> getCsvReader()
	{
		return csvReader;
	}

	public void setCsvReader(CsvToDatabase<E> csvReader)
	{
		this.csvReader = csvReader;
	}

	public String getIdField()
	{
		E object = this.create();
		return object.getClass().getSimpleName() +"_"+object.getIdField();
	}

	public List<ScreenCommand<E>> getCommands()
	{
		return new ArrayList<ScreenCommand<E>>(commands.values());
	}

	public ScreenCommand<E> getCurrentCommand()
	{
		return currentCommand;
	}

	public void setCurrentCommand(ScreenCommand<E> currentCommand)
	{
		this.currentCommand = currentCommand;
	};

	@SuppressWarnings("unchecked")
	public DatabasePager<E> getPager()
	{
		return ((FormController<E>) getController()).getPager();
	}

	public List<?> getSelectedIds()
	{
		return selectedIds;
	}

	public void setSelectedIds(List<?> selected)
	{
		this.selectedIds = selected;
	}

	public void setParentFilters(List<ParentFilter> parentFilters)
	{
		this.parentFilters = parentFilters;
	}

	public List<ParentFilter> getParentFilters()
	{
		return parentFilters;
	}

	public E getCurrent()
	{
		if(this.getRecords() != null && this.getRecords().size() > 0) return this.getRecords().get(0);
		return null;
	}
//
//	public void setCurrent(E current)
//	{
//		this.current = current;
//	}

}
