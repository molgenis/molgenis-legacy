package org.molgenis.matrix.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.general.AbstractSliceableMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.RenderDescriptor;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.matrix.component.interfaces.SourceMatrix;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public class PhenoMatrix
		extends
		AbstractSliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>>
		implements
		BasicMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>>,
		SourceMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>>,
		RenderDescriptor<ObservationTarget, ObservableFeature, List<ObservedValue>>
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
	public List<ObservedValue>[][] getValues() throws Exception
	{
		List<ObservedValue>[][] visibleValues = new List[rowCopy.size()][colCopy
				.size()];

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

		List<ObservedValue> vals = db
				.find(ObservedValue.class, new QueryRule(ObservedValue.TARGET,
						Operator.IN, targetIdList), new QueryRule(
						ObservedValue.FEATURE, Operator.IN, featureIdList));

		int i = 0;
		for (ObservationTarget target : rowCopy)
		{
			int j = 0;
			for (ObservableFeature feature : colCopy)
			{
				for (ObservedValue val : vals)
				{
					if (val.getTarget_Id().intValue() == target.getId()
							.intValue()
							&& val.getFeature_Id().intValue() == feature
									.getId().intValue())
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

	@Deprecated
	public List<ObservedValue>[][] getVisibleValues() throws Exception
	{
		return this.getValues();
	}

	/****************************************************************/
	/**************** SliceableMatrix implementation ****************/
	/****************************************************************/

	@Deprecated
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowValues(
			QueryRule rule) throws Exception
	{
		return this.sliceByRowValues(Integer.getInteger(rule.getField()),
				rule.getOperator(), rule.getValue());
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowValues(
			int index, Operator operator, Object value) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * PROOF OF PRINCIPLE! Needs rework.
	 */

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColValues(
			int featureIndex, Operator operator, Object filterTerm) throws Exception
	{
		List<ObservationTarget> targetsToKeep = new ArrayList<ObservationTarget>();
		List<Integer> indicesToKeep = new ArrayList<Integer>();
		List<ObservedValue>[][] values = this.getVisibleValues();

		int i = 0;
		for (ObservationTarget target : rowCopy)
		{
			List<ObservedValue> valueList = values[i][featureIndex];
			if (valueList != null && valueList.size() > 0)
			{
				ObservedValue value = valueList.get(0);
				if (value.getValue() != null
						&& value.getValue().equals(filterTerm))
				{
					targetsToKeep.add(target);
					indicesToKeep.add(i);
				}
				else
				{
					if (value.getRelation_Name() != null
							&& value.getRelation_Name().equals(filterTerm))
					{
						targetsToKeep.add(target);
						indicesToKeep.add(i);
					}
				}
			}
			i++;
		}

		this.rowCopy = targetsToKeep;
		this.rowIndicesCopy = indicesToKeep;
		return this;
	}

	@Deprecated
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColValues(
			MatrixQueryRule rule) throws Exception
	{
		return this.sliceByColValues(Integer.getInteger(rule.getField()),
				rule.getOperator(), rule.getValue());
	}

	@Deprecated
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowHeader(
			MatrixQueryRule rule) throws Exception
	{
		return this.sliceByRowProperty(rule.getField(), rule.getOperator(),
				rule.getValue());
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowProperty(
			String property, Operator operator, Object value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColProperty(
			String property, Operator operator, Object value) throws Exception
	{
		List<ObservableFeature> featuresToKeep = new ArrayList<ObservableFeature>();
		List<Integer> indicesToKeep = new ArrayList<Integer>();
		Object filterTerm = value;
		List<ObservableFeature> featList = db.find(ObservableFeature.class,
				new QueryRule(ObservableFeature.NAME, Operator.EQUALS,
						filterTerm));
		ObservableFeature featToKeep = featList.get(0);
		int featIndex = this.originalCols.indexOf(featToKeep);
		featuresToKeep.add(featToKeep);
		indicesToKeep.add(featIndex);
		this.colCopy = featuresToKeep;
		this.colIndicesCopy = indicesToKeep;
		return this;
	}

	@Deprecated
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColHeader(
			MatrixQueryRule rule) throws Exception
	{
		return this.sliceByColProperty(rule.getField(), rule.getOperator(),
				rule.getValue());
	}

	@Override
	public BasicMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> getResult()
			throws Exception
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
		// return originalRows.get(0).get__Type();
	}

	@Override
	public String getColType()
	{
		return "ObservableFeature";
		// return originalCols.get(0).get__Type();
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
	public String renderRowSimple(ObservationTarget row)
	{
		return row.getName();
	}

	@Override
	public String renderColSimple(ObservableFeature col)
	{
		return col.getName();
	}

	@Deprecated
	public List<String> getRowHeaderFilterAttributes()
	{
		return this.getRowPropertyNames();
	}
	
	@Override
	public List<String> getRowPropertyNames()
	{
		return new ObservationTarget().getFields();
	}

	@Deprecated
	public List<String> getColHeaderFilterAttributes()
	{
		List<String> returnList = new ArrayList<String>();
		returnList.add(ObservableFeature.NAME);
		returnList.add(ObservableFeature.__TYPE);
		return returnList;
	}
	
	public List<String> getColPropertyNames()
	{
		return new ObservableFeature().getFields();
	}

	@Override
	public RenderDescriptor<ObservationTarget, ObservableFeature, List<ObservedValue>> getRenderDescriptor()
			throws Exception
	{
		return this;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByRowValues(
			ObservationTarget row, Operator operator, Object value)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColValues(
			ObservableFeature col, Operator operator, Object value)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> slice(
			MatrixQueryRule rule) throws MatrixException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColValueProperty(
			ObservableFeature col, String property, Operator operator,
			Object value) throws MatrixException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SliceableMatrix<ObservationTarget, ObservableFeature, List<ObservedValue>> sliceByColValueProperty(
			int colIndex, String property, Operator operator, Object value)
			throws MatrixException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getValuePropertyNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRowLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRowLimit(int rowLimit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getRowOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRowOffset(int rowOffset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getColLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setColLimit(int colLimit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getColOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setColOffset(int colOffset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer getColCount() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getRowCount() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
