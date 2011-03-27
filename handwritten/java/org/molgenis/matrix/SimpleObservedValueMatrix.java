package org.molgenis.matrix;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;

/**
 * Naive implementation of a datbase backed matrix that will base its view (order) on what features and targets
 * there are set to be selected. No sorting, filtering or paging are part of
 * this. Also duplicate observations of the same target,feature pair are not supported.
 */
public class SimpleObservedValueMatrix extends MemoryMatrix<ObservedValue>
{
	public SimpleObservedValueMatrix(Database db, List<? extends ObservationElement> targets,
			List<? extends ObservationElement> features) throws MatrixException
	{
		// check params
		assert(db != null);
		if (targets == null || targets.size() == 0) throw new MatrixException("targets.size == 0");
		if (features == null || features.size() == 0) throw new MatrixException("features.size == 0");

		// set row dimension and get target ids
		List<MatrixDimension> rowDimension = new ArrayList<MatrixDimension>();
		List<Integer> rowIds = new ArrayList<Integer>();
		for (ObservationElement e : targets)
		{
			// check unique
			if (rowIds.contains(e.getId())) throw new MatrixException(
					"target ids must be unique in this matrix");

			// add to search lists
			rowIds.add(e.getId());
			rowDimension.add(new MatrixDimension(e.getName()));
		}

		// set column dimension
		List<MatrixDimension> columnDimension = new ArrayList<MatrixDimension>();
		List<Integer> colIds = new ArrayList<Integer>();
		for (ObservationElement e : features)
		{
			// check unique
			if (colIds.contains(e.getId())) throw new MatrixException(
					"feature ids must be unique in this matrix");

			// add to search list
			colIds.add(e.getId());
			columnDimension.add(new MatrixDimension(e.getName()));
		}

		// data will be loaded into values array
		try
		{
			//get observed values from database
			List<ObservedValue> valuesList = db.query(ObservedValue.class).in(
					ObservedValue.TARGET, rowIds).in(ObservedValue.FEATURE, colIds).find();
			
			//load into matrix
			ObservedValue[][] valuesMatrix = create(rowIds.size(),colIds.size());
			int rowIndex = -1;
			int colIndex = -1;
			for(ObservedValue value: valuesList)
			{
				rowIndex = rowIds.indexOf(value.getTarget_Id());
				colIndex = colIds.indexOf(value.getFeature_Id());
				valuesMatrix[rowIndex][colIndex] = value;
			}
			
			//setup the matrix
			this.setRows(rowDimension);
			this.setCols(columnDimension);
			this.setValues(valuesMatrix);
		}
		catch (Exception e)
		{

			e.printStackTrace();
			throw new MatrixException(e.getMessage());
		}
	}
	
	@Override
	protected ObservedValue[][] create(int rows, int cols)
	{
		ObservedValue[][] result = new ObservedValue[rows][cols];
		for(int i = 0; i < rows; i++) result[i] = create(cols);
		return result;
	}

	protected ObservedValue[] create (int size)
	{
		return new ObservedValue[size];
	}
}
