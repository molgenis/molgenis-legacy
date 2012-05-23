package plugins.catalogueTree;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class WriteExcel {

	private WritableCellFormat timesBoldUnderline;
	private WritableCellFormat times;
	private String inputFile;
	
	public void setOutputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	//write a line
	public void write(String label, int row, List<String> columns) throws IOException, WriteException {
		File file = new File(inputFile);
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));

		WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
		workbook.createSheet("Report", 0);
		WritableSheet excelSheet = workbook.getSheet(0);
		createLabel(excelSheet, label);
		createContent(excelSheet, row, columns);

		workbook.write();
		workbook.close();
	}

	private void createLabel(WritableSheet sheet, String label)
			throws WriteException {
	
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
		times = new WritableCellFormat(times10pt);
		times.setWrap(true);

		WritableFont times10ptBoldUnderline = new WritableFont(
				WritableFont.TIMES, 10, WritableFont.BOLD, false,
				UnderlineStyle.SINGLE);
		timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
		timesBoldUnderline.setWrap(true);

		CellView cv = new CellView();
		cv.setFormat(times);
		cv.setFormat(timesBoldUnderline);
		cv.setAutosize(true);

		//add one label 
		addCaption(sheet, 0, 0, label);
		//TODO : later add more parameters
		addCaption(sheet, 1, 0, "This is another header");
		

	}

	
	private void createContent(WritableSheet sheet, int row, List<String> columns) throws WriteException,
			RowsExceededException {
					Iterator<String> columnsIter = columns.iterator();
				    while ( columnsIter.hasNext() ){
				    	System.out.println( columnsIter.next() );
				    }
				    
				    for (int column=0; column<columns.size(); column++) {
						addLabel(sheet, column, row, columns.get(column));
	
				    }
		for (int k = 1; k < 11; k++)  {
			for (int l =0; l<10; l++) {
				addLabel(sheet, l, k ," at row"+ k + "at column" + l);
			}
		}
		// Now a bit of text
		for (int i = 12; i < 20; i++) {
			// First column
			addLabel(sheet, 0, i, "Boring text " + i);
			// Second column
			addLabel(sheet, 1, i, "Another text");
		}
	}

	private void addCaption(WritableSheet sheet, int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s, timesBoldUnderline);
		sheet.addCell(label);
	}

	private void addNumber(WritableSheet sheet, int column, int row,
			Integer integer) throws WriteException, RowsExceededException {
		Number number;
		number = new Number(column, row, integer, times);
		sheet.addCell(number);
	}

	private void addLabel(WritableSheet sheet, int column, int row, String s)
			throws WriteException, RowsExceededException {
		Label label;
		label = new Label(column, row, s, times);
		sheet.addCell(label);
	}

	public static void main(String[] args) throws WriteException, IOException {
		WriteExcel test = new WriteExcel();
		test.setOutputFile("/Users/despoina/out.xls");
		List<String> columns = new ArrayList<String>();
		
	
		columns.add("first line second column");
		test.write("User selections", 1, columns);
		
		List<String> columns2 = new ArrayList<String>();

		columns2.add("second line first column ");
		test.write("User selections", 2, columns2);
		
				
		System.out.println("Please check the result file under /Users/despoina/out.xls");
		
	}
}