package org.molgenis.matrix.component.general;

import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.component.interfaces.RenderableMatrix;

public class Mover<R, C, V>
{
	/**
	 * Move area of visible values to the left. Simply set the new filters here
	 * and let validateFilters do the validation work.
	 * 
	 * @throws Exception
	 */
	public void moveLeft(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		for (int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++)
		{
			Filter f = renderMe.getFilters().get(filterIndex);
			if (f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getField().equals("col")
					&& f.getQueryRule().getOperator().equals(Operator.OFFSET))
			{
				f.getQueryRule().setValue((Integer) f.getQueryRule().getValue() - renderMe.getStepSize());
			}
		}
	}

	/**
	 * Move area of visible values to the right. Simply set the new filters here
	 * and let validateFilters do the validation work. We assume the left-top
	 * corner is the start of paging, and therefore add a offset check here but
	 * not everywhere.
	 * 
	 * @throws Exception
	 */
	public void moveRight(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		boolean offsetFilterFound = false;
		for (int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++)
		{
			Filter f = renderMe.getFilters().get(filterIndex);
			if (f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getField().equals("col")
					&& f.getQueryRule().getOperator().equals(Operator.OFFSET))
			{
				f.getQueryRule().setValue((Integer) f.getQueryRule().getValue() + renderMe.getStepSize());
				offsetFilterFound = true;
			}
		}
		if (!offsetFilterFound)
		{
			Filter offset = new Filter(Filter.Type.paging, new MatrixQueryRule("col", Operator.OFFSET, renderMe
					.getStepSize()));
			renderMe.getFilters().add(offset);
		}
	}

	/**
	 * Move area of visible values downwards. Simply set the new filters here
	 * and let validateFilters do the validation work. We assume the left-top
	 * corner is the start of paging, and therefore add a offset check here but
	 * not everywhere.
	 * 
	 * @throws Exception
	 */
	public void moveDown(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		boolean offsetFilterFound = false;
		for (int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++)
		{
			Filter f = renderMe.getFilters().get(filterIndex);
			if (f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getField().equals("row")
					&& f.getQueryRule().getOperator().equals(Operator.OFFSET))
			{
				f.getQueryRule().setValue((Integer) f.getQueryRule().getValue() + renderMe.getStepSize());
				offsetFilterFound = true;
			}
		}
		if (!offsetFilterFound)
		{
			Filter offset = new Filter(Filter.Type.paging, new MatrixQueryRule("row", Operator.OFFSET, renderMe
					.getStepSize()));
			renderMe.getFilters().add(offset);
		}
	}

	/**
	 * Move area of visible values upwards. Simply set the new filters here and
	 * let validateFilters do the validation work.
	 * 
	 * @throws Exception
	 */
	public void moveUp(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		for (int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++)
		{
			Filter f = renderMe.getFilters().get(filterIndex);
			if (f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getField().equals("row")
					&& f.getQueryRule().getOperator().equals(Operator.OFFSET))
			{
				f.getQueryRule().setValue((Integer) f.getQueryRule().getValue() - renderMe.getStepSize());
			}
		}
	}

	/**
	 * Move area of visible values to the far left. (assumed to be the start of
	 * paging) We assume the left-top corner is the start of paging, and
	 * therefore add a offset check here but not everywhere.
	 */
	public void moveFarLeft(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		for (int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++)
		{
			Filter f = renderMe.getFilters().get(filterIndex);
			if (f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getField().equals("col")
					&& f.getQueryRule().getOperator().equals(Operator.OFFSET))
			{
				f.getQueryRule().setValue(0);
			}
		}
	}

	/**
	 * Move area of visible values to the far right.
	 * 
	 * TODO: naive implementation with renderMe.getVisibleCols(), might be wrong
	 * 
	 * @param renderMe
	 * @throws Exception
	 */
	public void moveFarRight(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		boolean offsetFilterFound = false;
		for (int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++)
		{
			Filter f = renderMe.getFilters().get(filterIndex);
			if (f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getField().equals("col")
					&& f.getQueryRule().getOperator().equals(Operator.OFFSET))
			{
				f.getQueryRule().setValue(renderMe.getTotalNumberOfCols() - renderMe.getVisibleCols().size());
				offsetFilterFound = true;
			}
		}
		if (!offsetFilterFound)
		{
			Filter offset = new Filter(Filter.Type.paging, new MatrixQueryRule("col", Operator.OFFSET, renderMe
					.getTotalNumberOfCols()
					- renderMe.getVisibleCols().size()));
			renderMe.getFilters().add(offset);
		}
	}

	/**
	 * Move area of visible values to the bottom.
	 * 
	 * TODO: naive implementations with renderMe.getVisibleRows(), might be
	 * wrong
	 * 
	 * @param renderMe
	 * @throws Exception
	 */
	public void moveFarDown(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		boolean offsetFilterFound = false;
		for (int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++)
		{
			Filter f = renderMe.getFilters().get(filterIndex);
			if (f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getField().equals("row")
					&& f.getQueryRule().getOperator().equals(Operator.OFFSET))
			{
				f.getQueryRule().setValue(renderMe.getTotalNumberOfRows() - renderMe.getVisibleRows().size());
				offsetFilterFound = true;
			}
		}
		if (!offsetFilterFound)
		{
			Filter offset = new Filter(Filter.Type.paging, new MatrixQueryRule("row", Operator.OFFSET, renderMe
					.getTotalNumberOfRows()
					- renderMe.getVisibleRows().size()));
			renderMe.getFilters().add(offset);
		}
	}

	/**
	 * Move area of visible values to the top. (assumed to be the start of
	 * paging) We assume the left-top corner is the start of paging, and
	 * therefore add a offset check here but not everywhere.
	 */
	public void moveFarUp(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		for (int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++)
		{
			Filter f = renderMe.getFilters().get(filterIndex);
			if (f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getField().equals("row")
					&& f.getQueryRule().getOperator().equals(Operator.OFFSET))
			{
				f.getQueryRule().setValue(0);
			}
		}
	}
}
