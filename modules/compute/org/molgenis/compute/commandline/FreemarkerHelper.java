package org.molgenis.compute.commandline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.molgenis.util.Tuple;

public class FreemarkerHelper
{
	ComputeBundle computeBundle;
	
	public FreemarkerHelper(ComputeBundle computeBundle) {
		this.computeBundle = computeBundle;
	}
	
	
	public String helloWorld(String name)
	{
		return "hello "+name;
	}
	
	public List<String> unfoldCSV(Tuple tuple)
	{
		return Worksheet.unfoldWorksheetCSV(Arrays.asList(new Tuple[]{tuple}));
	}
	
	/**
	 * We want to re-fold one tuple
	 */
	public List<Tuple> foldOn(Tuple tuple, String targets)
	{
		//first unfuld, then refold again
		List<Tuple> unfolded = Worksheet.unfoldWorksheet(Arrays.asList(new Tuple[]{tuple}));
		
		//fold again
		List<Tuple> folded = Worksheet.foldWorksheet(unfolded, this.computeBundle.getComputeParameters(), Arrays.asList(targets.split(",")));
		
		return folded;
	}
	
	public List<String> stringList(List<Tuple> folded, String var)
	{
		List<String> lst = new ArrayList<String>();
		for (int i = 0; i < folded.size(); i ++)
		{
			lst.add(folded.get(i).getString(var));
		}
		
		return lst;
	}
}
