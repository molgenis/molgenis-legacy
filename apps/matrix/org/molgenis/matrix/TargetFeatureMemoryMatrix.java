package org.molgenis.matrix;

import java.text.ParseException;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;

import app.JDBCDatabase;

public class TargetFeatureMemoryMatrix extends PhenoMemoryMatrix<ObservationTarget,ObservableFeature> implements TargetFeatureMatrix
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

	public TargetFeatureMemoryMatrix(JDBCDatabase db) throws MatrixException, DatabaseException, ParseException
	{
		super(ObservationTarget.class, ObservableFeature.class, db);
	}

}
