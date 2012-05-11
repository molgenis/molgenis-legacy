package org.molgenis.datatable.view;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.molgenis.datatable.model.SimpleTableModel;
import org.molgenis.fieldtypes.FieldType;
import org.molgenis.matrix.MatrixException;
import org.molgenis.model.elements.Field;
import org.molgenis.util.CsvWriter;


public class CsvExporter<RowType> extends AbstractExporter<RowType>
{
	protected CsvWriter d_writer;
	protected SimpleDateFormat d_dateFormat;
	
	public CsvExporter(SimpleTableModel<RowType> matrix, OutputStream os) {
		this(matrix, os, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	public CsvExporter(SimpleTableModel<RowType> matrix, OutputStream os, SimpleDateFormat simpleDateFormat)
	{
		super(matrix, os);
		d_writer = new CsvWriter(os);
		d_writer.setSeparator(",");
	}

	@Override	
	public void export() throws MatrixException {
		initHeaders();
		writeResults();
		d_writer.close();
	}

	protected void initHeaders() throws MatrixException {
		List<String> headers = new ArrayList<String>();
		for (Field column : tableModel.getColumns())
		{
			headers.add(column.getName());
		}
		d_writer.setHeaders(headers);
		d_writer.writeHeader();
	}

	@Override
	public void writeSingleCell(Object value, int iRow, int iColumn, FieldType colType) {
		// FIXME : typing
		if (value instanceof Date) {
			d_writer.writeValue(d_dateFormat.format(value));
		} else {
			d_writer.writeValue(value == null ? null : value.toString());
		}
	}
	
	@Override
	public void writeSeparator() {
		d_writer.writeSeparator();
	};
	
	@Override
	public void writeEndOfLine() {
		d_writer.writeEndOfLine();
	}

	@Override
	public String getFileExtension()
	{
		return ".csv";
	}

	@Override
	public String getMimeType()
	{
		return "text/csv";
	}
}
