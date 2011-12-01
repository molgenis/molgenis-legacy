package org.molgenis.matrix.Utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.molgenis.framework.db.Database;
import org.molgenis.matrix.Matrix;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.SliceablePhenoMatrixMV;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvWriter;
import org.mortbay.io.BufferDateCache;

/**
 * Utility class that makes it possible to Export Matrix data
 * The idea is that the Matrix interface is used!
 */
public class MatrixExporters {

    public static void getAsExcel(Database db, final SliceableMatrix matrix, OutputStream os) throws Exception {
        /* Create new Excel workbook and sheet */
        WorkbookSettings ws = new WorkbookSettings();
        ws.setLocale(new Locale("en", "EN"));

        /* Create Workbook */
        WritableWorkbook workbook = Workbook.createWorkbook(os, ws);
        WritableSheet s = workbook.createSheet("Sheet1", 0);

        /* Format the fonts */
        WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10,
                WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);
        headerFormat.setWrap(false);
        WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10,
                WritableFont.NO_BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
        cellFormat.setWrap(false);

        /* Write column headers */
        List<String> colNames = matrix.getColPropertyNames();
        for (int i = 0; i < colNames.size(); i++) {
            Label l = new Label(i, 0, colNames.get(i), headerFormat);
            s.addCell(l);
        }

        /* Write elements */
        ObservedValue[][] elements = (ObservedValue[][]) matrix.getValues();
        int colCount =  matrix.getColCount();
        int rowCount = elements.length;
        for (int i = 0; i < colCount; i++) {
            for (int j = 0; j < rowCount; j++) {
                if (elements[j][i] != null) {
                    Label l = new Label(i, j + 1,
                            elements[j][i].getValue().toString(), cellFormat);
                    s.addCell(l);
                } else {
                    s.addCell(new Label(i, j + 1, "", cellFormat));
                }
            }
        }

        /* Close workbook */
        workbook.write();
        workbook.close();
    }

    public static File getAsExcelFile(final SliceableMatrix matrix, Database db, String fileName) throws Exception {
        /* Create tmp file */
        File excelFile = new File(System.getProperty("java.io.tmpdir")
                + File.separator + fileName + ".xls");

        FileOutputStream fos = new FileOutputStream(excelFile);
        getAsExcel(db, matrix, fos);
        fos.flush();
        fos.close();

        return excelFile;
    }

	public static void getAsCSV(Database db,
			SliceableMatrix matrix,
			OutputStream os) {
		PrintWriter out = new PrintWriter(os);
		try {
			ObservedValue[][] elements = (ObservedValue[][]) matrix.getValues();
	        int colCount =  matrix.getColCount();
	        int rowCount = elements.length;
	        
	        List<String> colNames = matrix.getColPropertyNames();
	        for (int i = 0; i < colCount; i++) {
	        	out.print(colNames.get(i));
	        	if(i + 1 < colCount) {
	        		out.print(",");
	        	}
	        }
	        out.println();
	        
	        for(int iRow = 0; iRow < rowCount; ++iRow) {
	        	for(int iCol = 0; iCol < colCount; ++iCol) {
	        		out.print(elements[iRow][iCol].getValue());
	        		if(iCol + 1 < colCount) {
	        			out.print(",");
	        		}
	        	}
	        	out.println();
	        }	        
		} catch (MatrixException e) {
			throw new RuntimeException(e);
		}
		out.flush();
		//werkt niet zonder rowHeaders (deze heeft de slicableMatrix niet)
//		CsvWriter csvWriter = new CsvWriter(outputStream);
//		ObservedValue[][] elements;
//		try {
//			elements = (ObservedValue[][]) matrix.getValues();
//			csvWriter.writeMatrix(matrix.getRowNames(), matrix.getColPropertyNames(), elements);
//		} catch (MatrixException e) {
//			throw new RuntimeException(e);
//		}
		
	}
	
    public static File getAsCSV(final SliceableMatrix matrix, Database db, String fileName) throws Exception {
        /* Create tmp file */
        File excelFile = new File(System.getProperty("java.io.tmpdir")
                + File.separator + fileName + ".xls");

        FileOutputStream fos = new FileOutputStream(excelFile);
        getAsCSV(db, matrix, fos);
        fos.flush();
        fos.close();

        return excelFile;
    }
}
