package org.molgenis.matrix.component;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.JQueryDataTable;
import org.molgenis.framework.ui.html.MrefInput;
import org.molgenis.framework.ui.html.Newline;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.Observation;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

public class MatrixViewer extends HtmlWidget
{
	ScreenController<?> callingScreenController;
	
	SliceableMatrix<?,?,?> matrix;
	Logger logger = Logger.getLogger(this.getClass());
	private SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
	
	private boolean showLimitControls = true;
	private boolean columnsRestricted = false;
	private boolean selectMultiple = true;
	private boolean showValueValidRange = false;
	
	public String ROWLIMIT = getName() + "_rowLimit";
	public String CHANGEROWLIMIT = getName() + "_changeRowLimit";
	//public String COLLIMIT = getName() + "_colLimit";
	//public String CHANGECOLLIMIT = getName() + "_changeColLimit";
	public String MOVELEFTEND = getName() + "_moveLeftEnd";
	public String MOVELEFT = getName() + "_moveLeft";
	public String MOVERIGHT = getName() + "_moveRight";
	public String MOVERIGHTEND = getName() + "_moveRightEnd";
	public String MOVEUPEND = getName() + "_moveUpEnd";
	public String MOVEUP = getName() + "_moveUp";
	public String MOVEDOWN = getName() + "_moveDown";
	public String MOVEDOWNEND = getName() + "_moveDownEnd";
	public String COLID = getName() + "_colId";
	public String COLVALUE = getName() + "_colValue";
	public String FILTERCOL = getName() + "_filterCol";
	public String ROWHEADER = getName() + "_rowHeader";
	public String ROWHEADEREQUALS = getName() + "_rowHeaderEquals";
	public String CLEARFILTERS = getName() + "_clearValueFilters";
	public String REMOVEFILTER = getName() + "_removeFilter";
	public String RELOADMATRIX = getName() + "_reloadMatrix";
	public String SELECTED = getName() + "_selected";
	public String UPDATECOLHEADERFILTER = getName() + "_updateColHeaderFilter";
	public String MEASUREMENTCHOOSER = getName() + "_measurementChooser";
	public String OPERATOR = getName() + "_operator";
	
	//hack to pass database to toHtml() via toHtml(db)
	private Database toHtmlDb;
	public void setToHtmlDb(Database toHtmlDb)
	{
		this.toHtmlDb = toHtmlDb;
	}

	/**
	 * Default constructor.
	 * 
	 * @param callingScreenController
	 * @param name
	 * @param matrix
	 * @param showLimitControls
	 */
	public MatrixViewer(ScreenController<?> callingScreenController, String name, 
			SliceableMatrix<?,?,?> matrix,
			boolean showLimitControls, boolean selectMultiple, List<MatrixQueryRule> filterRules)
	{
		super(name);
		super.setLabel("");
		this.callingScreenController = callingScreenController;
		this.matrix = matrix;
		this.showLimitControls = showLimitControls;
		this.selectMultiple = selectMultiple;
		if (filterRules != null) {
			this.matrix.getRules().addAll(filterRules);
		}
	}
	
	/**
	 * Constructor where you immediately restrict the column set by applying a colHeader filter rule.
	 * 
	 * @param callingScreenController
	 * @param name
	 * @param matrix
	 * @param showLimitControls
	 * @param filterRules
	 * @throws Exception
	 */
	public MatrixViewer(ScreenController<?> callingScreenController, String name, 
			SliceablePhenoMatrix<? extends ObservationElement, ? extends ObservationElement> matrix,
			boolean showLimitControls, boolean selectMultiple, List<MatrixQueryRule> filterRules, 
			MatrixQueryRule columnRule) throws Exception
	{
		this(callingScreenController, name, matrix, showLimitControls, selectMultiple, filterRules);
		if (columnRule != null && columnRule.getFilterType().equals(MatrixQueryRule.Type.colHeader)) {
			columnsRestricted = true;
			this.matrix.getRules().add(columnRule);
		}
	}
	
	public void handleRequest(Database db, Tuple t) throws HandleRequestDelegationException
	{
		if (t.getAction().startsWith(REMOVEFILTER)) {
			try {
				removeFilter(t.getAction());
			} catch (MatrixException e) {
				e.printStackTrace();
				throw new HandleRequestDelegationException();
			}
			return;
		}
		String action = t.getAction().substring((getName() + "_").length());
		this.delegate(action, db, t);
	}
	
	public String toHtml()
	{	
		return toHtml(toHtmlDb);
	}
	
	public String toHtml(Database db)
	{	
		try {
			String result = "<table><tr><td>";
			result += renderReload();
			result += renderHeader(db);
			result += "</td></tr><tr><td>";
			result += renderTable(db);
			result += "</td></tr><tr><td>";
			result += renderFilterPart(db);
			result += "</td></tr></table>";
			return result;
		} catch (Exception e) {
			((PluginModel)this.callingScreenController).setError(e.getMessage());
			e.printStackTrace();
			return new Paragraph("error", e.getMessage()).render();
		}
	}
	
	public String renderReload() {
		// button to reload the matrix data, whilst keeping the filters intact
		ActionInput reload = new ActionInput(RELOADMATRIX, "", "");
		reload.setIcon("generated-res/img/update.gif");
		return reload.render();
	}
	
	public String renderHeader(Database db) throws MatrixException {
		String divContents = "";
		// move vertical (row paging)
		ActionInput moveUpEnd = new ActionInput(MOVEUPEND, "", "");
		moveUpEnd.setIcon("generated-res/img/first.png");
		divContents += moveUpEnd.render();
		ActionInput moveUp = new ActionInput(MOVEUP, "", "");
		moveUp.setIcon("generated-res/img/prev.png");
		divContents += moveUp.render();
		int rowOffset = this.matrix.getRowOffset();
		int rowLimit = this.matrix.getRowLimit();
		int rowCount = this.matrix.getRowCount(db);
		int rowMax = Math.min(rowOffset + rowLimit, rowCount);
		divContents += "Showing " + (rowOffset + 1) + " - " + rowMax + " of " + rowCount + " ";
		// collimit
		if (showLimitControls) {
			IntInput rowLimitInput = new IntInput(ROWLIMIT, rowLimit);
			divContents += "Row limit:";
			divContents += rowLimitInput.render();
			divContents += new ActionInput(CHANGEROWLIMIT, "", "Change").render();
		}
		ActionInput moveDown = new ActionInput(MOVEDOWN, "", "");
		moveDown.setIcon("generated-res/img/next.png");
		divContents += moveDown.render();
		ActionInput moveDownEnd = new ActionInput(MOVEDOWNEND, "", "");
		moveDownEnd.setIcon("generated-res/img/last.png");
		divContents += moveDownEnd.render();
		
		return divContents;
	}
	
//	public String renderVerticalMovers(Database db) throws MatrixException {
//		String divContents = "";
//		// move vertical
//		ActionInput moveUpEnd = new ActionInput(MOVEUPEND, "", "");
//		moveUpEnd.setIcon("generated-res/img/rowStart.png");
//		divContents += moveUpEnd.render();
//		divContents += new Newline().render();
//		ActionInput moveUp = new ActionInput(MOVEUP, "", "");
//		moveUp.setIcon("generated-res/img/up.png");
//		divContents += moveUp.render();
//		divContents += new Newline().render();
//		int rowOffset = this.matrix.getRowOffset();
//		int rowLimit = this.matrix.getRowLimit();
//		int rowCount = this.matrix.getRowCount(db);
//		int rowMax = Math.min(rowOffset + rowLimit, rowCount);
//		divContents += "Showing " + (rowOffset + 1) + " - " + rowMax + " of " + rowCount;
//		divContents += new Newline().render();
//		// rowLimit
//		if (showLimitControls) {
//			IntInput rowLimitInput = new IntInput(ROWLIMIT, rowLimit);
//			divContents += "Row limit:";
//			divContents += new Newline().render();
//			divContents += rowLimitInput.render();
//			divContents += new Newline().render();
//			divContents += new ActionInput(CHANGEROWLIMIT, "", "Change").render();
//			divContents += new Newline().render();
//		}
//		ActionInput moveDown = new ActionInput(MOVEDOWN, "", "");
//		moveDown.setIcon("generated-res/img/down.png");
//		divContents += moveDown.render();
//		divContents += new Newline().render();
//		ActionInput moveDownEnd = new ActionInput(MOVEDOWNEND, "", "");
//		moveDownEnd.setIcon("generated-res/img/rowStop.png");
//		divContents += moveDownEnd.render();
//		
//		return divContents;
//	}
	
	public String renderTable(Database db) throws MatrixException {
		JQueryDataTable dataTable = new JQueryDataTable(getName() + "DataTable");
		
		Object[][] values = null;
		try{
			values = matrix.getValueLists(db);
		}
		catch(UnsupportedOperationException ue)
		{
			values = matrix.getValues(db);
		}
		
		List<?> rows = matrix.getRowHeaders(db);
		List<?> cols = matrix.getColHeaders(db);
		
		//print colHeaders
		dataTable.addColumn("ID");
		dataTable.addColumn("Name");
		dataTable.addColumn(""); // for checkbox / radio input
		
		for (Object col: cols)
		{
			if(col instanceof ObservationElement)
			{
				ObservationElement colobs = (ObservationElement) col;
				dataTable.addColumn(colobs.getName());
			}else
			{
				dataTable.addColumn(col.toString());
			}
			
		}
		
		//print rowHeader + colValues
		for (int row = 0; row < values.length; row++)
		{
			// print empty rowHeader
			dataTable.addRow("");
			// print ID and name
			
			Object rowobj = rows.get(row);
			if(rowobj instanceof ObservationElement)
			{
				ObservationElement rowObs = (ObservationElement) rowobj;
				dataTable.setCell(0, row, rowObs.getId());
				dataTable.setCell(1, row, rowObs.getName());
			}
			else
			{
				dataTable.setCell(0, row, "0");
				dataTable.setCell(1, row, rowobj.toString());
			}
			
			// print checkbox or radio input for this row
			if (selectMultiple) {
				List<String> options = new ArrayList<String>();
				options.add("" + row);
				List<String> optionLabels = new ArrayList<String>();
				optionLabels.add("");
				CheckboxInput rowCheckbox = new CheckboxInput(SELECTED + "_" + row, options, 
						optionLabels, "", null, true, false);
				rowCheckbox.setId(SELECTED + "_" + row);
				dataTable.setCell(2, row, rowCheckbox);
			} else {
				// When the user may select only one, use a radio button group, which is mutually exclusive
				String radioButtonCode = "<input type='radio' name='" + SELECTED + "' id='" + 
						(SELECTED + "_" + row) + "' value='" + row + "'></input>";
				dataTable.setCell(2, row, radioButtonCode);
			}
			// get the data for this row
			Object valueObject = values[row][0];
			if (valueObject instanceof List) {
				List<Observation>[] rowValues = (List<Observation>[]) values[row];
				for (int col = 0; col < rowValues.length; col++) {
					if (rowValues[col] != null && rowValues[col].size() > 0) {
						boolean first = true;
						for (Observation val : rowValues[col]) {
							String valueToShow = (String) val.get("value");
							
							if (val instanceof ObservedValue && valueToShow == null) {
								valueToShow = ((ObservedValue)val).getRelation_Name();
							}
							// if timing should be shown:
							if (showValueValidRange) {
								if (val.get(ObservedValue.TIME) != null) {
									valueToShow += " (valid from " + newDateOnlyFormat.format(val.get(ObservedValue.TIME));
								}
								if (val.get(ObservedValue.ENDTIME) != null) {
									valueToShow += " through " + newDateOnlyFormat.format(val.get(ObservedValue.ENDTIME)) + ")";
								} else if (val.get(ObservedValue.TIME) != null) {
									valueToShow += ")";
								}
							}
							
							if (first) {
								first = false;
								dataTable.setCell(col + 3, row, valueToShow);
							} else {
								// Append to contents of cell, on new line
								dataTable.setCell(col + 3, row, dataTable.getCell(col + 3, row) + "<br />" + valueToShow);
							}
						}
					} else {
						dataTable.setCell(col + 3, row, "NA");
					}
				}
				
			}
			else
			{
				Object[] rowValues = values[row];
				for (int col = 0; col < rowValues.length; col++) {
					Object val = rowValues[col];
					if (val != null) {
						dataTable.setCell(col + 3, row, val);
					}
					else {
						dataTable.setCell(col + 3, row, "NA");
					}
				}
				
				
			}
			
			
		}
		
		return dataTable.toHtml();
	}
	
	public String renderFilterPart(Database db) throws MatrixException {
		String divContents = "";
					
		// Show applied filter rules
		String filterRules = " none";
		if (this.matrix.getRules().size() > 0) {
			filterRules = "<br />";
			int filterCnt = 0;
			for (MatrixQueryRule mqr : this.matrix.getRules()) {
				// Show only column value filters to user
				if (mqr.getFilterType().equals(MatrixQueryRule.Type.colValueProperty)) {
					String measurementName = "";
					for (Object meas : matrix.getColHeaders(db)) {
						
						if(meas instanceof ObservationElement)
						{
							ObservationElement measr = (ObservationElement)meas;
							if (measr.getId().intValue() == mqr.getDimIndex().intValue()) {
								measurementName = measr.getName();
							}
						}
						else{
							measurementName = meas.toString();
						}
						
						
					}
					filterRules +=  measurementName + " " + mqr.getOperator().toString() + " " + mqr.getValue();
					ActionInput removeButton = new ActionInput(REMOVEFILTER + "_" + filterCnt, "", "");
					removeButton.setIcon("generated-res/img/delete.png");
					filterRules += removeButton.render() + "<br />";
				}
				filterCnt++;
			}
		}
		divContents += new Paragraph("filterRules", "Applied filters:" + filterRules).render();
		// button to clear all value filter rules
		//divContents += new ActionInput(CLEARFILTERS, "", "Clear all filters").render();
		// add column filter
		SelectInput colId = new SelectInput(COLID);
		divContents += "Add filter:";
		
		List<? extends Object> colH = matrix.getColHeaders(db);
		if(colH.get(0) instanceof Entity)
		{
			List<? extends Entity> lala = (List<? extends Entity>) colH;
			colId.setEntityOptions(lala);
		}
		else
		{
			//TODO!!!!!
		}
		
		// NB: options are added with Measurement ID's as values and Names as labels
		colId.setNillable(true);
		divContents += colId.render();
		SelectInput operator = new SelectInput(OPERATOR);
		operator.addOption("Like", "Like");
		operator.addOption("Equals", "Equals");
		divContents += operator.render();
		StringInput colValue = new StringInput(COLVALUE);
		divContents += colValue.render();
		divContents += new ActionInput(FILTERCOL, "", "Apply").render();
		// column header filter
		if (columnsRestricted) {
			List selectedMeasurements = new ArrayList();
			selectedMeasurements.addAll(matrix.getColHeaders(db));
			MrefInput measurementChooser = new MrefInput(MEASUREMENTCHOOSER, "Add/remove columns:", 
					selectedMeasurements, false, false, 
					"Choose one or more columns (i.e. measurements) to be displayed in the matrix viewer", 
					Measurement.class);
			//disable display of button for adding new measurements from here
			measurementChooser.setIncludeAddButton(false);
			divContents += new Newline().render();
			divContents += new Newline().render();
			divContents += "Add/remove columns:";
			divContents += measurementChooser.render();
			divContents += new ActionInput(UPDATECOLHEADERFILTER, "", "Update").render();
		}
		
		return divContents;
	}
	
	public void removeFilter(String action) throws MatrixException
	{
		int filterNr = Integer.parseInt(action.substring(action.lastIndexOf("_") + 1));
		this.matrix.getRules().remove(filterNr);
		matrix.reload();
	}
	
	public void clearFilters(Database db, Tuple t) throws MatrixException
	{
		matrix.reset();
	}
	
	/**
	 * Remove only the colValueProperty type filters from the matrix.
	 * 
	 * @param db
	 * @param t
	 * @throws MatrixException
	 */
	public void clearValueFilters(Database db, Tuple t) throws MatrixException
	{
		List<MatrixQueryRule> removeList = new ArrayList<MatrixQueryRule>();
		for (MatrixQueryRule mqr : this.matrix.getRules()) {
			if (mqr.getFilterType().equals(MatrixQueryRule.Type.colValueProperty)) {
				removeList.add(mqr);
			}
		}
		this.matrix.getRules().removeAll(removeList);
		matrix.reload();
	}
	
	public void reloadMatrix(Database db, Tuple t) throws MatrixException
	{
		matrix.reload();
	}

	public void filterCol(Database db, Tuple t) throws Exception
	{
		// First find out whether to filter on the value or the relation_Name field
		String valuePropertyToUse = ObservedValue.VALUE;
		int measurementId = t.getInt(COLID);
		Measurement filterMeasurement = db.findById(Measurement.class, measurementId);
		if (filterMeasurement.getDataType().equals("xref")) {
			valuePropertyToUse = ObservedValue.RELATION_NAME;
		}
		// Find out operator to use
		QueryRule.Operator op;
		if (t.getString(OPERATOR).equals("Equals")) {
			op = QueryRule.Operator.EQUALS;
		} else {
			op = QueryRule.Operator.LIKE;
		}
		// Then do the actual slicing
		matrix.sliceByColValueProperty(measurementId,
				valuePropertyToUse, op, t.getObject(COLVALUE));
	}
	
	public void updateColHeaderFilter(Database db, Tuple t) throws Exception
	{
		List<?> chosenMeasurementIds;
		if (t.getList(MEASUREMENTCHOOSER) != null) {
			chosenMeasurementIds = t.getList(MEASUREMENTCHOOSER);
		} else {
			chosenMeasurementIds = new ArrayList<Object>();
		}
		List<String> chosenMeasurements = new ArrayList<String>();
		for (Object measurementId : chosenMeasurementIds) {
			int measId = Integer.parseInt((String)measurementId);
			chosenMeasurements.add(db.findById(Measurement.class, measId).getName());
		}
		// Find and update col header filter rule
		for (MatrixQueryRule mqr : matrix.getRules()) {
			if (mqr.getFilterType().equals(MatrixQueryRule.Type.colHeader)) {
				if (chosenMeasurements.size() > 0) {
					mqr.setValue(chosenMeasurements); // update
				} else {
					matrix.getRules().remove(mqr); // remove rule if no measurements selected
					// Consequence of this is that, if you 'click away' all measurements,
					// there is no restriction on measurement anymore and you end up with ALL columns.
					// Is this desirable? A user may not understand this and may expect to see
					// NO columns. How would this be achievable? Would we have to do some tricks
					// in createQuery()?
				}
				break;
			}
		}
		matrix.setColLimit(chosenMeasurements.size()); // grow with selected measurements
		matrix.reload();
	}
	
	public void rowHeaderEquals(Database db, Tuple t) throws Exception
	{
		matrix.sliceByRowProperty(ObservationElement.ID, QueryRule.Operator.EQUALS, t.getString(ROWHEADER));
	}

	public void changeRowLimit(Database db, Tuple t)
	{
		this.matrix.setRowLimit(t.getInt(ROWLIMIT));
	}

//	public void changeColLimit(Database db, Tuple t)
//	{
//		this.matrix.setColLimit(t.getInt(COLLIMIT));
//	}

	public void moveLeftEnd(Database db, Tuple t) throws MatrixException
	{
		this.matrix.setColOffset(0);
	}

	public void moveLeft(Database db, Tuple t) throws MatrixException
	{			
		this.matrix
				.setColOffset(matrix.getColOffset() - matrix.getColLimit() > 0 ? matrix
						.getColOffset() - matrix.getColLimit()
						: 0);
	}

	public void moveRight(Database db, Tuple t) throws MatrixException
	{
		this.matrix
				.setColOffset(matrix.getColOffset() + matrix.getColLimit() < matrix
						.getColCount(db) ? matrix.getColOffset()
						+ matrix.getColLimit() : matrix.getColOffset());
	}

	public void moveRightEnd(Database db, Tuple t) throws MatrixException
	{
		this.matrix
				.setColOffset((matrix.getColCount(db) % matrix.getColLimit() == 0 ? new Double(
						matrix.getColCount(db) / matrix.getColLimit()).intValue() - 1
						: new Double(matrix.getColCount(db)
								/ matrix.getColLimit()).intValue())
						* matrix.getColLimit());
	}

	public void moveUpEnd(Database db, Tuple t) throws MatrixException
	{
		this.matrix.setRowOffset(0);
	}

	public void moveUp(Database db, Tuple t) throws MatrixException
	{
		this.matrix
				.setRowOffset(matrix.getRowOffset() - matrix.getRowLimit() > 0 ? matrix
						.getRowOffset() - matrix.getRowLimit()
						: 0);
	}

	public void moveDown(Database db, Tuple t) throws MatrixException
	{
		this.matrix
				.setRowOffset(matrix.getRowOffset() + matrix.getRowLimit() < matrix
						.getRowCount(db) ? matrix.getRowOffset()
						+ matrix.getRowLimit() : matrix.getRowOffset());
	}

	public void moveDownEnd(Database db, Tuple t) throws MatrixException
	{
		this.matrix
				.setRowOffset((matrix.getRowCount(db) % matrix.getRowLimit() == 0 ? new Double(
						matrix.getRowCount(db) / matrix.getRowLimit()).intValue() - 1
						: new Double(matrix.getRowCount(db)
								/ matrix.getRowLimit()).intValue())
						* matrix.getRowLimit());
	}
	
	public void handleRequest(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException
	{
		// automatically calls functions with same name as action (ommiting widget specific prefix)
		delegate( request.getAction(), db, request);
	}

	public void delegate(String action, Database db, Tuple request)
			throws HandleRequestDelegationException
	{
		// try/catch for db.rollbackTx
		try
		{
			// try/catch for method calling
			try
			{
				db.beginTx();
				logger.debug("trying to use reflection to call "
						+ this.getClass().getName() + "." + action);
				Method m = this.getClass().getMethod(action, Database.class,
						Tuple.class);
				m.invoke(this, db, request);
				logger.debug("call of " + this.getClass().getName() + "(name="
						+ this.getName() + ")." + action + " completed");
				if(db.inTx())
                    db.commitTx();
			}
			catch (NoSuchMethodException e1)
			{
				this.callingScreenController.getModel().setMessages(
						new ScreenMessage("Unknown action: " + action, false));
				logger.error("call of " + this.getClass().getName() + "(name="
						+ this.getName() + ")." + action
						+ "(db,tuple) failed: " + e1.getMessage());
				db.rollbackTx();
			}
			catch (Exception e)
			{
				logger.error("call of " + this.getClass().getName() + "(name="
						+ this.getName() + ")." + action + " failed: "
						+ e.getMessage());
				e.printStackTrace();
				this.callingScreenController.getModel().setMessages(
						new ScreenMessage(e.getCause().getMessage(), false));
				db.rollbackTx();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public List<?> getSelection(Database db) throws MatrixException {
		return matrix.getRowHeaders(db);
	}
	
	public SliceableMatrix getMatrix()
	{
		return matrix;
	}
	
	public boolean getShowValueValidRange() {
		return showValueValidRange;
	}

	public void setShowValueValidRange(boolean showValueValidRange) {
		this.showValueValidRange = showValueValidRange;
	}

}
