package org.molgenis.compute.ui;

import java.text.ParseException;
import java.util.List;

import org.molgenis.MolgenisFieldTypes;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.ngs.NgsSample;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;

public class EditableValueMatrixView extends HtmlInput
{
	List<Measurement> measurements;
	List<NgsSample> samples;
	List<ObservedValue> values;

	public EditableValueMatrixView(String name,
			List<Measurement> measurements, List<NgsSample> samples,
			List<ObservedValue> values)
	{
		super(name, null);
		this.measurements = measurements;
		this.samples = samples;
		this.values = values;
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String render()
	{
		String result = "<table>";
		//create headers
		result +="<thead><tr><th>Sample</th>";
		for(Measurement m: this.measurements)
		{
			result += "<th>"+m.getName()+"</th>";
		}
		result +="</tr></thead>";
		
		//create the value rows
		for(NgsSample s: this.samples)
		{
			result +="<tr><td>"+s.getName()+"</td>";
			
			for(Measurement m: this.measurements)
			{
				try
				{
					result +="<td>"+MolgenisFieldTypes.createInput(m.getDataType(), "value_"+s.getId()+"_"+m.getId(), null).render()+"</td>";
				}
				catch (HtmlInputException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
			
			result += "</tr>";
		}
		
		result +="</table><script>$('#"+getId()+"')" +
				".css('min-height','100px')" +
				".dataTable({" +
				"\n\"bPaginate\": false," +
				"\n\"bLengthChange\": true," +
				"\n\"bFilter\": false," +
				"\n\"bSort\": false," +
				"\n\"bInfo\": false," +
				"\n\"bJQueryUI\": true})" +
				"</script>";
		return result;
	}

	@Override
	public String toHtml()
	{
		// TODO Auto-generated method stub
		return render();
	}

}
