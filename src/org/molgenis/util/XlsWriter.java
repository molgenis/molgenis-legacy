package org.molgenis.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.Label;
import jxl.write.biff.RowsExceededException;

/**
 * Write values to an Excel file
 */
public class XlsWriter implements SpreadsheetWriter
{
	private String inputFile = System.getProperty("java.io.tmpdir");
	private WritableCellFormat timesBoldUnderline;
	private WritableCellFormat times;
	
	WritableWorkbook workbook;
	private WritableSheet excelSheet;
	
	private static final transient Logger logger = Logger.getLogger(XlsWriter.class.getSimpleName());
	protected PrintWriter writer = null;

	/** number of rows written */
	private int count = 0;
	/** value to use for missing/null values such as "NULL" or "NA", default "" */
	private String missingValue = "";
	private List<String> headers = new ArrayList<String>();

	/**
	 * TODO : ----Construct the Writer, wrapping another writer.---
	 */
	public XlsWriter(PrintWriter writer, List<String> headers) {
		this(writer);
		this.headers = headers;
	}
	
	public XlsWriter(String string) {
		System.out.println(">>> The .xls file will be written in : " + string);
		this.setInputFile(string);

	}

	public XlsWriter(PrintWriter xlsDownload) {
		
		this.writer = xlsDownload;

		writeHeader();
		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setLocale(new Locale("en", "EN"));
		String inputFile = 	System.getProperty("java.io.tmpdir");
		System.out.println(">>>>>>>>>>>>>>Check for the xls file in " + inputFile + "xlswriter.xls");
		File file = new File(inputFile + "xlswriter.xls");
		
		wbSettings.setLocale(new Locale("en", "EN"));

		try	{
			WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
			workbook = Workbook.createWorkbook(file, wbSettings);
			workbook.createSheet("Report", 0);
			WritableSheet excelSheet = workbook.getSheet(0);
			createLabel(excelSheet);
			//createContent(excelSheet);
			workbook.write();
			workbook.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (WriteException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}


	public Object getInputFile() {
		return inputFile;
	}
	
	public void setOutputFile(String inputFile) {
		this.setInputFile(inputFile);
	}

	@Override
	public void writeHeader(WritableSheet excelSheet)  {
		addCaption(excelSheet, 0, 0, "Header 1");
		addCaption(excelSheet, 1, 0, "This is another header");
	}
	
	/* (non-Javadoc)
	 * @see org.molgenis.util.CsvWriter#writeHeader()
	 */
	@Override
	public void writeHeader()
	{
		for (int i = 0; i < headers.size(); i++)
		{
			if (i < headers.size() - 1)
			{
				writer.print(headers.get(i));
			}
			else
			{
				writer.print(headers.get(i));
			}
		}
		writer.println();
	}

	@Override
	public void writeRow(Entity e, WritableSheet sheet) {
		WritableCell number = null;
		//write all values of this entity to the current row
		//keep order as used in the headers
		try {
			sheet.addCell(number);
		} catch (RowsExceededException e1) {
			e1.printStackTrace();
		} catch (WriteException e1) {
			e1.printStackTrace();
		}
		
	}

	@Override
	public void writeRow(Tuple t,  WritableSheet sheet) {
		//write all values of this tuple to the current row
		//keep order as via writeHeaders
		WritableCell number = null;
		//write all values of this entity to the current row
		//keep order as used in the headers
		try {
			sheet.addCell(number);
		} catch (RowsExceededException e1) {
			e1.printStackTrace();
		} catch (WriteException e1) {
			e1.printStackTrace();
		}
		
	}

	@Override
	public void setHeaders(List<String> fields) {
		
	}
	
	@Override
	public void writeEndOfLine() {
		//go to next row
		
	}

	@Override
	public void close() {
		//close the excel file; no writing allowed anymore
		try
		{
			workbook.close();
		} catch (WriteException e) {
			e.printStackTrace();
		}	catch (IOException e) {
			e.printStackTrace();
		}

		
	}

	/**
	 * Write out an XGAP matrix. The inputs can be retrieved from any
	 * implementation of the XGAP matrix interface class.
	 * 
	 * @param rowNames
	 * @param colNames
	 * @param elements
	 */
	public void writeMatrix(List<String> rowNames, List<String> colNames, Object[][] elements)
	{
		//first write the headers (colnames), first cell is empty because this is a matrix
		//than for each row first write the rowname
		//than use the row[i] to write the rest of the row
		
		logger.info("writeMatrix called");
		String cols = "";
		for (String col : colNames)
		{
			cols += "\t" + col;
		}
		writer.println(cols);
		logger.info("printing: " + cols);
		for (int rowIndex = 0; rowIndex < rowNames.size(); rowIndex++)
		{
			String row = rowNames.get(rowIndex);
			for (int colIndex = 0; colIndex < colNames.size(); colIndex++)
			{
				if (elements[rowIndex][colIndex] == null)
				{
					row += "\t";
				}
				else
				{
					row += "\t" + elements[rowIndex][colIndex];
				}
			}
			writer.println(row);
			logger.info("printing: " + row);
		}
	}

	public void write(File file) throws IOException, WriteException {
		
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));

		WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
		workbook.createSheet("Report", 0);
		WritableSheet excelSheet = workbook.getSheet(0);
		createLabel(excelSheet);
		createContent(excelSheet);

		workbook.write();
		workbook.close();
	}

	/* 
	 * This produces some random data in the excel file. 
	 */
	private void createContent(WritableSheet excelSheet) throws WriteException, RowsExceededException {
		
		// Write a few number
		for (int i = 1; i < 10; i++) {
			// First column
			addNumber(excelSheet, 0, i, i + 10);
			// Second column
			addNumber(excelSheet, 1, i, i * i);
		}
		// Lets calculate the sum of it
		StringBuffer buf = new StringBuffer();
		buf.append("SUM(A2:A10)");
		Formula f = new Formula(0, 10, buf.toString());
		excelSheet.addCell(f);
		buf = new StringBuffer();
		buf.append("SUM(B2:B10)");
		f = new Formula(1, 10, buf.toString());
		excelSheet.addCell(f);

		// Now a bit of text
		for (int i = 12; i < 20; i++) {
			// First column
			addLabel(excelSheet, 0, i, "Boring text " + i);
			// Second column
			addLabel(excelSheet, 1, i, "Another text");
		}
	}

	private void addNumber(WritableSheet sheet, int column, int row, Integer integer) throws WriteException, RowsExceededException {
		Number number;
		number = new Number(column, row, integer, times);
		sheet.addCell(number);
	}

	private void addLabel(WritableSheet sheet, int column, int row, String s)  throws WriteException, RowsExceededException {
			Label label;
			label = new Label(column, row, s, times);
			sheet.addCell(label);		
	}

	private void createLabel(WritableSheet excelSheet)  throws WriteException {
			// Lets create a times font
			WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
			// Define the cell format
			times = new WritableCellFormat(times10pt);
			// Lets automatically wrap the cells
			times.setWrap(true);

			// Create create a bold font with underlines 
			WritableFont times10ptBoldUnderline = new WritableFont(
					WritableFont.TIMES, 10, WritableFont.BOLD, false,
					UnderlineStyle.SINGLE);
			timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
			// Lets automatically wrap the cells
			timesBoldUnderline.setWrap(true);

			CellView cv = new CellView();
			cv.setFormat(times);
			cv.setFormat(timesBoldUnderline);
			cv.setAutosize(true);

			// Write a few headers
			//TODO : add entities name for labels
			addCaption(excelSheet, 0, 0, "Header 1");
			addCaption(excelSheet, 1, 0, "This is another header");
		
	}

	private void addCaption(WritableSheet sheet, int column, int row, String s)  {
		Label label;
		label = new Label(column, row, s, timesBoldUnderline);
		try {
			sheet.addCell(label);
		} catch (RowsExceededException  e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		
	}
	
	/*****************************************************************
	 *  (non-Javadoc)
	 * @see org.molgenis.util.SpreadsheetWriter#writeRow(org.molgenis.util.Entity)
	 */
	@Override
	public void writeRow(Entity e)
	{
		
		boolean first = true;
		for (String col : headers)
		{
			if (first) {
				first = false;
			}
			//print value
			writeValue(e.get(col));

		}
		//newline
		writer.println();
		// writer.println(e.getValues(separator));
		if (++count % 10000 == 0) logger.debug("wrote line " + count + ": " + e);
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.CsvWriter#writeRow(org.molgenis.util.Tuple)
	 */
	@Override
	public void writeRow(Tuple t)
	{
		boolean first = true;
		for (String col : headers)
		{
			//print separator unless first element
			if (first)
			{
				first = false;
			}
			else
			{
				//writer.print(separator);  //TODO
			}
			//print value
			writeValue(t.getObject(col));
		}
		writer.println();
		if (count++ % 10000 == 0) logger.debug("wrote tuple to line " + count + ": " + t);
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.CsvWriter#writeValue(java.lang.Object)
	 */
	@Override
	public void writeValue(Object object)
	{
		
		excelSheet = workbook.getSheet(0);

		try {
			if (object == null)
			{
				writer.print(this.missingValue);
			}
	
			else
			{
				if (object instanceof List<?>)
				{
					List<?> list = (List<?>) object;
					for (int i = 0; i < list.size(); i++)
					{
						if (list.get(i) != null)
						{
							addLabel(excelSheet, 0, i, list.get(i).toString());
						}
						else
						{
							addLabel(excelSheet, 0, i, this.getMissingValue());
						}
					}
				}
				else
				{
					//writer.print(StringEscapeUtils.escapeCsv(object.toString().trim().replace("\n", "")));
					writer.print(StringEscapeUtils.escapeCsv(object.toString()));  //TODO : what about xls????
				}
			}
		}
		catch (RowsExceededException e)
		{
			e.printStackTrace();
		}
		catch (WriteException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Get the String that is used for missing or null values, default 'NA'.
	 */
	public String getMissingValue()
	{
		return missingValue;
	}

	/**
	 * Set the String that is used for missingValues such as null, default 'NA'.
	 * 
	 * @param missingValue
	 *            new missing value String.
	 */
	public void setMissingValue(String missingValue)
	{
		this.missingValue = missingValue;
	}

}
