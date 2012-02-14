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
}
