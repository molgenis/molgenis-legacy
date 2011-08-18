package org.molgenis.matrix.component;

import java.util.List;

import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.RenderableMatrix;
import org.molgenis.matrix.component.interfaces.SourceMatrix;

public class RenderableMatrixImpl<R, C, V> implements RenderableMatrix<R, C, V>{

	SourceMatrix<R, C, V> source;
	BasicMatrix<R, C, V> basic;
	List<Filter> filters;
	String constraintLogic;
	int stepSize;
	int colIndex;
	int rowIndex;
	String screenName;
	
	public RenderableMatrixImpl(SourceMatrix<R, C, V> source, BasicMatrix<R, C, V> basic, List<Filter> filters, String constraintLogic, int stepSize, String screenName){
		this.source = source;
		this.basic = basic;
		this.filters = filters;
		this.constraintLogic = constraintLogic;
		this.stepSize = stepSize;
		this.screenName = screenName;
	}
	
	




	@Override
	public List<R> getVisibleRows() throws Exception
	{
		return basic.getVisibleRows();
	}

	@Override
	public List<C> getVisibleCols() throws Exception
	{
		return basic.getVisibleCols();
	}

	@Override
	public V[][] getVisibleValues() throws Exception
	{
		return basic.getVisibleValues();
	}

	@Override
	public String getRowType()
	{
		return source.getRowType();
	}

	@Override
	public String getColType()
	{
		return source.getColType();
	}

	@Override
	public String renderValue(V value)
	{
		return source.renderValue(value);
	}

	@Override
	public String renderRow(R row)
	{
		return source.renderRow(row);
	}

	@Override
	public String renderCol(C col)
	{
		return source.renderCol(col);
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

	@Override
	public List<String> getRowHeaderFilterAttributes()
	{
		return source.getRowHeaderFilterAttributes();
	}

	@Override
	public List<String> getColHeaderFilterAttributes()
	{
		return source.getColHeaderFilterAttributes();
	}

	@Override
	public List<Filter> getFilters()
	{
		return this.filters;
	}

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
	
	public void setColIndex(int colIndex)
	{
		this.colIndex = colIndex;
	}

	public void setRowIndex(int rowIndex)
	{
		this.rowIndex = rowIndex;
	}
	
}
