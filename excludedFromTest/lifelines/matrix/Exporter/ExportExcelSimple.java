package lifelines.matrix.Exporter;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.molgenis.framework.db.Database;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import lifelines.loaders.LoaderUtils;
import lifelines.matrix.Column;
import lifelines.matrix.ColumnUtils;
import lifelines.matrix.PagableMatrix;

public class ExportExcelSimple {
	/**
	 * The corresponding variables are the columns that are selected by the user
	 * in the UI , this is returned by getColNames() , and the data of this
	 * matrix are returned by the matrix.getData(); for example if the user has
	 * selected in the matrix viewer : PA_ID , ztgewicht and gewicht the
	 * exported SPSS file should contain : PA_ID 1 2 3 4 3 7 ztgewicht 1 2 3 4 3
	 * 7 gewicht 1 2 3 4 3 7
	 * 
	 * @param matrix
	 * @param os
	 * @throws IOException
	 * @throws Exception
	 */

	public static void export(List<Column> columns, OutputStream os, Database db, String query)
			throws IOException, Exception {
		/* Create new Excel workbook and sheet */
		WorkbookSettings ws = new WorkbookSettings();
		ws.setLocale(new Locale("en", "EN"));
		WritableWorkbook workbook = Workbook.createWorkbook(os);
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
		for (int i = 0; i < columns.size(); i++) {
			Label l = new Label(i, 0, columns.get(i).getName(),
					headerFormat);
			s.addCell(l);
		}

		Connection conn = ((app.JpaDatabase) db).createJDBCConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		// //Should use target Id's
		// /* Write row headers */
		// for (int i = 0; i < elements.length; i++) {
		// Label l = new Label(0, i + 1, "" + i, headerFormat);
		// s.addCell(l);
		// }

		int row = 1;
		while (rs.next()) {
			for (int column = 0; column < columns.size(); column++) {
				WritableCell cellValue = null;
				Object dataElement = rs.getObject(column + 1);
				Column.ColumnType columnType = columns.get(column).getType();

				if (dataElement == null) {
					cellValue = new Blank(column, row);
				} else {
					if (columnType.equals(Column.ColumnType.String)) {
						cellValue = new Label(column, row,
								dataElement.toString());
					} else if (columnType.equals(Column.ColumnType.Integer)) {
						cellValue = new jxl.write.Number(column, row,
								Double.parseDouble(dataElement.toString()));
					} else if (columnType.equals(Column.ColumnType.Code)) {
						cellValue = new jxl.write.Label(column, row,
								dataElement.toString());
					} else if (columnType.equals(Column.ColumnType.Decimal)) {
						cellValue = new jxl.write.Number(column, row,
								Double.parseDouble(dataElement.toString()));
					} else if (columnType.equals(Column.ColumnType.Timestamp)) {
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"y/M/d H:m:s");
						cellValue = new jxl.write.DateTime(column, row,
								dateFormat.parse(dataElement.toString()));
					} else {
						throw new UnsupportedOperationException(String.format(
								"Type %s not available", columnType));
					}
				}
				s.addCell(cellValue);
			}
			++row;
			if(row == 20)
				break;
		}

		/* Close connection and workbook */
		rs.close();
		conn.close();

		/* Close workbook */
		workbook.write();
		workbook.close();
	}	

	public static String getContentType() {
		return "application/vnd.ms-excel";
	}
	
	public static String getFileExtenstion() {
		return "xls"; 
	}	
}
