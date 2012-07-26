package plugins.searchingForProxy;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
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
	private String inputFile =  "/Users/despoina/userselection.xls";
    ByteArrayOutputStream file = new ByteArrayOutputStream();

	//private ByteArrayOutputStream file = new File(inputFile);

	
	public void setOutputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	//write a line
	public void write(List<String> label, HashMap<Integer, ArrayList<String>> usrselect) throws IOException, WriteException {
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));

		WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
		workbook.createSheet("Report", 0);
		WritableSheet excelSheet = workbook.getSheet(0);
		createLabel(excelSheet, label);
		createContent(excelSheet, usrselect);

		workbook.write();
		workbook.close();
	}

	private void createLabel(WritableSheet sheet, List<String> labels)
			throws WriteException {
	
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
		times = new WritableCellFormat(times10pt);
		times.setWrap(true);

		WritableFont times10ptBoldUnderline = new WritableFont( WritableFont.TIMES, 10, WritableFont.BOLD, false, UnderlineStyle.SINGLE);
		timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
		timesBoldUnderline.setWrap(true);

		CellView cv = new CellView();
		cv.setFormat(times);
		cv.setFormat(timesBoldUnderline);
		cv.setAutosize(true);

		//add one label 
		for (int i=0; i<labels.size(); i++)		addCaption(sheet, i, 0, labels.get(i));

	}

	private void createContent(WritableSheet sheet, HashMap<Integer, ArrayList<String>> usrselect) throws WriteException,
		RowsExceededException {
		
		for(Entry<Integer, ArrayList<String>> eachEntry : usrselect.entrySet()){
			
			System.out.println(eachEntry.getValue().size());
				for (int column=0; column<eachEntry.getValue().size(); column++) {
				  addLabel(sheet, column ,eachEntry.getKey(), usrselect.get(eachEntry.getKey()).get(column));
				}
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

	public ByteArrayOutputStream getFile() {
		return this.file;
	}
//	public static void main(String[] args) throws WriteException, IOException {
//		WriteExcel test = new WriteExcel();
//		test.setOutputFile("/Users/despoina/out.xls");
//		
//		List<String> columns = new ArrayList<String>();
//		
//	
//		columns.add("first line 1st column");
//		columns.add("1-2");
//		test.write("User selections", 0, columns);
//		
//		List<String> columns2 = new ArrayList<String>();
//
//		columns2.add("second line first column ");
//		test.write("User selections", 1, columns2);
//		
//				
//		System.out.println("Please check the result file under /Users/despoina/out.xls");
//		
//	}
}