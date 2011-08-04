package lifelines.matrix.Exporter;

import java.io.*;

import com.pmstation.spss.*;


import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BlankCell;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import lifelines.matrix.Column;
import lifelines.matrix.ColumnUtils;
import lifelines.matrix.PagableMatrix;
import lifelines.matrix.Column.ColumnType;


public class ExcelExporter implements MatrixExporter {
	
	/**
	 * The corresponding variables are the columns that are selected by the user in the UI , 
	 * this is returned by getColNames() , and the data of this matrix are returned by the matrix.getData(); 
	 * for example if the user has selected in the matrix viewer : PA_ID , ztgewicht and gewicht  the exported 
	 * SPSS file should contain : 
	 * PA_ID     1 2 3 4 3 7
	   ztgewicht 1 2 3 4 3 7
       gewicht   1 2 3 4 3 7

	 * @param matrix
	 * @param os
	 * @throws IOException 
	 * @throws Exception 
	 */
	@Override
	public void export(PagableMatrix matrix, OutputStream os) throws IOException, Exception {
		// TODO Auto-generated method stub
		
	       /* Create new Excel workbook and sheet */
	       WorkbookSettings ws = new WorkbookSettings();
	       ws.setLocale(new Locale("en", "EN"));
	       WritableWorkbook workbook = Workbook.createWorkbook(os);
	       WritableSheet s = workbook.createSheet("Sheet1", 0);

	       /* Format the fonts */
	       WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
	       WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
	       headerFormat.setWrap(false);
	       WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
	       WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
	       cellFormat.setWrap(false);
	       
	       List<Column> columns = matrix.getColumns(); 
	       
	       /* Write column headers */
	       List<String> colNames = ColumnUtils.getColNames(columns);
	       for (int i = 0; i < colNames.size(); i++) {
	           Label l = new Label(i + 1, 0, colNames.get(i), headerFormat);
	           s.addCell(l);
	       }
	       
	       /* retrieve data */
	       Object[][] elements = matrix.getData();
	       int numberOfRows = elements.length;
	       
//	     /* Write row headers */
//	     List<String> rowNames = matrix.getRowNames();
//	     for (int i = 0; i < rowNames.size(); i++) {
//	         Label l = new Label(0, i + 1, rowNames.get(i), headerFormat);
//	         s.addCell(l);
//	     }
	       
	       //Should use target Id's
		   /* Write row headers */
	       for (int i = 0; i < elements.length; i++) {
		         Label l = new Label(0, i + 1, "" + i, headerFormat);
		         s.addCell(l);
		   }        

	       for (int i = 0; i < columns.size(); i++) {
	           for (int j = 0; j < numberOfRows; j++) {
	        	    WritableCell cellValue = null; 
	        	    Object dataElement = elements[j][i];
	        	   	Column.ColumnType columnType = columns.get(i).getType();
	        	   	int row = i + 1;
	        	   	int column = j + 1;
	        	   	
	        	   	if(dataElement == null) {
	        	   		cellValue = new Blank(row, column);
	        	   	} else {
						if(columnType.equals(Column.ColumnType.String)) {
							cellValue = new Label(row, column, dataElement.toString());
						} else if(columnType.equals(Column.ColumnType.Integer)) {
							cellValue = new jxl.write.Number(row, column, Double.parseDouble(dataElement.toString())); 					
						} else if(columnType.equals(Column.ColumnType.Code)) {
							cellValue = new jxl.write.Label(row, column, dataElement.toString());
						} else if(columnType.equals(Column.ColumnType.Decimal)) {
							cellValue = new jxl.write.Number(row, column, Double.parseDouble(dataElement.toString()));
						} else if(columnType.equals(Column.ColumnType.Timestamp)) {
							SimpleDateFormat dateFormat = new SimpleDateFormat("y/M/d H:m:s");
							cellValue = new jxl.write.DateTime(row, column, dateFormat.parse(dataElement.toString()));
						} else {
							throw new UnsupportedOperationException(String.format("Type %s not available",columnType));
						}        	   
	        	   }
			       s.addCell(cellValue);        		   
	           }
	       }
	           
	       /* Close workbook */
	       workbook.write();
	       workbook.close();
	}
	


	@Override
	public String getContentType() {
		return "application/vnd.ms-excel";
	}
	
	@Override
	public String getFileExtenstion() {
		return "xls"; 
	}
}