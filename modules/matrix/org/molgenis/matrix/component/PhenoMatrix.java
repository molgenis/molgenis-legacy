package org.molgenis.matrix.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.RenderableMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.matrix.component.interfaces.SourceMatrix;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public class PhenoMatrix implements 
	BasicMatrix<ObservationTarget, ObservableFeature, ObservedValue>,
	SourceMatrix<ObservationTarget, ObservableFeature, ObservedValue>,
	SliceableMatrix<ObservationTarget, ObservableFeature, ObservedValue> {
	
	// Basic matrix
	private List<ObservationTarget> visibleRows;
	private List<ObservableFeature> visibleCols;
	private List<ObservedValue>[][] visibleValues;
	
	// Source matrix
	private int totalNumberOfRows;
	private int totalNumberOfCols;
	
	// Pheno-specific
	private Database db;
	
	Logger logger = Logger.getLogger(PhenoMatrix.class);
	
	/************************************************************/
	/**************** BasicMatrix implementation ****************/
	/************************************************************/

	@Override
	public List<ObservationTarget> getVisibleRows() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ObservableFeature> getVisibleCols() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObservedValue[][] getVisibleValues() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	/****************************************************************/
	/**************** SliceableMatrix implementation ****************/
	/****************************************************************/
	
	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, ObservedValue> sliceByRowIndex(QueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, ObservedValue> sliceByColIndex(QueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, ObservedValue> sliceByRowValues(QueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, ObservedValue> sliceByColValues(QueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, ObservedValue> sliceByRowHeader(QueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, ObservedValue> sliceByColHeader(QueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BasicMatrix<ObservationTarget, ObservableFeature, ObservedValue> getResult() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void createFresh() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	/*************************************************************/
	/**************** SourceMatrix implementation ****************/
	/*************************************************************/
	
	@Override
	public String getRowType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String renderValue(ObservedValue value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String renderRow(ObservationTarget row)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String renderCol(ObservableFeature col)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTotalNumberOfRows()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalNumberOfCols()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> getRowHeaderFilterAttributes()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getColHeaderFilterAttributes()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
