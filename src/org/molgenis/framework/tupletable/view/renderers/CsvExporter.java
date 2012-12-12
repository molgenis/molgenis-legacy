package org.molgenis.framework.tupletable.view.renderers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.io.csv.CsvWriter;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;
import org.molgenis.util.tuple.AbstractTuple;
import org.molgenis.util.tuple.DeprecatedTupleTuple;

/**
 * Export TupleTable to CSV file
 */
public class CsvExporter extends AbstractExporter
{
	private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

	public CsvExporter(TupleTable table)
	{
		super(table);
	}

	@Override
	public void export(OutputStream os) throws IOException, TableException
	{
		CsvWriter csvWriter = new CsvWriter(new OutputStreamWriter(os, CHARSET_UTF8));
		try
		{
			csvWriter.writeColNames(new FieldHeaderTuple(tupleTable.getColumns()));
			for (Tuple row : tupleTable)
				csvWriter.write(new DeprecatedTupleTuple(row));
		}
		finally
		{
			csvWriter.close();
		}
	}

	public void initHeaders(OutputStream os)
	{
		// noop
	}

	private static class FieldHeaderTuple extends AbstractTuple
	{
		private final List<Field> fields;

		public FieldHeaderTuple(List<Field> fields)
		{
			if (fields == null) throw new IllegalArgumentException("fields is null");
			this.fields = fields;
		}

		@Override
		public int getNrCols()
		{
			return fields.size();
		}

		@Override
		public Iterator<String> getColNames()
		{
			return new Iterator<String>()
			{
				private Iterator<Field> it = fields.iterator();

				@Override
				public boolean hasNext()
				{
					return it.hasNext();
				}

				@Override
				public String next()
				{
					return it.next().getSqlName();
				}

				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();
				}

			};
		}

		@Override
		public Object get(String colName)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Object get(int col)
		{
			throw new UnsupportedOperationException();
		}
	}
}
