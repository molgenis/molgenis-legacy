package org.molgenis.matrix.Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.zip.ZipOutputStream;
import org.hibernate.ScrollableResults;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.PhenoMatrix;
import org.molgenis.matrix.component.Column;
import org.molgenis.matrix.component.Column.ColumnType;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

public class newSPSSExporter<R extends ObservationTarget, C extends Measurement, V extends ObservedValue> 
extends CsvExporter<R, C, V>
{

	private  OutputStream d_spsOs;

	public newSPSSExporter(PhenoMatrix<R, C, V> matrix, OutputStream os, OutputStream dpsOs)
	{
		super(matrix, new ZipOutputStream(os), new SimpleDateFormat("MM/dd/yyyy"));
		d_spsOs = dpsOs;
	}
		
	@Override
	protected void export(boolean exportVisible) throws MatrixException
	{
		super.initHeaders();
		ScrollableResults sr = null;
		try {
			writeSPSFile();
			sr = matrix.getScrollableValues(exportVisible);
			// FIXME : hack because an extra column is added *only* when offset is not 0 
			// (probably also database dependent, ie oracle/mysql)
			writeResults(sr, exportVisible && matrix.getRowOffset() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MatrixException(e);
		} finally {
			d_writer.close();
			sr.close();
			d_writer.close();
		}
	}

	private void writeSPSFile() throws IOException
	{
		BufferedWriter spsWriter = new BufferedWriter(new OutputStreamWriter(d_spsOs));
		
		List<C> measurements = matrix.getMeasurements();
		Map<Measurement, List<Category>> categoryLabels = new HashMap<Measurement, List<Category>>();
		StringWriter valLabels = new StringWriter();
		StringWriter colNames = new StringWriter();
		for (Measurement m : measurements) {
			ColumnType columnType = Column.getColumnType(m.getDataType());
			if (columnType.equals(ColumnType.CODE)) {
				categoryLabels.put(m, m.getCategories());
			}
			colNames.write(String.format("%s %s", m.getName(), colTypeToSPSSType(columnType)));
		}
		
		
		if (categoryLabels.size() > 0) {
			for (Measurement m : categoryLabels.keySet()) {
				valLabels.write(String.format("ADD VALUE LABELS %s ", m.getName()));
				for (Category c : categoryLabels.get(m)) {
					valLabels.write(String.format(" \'%s\' \'%s\' ", c.getCode_String(), c.getLabel()));
				}
			}
		}		
		
		String spsFormatStr = String.format("GET DATA\n" +
		"/type = txt\n" + 
		"/file = \'%s\'\n +" +
		"/qualifier = \'\"\'" +
		"delimiters = \',\'\n" +
		"firstcase = 2\n" +
		"variables = %s" +
		"execute.", colNames, valLabels);
		
		spsWriter.write(spsFormatStr);
		spsWriter.flush();
		spsWriter.close();
	}

	private static String colTypeToSPSSType(ColumnType columnType)
	{
		switch(columnType) {
			case CODE:
				return "F";
			case DATE:
				return "ADATE";
			case DATETIME:
				return "ADATE";
			case DECIMAL:
				return "F";
			case INTEGER:
				return "F";
			case STRING:
				return "A";
			case TIMESTAMP:
				return "A";
		}
		return null;
	}
}
