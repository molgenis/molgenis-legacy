package org.molgenis.matrix.component.general;

import java.util.List;

import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.RenderDescriptor;
import org.molgenis.matrix.component.interfaces.RenderableMatrix;
import org.molgenis.matrix.component.interfaces.SourceMatrix;
import org.molgenis.pheno.ObservedValue;

public class RenderableMatrixImpl<R, C, V> implements RenderableMatrix<R, C, V>
{
	SourceMatrix<R, C, V> source;
	
	//good, so Render uses a decorator design pattern
	BasicMatrix<R, C, V> basic;
	
	//currently used filters
	List<MatrixQueryRule> filters;
	
	//TODO very unclear
	@Deprecated
	String constraintLogic;
	
	//size of steps used for X and Y 
	//TODO should they not be set indepently
	int stepSize;
	
	//this is out of place!
	@Deprecated
	String screenName;

	public RenderableMatrixImpl(SourceMatrix<R, C, V> source, BasicMatrix<R, C, V> basic, List<MatrixQueryRule> filters,
			String constraintLogic, int stepSize, String screenName)
	{
		this.source = source;
		this.basic = basic;
		this.filters = filters;
		this.constraintLogic = constraintLogic;
		this.stepSize = stepSize;
		this.screenName = screenName;
	}
	
	@Override
	public List<R> getRowHeaders() throws MatrixException
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<C> getColHeaders() throws MatrixException
	{
		return basic.getColHeaders();
	}

	@Override
	public List<Integer> getRowIndices() throws MatrixException
	{
		return basic.getRowIndices();
	}

	@Override
	public List<Integer> getColIndices() throws MatrixException
	{
		return basic.getColIndices();
	}

	@Override
	public String getRowType() throws Exception
	{
		return source.getRowType();
	}

	@Override
	public String getColType() throws Exception
	{
		return source.getColType();
	}

	@Override
	public int getTotalNumberOfRows()
	{
		return source.getTotalNumberOfRows();
	}

	@Override
	public int getTotalNumberOfCols()
	{
		return source.getTotalNumberOfCols();
	}

	// BAD: not part of Renderable interface...
//	public List<MatrixQueryRule> getPagingFilters()
//	{
//		List<MatrixQueryRule> returnList = new ArrayList<MatrixQueryRule>();
//		for (MatrixQueryRule f : this.filters)
//		{
//			if (f.getFilterType().equals(MatrixQueryRule.Type.paging))
//			{
//				returnList.add(f);
//			}
//		}
//		return returnList;
//	}

	// BAD: not part of Renderable interface...
//	public List<MatrixQueryRule> getOtherFilters()
//	{
//		List<MatrixQueryRule> returnList = new ArrayList<MatrixQueryRule>();
//		for (MatrixQueryRule f : this.filters)
//		{
//			if (!f.getFilterType().equals(MatrixQueryRule.Type.paging))
//			{
//				returnList.add(f);
//			}
//		}
//		return returnList;
//	}

	@Override
	public String getConstraintLogic()
	{
		return this.constraintLogic;
	}

	@Override
	public int getStepSize()
	{
		return this.stepSize;
	}

	@Override
	public String getScreenName()
	{
		return screenName;
	}

	@Override
	public RenderDescriptor<R, C, V> getRenderDescriptor() throws Exception {
		return source.getRenderDescriptor();
	}
	@Override
	public V[][] getValues() throws MatrixException
	{
		return basic.getValues();
	}

	@Override
	public Integer getColCount() throws MatrixException
	{
		return this.getRowHeaders().size();
	}

	@Override
	public Integer getRowCount() throws MatrixException
	{
		return this.getColHeaders().size();
	}

	@Override
	public void refresh() throws MatrixException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<MatrixQueryRule> getRules()
	{
		// TODO Auto-generated method stub
		return this.filters;
	}

	@Override
	public List<ObservedValue>[][] getValueLists() throws MatrixException
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
