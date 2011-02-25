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
	List<String> cols = new ArrayList<String>();
	List<String> rows = new ArrayList<String>();
	
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
		
		// Print the headers
		result += "<tr><th></th>";
		for (String col : cols) {
			result += ("<th>" + col + "</th>");
		}
		result += "</tr>";
		
		// Print the rows
		int rowCount = 0;
		for (String row : rows) {
			result += "<tr>";
			result += ("<th>" + row + "</th>");
			for (int colCount = 0; colCount < cols.size(); colCount++) {
				result += ("<td>" + getCellString(colCount, rowCount) + "</td>");
			}
			result += "</tr>";	
			rowCount++;
		}
		
		result += "</table>";
		
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
	
	public void setCell(int col, int row, Object contents) {
		cells.put(new Pair<Integer, Integer>(col, row), contents);
	}
	
	public Object getCell(int col, int row) { 
		return cells.get(new Pair<Integer, Integer>(col, row));
	}
	
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
}
