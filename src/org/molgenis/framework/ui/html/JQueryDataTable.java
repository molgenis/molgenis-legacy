package org.molgenis.framework.ui.html;

/**
 * An extension of Table that renders as a jQuery DataTable instead of
 * a plain HTML table.
 * 
 * @author erikroos
 *
 */
public class JQueryDataTable extends Table
{

	public JQueryDataTable(String name)
	{
		this(name, null);
	}
	
	public JQueryDataTable(String name, String label)
	{
		super(name, label);
		this.setLabel(label);
		super.setDefaultCellStyle("");
		super.setHeaderCellStyle("");
	}
	
	@Override
	public String toHtml() {
		String result = super.toHtml();
		result += "<script>$('#"+getId()+"')" +
				  ".css('min-height','10px')" +
				  ".dataTable({" +
				  "\n\"bPaginate\": false," +
				  "\n\"bLengthChange\": true," +
				  "\n\"bFilter\": false," +
				  "\n\"bInfo\": false," +
				  "\n\"bAutoWidth\": false," +
				  "\n\"bJQueryUI\": true," +
				  "\n\"bSort\": false," +
				  "\n\"aoColumns\": [";
		// Prevent fancy auto-detected sorting types by hard-setting to 'string' for every column
		for (int i = 0; i < super.cols.size() + 1; i++) {
			result += "\n{ \"sType\": \"string\" },";
		}
		result = result.substring(0, result.length() - 1); // chop off last ,
		result += "\n]";
		result += "\n})</script>";
		return result;
	}

}
