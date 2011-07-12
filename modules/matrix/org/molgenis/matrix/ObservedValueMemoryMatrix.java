package org.molgenis.matrix;

import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.ngs.NgsSample;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvStringWriter;
import org.molgenis.util.Entity;

/**
 * Simple memory based matrix of ObservedValue
 */
public class ObservedValueMemoryMatrix<E extends ObservationElement, A extends ObservationElement> extends
		MemoryMatrix<E, A, ObservedValue>
{
	public ObservedValueMemoryMatrix(Database db, List<E> rows,
			List<A> cols) throws MatrixException,
			DatabaseException, ParseException
	{
		// names are unique in whole database???
		super(rows, cols);

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

	private static List<Object> getIds(List<? extends Entity> entities)
	{
		List<Object> result = new ArrayList<Object>();
		for (Entity e : entities)
			result.add(e.getIdValue());
		return result;

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
				//take care of separator
				if(!first) writer.writeSeparator();
				else first = false;
				//write a header value
				writer.writeValue(col.getName());
			}
			writer.writeEndOfLine();

			for (E row : getRowNames())
			{
				writer.writeValue(row.getName());
				for (A col : getColNames())
				{
					writer.writeSeparator();
					writer.writeValue(this.getValue(row, col).getValue());
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
