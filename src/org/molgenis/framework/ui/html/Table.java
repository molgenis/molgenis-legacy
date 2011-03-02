package org.molgenis.framework.ui.html;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.molgenis.util.Pair;
import org.molgenis.util.Tuple;

/*
 * Provides an html table with input components in the cells.
 */
public class Table extends HtmlInput
{
	LinkedHashMap<Pair<Integer, Integer>, Object> cells = new LinkedHashMap<Pair<Integer, Integer>, Object>();
	LinkedHashMap<Pair<Integer, Integer>, String> cellStyles = new LinkedHashMap<Pair<Integer, Integer>, String>();
	List<String> cols = new ArrayList<String>();
	List<String> rows = new ArrayList<String>();
	private String defaultCellStyle = "border: 1px solid black; padding:2px";
	private String headerCellStyle = "border: 1px solid black; padding:2px; background-color: #5B82A4";
	
	public Table()
	{
		this(null, null);
	}
	
	public Table(String name, String label)
	{
		super(name, label);
		this.setLabel(label);
	}
	
	@Override
	/**
	 * Renders the table.
	 */
	public String toHtml()
	{
		String result = "<table>";
		
		result += printHeaders();
		
		int rowCount = 0;
		for (String row : rows) {
			result += printRow(row, rowCount);
			rowCount++;
		}
		
		result += "</table>";
		
		return result;
	}
	
	private String printHeaders() {
		String result = "<tr><th></th>";
		for (String col : cols) {
			result += ("<th style=\"" + getHeaderCellStyle() + "\">" + col + "</th>");
		}
		result += "</tr>";
		
		return result;
	}
	
	private String printRow(String row, int rowCount) {
		String result = "<tr>";
		result += ("<th style=\"" + getHeaderCellStyle() + "\">" + row + "</th>");
		for (int colCount = 0; colCount < cols.size(); colCount++) {
			result += ("<td style=\"" + getCellStyle(colCount, rowCount) + "\">" + 
					getCellString(colCount, rowCount) + "</td>");
		}
		result += "</tr>";
		
		return result;
	}
	
	public Integer addColumn(String colName) {
		cols.add(colName);
		return cols.size() - 1;
	}
	
	public String removeColumn(int colNr) {
		return cols.remove(colNr);
	}
	
	public Integer addRow(String rowName) {
		rows.add(rowName);
		return rows.size() - 1;
	}
	
	public String removeRow(int rowNr) {
		return rows.remove(rowNr);
	}
	
	/**
	 * Set the contents of the cell at col, row.
	 * 
	 * @param col
	 * @param row
	 * @param contents
	 */
	public void setCell(int col, int row, Object contents) {
		cells.put(new Pair<Integer, Integer>(col, row), contents);
	}
	
	/**
	 * Get the contents of the cell at col, row.
	 * @param col
	 * @param row
	 * @return
	 */
	public Object getCell(int col, int row) { 
		return cells.get(new Pair<Integer, Integer>(col, row));
	}
	
	/**
	 * Get the contents of the cell at col, row as a String.
	 * 
	 * @param col
	 * @param row
	 * @return
	 */
	public String getCellString(int col, int row) { 
		Object o = cells.get(new Pair<Integer, Integer>(col, row));
		if (o == null) {
			return "";
		}
		if (o instanceof HtmlInput) {
			return ((HtmlInput) o).toHtml();
		}
		return o.toString();
	}
	
	/**
	 * Set the default CSS style parameters for all non-header cells.
	 * 
	 * @param defaultCellStyle
	 */
	public void setDefaultCellStyle(String defaultCellStyle) {
		this.defaultCellStyle = defaultCellStyle;
	}
	
	/**
	 * Set CSS style parameters for the cell at col, row.
	 * E.g.: setCellStyle(1, 1, "border: 1px")
	 * 
	 * @param col
	 * @param row
	 * @param cellStyle
	 */
	public void setCellStyle(int col, int row, String cellStyle) {
		cellStyles.put(new Pair<Integer, Integer>(col, row), cellStyle);
	}
	
	/**
	 * Get the CSS style parameters for the cell at col, row.
	 * 
	 * @param col
	 * @param row
	 * @return
	 */
	public String getCellStyle(int col, int row) { 
		String style = cellStyles.get(new Pair<Integer, Integer>(col, row));
		if (style != null) {
			return style;
		} else {
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
}
