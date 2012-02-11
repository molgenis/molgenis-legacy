package org.molgenis.matrix;

import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvStringWriter;
import org.molgenis.util.Entity;

/**
 * Simple memory based matrix of ObservedValue
 */
public class PhenoMemoryMatrix<E extends ObservationElement, A extends ObservationElement>
		extends MemoryMatrix<E, A, ObservedValue>
{
	Class<E> rowType;
	Class<A> colType;
	
	public PhenoMemoryMatrix(Class<E> rowType, Class<A> colType, Database db)
			throws MatrixException, DatabaseException, ParseException
	{
		this(db.find(rowType), db.find(colType), db);
		this.rowType = rowType;
		this.colType = colType;
	}

	/** Construct a pheno matrix from database, only using selected rows/cols */
	public PhenoMemoryMatrix(List<E> rows, List<A> cols, Database db)
			throws MatrixException, DatabaseException, ParseException
	{
		// names are unique in whole database???
		super(rows, cols, ObservedValue.class);
		
		//get rowType and colType
		for(E row: rows) if(row == null) throw new MatrixException("rows may not have null values");
		for(A col: cols) if(col == null) throw new MatrixException("cols may not have null values");
		this.rowType = (Class<E>) rows.get(0).getClass();
		this.colType = (Class<A>) cols.get(0).getClass();

		// get a map of index->id
		List<Object> rowIds = getIds(rows);
		List<Object> colIds = getIds(cols);

		// retrieve observed values for this matrix, on duplicate, sorted by
		// timestamp
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.in(ObservedValue.FEATURE, colIds);
		q.in(ObservedValue.TARGET, rowIds);
		q.sortASC(ObservedValue.TIME);

		List<ObservedValue> values = q.find();

		// map values to right position, sorting by timestamp ensures we have
		// most recent value for each row,col pair
		for (ObservedValue v : values)
		{
			this.setValue(rowIds.indexOf(v.getTarget()), colIds.indexOf(v
					.getFeature()), v);
		}

	}

	/**
	 * Construct a pheno matrix from a StringMatrix (such as StringCsvMatrix)
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws MatrixException
	 */
	public PhenoMemoryMatrix(Class<E> rowType, Class<A> colType,
			Matrix<String, String, String> matrix) throws MatrixException
	{
		super();
		this.rowType = rowType;
		this.colType = colType;

		try
		{
			// create new rowType instances
			List<E> rows = new ArrayList<E>();
			for (String rowName : matrix.getRowNames())
			{
				E rowEntity = rowType.newInstance();
				rowEntity.setName(rowName);
				rows.add(rowEntity);
			}
			this.setRowNames(rows);
			// create new colType instances
			List<A> cols = new ArrayList<A>();
			for (String colName : matrix.getColNames())
			{
				A colEntity = colType.newInstance();
				colEntity.setName(colName);
				cols.add(colEntity);
			}
			this.setColNames(cols);
			// create new ObservedValue instances
			ObservedValue[][] values = this.create(rows.size(), cols.size(),
					ObservedValue.class);
			for (int i = 0; i < rows.size(); i++)
			{
				for (int j = 0; j < cols.size(); j++)
				{
					ObservedValue v = new ObservedValue();
					v.setTarget_Name(rows.get(i).getName());
					//v.setTarget___Type(this.rowType.getSimpleName());
					v.setFeature_Name(cols.get(j).getName());
					//v.setFeature___Type(this.colType.getSimpleName());
					v.setValue(matrix.getValue(i, j));
					values[i][j] = v;
				}
			}
			this.setValues(values);

		}
		catch (Exception e)
		{
			throw new MatrixException(e);
		}

	}

	private static List<Object> getIds(List<? extends Entity> entities)
	{
		List<Object> result = new ArrayList<Object>();
		for (Entity e : entities)
			result.add(e.getIdValue());
		return result;

	}

	/**
	 * Write this matrix to the database. Use 'action' parameter to specify
	 * behaviour in case of duplicates.
	 * 
	 * @param db
	 * @param action
	 * @throws DatabaseException
	 */
	public void store(Database db, Database.DatabaseAction action)
			throws DatabaseException
	{
		boolean inTransaction = false;
		try
		{
			if (!db.inTx())
			{
				db.beginTx();
				inTransaction = true;
			}

			// except in case of remove, first add the targets.
			if (!action.equals(Database.DatabaseAction.REMOVE)
					&& !action
							.equals(Database.DatabaseAction.REMOVE_IGNORE_MISSING))
			{
				db.update(this.getRowNames(), action, ObservationElement.NAME);
				db.update(this.getColNames(), action, ObservationElement.NAME);
			}

			for (int i = 0; i < this.getRowCount(); i++)
			{
				db.update(Arrays.asList(this.getRow(i)), action,
						ObservedValue.FEATURE_NAME, ObservedValue.TARGET_NAME,
						ObservedValue.VALUE, ObservedValue.TIME);
			}

			// remove unless linked to another value. BIG FIXME!!!
			if (action.equals(Database.DatabaseAction.REMOVE)
					|| action
							.equals(Database.DatabaseAction.REMOVE_IGNORE_MISSING))
			{
				db.update(this.getRowNames(), action, ObservationElement.NAME);
				db.update(this.getColNames(), action, ObservationElement.NAME);
			}

			if(inTransaction) db.commitTx();
		}
		catch (Exception e)
		{
			if(inTransaction) db.rollbackTx();
			throw new DatabaseException(e);
		}
	}

	public void store(Database db) throws DatabaseException
	{
		store(db, Database.DatabaseAction.ADD_IGNORE_EXISTING);
	}

	public String toString()
	{
		StringWriter string = new StringWriter();
		try
		{
			CsvStringWriter writer = new CsvStringWriter(string);

			// print headers
			boolean first = true;
			for (A col : getColNames())
			{
				// take care of separator
				if (!first) writer.writeSeparator();
				else
					first = false;
				// write a header value
				writer.writeValue(col.getName());
			}
			writer.writeEndOfLine();

			for (E row : getRowNames())
			{
				writer.writeValue(row.getName());
				for (A col : getColNames())
				{
					writer.writeSeparator();
					if(this.getValue(row, col) != null)
						writer.writeValue(this.getValue(row, col).getValue());
					else
						writer.writeValue(writer.getMissingValue()	);
				}
				writer.writeEndOfLine();
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "ERROR";
		}

		return string.toString();
	}

}
