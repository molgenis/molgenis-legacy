package org.molgenis.matrix.component;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
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
	public String COLEQUALS = getName() + "_colEquals";
//	public String ROWINDEX = getName() + "_rowIndex";
//	public String ROWVALUE = getName() + "_rowValue";
//	public String ROWEQUALS = getName() + "_rowEquals";
	public String CLEARFILTERS = getName() + "_clearFilters";

	/**
	 * Default constructor.
	 * 
	 * @param callingScreenController
	 * @param name
	 * @param matrix
	 */
	public ObservationElementMatrixViewer(ScreenController<?> callingScreenController, String name, 
			SliceablePhenoMatrix<? extends ObservationElement, ? extends ObservationElement> matrix)
	{
		super(name);
		super.setLabel("");
		this.callingScreenController = callingScreenController;
		this.matrix = matrix;
	}
	
	/**
	 * Constructor where you immediately restrict the column set by applying a column filter.
	 * TODO: make suitable for multiple column filters combined using OR, e.g.:
	 * measurementName = Species OR measurementName = Sex.
	 * 
	 * @param callingScreenController
	 * @param name
	 * @param matrix
	 * @param measurementName
	 * @throws Exception
	 */
	public ObservationElementMatrixViewer(ScreenController<?> callingScreenController, String name, 
			SliceablePhenoMatrix<? extends ObservationElement, ? extends ObservationElement> matrix,
			String measurementName) throws Exception
	{
		this(callingScreenController, name, matrix);
		this.matrix.sliceByColProperty(Measurement.NAME, Operator.EQUALS, measurementName);
	}
	
	public void handleRequest(Database db, Tuple t) throws HandleRequestDelegationException
	{
		this.delegate(t.getAction(), db, t);
	}
	
	public String toHtml()
	{	
		FlowLayout f = new FlowLayout();
		
		// TODO: set images on buttons (using setIcon())
		
		//create all the buttons
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

		// move horizontal
		f.add(new ActionInput(MOVELEFTEND, "", "Move left end"));
		f.add(new ActionInput(MOVELEFT, "", "Move left"));
		f.add(new ActionInput(MOVERIGHT, "", "Move right"));
		f.add(new ActionInput(MOVERIGHTEND, "", "Move right end"));
		f.add(new Newline());

		// move vertical
		f.add(new ActionInput(MOVEUPEND, "", "Move up end"));
		f.add(new ActionInput(MOVEUP, "", "Move up"));
		f.add(new ActionInput(MOVEDOWN, "", "Move down"));
		f.add(new ActionInput(MOVEDOWNEND, "", "Move down end"));
		f.add(new Newline());
		
		try
		{
			// test column filters, currently only 'equals'.
			// Of course this should only show fields in the list
			SelectInput colId = new SelectInput(COLID);
			colId.setLabel("Add column filter:");
			colId.setEntityOptions(matrix.getColHeaders());
			// Options are added with ID's as values and labels as labels
			colId.setNillable(true);
			f.add(colId);
			StringInput colValue = new StringInput(COLVALUE);
			colValue.setLabel("");
			f.add(colValue);
			f.add(new ActionInput(COLEQUALS, "", "Equals"));
			f.add(new Newline());

			// test row filters, currently only 'equals'
//			SelectInput rowIndex = new SelectInput(ROWINDEX);
//			rowIndex.setLabel("Add row filter:");
//			rowIndex.setEntityOptions(matrix.getRowHeaders());
//			rowIndex.setNillable(true);
//			f.add(rowIndex);
//			StringInput rowValue = new StringInput(ROWVALUE);
//			rowValue.setLabel("");
//			f.add(rowValue);
//			f.add(new ActionInput(ROWEQUALS, "", "Equals"));
//			f.add(new Newline());
			
			// show applied filter rules
			String filterRules = "<ul>";
			for (MatrixQueryRule mqr : this.matrix.rules) {
				filterRules += "<li>" + mqr.toString() + "</li>";
			}
			filterRules += "</ul>";
			f.add(new TextParagraph("filterRules", "Applied filter rules:" + filterRules));
			
			// button to clear all filter rules
			f.add(new ActionInput(CLEARFILTERS, "", "Clear all filters"));

		}
		catch (Exception e)
		{
			((EasyPluginController)this.callingScreenController).setError(e.getMessage());
			e.printStackTrace();
		}

		String result = f.render();
		
		try
		{
			JQueryDataTable dataTable = new JQueryDataTable("MatrixDataTable");
			
			List<ObservedValue>[][] values = matrix.getValueLists();
			List<? extends ObservationElement> rows = matrix.getRowHeaders();
			List<? extends ObservationElement> cols = matrix.getColHeaders();
			
			//print colHeaders
			for (ObservationElement col: cols)
			{
				dataTable.addColumn(col.getName());
			}
			
			//print rowHeader + colValues
			for (int row = 0; row < values.length; row++)
			{
				List<ObservedValue>[] rowValues = values[row];
				
				//print rowheader
				dataTable.addRow(rows.get(row).getName());
				
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
								dataTable.setCell(col, row, valueToShow);
							} else {
								dataTable.setCell(col, row, dataTable.getCell(col, row) + ", " + valueToShow);
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
	
	public void clearFilters(Database db, Tuple t) throws MatrixException
	{
		matrix.reset();
	}

	public void colEquals(Database db, Tuple t) throws Exception
	{
		String valuePropertyToUse = ObservedValue.VALUE;
		int measurementId = t.getInt(COLID);
		Measurement filterMeasurement = db.findById(Measurement.class, measurementId);
		// NB: be sure that you use Measurements for the columns!
		if (filterMeasurement.getDataType().equals("xref")) {
			valuePropertyToUse = ObservedValue.RELATION_NAME;
		}
		matrix.sliceByColValueProperty(measurementId,
				valuePropertyToUse, QueryRule.Operator.LIKE,
				t.getObject(COLVALUE));
	}
	
//	public void rowEquals(Database db, Tuple t) throws MatrixException
//	{
//		matrix.sliceByRowValueProperty(t.getInt(ROWINDEX),
//				ObservedValue.VALUE, QueryRule.Operator.LIKE,
//				t.getObject(ROWVALUE));
//	}

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
				action = request.getAction().substring((getName() + "_").length());
				
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
