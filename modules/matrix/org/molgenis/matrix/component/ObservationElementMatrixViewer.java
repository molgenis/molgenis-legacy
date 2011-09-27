package org.molgenis.matrix.component;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.framework.ui.html.FlowLayout;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.JQueryDataTable;
import org.molgenis.framework.ui.html.Newline;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.TextParagraph;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

public class ObservationElementMatrixViewer extends HtmlWidget
{
	ScreenController<?> callingScreenController;
	
	SliceablePhenoMatrix<? extends ObservationElement, ? extends ObservationElement> matrix;
	Logger logger = Logger.getLogger(this.getClass());
	
	private boolean showLimitControls = true;
	
	public String ROWLIMIT = getName() + "_rowLimit";
	public String CHANGEROWLIMIT = getName() + "_changeRowLimit";
	public String COLLIMIT = getName() + "_colLimit";
	public String CHANGECOLLIMIT = getName() + "_changeColLimit";
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
	public String COLLIKE = getName() + "_colLike";
	public String ROWHEADER = getName() + "_rowHeader";
	public String ROWHEADEREQUALS = getName() + "_rowHeaderEquals";
	public String CLEARFILTERS = getName() + "_clearFilters";
	public String REMOVEFILTER = getName() + "_removeFilter";
	public String RELOADMATRIX = getName() + "_reloadMatrix";
	public String SELECTED = getName() + "_selected";
	
	/**
	 * Default constructor.
	 * 
	 * @param callingScreenController
	 * @param name
	 * @param matrix
	 * @param showLimitControls
	 */
	public ObservationElementMatrixViewer(ScreenController<?> callingScreenController, String name, 
			SliceablePhenoMatrix<? extends ObservationElement, ? extends ObservationElement> matrix,
			boolean showLimitControls)
	{
		super(name);
		super.setLabel("");
		this.callingScreenController = callingScreenController;
		this.matrix = matrix;
		this.showLimitControls = showLimitControls;
	}
	
	/**
	 * Constructor where you immediately restrict the column set by applying one or more
	 * filter rules.
	 * 
	 * @param callingScreenController
	 * @param name
	 * @param matrix
	 * @param showLimitControls
	 * @param filterRules
	 * @throws Exception
	 */
	public ObservationElementMatrixViewer(ScreenController<?> callingScreenController, String name, 
			SliceablePhenoMatrix<? extends ObservationElement, ? extends ObservationElement> matrix,
			boolean showLimitControls, List<MatrixQueryRule> filterRules) throws Exception
	{
		this(callingScreenController, name, matrix, showLimitControls);
		this.matrix.rules.addAll(filterRules);
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
		FlowLayout f = new FlowLayout();
		
		try
		{
			// Option to add column filters, currently only 'like'.
			// Of course this should only show fields in the list
			SelectInput colId = new SelectInput(COLID);
			colId.setLabel("Add column filter:");
			colId.setEntityOptions(matrix.getColHeaders());
			// NB: options are added with Measurement ID's as values and Names as labels
			colId.setNillable(true);
			f.add(colId);
			f.add(new Newline());
			StringInput colValue = new StringInput(COLVALUE);
			colValue.setLabel("Like");
			f.add(colValue);
			f.add(new Newline());
			f.add(new ActionInput(COLLIKE, "", "Apply"));
			f.add(new Newline());

			// Option to add row header filters, currently only 'equals'.
			SelectInput rowHeader = new SelectInput(ROWHEADER);
			rowHeader.setLabel("Add row header (target name) filter:");
			rowHeader.setEntityOptions(matrix.getRowHeaders());
			// NB: options are added with Individual ID's as values and Names as labels
			rowHeader.setNillable(true);
			f.add(rowHeader);
			f.add(new Newline());
			f.add(new ActionInput(ROWHEADEREQUALS, "", "Apply"));
			f.add(new Newline());
			
			// Show applied filter rules
			String filterRules = " none";
			if (this.matrix.rules.size() > 0) {
				filterRules = "<br />";
				int filterCnt = 0;
				for (MatrixQueryRule mqr : this.matrix.rules) {
					ActionInput removeButton = new ActionInput(REMOVEFILTER + "_" + filterCnt, "", "");
					removeButton.setIcon("generated-res/img/delete.png");
					removeButton.setIconHeight(22);
					removeButton.setIconHeight(22);
					filterRules += mqr.toString() + removeButton.renderDefault() + "<br />";
					filterCnt++;
				}
			}
			f.add(new TextParagraph("filterRules", "Applied filter rules:" + filterRules));
			
			// button to clear all filter rules
			f.add(new ActionInput(CLEARFILTERS, "", "Clear all filters"));
			// button to reload the matrix data, whilst keeping the filters intact
			f.add(new ActionInput(RELOADMATRIX, "", "Reload data"));
			f.add(new Newline());

		}
		catch (Exception e)
		{
			((EasyPluginController)this.callingScreenController).setError(e.getMessage());
			e.printStackTrace();
		}
		
		if (showLimitControls) {
			// rowlimit
			IntInput rowLimitInput = new IntInput(ROWLIMIT, matrix.getRowLimit());
			rowLimitInput.setLabel("Row limit:");
			f.add(rowLimitInput);
			f.add(new ActionInput(CHANGEROWLIMIT, "", "Change"));
			f.add(new Newline());
			// colLimit
			IntInput colLimitInput = new IntInput(COLLIMIT, matrix.getColLimit());
			colLimitInput.setLabel("Column limit:");
			f.add(colLimitInput);
			f.add(new ActionInput(CHANGECOLLIMIT, "", "Change"));
			f.add(new Newline());
		}
		
		// move horizontal
		ActionInput moveLeftEnd = new ActionInput(MOVELEFTEND, "", "");
		moveLeftEnd.setIcon("generated-res/img/first.png");
		moveLeftEnd.setIconWidth(22);
		moveLeftEnd.setIconHeight(22);
		f.add(moveLeftEnd);
		ActionInput moveLeft = new ActionInput(MOVELEFT, "", "");
		moveLeft.setIcon("generated-res/img/prev.png");
		moveLeft.setIconWidth(22);
		moveLeft.setIconHeight(22);
		f.add(moveLeft);
		ActionInput moveRight = new ActionInput(MOVERIGHT, "", "");
		moveRight.setIcon("generated-res/img/next.png");
		moveRight.setIconWidth(22);
		moveRight.setIconHeight(22);
		f.add(moveRight);
		ActionInput moveRightEnd = new ActionInput(MOVERIGHTEND, "", "");
		moveRightEnd.setIcon("generated-res/img/last.png");
		moveRightEnd.setIconWidth(22);
		moveRightEnd.setIconHeight(22);
		f.add(moveRightEnd);
		f.add(new Newline());

		// move vertical
		ActionInput moveUpEnd = new ActionInput(MOVEUPEND, "", "");
		moveUpEnd.setIcon("generated-res/img/rowStart.png");
		moveUpEnd.setIconWidth(22);
		moveUpEnd.setIconHeight(22);
		f.add(moveUpEnd);
		ActionInput moveUp = new ActionInput(MOVEUP, "", "");
		moveUp.setIcon("generated-res/img/up.png");
		moveUp.setIconWidth(22);
		moveUp.setIconHeight(22);
		f.add(moveUp);
		ActionInput moveDown = new ActionInput(MOVEDOWN, "", "");
		moveDown.setIcon("generated-res/img/down.png");
		moveDown.setIconWidth(22);
		moveDown.setIconHeight(22);
		f.add(moveDown);
		ActionInput moveDownEnd = new ActionInput(MOVEDOWNEND, "", "");
		moveDownEnd.setIcon("generated-res/img/rowStop.png");
		moveDownEnd.setIconWidth(22);
		moveDownEnd.setIconHeight(22);
		f.add(moveDownEnd);
		f.add(new Newline());

		String result = f.render();
		
		try
		{
			JQueryDataTable dataTable = new JQueryDataTable(getName() + "DataTable");
			
			List<ObservedValue>[][] values = matrix.getValueLists();
			List<? extends ObservationElement> rows = matrix.getRowHeaders();
			List<? extends ObservationElement> cols = matrix.getColHeaders();
			
			//print colHeaders
			dataTable.addColumn("");
			for (ObservationElement col: cols)
			{
				dataTable.addColumn(col.getName());
			}
			
			//print rowHeader + colValues
			for (int row = 0; row < values.length; row++)
			{
				List<ObservedValue>[] rowValues = values[row];
				
				//print rowHeader
				dataTable.addRow(rows.get(row).getName());
				// print selectbox for this row
				List<String> options = new ArrayList<String>();
				options.add("" + row);
				List<String> optionLabels = new ArrayList<String>();
				optionLabels.add("");
				CheckboxInput rowCheckbox = new CheckboxInput(SELECTED + "_" + row, options, 
						optionLabels, "", null, true, false);
				rowCheckbox.setId(SELECTED + "_" + row);
				dataTable.setCell(0, row, rowCheckbox);
				for (int col = 0; col < rowValues.length; col++)
				{
					if (rowValues[col] != null || rowValues[col].size() == 0)
					{
						boolean first = true;
						for(ObservedValue val: rowValues[col])
						{
							String valueToShow = val.getValue();
							if (valueToShow == null) {
								valueToShow = val.getRelation_Name();
							}
							if (first) 
							{
								first = false;
								dataTable.setCell(col + 1, row, valueToShow);
							} else {
								dataTable.setCell(col + 1, row, dataTable.getCell(col + 1, row) + ", " + valueToShow);
							}
						}
					} else {
						dataTable.setCell(col, row, "NA");
					}
				}
			}
			
			result += dataTable.toHtml();
			
			return result;
		}
		catch (MatrixException e)
		{
			e.printStackTrace();
			return "ERROR: " + e.getMessage();
		}
	}
	
	public void removeFilter(String action) throws MatrixException
	{
		int filterNr = Integer.parseInt(action.substring(action.lastIndexOf("_") + 1));
		this.matrix.rules.remove(filterNr);
		matrix.reload();
	}
	
	public void clearFilters(Database db, Tuple t) throws MatrixException
	{
		matrix.reset();
	}
	
	public void reloadMatrix(Database db, Tuple t) throws MatrixException
	{
		matrix.reload();
	}

	public void colLike(Database db, Tuple t) throws Exception
	{
		// First find out whether to filter on the value or the relation_Name field
		String valuePropertyToUse = ObservedValue.VALUE;
		int measurementId = t.getInt(COLID);
		Measurement filterMeasurement = db.findById(Measurement.class, measurementId);
		if (filterMeasurement.getDataType().equals("xref")) {
			valuePropertyToUse = ObservedValue.RELATION_NAME;
		}
		// Then do the actual slicing
		matrix.sliceByColValueProperty(measurementId,
				valuePropertyToUse, QueryRule.Operator.LIKE,
				t.getObject(COLVALUE));
	}
	
	public void rowHeaderEquals(Database db, Tuple t) throws Exception
	{
		matrix.sliceByRowProperty(Individual.ID, QueryRule.Operator.EQUALS, t.getString(ROWHEADER));
	}

	public void changeRowLimit(Database db, Tuple t)
	{
		this.matrix.setRowLimit(t.getInt(ROWLIMIT));
	}

	public void changeColLimit(Database db, Tuple t)
	{
		this.matrix.setColLimit(t.getInt(COLLIMIT));
	}

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
						.getColCount() ? matrix.getColOffset()
						+ matrix.getColLimit() : matrix.getColOffset());
	}

	public void moveRightEnd(Database db, Tuple t) throws MatrixException
	{
		this.matrix
				.setColOffset((matrix.getColCount() % matrix.getColLimit() == 0 ? new Double(
						matrix.getColCount() / matrix.getColLimit()).intValue() - 1
						: new Double(matrix.getColCount()
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
						.getRowCount() ? matrix.getRowOffset()
						+ matrix.getRowLimit() : matrix.getRowOffset());
	}

	public void moveDownEnd(Database db, Tuple t) throws MatrixException
	{
		this.matrix
				.setRowOffset((matrix.getRowCount() % matrix.getRowLimit() == 0 ? new Double(
						matrix.getRowCount() / matrix.getRowLimit()).intValue() - 1
						: new Double(matrix.getRowCount()
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
	
	public List<? extends ObservationElement> getSelection() throws MatrixException {
		return matrix.getRowHeaders();
	}

}
