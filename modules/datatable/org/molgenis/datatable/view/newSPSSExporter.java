//package org.molgenis.datatable.view;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.StringWriter;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
//import org.molgenis.datatable.model.TupleTable;
//import org.molgenis.fieldtypes.CategoricalType;
//import org.molgenis.fieldtypes.FieldType;
//import org.molgenis.matrix.MatrixException;
//import org.molgenis.model.elements.Field;
//
///**
// * 
// * @author Daan Reid
// *
// *	Exporter that writes to two streams; one of comma-separated values, and one SPSS script file to read them.
// */
//public class newSPSSExporter extends CsvExporter
//{
//
//	private  OutputStream d_spsOs;
//
//	public newSPSSExporter(TupleTable matrix, OutputStream os, OutputStream spsOs)
//	{
//		super(matrix, os, new SimpleDateFormat("MM/dd/yyyy"));
//		d_spsOs = spsOs;
//	}
//		
//	@Override
//	public void export() throws MatrixException
//	{
//		super.initHeaders();
//		try {
//			writeSPSFile();
//			writeResults();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new MatrixException(e);
//		} finally {
//			d_writer.close();
//		}
//	}
//
//	private void writeSPSFile() throws IOException
//	{
//		BufferedWriter spsWriter = new BufferedWriter(new OutputStreamWriter(d_spsOs));
//		
//		List<Field> columns = tableModel.getColumns();
//		StringWriter valLabels = new StringWriter();
//		StringWriter colNames = new StringWriter();
//		List<Field> categoricalFields = new ArrayList<Field>();
//		
//		// write variable definitions
//		for (Field field : columns) {
//			FieldType fieldType = field.getType();
//			if (fieldType.getEnumType() == FieldTypeEnum.CATEGORICAL) {
//				categoricalFields.add(field);
//			}
//			colNames.write(String.format("%s %s", field.getName(), colTypeToSPSSType(fieldType.getEnumType())));
//		}
//		
//		// add category labels to variables if appropriate
//		for (Field field : categoricalFields) {
//			Map<String, String> categoryMapping = ((CategoricalType)field.getType()).getCategoryMapping();
//			valLabels.write(String.format("ADD VALUE LABELS %s ", field.getName()));
//			for (Entry<String, String> entry : categoryMapping.entrySet()) {
//				valLabels.write(String.format(" %s \'%s\' ", entry.getKey(), entry.getValue()));
//			}
//			valLabels.write("\n");
//		}
//		
//		String spsFormatStr = String.format("GET DATA\n" +
//		"/type = txt\n" + 
//		"/file = \'%s\'\n +" +
//		"/qualifier = \'\"\'" +
//		"delimiters = \',\'\n" +
//		"firstcase = 2\n" +
//		"variables = %s" +
//		"execute.",  File.createTempFile("real",".howto").getAbsoluteFile(), colNames.toString() + valLabels.toString());
//		
//		spsWriter.write(spsFormatStr);
//		spsWriter.flush();
//		spsWriter.close();
//	}
//
//	private static String colTypeToSPSSType(FieldTypeEnum columnType)
//	{
//		switch(columnType) {
//			case CATEGORICAL:
//				return "F";
//			case DATE:
//				return "ADATE";
//			case DATE_TIME:
//				return "ADATE";
//			case DECIMAL:
//				return "F";
//			case INT:
//				return "F";
//			case STRING:
//				return "A";
//		}
//		throw new IllegalArgumentException("Unknown field type: " + columnType);
//	}
//}
