package org.molgenis.sandbox.ui;

import java.util.List;

import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;

public class MatrixViewer extends HtmlWidget
{
	SliceablePhenoMatrix<? extends ObservationElement, ? extends ObservationElement> matrix;

	public MatrixViewer(String name, SliceablePhenoMatrix<? extends ObservationElement, ? extends ObservationElement> matrix)
	{
		super(name);
		this.matrix = matrix;
	}
	
	public String toHtml()
	{	
		try
		{
			//first try: simple table
			String result = "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"display\" id=\""+this.getId()+"\">";
			
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
							if(first) 
							{
								first = false;
								result += val.getValue();
							}
							else
							{
								result += "," + val.getValue();
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

}
