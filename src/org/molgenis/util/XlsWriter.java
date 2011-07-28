package org.molgenis.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

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

public class XlsWriter implements CsvWriter
{
	private String inputFile = System.getProperty("java.io.tmpdir");
	private WritableCellFormat timesBoldUnderline;
	private WritableCellFormat times;
	
	WritableWorkbook workbook;
	private WritableSheet excelSheet;
	

	
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}


	public Object getInputFile() {
		return inputFile;
	}
	
	public void setOutputFile(String inputFile) {
		this.setInputFile(inputFile);
	}
	
	
	public XlsWriter() {

	}

	public XlsWriter(String string) {
		System.out.println(">>> The .xls file will be written in : " + string);
		this.setInputFile(string);

	}

	@Override
	public void writeHeader()  {
		addCaption(excelSheet, 0, 0, "Header 1");
		addCaption(excelSheet, 1, 0, "This is another header");
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
	public void writeValue(Object object) {
		//put this object in the current cell and go to next cell
		excelSheet = workbook.getSheet(0);
		
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

	@Override
	public void writeMatrix(List<String> rowNames, List<String> colNames, Object[][] elements) {
		//first write the headers (colnames), first cell is empty because this is a matrix
		//than for each row first write the rowname
		//than use the row[i] to write the rest of the row
		
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


	@Override
	public void writeRow(Entity e)	{
		
	}


	@Override
	public void writeRow(Tuple t) {
		
	}


	
	

	
}
