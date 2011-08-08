package org.molgenis.matrix.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.MolgenisService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.NameNotUniqueException;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.ScreenModel.Show;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.LabelInput;
import org.molgenis.matrix.Matrix;
import org.molgenis.matrix.MatrixException;
import org.molgenis.util.Tuple;

/**
 * Wrapper of jQuery DataTable to view MOLGENIS matrices. The setCols and
 * setRows have the meaning of selecting a subset of cols/rows, or to customize
 * how they should be rendered (i.e. not as labels but something more advanced).
 * Default the whole matrix will be used (paginated optionally in row/col
 * direction).
 * 
 * TODO: separate interface from implementation so that we can also have
 * alternative DataTableWidgets.
 * 
 * Features: <li>column headers (have a unique identifier and optionally a
 * MolgenisWidget) <li>column groups <li>row headers (have a unique identifier
 * and optionally a MolgenisWidget) <li>row groups <li>a data loading service
 * (using Matrix interface)
 * 
 */
public class MatrixViewer extends HtmlInput<Matrix> implements MolgenisService
{	
	// map of rowids and the widget to show as rowheader
	private Map<String, HtmlInput> rows = null;
	// map of colids and the widget to show as colheader
	private Map<String, HtmlInput> cols = null;
	// limit of rows to be shown
	private int rowLimit = 10;
	// limit of cols to be shown
	private int colLimit = 10;
	
	/**
	 * Default constructor that will select all row and col identifiers from the
	 * matrix for view. Large matrices will paginate the columns as well as the
	 * rows.
	 * @throws NameNotUniqueException 
	 */
	public MatrixViewer(ScreenController<?> controller, String name, Matrix<String,String,String> values) throws MatrixViewException,
			MatrixException, NameNotUniqueException
	{
		this(controller,name,values.getRowNames(),values.getColNames(),values);
	}

	/**
	 * Constructure where it is selected which rows and columns of the data will
	 * be visible to the user.
	 */
	public MatrixViewer(ScreenController<?> controller, String name, List<String> rows, List<String> cols, Matrix<String,String,String> values)
			throws MatrixViewException, NameNotUniqueException
	{
		super(name, null);
		
		//controller.getApplicationController().addService(this);
		
		this.setCols(cols);
		this.setRows(rows);
		this.setValue(values);
	}

	@Override
	public Show handleRequest(Database db, Tuple request,
			PrintWriter outputStream) throws ParseException,
			DatabaseException, IOException
	{
		//write result to outputStream
		//TODO
		
		// TODO Auto-generated method stub
		return ScreenModel.Show.SHOW_JSON;
	}
	
	@Override
	public String toHtml()
	{
		Map<String,Object> templateArgs = new LinkedHashMap<String,Object>();
		templateArgs.put("model",this);
		return new FreemarkerView("MatrixView.ftl", templateArgs).render();	
	}
	
	/**
	 * Set the row identifiers that are selected for view. Values should be
	 * unique. For more complicated layouts use setRows(Map<String,HtmlInput>)
	 * 
	 * @throws MatrixViewException
	 */
	public void setRows(List<String> rowIds) throws MatrixViewException
	{
		Map<String, HtmlInput> temp = new LinkedHashMap<String, HtmlInput>();
		for (String id : rowIds)
		{
			if (temp.containsKey(id)) throw new MatrixViewException(
					"rowIds should be unique");
			temp.put(id, new LabelInput(id, id));
		}

		this.rows = temp;
	}

	/**
	 * Set the unique identifiers from array. @see setRows(List<String> rowIds
	 * 
	 * @throws MatrixViewException
	 */
	public void setRows(String... rowIds) throws MatrixViewException
	{
		this.setRows(Arrays.asList(rowIds));
	}

	/**
	 * Set the row identifiers with custom widgets. In the simple case that the
	 * DataTable should just show the rowIds as string you can use
	 * setRows(List<String>)
	 * 
	 * @throws MatrixViewException
	 */
	public void setRows(Map<String, HtmlInput> rowIds)
			throws MatrixViewException
	{
		if (rowIds == null) throw new MatrixViewException(
				"rowIds cannot be null");
		this.rows = rowIds;
	}

	/** Map of RowIds and the widget used to visualize it */
	public Map<String, HtmlInput> getRows()
	{
		return this.rows;
	}

	public Map<String,HtmlInput> getVisibleRows()
	{
		return this.rows;
	}
	
	public Matrix getVisibleMatrix()
	{
		return getObject();
	}
	
	/**
	 * Set the col identifiers. Values should be unique. For more complicated
	 * layouts use setCols(Map<String,HtmlInput>)
	 * 
	 * @throws MatrixViewException
	 */
	public void setCols(List<String> colIds) throws MatrixViewException
	{
		Map<String, HtmlInput> temp = new LinkedHashMap<String, HtmlInput>();
		for (String id : colIds)
		{
			if (temp.containsKey(id)) throw new MatrixViewException(
					"colIds should be unique");
			temp.put(id, new LabelInput(id, id));
		}

		this.cols = temp;
	}

	/**
	 * Set the unique identifiers from array. @see setCows(List<String> colIds)
	 * 
	 * @throws MatrixViewException
	 */
	public void setCols(String... colIds) throws MatrixViewException
	{
		this.setRows(Arrays.asList(colIds));
	}

	/**
	 * Set the col identifiers with custom widgets. In the simple case that the
	 * DataTable should just show the rowIds as string you can use
	 * setRows(List<String>)
	 * 
	 * @throws MatrixViewException
	 */
	public void setCols(Map<String, HtmlInput> colIds)
			throws MatrixViewException
	{
		if (colIds == null) throw new MatrixViewException(
				"colIds cannot be null");
		this.cols = colIds;
	}

	/** Map of ColIds and the widget used to visualize it */
	public Map<String, HtmlInput> getCols()
	{
		return this.cols;
	}

	public Map<String, HtmlInput> getVisibleCols()
	{
		return this.cols;
	}
	
	/** Set the values of the data table 
	 * @throws MatrixViewException */
	public void setValue(Matrix<?,?,?> values) throws MatrixViewException
	{
		if (values == null) throw new MatrixViewException(
				"values matrix cannot be null");
		super.setValue(values);
	}

	public int getRowLimit()
	{
		return rowLimit;
	}

	public void setRowLimit(int rowLimit)
	{
		this.rowLimit = rowLimit;
	}

	public int getColLimit()
	{
		return colLimit;
	}

	public void setColLimit(int colLimit)
	{
		this.colLimit = colLimit;
	}
	
	public static final class MatrixViewException extends Exception
	{
		public MatrixViewException(String string)
		{
			super(string);
		}

		public MatrixViewException(Exception ex)
		{
			super(ex);
		}

		private static final long serialVersionUID = 1L;
	}
}
