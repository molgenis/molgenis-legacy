package org.molgenis.framework.ui.html;

/**
 * An extension of Table that renders as a jQuery DataTable instead of a plain
 * HTML table.
 * 
 * @author erikroos
 * 
 */
public class JQueryDataTable extends Table
{

	private boolean bSort = false;
	private boolean bFilter = false;
	private boolean bPaginate = false;

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
	public String toHtml()
	{
		String result = super.toHtml();
		result += "<script>$('#" + getId() + "')" + ".css('min-height','10px')" + ".dataTable({" +
		// "\n\"bPaginate\": false," +
				"\n\"bLengthChange\": true," +
				// "\n\"bFilter\": false," +
				"\n\"bInfo\": false," + "\n\"bAutoWidth\": false," + "\n\"bJQueryUI\": true,";
		// "\n\"bSort\": false," +
		if (bSort)
		{
			result += "\n\"bSort\": true,";
		}
		else
		{
			result += "\n\"bSort\": false,";
		}
		if (bFilter)
		{
			result += "\n\"bFilter\": true,";
		}
		else
		{
			result += "\n\"bFilter\": false,";
		}
		if (bPaginate)
		{
			result += "\n\"bPaginate\": true,";
		}
		else
		{
			result += "\n\"bPaginate\": false,";
		}

		result += "\n\"aoColumns\": [";
		// Prevent fancy auto-detected sorting types by hard-setting to 'string'
		// for every column
		for (int i = 0; i < super.cols.size() + 1; i++)
		{
			result += "\n{ \"sType\": \"string\" },";
		}
		result = result.substring(0, result.length() - 1); // chop off last ,
		result += "\n]";
		result += "\n})</script>";
		return result;
	}

	public boolean isbSort()
	{
		return bSort;
	}

	public void setbSort(boolean bSort)
	{
		this.bSort = bSort;
	}

	public boolean isbFilter()
	{
		return bFilter;
	}

	public void setbFilter(boolean bFilter)
	{
		this.bFilter = bFilter;
	}

	public boolean isbPaginate()
	{
		return bPaginate;
	}

	public void setbPaginate(boolean bPaginate)
	{
		this.bPaginate = bPaginate;
	}

}
