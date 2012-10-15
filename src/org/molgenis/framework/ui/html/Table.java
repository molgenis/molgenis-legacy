package org.molgenis.framework.ui.html;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.molgenis.util.Pair;
import org.molgenis.util.Tuple;

/*
 * Provides an html table with objects, e.g. input components, in the cells.
 * First you add columns (addColumn) and rows (addRows), then you set the contents
 * of the cells (setCell).
 * You can set/override the default CSS styles for the header and for the individual cells
 * (setHeaderCellStyle, setDefaultCellStyle and setCellStyle).
 * Row and colspan are currently not supported.
 */
public class Table extends HtmlWidget
{
	LinkedHashMap<Pair<Integer, Integer>, Object> cells = new LinkedHashMap<Pair<Integer, Integer>, Object>();
	LinkedHashMap<Pair<Integer, Integer>, String> cellStyles = new LinkedHashMap<Pair<Integer, Integer>, String>();
	List<String> cols = new ArrayList<String>();
	List<String> rows = new ArrayList<String>();
	String style = null;
	protected String defaultCellStyle = "border: 1px solid black; padding:2px";
	protected String headerCellStyle = "border: 1px solid black; padding:2px; background-color: #5B82A4; color: white";
	boolean headerColumn = true;
	boolean headerRow = true;

	/**
	 * Constructor with empty label.
	 * 
	 * @param name
	 */
	public Table(String name)
	{
		this(name, "");
	}

	public Table(String name, String label)
	{
		super(name, label);
		this.setLabel(label);
	}

	/**
	 * Constructor used to control the presence of the header column and row
	 * 
	 * @param name
	 * @param label
	 * @param headerColumn
	 *            specify the presence of header column and row, defaults to
	 *            true
	 */
	public Table(String name, String label, boolean headerColumn, boolean headerRow)
	{
		super(name, label);
		this.setLabel(label);
		this.headerColumn = headerColumn;
		this.headerRow = headerRow;

	}

	@Override
	/**
	 * Renders the table.
	 */
	public String toHtml()
	{
		String result = "<table";

		if (style != null)
		{
			result += " style=\"clear:both;" + style + "\"";
		}

		result += " width=\"400\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"display\" id=\""
				+ this.getId() + "\">";
		if (headerRow)
		{
			result += printHeaders();
		}
		result += printBody();
		result += "</table>";

		return result;
	}

	private String printHeaders()
	{

		String result = "<thead><tr>";
		if (headerColumn)
		{
			result += "<th></th>";
		}
		for (String col : cols)
		{
			result += ("<th style=\"" + getHeaderCellStyle() + "\">" + col + "</th>");
		}
		result += "</tr></thead>";
		return result;
	}

	private String printBody()
	{
		String result = "<tbody>";
		int rowCount = 0;
		for (String row : rows)
		{
			result += printRow(row, rowCount);
			rowCount++;
		}
		result += "</tbody>";
		return result;
	}

	// default visibility for subclassing in the same package
	String printRow(String row, int rowCount)
	{
		String result = "<tr>";
		if (headerColumn)
		{
			result += ("<th style=\"" + getHeaderCellStyle() + "\">" + row + "</th>");
		}

		for (int colCount = 0; colCount < cols.size(); colCount++)
		{
			result += ("<td style=\"" + getCellStyle(colCount, rowCount) + "\">" + getCellString(colCount, rowCount) + "</td>");
		}
		result += "</tr>";
		return result;
	}

	/**
	 * Set the containing div's css style.
	 */
	public HtmlInput setStyle(String style)
	{
		this.style = style;
		return this;
	}

	/**
	 * Add a column to the Table.
	 * 
	 * @param colName
	 * @return
	 */
	public Integer addColumn(String colName)
	{
		cols.add(colName);
		return cols.size() - 1;
	}

	/**
	 * Remove the column at 'colNr' from the Table.
	 * 
	 * @param colNr
	 * @return
	 */
	public String removeColumn(int colNr)
	{
		return cols.remove(colNr);
	}

	/**
	 * Add a row to the Table.
	 * 
	 * @param rowName
	 * @return
	 */
	public Integer addRow(String rowName)
	{
		rows.add(rowName);
		return rows.size() - 1;
	}

	/**
	 * Remove the row at 'rowNr' from the Table.
	 * 
	 * @param rowNr
	 * @return
	 */
	public String removeRow(int rowNr)
	{
		return rows.remove(rowNr);
	}

	/**
	 * Set the contents of the cell at col, row.
	 * 
	 * @param col
	 * @param row
	 * @param contents
	 */
	public void setCell(int col, int row, Object contents)
	{
		cells.put(new Pair<Integer, Integer>(col, row), contents);
	}

	/**
	 * Get the contents of the cell at col, row.
	 * 
	 * @param col
	 * @param row
	 * @return
	 */
	public Object getCell(int col, int row)
	{
		return cells.get(new Pair<Integer, Integer>(col, row));
	}

	/**
	 * Get the contents of the cell at col, row as a String.
	 * 
	 * @param col
	 * @param row
	 * @return
	 */
	public String getCellString(int col, int row)
	{
		Object o = cells.get(new Pair<Integer, Integer>(col, row));
		if (o == null)
		{
			return "";
		}
		if (o instanceof HtmlInput<?>)
		{
			return ((HtmlInput<?>) o).toHtml();
		}
		return o.toString();
	}

	/**
	 * Set the default CSS style parameters for all non-header cells.
	 * 
	 * @param defaultCellStyle
	 */
	public void setDefaultCellStyle(String defaultCellStyle)
	{
		this.defaultCellStyle = defaultCellStyle;
	}

	/**
	 * Set CSS style parameters for the cell at col, row. E.g.: setCellStyle(1,
	 * 1, "border: 1px")
	 * 
	 * @param col
	 * @param row
	 * @param cellStyle
	 */
	public void setCellStyle(int col, int row, String cellStyle)
	{
		cellStyles.put(new Pair<Integer, Integer>(col, row), cellStyle);
	}

	/**
	 * Get the CSS style parameters for the cell at col, row.
	 * 
	 * @param col
	 * @param row
	 * @return
	 */
	public String getCellStyle(int col, int row)
	{
		String style = cellStyles.get(new Pair<Integer, Integer>(col, row));
		if (style != null)
		{
			return style;
		}
		else
		{
			return defaultCellStyle;
		}
	}

	/**
	 * Set CSS style parameters for all header cells.
	 * 
	 * @param headerCellStyle
	 */
	public void setHeaderCellStyle(String headerCellStyle)
	{
		this.headerCellStyle = headerCellStyle;
	}

	/**
	 * Get the CSS style parameters for all header cells.
	 * 
	 * @return
	 */
	public String getHeaderCellStyle()
	{
		return headerCellStyle;
	}

	@Override
	public String toHtml(Tuple params) throws ParseException, HtmlInputException
	{
		// TODO?
		throw new UnsupportedOperationException();
	}
}
