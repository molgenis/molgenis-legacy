package org.molgenis.matrix.component.legacy;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.BasicMatrix;

public class Validate<R, C, V>
{
	public void validateFilter(MatrixQueryRule f, RenderableMatrix<R, C, V> rm) throws Exception
	{
		List<MatrixQueryRule> fList = new ArrayList<MatrixQueryRule>();
		fList.add(f);
		validateFilters(fList, rm);
	}
	
	public void validateFilters(List<MatrixQueryRule> filters, RenderableMatrix<R, C, V> rm) throws Exception
	{
		for (MatrixQueryRule f : filters)
		{
			System.out.println("checking filter: " + f.toString());

			switch (f.getFilterType())
			{
				case rowIndex:
					// TODO
					break;
				case rowHeader:
					// TODO
					break;
				case colIndex:
					break;
				case colHeader:
					// TODO
					break;
				case rowValues:
					// TODO
					break;
				case colValues:
					// TODO
					break;
			}
		}

		// throw new Exception("something not ok!");
	}

	public void validateResult(BasicMatrix<R, C, V> bm) throws Exception
	{
		if (bm.getColHeaders().size() < 0)
		{
			throw new MatrixException("No visible cols in the matrix");
		}
		if (bm.getRowHeaders().size() < 0)
		{
			throw new MatrixException("No visible rows in the matrix");
		}
		if (bm.getColIndices().size() < 0)
		{
			throw new MatrixException("No col indices in the matrix");
		}
		if (bm.getRowIndices().size() < 0)
		{
			throw new MatrixException("No row indices in the matrix");
		}
		if (bm.getColIndices().size() != bm.getColHeaders().size())
		{
			throw new MatrixException("Number of col indices (" + bm.getColIndices().size()
					+ ") does not match number of visible cols (" + bm.getColHeaders().size() + ")");
		}
		if (bm.getRowIndices().size() != bm.getRowHeaders().size())
		{
			throw new MatrixException("Number of row indices (" + bm.getRowIndices().size()
					+ ") does not match number of visible rows (" + bm.getRowHeaders().size() + ")");
		}
		if (bm.getRowIndices().size() != bm.getValues().length)
		{
			throw new MatrixException("Number of rows (" + bm.getRowIndices().size()
					+ ") does not match number of visible row elements (" + bm.getValues().length + ")");
		}
		if (bm.getColIndices().size() != bm.getValues()[0].length)
		{
			throw new MatrixException("Number of cols (" + bm.getColIndices().size()
					+ ") does not match number of visible col elements (" + bm.getValues()[0].length + ")");
		}
	}

	public void validateAction(String action, String pref) throws Exception
	{
		if (!action.startsWith(pref))
		{
			throw new MatrixException("Action '" + action + "' does not include the matrix renderer prefix '" + pref
					+ "'for request delegation.");
		}
	}

	public void validateFilterInputs(String field, Operator operator, Object value) throws Exception
	{
		if(field == null)
		{
			throw new MatrixException("Filter field is null");
		}
		if(field.trim().equals(""))
		{
			throw new MatrixException("Filter field is empty");
		}
		if(operator == null)
		{
			throw new MatrixException("Filter operator is null");
		}
		if(value == null)
		{
			throw new MatrixException("Filter value is null");
		}
		if(value.toString().trim().equals(""))
		{
			throw new MatrixException("Filter value is empty");
		}
	}
}
