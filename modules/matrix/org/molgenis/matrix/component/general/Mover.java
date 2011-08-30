package org.molgenis.matrix.component.general;

import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.component.interfaces.RenderableMatrix;

public class Mover<R, C, V>
{
	/**
	 * Move area of visible values to the left.
	 * 
	 * @throws Exception
	 */
	public void moveLeft(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		MatrixQueryRule f = MatrixRendererHelper.getFilterWhere(renderMe.getRules(), MatrixQueryRule.Type.paging, "col",
				Operator.OFFSET);

		if (f != null)
		{
			f.setValue((Integer) f.getValue() - renderMe.getStepSize());
		}
		else
		{
			// will result in error message, but is consistent/expected
			// behaviour
			renderMe.getRules().add(
					new MatrixQueryRule(MatrixQueryRule.Type.paging, "col", Operator.OFFSET, 0 - renderMe
							.getStepSize()));
		}
	}

	/**
	 * Move area of visible values to the right.
	 * 
	 * @throws Exception
	 */
	public void moveRight(RenderableMatrix<R, C, V> renderMe) throws Exception
	{

		MatrixQueryRule f = MatrixRendererHelper.getFilterWhere(renderMe.getRules(), MatrixQueryRule.Type.paging, "col",
				Operator.OFFSET);

		if (f != null)
		{
			f.setValue((Integer) f.getValue() + renderMe.getStepSize());
		}
		else
		{
			renderMe.getRules()
					.add(new MatrixQueryRule(MatrixQueryRule.Type.paging, "col", Operator.OFFSET, renderMe
							.getStepSize()));
		}
	}

	/**
	 * Move area of visible values downwards.
	 * 
	 * @throws Exception
	 */
	public void moveDown(RenderableMatrix<R, C, V> renderMe) throws Exception
	{

		MatrixQueryRule f = MatrixRendererHelper.getFilterWhere(renderMe.getRules(), MatrixQueryRule.Type.paging, "row",
				Operator.OFFSET);

		if (f != null)
		{
			f.setValue((Integer) f.getValue() + renderMe.getStepSize());
		}
		else
		{
			renderMe.getRules()
					.add(new MatrixQueryRule(MatrixQueryRule.Type.paging, "row", Operator.OFFSET, renderMe
							.getStepSize()));
		}
	}

	/**
	 * Move area of visible values upwards.
	 * 
	 * @throws Exception
	 */
	public void moveUp(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		MatrixQueryRule f = MatrixRendererHelper.getFilterWhere(renderMe.getRules(), MatrixQueryRule.Type.paging, "row",
				Operator.OFFSET);

		if (f != null)
		{
			f.setValue((Integer) f.getValue() - renderMe.getStepSize());
		}
		else
		{
			// will result in error message, but is consistent/expected
			// behaviour
			renderMe.getRules().add(
					new MatrixQueryRule(MatrixQueryRule.Type.paging, "row", Operator.OFFSET, 0 - renderMe
							.getStepSize()));
		}
	}

	/**
	 * Move area of visible values to the far left.
	 */
	public void moveFarLeft(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		MatrixQueryRule f = MatrixRendererHelper.getFilterWhere(renderMe.getRules(), MatrixQueryRule.Type.paging, "col",
				Operator.OFFSET);

		if (f != null)
		{
			f.setValue(0);
		}
		else
		{
			// default situation, but make explicit for consistency
			renderMe.getRules().add(new MatrixQueryRule(MatrixQueryRule.Type.paging, "col", Operator.OFFSET, 0));
		}
	}

	/**
	 * Move area of visible values to the far right.
	 * 
	 * TODO: naive implementation with renderMe.getVisibleCols(), might be wrong
	 * 
	 */
	public void moveFarRight(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		MatrixQueryRule f = MatrixRendererHelper.getFilterWhere(renderMe.getRules(), MatrixQueryRule.Type.paging, "col",
				Operator.OFFSET);

		if (f != null)
		{
			f.setValue(renderMe.getTotalNumberOfCols() - renderMe.getVisibleCols().size());
		}
		else
		{
			renderMe.getRules().add(
					new MatrixQueryRule(MatrixQueryRule.Type.paging, "col", Operator.OFFSET, renderMe
							.getTotalNumberOfCols() - renderMe.getVisibleCols().size()));
		}
	}

	/**
	 * Move area of visible values to the bottom.
	 * 
	 * TODO: naive implementations with renderMe.getVisibleRows(), might be
	 * wrong
	 * 
	 */
	public void moveFarDown(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		MatrixQueryRule f = MatrixRendererHelper.getFilterWhere(renderMe.getRules(), MatrixQueryRule.Type.paging, "row",
				Operator.OFFSET);

		if (f != null)
		{
			f.setValue(renderMe.getTotalNumberOfRows() - renderMe.getVisibleRows().size());
		}
		else
		{
			renderMe.getRules().add(
					new MatrixQueryRule(MatrixQueryRule.Type.paging, "row", Operator.OFFSET, renderMe.getTotalNumberOfRows() - renderMe.getVisibleRows().size()));
		}
	}

	/**
	 * Move area of visible values to the top.
	 */
	public void moveFarUp(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		MatrixQueryRule f = MatrixRendererHelper.getFilterWhere(renderMe.getRules(), MatrixQueryRule.Type.paging, "row",
				Operator.OFFSET);

		if (f != null)
		{
			f.setValue(0);
		}
		else
		{
			renderMe.getRules().add(
					new MatrixQueryRule(MatrixQueryRule.Type.paging, "row", Operator.OFFSET, 0));
		}
	}
}
