package org.molgenis.matrix.ui;

import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.matrix.Matrix;
import org.molgenis.matrix.PhenoMemoryMatrix;
import org.molgenis.ngs.LibraryLane;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;

public class PhenoMatrixView<E extends ObservationElement,A extends ObservationElement> extends HtmlInput<Matrix<E, A,ObservedValue>>
{
	public PhenoMatrixView(String name, PhenoMemoryMatrix<E, A> phenoMatrix)
	{
		super(name, phenoMatrix);
	}

	@Override
	public String toHtml()
	{
		try
		{
			Matrix<E,A,ObservedValue> m = getObject();
			
			
			// very naive
			String result = "";
			
			// add a pulldown to select cols
				
				
			//render table
			result += "<table border=\"1\"><thead><tr><td>&nbsp;</td>";

			// header
			for (A col : m.getColNames())
			{
				result += "<td><b>" + col.getName() + "</b></td>";
			}

			result += "</tr></thead><tbody>";

			for (E row : m.getRowNames())
			{
				result += "<tr><td><b>"+row.getName()+"</b></td>";
				for(A col : m.getColNames())
				{
					if(m.getValue(row, col) != null)
						result += "<td>"+m.getValue(row, col).getValue()+"</td>";
					else
						result += "<td>&nbsp</td>";
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
