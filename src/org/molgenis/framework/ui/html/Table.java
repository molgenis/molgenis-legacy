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
	LinkedHashMap<Pair<Integer, Integer>, HtmlInput> cells = new LinkedHashMap<Pair<Integer, Integer>, HtmlInput>();
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
			int colCount = 0;
			for (String col : cols) {
				String cellContents = "";
				if (getCell(colCount, rowCount) != null) {
					cellContents = getCell(colCount, rowCount).toHtml();
				}
				result += ("<td>" + cellContents + "</td>");
				
				colCount++;
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
	
	public Integer addRow(String rowName) {
		rows.add(rowName);
		return rows.size() - 1;
	}
	
	public void setCell(int col, int row, HtmlInput input) {
		cells.put(new Pair<Integer, Integer>(col, row), input);
	}
	
	public HtmlInput getCell(int col, int row) { 
		return cells.get(new Pair<Integer, Integer>(col, row));
	}
}
