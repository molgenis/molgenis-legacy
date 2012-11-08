package org.molgenis.framework.tupletable.view.renderers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.model.elements.Field;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Tuple;

/**
 * Export TupleTable to CSV file.
 * 
 * This should replace CsvWriter class. TODO:
 */
public class CsvExporter extends AbstractExporter
{
	public CsvExporter(TupleTable table)
	{
		super(table);
	}

	@Override
	public void export(OutputStream os) throws IOException, TableException
	{
		CsvWriter csv = new CsvWriter(os);

		// write headers
		List<String> headers = new ArrayList<String>();
		for (Field column : tupleTable.getColumns())
		{
			headers.add(column.getSqlName());
		}
		csv.setHeaders(headers);
		csv.writeHeader();

		// write rows
		for (Tuple row : tupleTable)
		{
			csv.writeRow(row);
		}

		csv.close();
	}

	public void initHeaders(OutputStream os)
	{
		// TODO Auto-generated method stub

	}
}
