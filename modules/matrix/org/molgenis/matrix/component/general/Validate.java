package org.molgenis.matrix.component.general;

import java.util.List;

import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.SourceMatrix;

public class Validate<R, C, V>
{
	public void validateFilters(List<Filter> filters, SourceMatrix<R, C, V> source) throws Exception
	{
		//throw new Exception("something not ok!");
	}
	
	public void validateResult(BasicMatrix<R, C, V> bm) throws Exception
	{
		//throw new Exception("something not ok!");
	}
	
	public void validateAction(String action, String pref) throws Exception{
		if (!action.startsWith(pref))
		{
			throw new Exception("Action '" + action + "' does not include the matrix renderer prefix '"
					+ pref + "'for request delegation.");
		}
	}
}
