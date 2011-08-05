package org.molgenis.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.log4j.Logger;
import org.molgenis.model.elements.Field;

/**
 * Write values to an Excel file
 */
public class XlsWriter implements SpreadsheetWriter
{
	private WritableWorkbook workbook;
	private WritableSheet sheet;
	private WritableFont headerFont;
	private WritableCellFormat headerFormat;
	private WritableFont cellFont;
	private WritableCellFormat cellFormat;
	private List<String> headers = new ArrayList<String>();
	
	//need to keep track of the rownumber we're writing in!
	public int rowIndex = 1;
	
	private static final transient Logger logger = Logger.getLogger(XlsWriter.class.getSimpleName());

	
	public XlsWriter(OutputStream writer, List<String> headers) throws WriteException, IOException {
		this(writer);
		this.headers = headers;
	}
	
	/**
	 * Construct an Excel writer using a PrintWriter.
	 * This is the constructor that is used by db.find().
	 * 
	 * @param writer
	 * @throws IOException 
	 * @throws WriteException 
	 */
	public XlsWriter(OutputStream outputStream) throws IOException, WriteException {
		
		// Create workbook around the output stream
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(new Locale("en", "EN"));
		this.workbook = Workbook.createWorkbook(outputStream, ws);

		// Format the fonts
	    this.headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
	    this.headerFormat = new WritableCellFormat(headerFont);
	    this.headerFormat.setWrap(false);
	    this.cellFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
	    this.cellFormat = new WritableCellFormat(cellFont);
	    this.cellFormat.setWrap(false);
	    
	    // Create a sheet to write in
	    // TODO: give sheet the name of the entity somehow!!
	    String sheetName = "untitled";
	    int sheetIndex = 0;
	    sheet = workbook.createSheet(sheetName, sheetIndex);
	}
	
	@Override
	public void writeHeader() throws Exception
	{
		// Add and store headers
		for(int i = 0; i < headers.size(); i++){
			String header = headers.get(i);
			Label l = new Label(i, 0, header, headerFormat);
			sheet.addCell(l);
		}
	}

	@Override
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}
	
	@Override
	public void writeEndOfLine() {
		//go to next row
		
	}

	@Override
	public void close() throws Exception {
		//close the excel file; no writing allowed anymore
		workbook.write();
		workbook.close();
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
		
	}
	
	@Override
	public void writeRow(Entity e) throws Exception
	{
		for(int i = 0; i < headers.size(); i++){
			
			String contents = "";
			
			Object fieldValue = e.get(headers.get(i));
			if(fieldValue != null){
				
				if (fieldValue instanceof List<?>)
				{
					List<?> list = (List<?>) fieldValue;
					for (int j = 0; j < list.size(); j++)
					{
						if (j != 0) {
							contents += ",";
						}
						if (list.get(j) != null)
						{
							contents += list.get(j).toString();
						}
						else
						{
							throw new Exception("List contains null value(s)");
						}
					}

				} else {
					contents = fieldValue.toString();
				}
				
			}
			Label l = new Label(i, rowIndex, contents, cellFormat);
			sheet.addCell(l);
		}
		rowIndex++;
	}

	@Override
	public void writeRow(Tuple t)
	{
		
	}

	@Override
	public void writeValue(Object object)
	{

	}

}
