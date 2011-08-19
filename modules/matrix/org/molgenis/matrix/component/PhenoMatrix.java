package org.molgenis.matrix.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.component.general.GenericFunctions;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.matrix.component.interfaces.SourceMatrix;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public class PhenoMatrix extends GenericFunctions<ObservationTarget, ObservableFeature, List<ObservedValue>>
		implements BasicMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>>,
		SourceMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>>
{

	private Database db;
	Logger logger = Logger.getLogger(PhenoMatrix.class);

	/************************************************************/
	/*********************** Constructors ***********************/
	/************************************************************/

	public PhenoMatrix(Database db) throws Exception
	{
		this.db = db;
		originalRows = db.find(ObservationTarget.class);
		originalCols = db.find(ObservableFeature.class);
	}

	/************************************************************/
	/**************** BasicMatrix implementation ****************/
	/************************************************************/

	@Override
	public List<ObservedValue>[][] getVisibleValues() throws Exception
	{
		List<ObservedValue>[][] visibleValues = new List[rowCopy.size()][colCopy.size()];

		List<Integer> targetIdList = new ArrayList<Integer>();
		for (ObservationTarget t : rowCopy)
		{
			targetIdList.add(t.getId());
		}

		List<Integer> featureIdList = new ArrayList<Integer>();
		for (ObservableFeature f : colCopy)
		{
			featureIdList.add(f.getId());
		}

		List<ObservedValue> vals = db.find(ObservedValue.class, new QueryRule(ObservedValue.TARGET, Operator.IN,
				targetIdList), new QueryRule(ObservedValue.FEATURE, Operator.IN, featureIdList));

		int i = 0;
		for (ObservationTarget target : rowCopy)
		{
			int j = 0;
			for (ObservableFeature feature : colCopy)
			{
				for (ObservedValue val : vals)
				{
					if (val.getTarget_Id().intValue() == target.getId().intValue()
							&& val.getFeature_Id().intValue() == feature.getId().intValue())
					{
						if (visibleValues[i][j] == null)
						{
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
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowValues(QueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * PROOF OF PRINCIPLE! Needs rework.
	 */
	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColValues(MatrixQueryRule rule)
			throws Exception
	{
		int featureIndex = Integer.parseInt(rule.getField());
		String filterTerm = rule.getValue().toString();
		List<ObservationTarget> targetsToKeep = new ArrayList<ObservationTarget>();
		List<ObservedValue>[][] values = this.getVisibleValues();
		
		int i = 0;
		for (ObservationTarget target : rowCopy) {
			List<ObservedValue> valueList = values[i][featureIndex];
			if (valueList != null && valueList.size() > 0) {
				ObservedValue value = valueList.get(0);
				if (value.getValue() != null && value.getValue().equals(filterTerm)) {
					targetsToKeep.add(target);
				} else {
					if (value.getRelation_Name() != null && value.getRelation_Name().equals(filterTerm)) {
						targetsToKeep.add(target);
					}
				}
			}
			i++;
		}
		
		this.rowCopy = targetsToKeep;
		return this;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowHeader(MatrixQueryRule rule)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColHeader(MatrixQueryRule rule)
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

	/*************************************************************/
	/**************** SourceMatrix implementation ****************/
	/*************************************************************/

	@Override
	public String getRowType()
	{
		return "ObservationTarget";
		//return originalRows.get(0).get__Type();
	}

	@Override
	public String getColType()
	{
		return "ObservableFeature";
		//return originalCols.get(0).get__Type();
	}

	@Override
	public String renderValue(List<ObservedValue> values)
	{
		if (values != null && values.size() > 0)
		{
			if (values.get(0).getValue() != null)
			{
				return values.get(0).getValue();
			}
			else
			{
				if (values.get(0).getRelation_Name() != null)
				{
					return values.get(0).getRelation_Name();
				}
				else
				{
					return "";
				}
			}
		}
		else
		{
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
