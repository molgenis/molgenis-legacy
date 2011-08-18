package org.molgenis.matrix.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.matrix.component.interfaces.SourceMatrix;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public class PhenoMatrix implements 
	BasicMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>>,
	SourceMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>>,
	SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> {
	
	private Database db;
	private List<ObservationTarget> totalRows = null;
	private List<ObservableFeature> totalCols = null;
	
	Logger logger = Logger.getLogger(PhenoMatrix.class);
	
	/************************************************************/
	/*********************** Constructors ***********************/
	/************************************************************/
	
	public PhenoMatrix(Database db) throws Exception {
		this.db = db;
		
		totalRows = db.find(ObservationTarget.class);
		totalCols = db.find(ObservableFeature.class);

	}
	
	/************************************************************/
	/**************** BasicMatrix implementation ****************/
	/************************************************************/

	private List<ObservationTarget> rowsCopy;
	private List<ObservableFeature> colsCopy;
	
	@Override
	public List<ObservationTarget> getVisibleRows() throws Exception
	{
		return rowsCopy;
	}

	@Override
	public List<ObservableFeature> getVisibleCols() throws Exception
	{
		return colsCopy;
	}

	@Override
	public List<ObservedValue>[][] getVisibleValues() throws Exception
	{
		List<ObservedValue>[][] visibleValues = new List[rowsCopy.size()][colsCopy.size()];
		
		List<Integer> targetIdList = new ArrayList<Integer>();
		for (ObservationTarget t : rowsCopy) {
			targetIdList.add(t.getId());
		}
		
		List<Integer> featureIdList = new ArrayList<Integer>();
		for (ObservableFeature f : colsCopy) {
			featureIdList.add(f.getId());
		}
		
		List<ObservedValue> vals = db.find(ObservedValue.class, new QueryRule(ObservedValue.TARGET, Operator.IN, targetIdList),
				new QueryRule(ObservedValue.FEATURE, Operator.IN, featureIdList));
		
		int i = 0;
		for (ObservationTarget target : rowsCopy) {
			int j = 0;
			for (ObservableFeature feature : colsCopy) {
				for (ObservedValue val : vals) {
					if (val.getTarget_Id().intValue() == target.getId().intValue() && val.getFeature_Id().intValue() == feature.getId().intValue()) {
						if (visibleValues[i][j] == null) {
							visibleValues[i][j] = new ArrayList<ObservedValue>();
						}
						visibleValues[i][j].add(val);
						vals.remove(val);
						break;
					}
				}
				j++;
			}
			i++;
		}
		
		return visibleValues;
	}

	/****************************************************************/
	/**************** SliceableMatrix implementation ****************/
	/****************************************************************/
	
	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowIndex(QueryRule rule)
			throws Exception
	{
		int val = (Integer) rule.getValue();
		int total = rowsCopy.size();
		switch (rule.getOperator())
		{
            case EQUALS:
            	//this.setRowNames(this.getRowNames().subList(val, val+1));
            	break;
            case LESS_EQUAL:
            	rowsCopy = rowsCopy.subList(0, val);
            	break;
            case GREATER_EQUAL:
            	rowsCopy = rowsCopy.subList(val, total);
            	break;
            case LESS:
            	//this.setRowNames(this.getRowNames().subList(0, val));
            	break;
            case GREATER:
            	//this.setRowNames(this.getRowNames().subList(val+1, total));
            	break;
		}
		return this;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColIndex(QueryRule rule)
			throws Exception
	{
		int val = (Integer) rule.getValue();
		int total = colsCopy.size();
		switch (rule.getOperator())
		{
            case EQUALS:
            	//this.setColNames(this.getColNames().subList(val, val+1));
            	break;
            case LESS_EQUAL:
            	colsCopy = colsCopy.subList(0, val);
            	break;
            case GREATER_EQUAL:
            	colsCopy = colsCopy.subList(val, total);
            	break;
            case LESS:
            	//this.setColNames(this.getColNames().subList(0, val));
            	break;
            case GREATER:
            	//this.setColNames(this.getColNames().subList(val+1, total));
            	break;
		}
		return this;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowValues(QueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColValues(QueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowHeader(QueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColHeader(QueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BasicMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> getResult() throws Exception
	{
		return this;
	}
	
	@Override
	public void createFresh(){
		this.rowsCopy = new ArrayList<ObservationTarget>(this.getTotalNumberOfRows());
	    for (ObservationTarget item : this.totalRows) { 
	    	this.rowsCopy.add(item);
	    }
	    this.colsCopy = new ArrayList<ObservableFeature>(this.getTotalNumberOfCols());
	    for (ObservableFeature item : this.totalCols) {
	    	this.colsCopy.add(item);
	    }
	}

	/*************************************************************/
	/**************** SourceMatrix implementation ****************/
	/*************************************************************/
	
	@Override
	public String getRowType()
	{
		return totalRows.get(0).get__Type();
	}

	@Override
	public String getColType()
	{
		return totalCols.get(0).get__Type();
	}

	@Override
	public String renderValue(List<ObservedValue> values)
	{
		if (values != null && values.size() > 0) {
			if (values.get(0).getValue() != null) {
				return values.get(0).getValue();
			} else {
				if (values.get(0).getRelation_Name() != null) {
					return values.get(0).getRelation_Name();
				} else {
					return "";
				}
			}
		} else {
			return "";
		}
	}

	@Override
	public String renderRow(ObservationTarget row)
	{
		return row.getName();
	}

	@Override
	public String renderCol(ObservableFeature col)
	{
		return col.getName();
	}

	@Override
	public int getTotalNumberOfRows()
	{
		return totalRows.size();
	}

	@Override
	public int getTotalNumberOfCols()
	{
		return totalCols.size();
	}

	@Override
	public List<String> getRowHeaderFilterAttributes()
	{
		List<String> returnList = new ArrayList<String>();
		returnList.add(ObservationTarget.NAME);
		return returnList;
	}

	@Override
	public List<String> getColHeaderFilterAttributes()
	{
		List<String> returnList = new ArrayList<String>();
		returnList.add(ObservableFeature.NAME);
		return returnList;
	}

}
