/**
 * File: invengine.screen.Controller <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2005-05-07; 1.0.0; MA Swertz; Creation.
 * <li>2005-12-02; 1.0.0; RA Scheltema; Moved to the new structure, made the
 * method reset abstract and added documentation.
 * <li>2007-05-15; 1.1.0; MA Swertz; refactored to use pluggable pager.
 * </ul>
 */

package org.molgenis.framework.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.paging.DatabasePager;
import org.molgenis.framework.db.paging.LimitOffsetPager;
import org.molgenis.framework.ui.FormModel.Mode;
import org.molgenis.framework.ui.commands.ScreenCommand;
import org.molgenis.framework.ui.commands.SimpleCommand;
import org.molgenis.framework.ui.html.HtmlForm;
import org.molgenis.framework.ui.html.Input;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

/**
 * @param <E>
 */
public abstract class FormController<E extends Entity> extends
		SimpleScreenController<FormModel<E>>
{
	// member variables
	/** */
	private static final transient Logger logger = Logger
			.getLogger(FormController.class.getSimpleName());

	/** Helper object that takes care of database paging */
	protected DatabasePager<E> pager;
	/** */
	private static final long serialVersionUID = 7813540700458832850L;

	// constructor
	/**
	 * @param model
	 * @throws DatabaseException
	 */
	public FormController(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new FormModel<E>(this));
		this.setView(new FreemarkerView("FormView.ftl", getModel()));

		FormModel<E> model = getModel();
		resetSystemHiddenColumns();
		model.resetUserHiddenColumns();

		// setViewMacro(FormModel.class.getSimpleName().replace("Model",
		// "View"));
		// FIXME: this assumes first column is sortable...
		try
		{
			this.pager = new LimitOffsetPager<E>(getEntityClass(), model
					.create().getFields().firstElement());
			// this.pager = new PrimaryKeyPager<E>(view.getEntityClass(),
			// view.getDatabase(), view.create().getIdField());

			// copy default sort from view
			pager.setOrderByField(model.getSort());
			pager.setOrderByOperator(model.getSortMode());
			pager.setLimit(model.getLimit());

		}
		catch (DatabaseException e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		this.handleRequest(db, request, null);
	}

	@Override
	public void handleRequest(Database db, Tuple request, PrintWriter out)
	{
		logger.debug("handleRequest(" + request + ")");

		// clear the old messages
		FormModel<E> model = getModel();
		model.setMessages(new Vector<ScreenMessage>()); // clear messsages

		try
		{
			String action = request.getString(FormModel.INPUT_ACTION);

			// get the selected ids into the screen list (if any)
			model.setSelectedIds(request.getList(FormModel.INPUT_SELECTED));

			// if none selected, make empty list
			if (model.getSelectedIds() == null) model
					.setSelectedIds(new ArrayList<Object>());

			// get the current command if any
			ScreenCommand command = model.getCommand(action);

			if (action == null || action == "")
			{
				logger.debug("action does not exist");
				return;
			}
			// delegate to a command
			else if (command != null && command instanceof SimpleCommand)
			{
				logger.debug("delegating to PluginCommand");
				model.setCurrentCommand(command);
				command.handleRequest(db, request, out);
			}
			else if (action.equals("filter_add"))
			{
				this.addFilter(pager, db, request);
			}
			else if (action.equals("filter_remove"))
			{
				int index = request.getInt("filter_id");
				// watch out not to remove system level filters!
				// pager.removeFilter(index + this.systemRules.size() - 1);
				model.getUserRules().remove(index);

				// reset the filters...
				pager.resetFilters();
				for(QueryRule rewrittenRule: this.rewriteAllRules(db, model.getUserRules()))
				{
					pager.addFilter(rewrittenRule);
				}
				for (QueryRule r : model.getSystemRules())
				{
					pager.addFilter(r);
				}
			}
			else if (action.equals("filter_set"))
			{
				// remove all existing filters and than add this as a new one.
				model.setUserRules(new ArrayList<QueryRule>());

				this.addFilter(pager, db, request);

				// go to this screen if it is not selected
				if (getParent() != null)
				{
					getParent().setSelected(model.getName());
				}

			}
			else if (action.equals("update"))
			{
				this.doUpdate(db, request);
			}
			else if (action.equals("remove"))
			{
				this.doRemove(db, request);
			}
			else if (action.equals("add"))
			{
				this.doAdd(db, request);
			}
			else if (action.equals("prev"))
			{
				pager.prev(db);
			}
			else if (action.equals("next"))
			{
				pager.next(db);
			}
			else if (action.equals("first"))
			{
				pager.first(db);
			}
			else if (action.equals("last"))
			{
				pager.last(db);
			}
			else if (action.equals("sort"))
			{
				String attribute = getSearchField(request
						.getString("__sortattribute"));

				if (pager.getOrderByField().equals(attribute))
				{
					if (pager.getOrderByOperator().equals(Operator.SORTASC))
					{
						pager.setOrderByOperator(Operator.SORTDESC);
					}
					else
					{
						pager.setOrderByOperator(Operator.SORTASC);
					}
				}
				else
				{
					pager.setOrderByField(attribute);
					pager.setOrderByOperator(Operator.SORTASC);
				}
			}
			else if (action.equals("xref_select")) // this is used to link from
			// one
			// form to another based on an xref
			{
				this.doXrefselect(request);
			}
			else if (action.equals("hideColumn"))
			{
				List<String> UserHiddencols = model.getUserHiddenColumns();
				String attribute = request.getString("attribute");

				if (!UserHiddencols.contains(attribute)) UserHiddencols
						.add(attribute);
				model.setUserHiddenColumns(UserHiddencols);

			}
			else if (action.equals("showColumn"))
			{
				List<String> UserHiddencols = model.getUserHiddenColumns();
				String attribute = request.getString("attribute");

				if (UserHiddencols.contains(attribute)) UserHiddencols
						.remove(attribute);
				model.setUserHiddenColumns(UserHiddencols);
			}
			// ACTIONS BELOW HAVE BEEN MOVED TO 'form.command' package
			// else if (action.equals("createMassupdate"))
			// {
			// this.view.setMode(Mode.EDIT_VIEW);
			// }
			// else if (action.equals("removeselected"))
			// {
			// this.doRemoveSelected(request);
			// }
			// else if (action.equals("recordview"))
			// {
			// this.view.setMode(Mode.RECORD_VIEW);
			// Integer offset = request.getInt("__offset");
			// if (offset != null) pager.setOffset(offset - 1);
			// }
			// else if (action.equals("editview"))
			// {
			// this.view.setMode(Mode.EDIT_VIEW);
			// Integer offset = request.getInt("__offset");
			// if (offset != null) pager.setOffset(offset - 1);
			// }
			// else if (action.equals("listview"))
			// {
			// this.view.setMode(Mode.LIST_VIEW);
			// Integer offset = request.getInt("__offset");
			// if (offset != null)
			// {
			// pager.setOffset(offset - 1);
			// }
			// else
			// {
			// pager.setOffset(view.getOffset());
			// }
			// }
			// else if (action.equals("limit"))
			// {
			// view.setLimit(request.getInt("limit"));
			// }
			// else if (action.equals("massUpdate"))
			// {
			// this.view.setMode(Mode.LIST_VIEW);
			// this.doMassUpdate(request);
			// }
			// else if (action.equals("upload"))
			// {
			// this.doCsvUpload(request);
			// }
			else
			{
				logger.debug("action '" + action + "' unknown");
			}

			logger.debug("handleRequest finished.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e);
		}
	}

	private void addFilter(DatabasePager<E> pager, Database db, Tuple request)
			throws DatabaseException, MolgenisModelException
	{
		FormModel<E> model = getModel();

		Operator operator = QueryRule.Operator.valueOf(request
				.getString("__filter_operator"));
		System.out.println(">>>>>>>>>>>>Operator" + operator);
		String value = request.getString("__filter_value");
		System.out.println(">>>>>>>>>>>>value" + value);
		// automatically add LIKE delimiters %
		if (operator.equals(Operator.LIKE) && !value.contains("%"))
		{
			value = "%" + value + "%";
		}

		// if (request.getString("__filter_attribute").equals("all")) {
		// String entityLabel = request.getString("__target");
		// String controllerClassName = entityLabel + "FormController";
		// //System.out.println(">>>>>>>getMetaData().getClass" +
		// db.getMetaData());
		// Class<? extends Entity> entityClass = this.getEntityClass();
		//		
		//			
		// //System.out.println(">>>>>>>getMetaData().getClass" +
		// entityClass.getSuperclass().toString());
		//			
		// //entityClass.getClassLoader();
		//
		// //entityClass.getSuperclass();
		// //entityClass.getSuperclass().getTypeParameters();
		// //System.out.println(">>>>>>> getSimpleName " +
		// entityClass.getSuperclass().getSimpleName());
		//			
		// // 1 - get the possible fields for the entity that were looking at
		// System.out.println(">>>>>>>entityLabel" + entityLabel);
		// System.out.println(">>>>>>>entityControllerClassName"+
		// entityClass.getSuperclass().getTypeParameters().toString());
		// //org.molgenis.model.elements.Entity eType =
		// db.getMetaData().getEntity(entityName);
		// org.molgenis.model.elements.Entity eType =
		// db.getMetaData().getEntity(entityClass.getSimpleName()); //TODO
		//
		// QueryRule orRule = new QueryRule(Operator.OR);
		// boolean first = true;
		//			
		// // 2 - iterate fields and build the queryrules - if field != "__Type"
		// for (Field field : eType.getFields()) {
		// System.out.println("*** FIELD NAME : " +
		// field.getName().toLowerCase());
		// if (!field.getName().equals("__Type") ) {
		//
		// //getsSearchField will map the field to field_name in case of
		// xref/mref
		// QueryRule fieldRule = new QueryRule(field.getName(), operator,
		// value);
		// System.out.println("*** QUERYRULE : " + fieldRule.toString());
		// //add 'or' except for first filter rule
		// if(first)
		// first = false;
		// else
		// model.getUserRules().add(orRule);
		// model.getUserRules().add(fieldRule);
		// }
		// }
		// }else if
		// (request.getString("__filter_attribute").equals("searchIndex")) {
		// //TODO: plugins.LuceneIndex.DBIndexPlugin.searchIndex()
		// }
		// else{
		System.out.println("*** ELSE : ");
		QueryRule rule = new QueryRule(request.getString("__filter_attribute"),
				operator, value);
		System.out.println(">>>>>>>>>>>>rule" + rule);
		model.getUserRules().add(rule);
		// }

		// reload the filters...
		pager.resetFilters();
		for(QueryRule rewrittenRule: this.rewriteAllRules(db, model.getUserRules()))
		{
			pager.addFilter(rewrittenRule);
		}

		for (QueryRule r : model.getSystemRules())
		{
			pager.addFilter(r);
		}
		pager.first(db);
	}

	/**
	 * the 'all' field needs special treatment
	 * 
	 * @throws DatabaseException
	 * @throws MolgenisModelException
	 */
	public QueryRule[] rewriteAllRules(Database db, List<QueryRule> rulesToRewrite)
			throws DatabaseException, MolgenisModelException
	{
		List<QueryRule> result = new ArrayList<QueryRule>();
		for (QueryRule r : rulesToRewrite)
		{
			if ("all".equals(r.getField()))
			{
				List<QueryRule> all = new ArrayList<QueryRule>();

				Class<? extends Entity> entityClass = this.getEntityClass();

				org.molgenis.model.elements.Entity eType = db.getMetaData()
						.getEntity(entityClass.getSimpleName()); // TODO

				QueryRule orRule = new QueryRule(Operator.OR);
				boolean first = true;

				// 2 - iterate fields and build the queryrules - if field !=
				// "__Type"
				for (Field field : eType.getFields())
				{
					System.out.println("*** FIELD NAME : "
							+ field.getName().toLowerCase());
					if (!field.getName().equals("__Type"))
					{

						// getsSearchField will map the field to field_name in
						// case of xref/mref
						QueryRule fieldRule = new QueryRule(this.getSearchField(field.getName()), r.getOperator(), r.getValue());
						// if(field.getType().getClass().getSimpleName().equals("XrefField")
						// ||
						// field.getType().getClass().getSimpleName().equals("MrefField")
						// )
						// {
						// fieldRule = new QueryRule(field.getName(),
						// r.getOperator(), r.getValue());
						// }
						System.out.println("*** QUERYRULE : "
								+ fieldRule.toString());
						// add 'or' except for first filter rule
						if (first) first = false;
						else
							all.add(orRule);
						all.add(fieldRule);
					}
				}

				//return pager.addFilter(new QueryRule(all));
				result.add(new QueryRule(all));
			}
			else
			{
				//pager.addFilter(r);
				result.add(r);
			}
		}
		return result.toArray(new QueryRule[result.size()]);
	}

	// overrides
	@Override
	public void reload(Database db)
	{
		logger.info("reloading...");
		FormModel<E> model = getModel();

		// FIXME
		try
		{
			// reload login only on login/logout events
			// view.getRootScreen().getLogin().reload(db);

			pager.setDirty(true);

			// check whether the parent has changed and then reset
			List<QueryRule> newSystemRules = model.getSystemRules();

			if (!newSystemRules.equals(model.getSystemRules()))
			{
				// remember old user filters
				// List<QueryRule> oldRules = Arrays.asList(pager.getFilters());
				pager.resetFilters();
				for (QueryRule rule : newSystemRules)
				{
					pager.addFilter(rule);
				}

				model.setSystemRules(newSystemRules);

				
				 for (QueryRule rule : this.rewriteAllRules(db, model.getUserRules()))
				 {
					 pager.addFilter(rule);
				 }
			}

			// check view and set limit accordingly
			// if (view.getMode().equals(Mode.EDIT_VIEW) && !view.isReadonly())
			// view.setMode(Mode.RECORD_VIEW);

			if (model.getMode().equals(Mode.EDIT_VIEW))
				pager.setLimit(1);
			else
				pager.setLimit(model.getLimit());

			// refresh pager and options
			if (model.isReadonly())
			// view.getDatabase().cacheXrefOptions(view.getEntityClass());
				pager.refresh(db);

			// update view

			// set readonly records
			// view.setRecords( pager.getPage() );
			model.setRecords(this.getData(db));
			model.setCount(pager.getCount(db));

			model.setOffset(pager.getOffset());
			model.setSort(pager.getOrderByField());
			model.setSortMode(pager.getOrderByOperator());

			// show filters without system level filters
			// FIXME make simpler
			// List<QueryRule> allRules = Arrays.asList(pager.getFilters());
			// for(QueryRule r: allRules)
			// {
			// logger.debug("current rules: "+r);
			// }
			// view.setUserRules(allRules.subList(systemRules.size(),
			// allRules.size()));

			// update child views
			if (model.getMode().equals(Mode.EDIT_VIEW))
			{
				for (ScreenController<?> c : this.getChildren())
				{
					// only the real screens, not the commands
					if (c instanceof SimpleScreenController<?>)
					{
						c.reload(db);
					}
				}
			}
			logger.debug("reload finished.");
		}
		catch (Exception e)
		{
			logger.error("reload() failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * This function is actually responsible for querying the database.
	 * 
	 * @return list of entity objects currently in view
	 * @throws DatabaseException
	 */
	public List<E> getData(Database db) throws DatabaseException
	{
		// TODO: move the row level security to the entitymapper...
		FormModel<E> model = getModel();

		// set form level rights
		boolean formReadonly = model.isReadonly()
				|| !model.getSecurity().canWrite(model.create().getClass());
		model.setReadonly(formReadonly);

		// load the rows
		List<E> visibleRecords = new ArrayList<E>();

		// load all records and select rows that can be visible
		// List allRecords = view.getDatabase().find( view.getEntityClass(),
		// allRules );
		List<E> allRecords = pager.getPage(db);

		for (E record : allRecords)
		{
			boolean rowReadonly = formReadonly
					|| !model.getSecurity().canWrite(record.getClass());

			if (rowReadonly) 
				record.setReadonly(true);
			// else
			// recordreadonly = false;

			visibleRecords.add(record);
		}

		return visibleRecords;
	}

	/**
	 * Helper function to add a record from UI
	 * 
	 * @param request
	 * @return true if add was successfull, false if it wassen't
	 * @throws ParseException
	 * @throws DatabaseException
	 * @throws IOException
	 */
	public boolean doAdd(Database db, Tuple request) throws ParseException,
			DatabaseException, IOException
	{
		ScreenMessage msg = null;
		Entity entity = getModel().create();
		boolean result = false;

		try
		{
			db.beginTx();
			entity.set(request, false);
			int updatedRows = 0;
			if (request.getObject(FormModel.INPUT_BATCHADD) != null
					&& request.getInt(FormModel.INPUT_BATCHADD) > 1)
			{
				// batch
				int i;
				for (i = 0; i < request.getInt(FormModel.INPUT_BATCHADD); i++)
				{
					updatedRows += db.add(entity);
				}
			}
			else
			{
				updatedRows = db.add(entity);

			}
			db.commitTx();
			msg = new ScreenMessage("ADD SUCCESS: affected " + updatedRows,
					null, true);
			result = true;
			// navigate to newly added record
			pager.last(db);

		}
		catch (Exception e)
		{
			db.rollbackTx();
			msg = new ScreenMessage("ADD FAILED: " + e.getMessage(), null,
					false);
			result = false;
		}
		getModel().getMessages().add(msg);

		/* make sure the user sees the newly added record(s) */
		// view.setMode(FormScreen.Mode.RECORD_VIEW);
		// pager.setLimit(1);
		pager.resetOrderBy();
		pager.last(db);
		// should reset to an order that shows the record on the end

		return result;
	}

	// helper method
	protected void doUpdate(Database db, Tuple request)
			throws DatabaseException, IOException, ParseException
	{
		Entity entity = getModel().create();
		ScreenMessage msg = null;
		try
		{
			entity.set(request, false);
			int updatedRows = db.update(entity);
			msg = new ScreenMessage("UPDATE SUCCESS: affected " + updatedRows,
					null, true);
		}
		catch (Exception e)
		{
			logger.error("doUpdate(): " + e);
			e.printStackTrace();
			msg = new ScreenMessage("UPDATE FAILED: " + e.getMessage(), null,
					false);
		}
		getModel().getMessages().add(msg);
		if (msg.isSuccess())
		{
			pager.setDirty(true);
			// resetChildren();
		}
	}

	// helper method
	protected void doRemove(Database db, Tuple request)
			throws DatabaseException, ParseException, IOException
	{
		Entity entity = getModel().create();
		ScreenMessage msg = null;
		try
		{
			entity.set(request);
			int updatedRows = db.remove(entity);
			if (updatedRows > 0) msg = new ScreenMessage(
					"REMOVE SUCCESS: affected " + updatedRows, null, true);
			else
				msg = new ScreenMessage(
						"REMOVE FAILED: call system administrator", null, false);
		}
		catch (Exception e)
		{
			msg = new ScreenMessage("REMOVE FAILED: " + e.getMessage(), null,
					false);
		}
		getModel().getMessages().add(msg);

		// **make sure the user sees a record**/
		if (msg.isSuccess())
		{
			pager.prev(db);
			// resetChildren();
		}
	}

	/**
	 * Needed for hyperlink form switches...
	 * 
	 * @param request
	 * @throws DatabaseException
	 */
	public void doXrefselect(Tuple request) throws DatabaseException
	{
		// also set the parent menu
		if (getParent() != null && getParent() instanceof MenuController)
		{
			// set the filter to select the xref-ed entity
			pager.resetFilters();
			getModel().setUserRules(new ArrayList<QueryRule>());
			QueryRule rule = new QueryRule(request.getString("attribute"),
					QueryRule.Operator.valueOf(request.getString("operator")),
					request.getString("value"));
			pager.addFilter(rule);

			// tell "my" menu to select me
			Tuple parentRequest = new SimpleTuple();
			String aChildName = getModel().getName();
			ScreenController<?> aParent = getParent();
			while (aParent != null)
			{
				if (aParent instanceof MenuModel)
				{
					parentRequest.set("select", aChildName);
					MenuController c = (MenuController) (Object) aParent;
					c.doSelect(parentRequest);
				}
				aChildName = aParent.getName();
				aParent = aParent.getParent();
			}
		}
	}

	public DatabasePager<E> getPager()
	{
		return pager;
	}

	// Actions below have been moved to form.command package
	// public void doRemoveSelected(Tuple request) throws DatabaseException,
	// ParseException, IOException
	// {
	// Database db = view.getDatabase();
	// ScreenMessage msg = null;
	// try
	// {
	// // get ids
	// List<Object> idList = request.getList("massUpdate");
	// if (idList == null || idList.size() == 0) throw new
	// Exception("no items selected");
	// for (Object id : idList)
	// {
	// logger.info("mass removing id: " + id);
	// }
	//
	// // find selected entities
	// Query q =
	// view.getDatabase().query(view.getEntityClass()).in(view.create()
	// .getIdField(), idList);
	// List selection = q.find();
	//
	// // delete selected entities
	// db.remove(selection);
	// msg = new ScreenMessage("REMOVED " + selection.size() + " records",
	// null, true);
	// }
	// catch (Exception e)
	// {
	// msg = new ScreenMessage("REMOVE SELECTION FAILED: " + e.getMessage(),
	// null, false);
	// }
	// view.getMessages().add(msg);
	//
	// // **make sure the user sees a record**/
	// if (msg.isSuccess())
	// {
	// pager.prev();
	// // resetChildren();
	// }
	// }
	// // FIXME move to mapper
	// public void doMassUpdate(Tuple request)
	// {
	// List<Object> idList = request.getList("massUpdate");
	// for (Object id : idList)
	// {
	// logger.info("mass updating id: " + id);
	// }
	//
	// ScreenMessage msg = null;
	// Database db = view.getDatabase();
	// int row = 0;
	// try
	// {
	// Query q = db.query(view.getEntityClass()).in(view.create().getIdField(),
	// idList);
	// List entities = q.find();
	//
	// db.beginTx();
	// for (E e : entities)
	// {
	// row++;
	// e.set(request, false);
	// db.update(e);
	// }
	// db.commitTx();
	// msg = new ScreenMessage("MASS UPDATE SUCCESS: updated " +
	// entities.size() + " rows", null, true);
	// }
	//
	// catch (Exception e)
	// {
	// try
	// {
	// db.rollbackTx();
	// }
	// catch (DatabaseException e1)
	// {
	// logger.error("doMassUpdate() Should never happen: "+e1);
	// e1.printStackTrace();
	// }
	// msg = new ScreenMessage("MASS UPDATE FAILED on item '" + row + "': " +
	// e, null, false);
	// }
	//
	// view.getMessages().add(msg);
	// }

	// public void doCsvUpload(Tuple request)
	// {
	// logger.debug("doCsvUpload: " + request);
	// ScreenMessage msg = null;
	// try
	// {
	// CsvEntityReader csvReader = this.view.getCsvReader();
	//
	// int updatedRows = csvReader.importCsv(view.getDatabase(), new
	// CsvStringReader(request.getString("__csvdata")), request);
	// //for (E entity : entities)
	// // logger.debug("parsed: " + entity);
	// //view.getDatabase().add(entities);
	// msg = new ScreenMessage("CSV UPLOAD SUCCESS: added " + updatedRows +
	// " rows", null, true);
	// logger.debug("CSV UPLOAD SUCCESS: added " + updatedRows + " rows");
	// pager.resetFilters();
	// pager.last();
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// msg = new ScreenMessage("CSV UPLOAD FAILED: " + e.getMessage(), null,
	// false);
	// logger.error("CSV UPLOAD FAILED: " + e.getMessage());
	// }
	// view.getMessages().add(msg);
	// }

	// FIXME move to mapper
	// public void getDataAsText(Tuple requestTuple, PrintWriter out) throws
	// DatabaseException
	// {
	//
	// if (requestTuple.getString("limit").equals("selected"))
	// {
	//
	// Object recordsObject = requestTuple.getObject("massUpdate");
	// List<String> records = new ArrayList<String>();
	//
	// if (recordsObject != null)
	// {
	// if (recordsObject.getClass().equals(Vector.class)) records =
	// (Vector<String>) recordsObject;
	// else
	// records.add(recordsObject.toString());
	// }
	//
	// if(records.size() == 0)
	// {
	// out.println("No records selected.");
	// return;
	// }
	// // watch out, the "IN" operator expects an Object[]
	// view.getDatabase().find(view.getEntityClass(), new CsvWriter(out),
	// new QueryRule("id", Operator.IN, records));
	// }
	// else if (requestTuple.getString("limit").equals("all"))
	// {
	// // remove limit/offset
	// int oldLimit = view.getLimit();
	// int oldOffset = view.getOffset();
	// view.setLimit(0);
	// view.setOffset(0);
	//
	// view.getDatabase().find(view.getEntityClass(), new CsvWriter(out),
	// view.getRules());
	//
	// // restore limit/offset
	// view.setOffset(oldOffset);
	// view.setLimit(oldLimit);
	// }
	// else
	// {
	// view.getDatabase().find(view.getEntityClass(), new CsvWriter(out),
	// view.getRules());
	// }
	// }

	/**
	 * Calculates visible columns. Needed for downloads.
	 */
	public List<String> getVisibleColumnNames()
	{
		// FIXME temporary fix because model not properly used
		// we should instead use getModel().getHiddenColumns().

		List<String> showColumns = new ArrayList<String>();
		try
		{
			HtmlForm f = getInputs(this.getEntityClass().newInstance(), false);

			for (Input i : f.getInputs())
			{
				if (!i.isHidden())
				{
					// strip prefix = this.getEntityClass() + "_"
					String name = i.getName();
					name = name.substring(this.getEntityClass().getSimpleName()
							.length() + 1);

					// then add _label using getSearchField() where needed
					showColumns.add(getSearchField(name));
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return showColumns;
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

	public abstract Vector<String> getHeaders();

	/**
	 * Helper function that translates xref field name into its label (for
	 * showing that in the UI).
	 */
	public abstract String getSearchField(String fieldName);
}
