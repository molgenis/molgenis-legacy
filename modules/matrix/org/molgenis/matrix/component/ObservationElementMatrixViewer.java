package org.molgenis.matrix.component;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.FlowLayout;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.LabelInput;
import org.molgenis.framework.ui.html.Newline;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.matrix.MatrixException;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

public class ObservationElementMatrixViewer extends HtmlWidget
{
	ScreenController<?> parentScreenController;
	
	SliceablePhenoMatrix<? extends ObservationElement, ? extends ObservationElement> matrix;
	Logger logger = Logger.getLogger(this.getClass());

	public ObservationElementMatrixViewer(ScreenController<?> parentScreenController, String name, SliceablePhenoMatrix<? extends ObservationElement, ? extends ObservationElement> matrix)
	{
		super(name);
		this.parentScreenController = parentScreenController;
		this.matrix = matrix;
	}
	
	public void handleRequest(Database db, Tuple t) throws HandleRequestDelegationException
	{
		this.delegate(t.getAction(), db, t);
	}
	
	public String toHtml()
	{	
		//TODO all strings should be replace with string constants.
		//e.g. public static String CHANGEROWLIMIT = "matrix_changeRowLimit";
		//
		
		FlowLayout f = new FlowLayout();
		
		//create all the buttons
		// rowlimit
		f.add(new IntInput(getName()+"_rowLimit", matrix.getRowLimit()));
		f.add(new ActionInput(getName()+"_changeRowLimit"));
		f.add(new Newline());
		// colLimit
		f.add(new IntInput(getName()+"_colLimit", matrix.getColLimit()));
		f.add(new ActionInput(getName()+"_changeColLimit"));
		f.add(new Newline());

		// move horizontal
		f.add(new ActionInput(getName()+"_moveLeftEnd"));
		f.add(new ActionInput(getName()+"_moveLeft"));
		f.add(new ActionInput(getName()+"_moveRight"));
		f.add(new ActionInput(getName()+"_moveRightEnd"));
		f.add(new Newline());

		// move vertical
		f.add(new ActionInput(getName()+"_moveUpEnd"));
		f.add(new ActionInput(getName()+"_moveUp"));
		f.add(new ActionInput(getName()+"_moveDown"));
		f.add(new ActionInput(getName()+"_moveDownEnd"));
		f.add(new Newline());
		
		try
		{
			// test column filters, currently only 'equals' and 'sort'. Of
			// course this should only show fields in the list
			f.add(new LabelInput("Add column filter:"));
			f.add(new Newline());
			SelectInput colIndex = new SelectInput(getName()+"_colIndex");
			colIndex.setEntityOptions(matrix.getColHeaders());
			colIndex.setNillable(true);
			f.add(colIndex);
			f.add(new StringInput(getName()+"_colValue"));
			f.add(new ActionInput(getName()+"_colEquals"));
			f.add(new Newline());

			// test column filters, currently only 'equals' and 'sort'
			f.add(new LabelInput("Add row filter:"));
			f.add(new Newline());
			SelectInput rowIndex = new SelectInput(getName()+"_rowIndex");
			rowIndex.setEntityOptions(matrix.getRowHeaders());
			colIndex.setNillable(true);
			f.add(rowIndex);
			f.add(new StringInput(getName()+"_rowValue"));
			f.add(new ActionInput(getName()+"_rowEquals"));
			f.add(new Newline());

			f.add(new ActionInput(getName()+"_clearFilters", "", "Reset"));

		}
		catch (Exception e)
		{
			((EasyPluginController)this.parentScreenController).setError(e.getMessage());
			e.printStackTrace();
		}

		String result = f.render();
		
		try
		{
			//first try: simple table
			result += "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"display\" id=\""+this.getId()+"\">";
			
			List<ObservedValue>[][] values = matrix.getValueLists();
			List<? extends ObservationElement> rows = matrix.getRowHeaders();
			List<? extends ObservationElement> cols = matrix.getColHeaders();
			
			//print colHeaders
			result += "<thead><tr><th>&nbsp;</th>";
			for(ObservationElement col: cols)
			{
				result +="<th>"+col.getName()+"</th>";
			}
			result += "</thead><tbody>";
			
			//print rowHeader + colValues
			for(int row = 0; row < values.length; row++)
			{
				List<ObservedValue>[] rowValues = values[row];
				
				//print rowheader
				result +="<tr><td>"+rows.get(row).getName()+"</td>";
				
				for(int col = 0; col < rowValues.length; col++)
				{
					result +="<td>";
					if(rowValues[col] != null || rowValues[col].size() == 0)
					{
						boolean first = true;
						for(ObservedValue val: rowValues[col])
						{
							String valueToShow = val.getValue();
							if (valueToShow == null) {
								valueToShow = val.getRelation_Name();
							}
							if(first) 
							{
								first = false;
								result += valueToShow;
							}
							else
							{
								result += ("," + valueToShow);
							}
						}
					}
					else
					{
						result += "NA";
					}
					result += "</td>";

				}
				
				//close row
				result += "</tr>";
			}
			//close table
			result += "</tbody></table><script>$('#"+getId()+"').dataTable({" +
					"\n\"bPaginate\": false," +
					"\n\"bLengthChange\": false," +
					"\n\"bFilter\": false," +
					"\n\"bSort\": false," +
					"\n\"bInfo\": false," +
					"\n\"bJQueryUI\": true,});</script>";
			
			return result;
		}
		catch (MatrixException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ERROR: "+e.getMessage();
		}
	}
	
	public void clearFilters(Database db, Tuple t) throws MatrixException
	{
		matrix.reset();
	}

	public void colEquals(Database db, Tuple t) throws MatrixException
	{
		matrix.sliceByColValueProperty(t.getInt("colIndex"),
				ObservedValue.VALUE, QueryRule.Operator.LIKE,
				t.getObject("colValue"));
	}
	
	public void rowEquals(Database db, Tuple t) throws MatrixException
	{
		matrix.sliceByRowValueProperty(t.getInt("rowIndex"),
				ObservedValue.VALUE, QueryRule.Operator.LIKE,
				t.getObject("rowValue"));
	}

	public void changeRowLimit(Database db, Tuple t)
	{
		this.matrix.setRowLimit(t.getInt("rowLimit"));
	}

	public void changeColLimit(Database db, Tuple t)
	{
		this.matrix.setColLimit(t.getInt("colLimit"));
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
				action = request.getAction().substring( (getName()+"_").length());
				
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
				this.parentScreenController.getModel().setMessages(
						new ScreenMessage("Unknown action: " + action, false));
				logger.error("call of " + this.getClass().getName() + "(name="
						+ this.getName() + ")." + action
						+ "(db,tuple) failed: " + e1.getMessage());
				db.rollbackTx();
				// useless - can't do this on every error! we cannot distinguish
				// exceptions because they are all InvocationTargetException
				// anyway
				// }catch (InvocationTargetException e){
				// throw new RedirectedException(e);
			}
			catch (Exception e)
			{
				logger.error("call of " + this.getClass().getName() + "(name="
						+ this.getName() + ")." + action + " failed: "
						+ e.getMessage());
				e.printStackTrace();
				this.parentScreenController.getModel().setMessages(
						new ScreenMessage(e.getCause().getMessage(), false));
				db.rollbackTx();
			}
		}
		// catch (RedirectedException e){
		// throw e;
		// }
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
