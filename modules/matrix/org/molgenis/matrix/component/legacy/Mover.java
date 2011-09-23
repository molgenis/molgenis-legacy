//PAGING should be done using limit/offset
//this should not be a queryrule as you can do this only 1x for row and 1x for col.





package org.molgenis.matrix.component.legacy;

import org.molgenis.framework.db.QueryRule.Operator;

public class Mover<R, C, V>
{
	/**
	 * Move area of visible values to the left.
	 * 
	 * @throws Exception
	 */
	public void moveLeft(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		//TODO reimplement using limit/offset
		

	}

	/**
	 * Move area of visible values to the right.
	 * 
	 * @throws Exception
	 */
	public void moveRight(RenderableMatrix<R, C, V> renderMe) throws Exception
	{

		//TODO reimplement using limit/offset
	}

	/**
	 * Move area of visible values downwards.
	 * 
	 * @throws Exception
	 */
	public void moveDown(RenderableMatrix<R, C, V> renderMe) throws Exception
	{

		//TODO reimplement using limit/offset
	}

	/**
	 * Move area of visible values upwards.
	 * 
	 * @throws Exception
	 */
	public void moveUp(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		//TODO reimplement using limit/offset
	}

	/**
	 * Move area of visible values to the far left.
	 */
	public void moveFarLeft(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		//TODO reimplement using limit/offset
	}

	/**
	 * Move area of visible values to the far right.
	 * 
	 * TODO: naive implementation with renderMe.getVisibleCols(), might be wrong
	 * 
	 */
	public void moveFarRight(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		//TODO reimplement using limit/offset
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
		//TODO reimplement using limit/offset
	}

	/**
	 * Move area of visible values to the top.
	 */
	public void moveFarUp(RenderableMatrix<R, C, V> renderMe) throws Exception
	{
		//TODO reimplement using limit/offset
	}
}
