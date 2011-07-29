package org.molgenis.matrix;

import java.text.ParseException;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public class TargetFeatureMemoryMatrix extends PhenoMemoryMatrix<ObservationTarget,ObservableFeature> 
	implements TargetFeatureMatrix, EditableMatrix<ObservationTarget,ObservableFeature, ObservedValue>, 
	FilterableMatrix<ObservationTarget,ObservableFeature, ObservedValue>
{

	public TargetFeatureMemoryMatrix(Class<ObservationTarget> rowType,
			Class<ObservableFeature> colType, Database db)
			throws MatrixException, DatabaseException, ParseException
	{
		super(rowType, colType, db);
	}

	public TargetFeatureMemoryMatrix(StringMemoryMatrix m) throws MatrixException
	{
		super(ObservationTarget.class, ObservableFeature.class, m);
	}

	public TargetFeatureMemoryMatrix(Database db) throws MatrixException, DatabaseException, ParseException
	{
		super(ObservationTarget.class, ObservableFeature.class, db);
	}

	@Override
	public void setRowFilters(List<QueryRule> filters) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColFilters(List<QueryRule> filters) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setCol(int col, List<ObservedValue> colValues) throws MatrixException
	{
		// colValues must be of same lenght as values
		if (this.values.length != colValues.size()) throw new MatrixException(
				"setCol failed: colValues != getRowCount()");

		// col must be inside getColCount
		if (col >= getColCount()) throw new MatrixException(
				"setCol failed: col >= getColCount()");

		// in each row set
		for (int i = 0; i < colValues.size(); i++)
		{
			this.values[i][col] = colValues.get(i);
		}

	}

	@Override
	public void setCol(ObservableFeature col, List<ObservedValue> colValues) throws MatrixException
	{
		this.setCol(this.getColId(col), colValues);

	}

	@Override
	public void setRow(int row, List<ObservedValue> rowValues) throws MatrixException
	{
		// rowValues must be in size of colCount
		if (rowValues.size() != this.getColCount()) throw new MatrixException(
				"setRow failed: rowValues.size() != getColCount()");

		// row must be in size of rowCount
		if (row >= getRowCount()) throw new MatrixException(
				"setRow failed: row >= getRowCount()");

		// iterate
		for (int i = 0; i < rowValues.size(); i++)
		{
			this.values[row][i] = rowValues.get(i);
		}

	}

	@Override
	public void setRow(ObservationTarget row, List<ObservedValue> rowValues) throws MatrixException
	{
		this.setRow(getRowId(row), rowValues);

	}

	public TargetFeatureMemoryMatrix getSubMatrixFilterByColMatrixValues(
			QueryRule q) {
		// TODO Auto-generated method stub
		return null;
	}

	public TargetFeatureMemoryMatrix getSubMatrixFilterByRowMatrixValues(
			QueryRule q) {
		// TODO Auto-generated method stub
		return null;
	}
}
