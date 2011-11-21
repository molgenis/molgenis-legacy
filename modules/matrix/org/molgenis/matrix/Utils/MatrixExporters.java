package org.molgenis.matrix.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.molgenis.framework.db.Database;
import org.molgenis.matrix.Matrix;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;

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
            Label l = new Label(i + 1, 0, colNames.get(i), headerFormat);
            s.addCell(l);
        }

        /* Write row headers */
        List<String> rowNames = matrix.getRowPropertyNames();
        for (int i = 0; i < rowNames.size(); i++) {
            Label l = new Label(0, i + 1, rowNames.get(i), headerFormat);
            s.addCell(l);
        }

        /* Write elements */
        Object[][] elements = matrix.getValues(db);
        for (int i = 0; i < matrix.getColCount(db); i++) {
            for (int j = 0; j < matrix.getRowCount(db); j++) {
                if (elements[j][i] != null) {
                    Label l = new Label(i + 1, j + 1,
                            elements[j][i].toString(), cellFormat);
                    s.addCell(l);
                } else {
                    s.addCell(new Label(i + 1, j + 1, "", cellFormat));
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
}
