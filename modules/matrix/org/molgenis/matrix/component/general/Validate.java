package org.molgenis.matrix.component.general;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.RenderableMatrix;

public class Validate<R, C, V>
{
	public void validateFilter(Filter f, RenderableMatrix<R, C, V> rm) throws Exception
	{
		List<Filter> fList = new ArrayList<Filter>();
		fList.add(f);
		validateFilters(fList, rm);
	}
	
	public void validateFilters(List<Filter> filters, RenderableMatrix<R, C, V> rm) throws Exception
	{
		for (Filter f : filters)
		{
			System.out.println("checking filter: " + f.toString());

			switch (f.getFilterType())
			{
				case index:
					// TODO
					break;
				case paging:
					switch (f.getQueryRule().getOperator())
					{
						case OFFSET:
							if ((Integer) f.getQueryRule().getValue() < 0)
							{
								throw new MatrixException("You cannot page below 0.");
							}
							if (f.getQueryRule().getField().equals("row")
									&& (Integer) f.getQueryRule().getValue() > rm.getTotalNumberOfRows()-1)
							{
								throw new MatrixException("You cannot page beyond " + (rm.getTotalNumberOfRows()-1));
							}
							if (f.getQueryRule().getField().equals("col")
									&& (Integer) f.getQueryRule().getValue() > rm.getTotalNumberOfCols()-1)
							{
								throw new MatrixException("You cannot page beyond " + (rm.getTotalNumberOfCols()-1));
							}
							break;
					}
					break;
				case rowHeader:
					// TODO
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
		if (bm.getVisibleCols().size() < 0)
		{
			throw new MatrixException("No visible cols in the matrix");
		}
		if (bm.getVisibleRows().size() < 0)
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
		if (bm.getColIndices().size() != bm.getVisibleCols().size())
		{
			throw new MatrixException("Number of col indices (" + bm.getColIndices().size()
					+ ") does not match number of visible cols (" + bm.getVisibleCols().size() + ")");
		}
		if (bm.getRowIndices().size() != bm.getVisibleRows().size())
		{
			throw new MatrixException("Number of row indices (" + bm.getRowIndices().size()
					+ ") does not match number of visible rows (" + bm.getVisibleRows().size() + ")");
		}
		if (bm.getRowIndices().size() != bm.getVisibleValues().length)
		{
			throw new MatrixException("Number of rows (" + bm.getRowIndices().size()
					+ ") does not match number of visible row elements (" + bm.getVisibleValues().length + ")");
		}
		if (bm.getColIndices().size() != bm.getVisibleValues()[0].length)
		{
			throw new MatrixException("Number of cols (" + bm.getColIndices().size()
					+ ") does not match number of visible col elements (" + bm.getVisibleValues()[0].length + ")");
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
