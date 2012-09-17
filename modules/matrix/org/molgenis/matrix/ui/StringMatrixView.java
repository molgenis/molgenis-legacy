package org.molgenis.matrix.ui;

import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.matrix.Matrix;

public class StringMatrixView extends HtmlInput<Matrix<String,String,String>>
{
	public StringMatrixView(String name, Matrix<String,String,String> matrix)
	{
		super(name, matrix);
	}

	@Override
	public String toHtml()
	{
		try
		{
			Matrix<String,String,String> m = getObject();
			
			
			// very naive
			String result = "";
			
			// add a pulldown to select cols
				
				
			//render table
			result += "<table border=\"1\"><thead><tr><td>&nbsp;</td>";

			// header
			for (String col : m.getColNames())
			{
				result += "<td><b>" + col + "</b></td>";
			}

			result += "</tr></thead><tbody>";

			for (String row : m.getRowNames())
			{
				result += "<tr><td><b>"+row+"</b></td>";
				for(String col : m.getColNames())
				{
					result += "<td>"+m.getValue(row, col)+"</td>";
				}
				result += "</tr>";
			}

			result += "</tbody></table>";
			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "ERROR: " + e.getMessage();
		}
	}
}
