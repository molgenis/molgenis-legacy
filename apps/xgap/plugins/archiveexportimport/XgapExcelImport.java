package plugins.archiveexportimport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.molgenis.framework.db.Database;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import app.ExcelImport;

public class XgapExcelImport
{

	public XgapExcelImport(File extractDir, Database db, boolean skipWhenDestExists) throws Exception
	{

		File excelFile = null;
		File dataDir = null;

		// at this point we are sure that:
		// 1) extractDir has 1 or 2 file objects
		// 2) one of these ends with '.xls'
		// 3) if two: one of these is called 'data'

		for (File f : extractDir.listFiles())
		{
			if (f.getName().endsWith(".xls"))
			{
				excelFile = f;
			}
			if (f.getName().equals("data"))
			{
				// FIXME: test on all OS. Does this fix another Windows File
				// bug? does it break on other OS'es?
				dataDir = new File(f.getAbsolutePath());
			}
		}

		db.beginTx();

		try
		{
			ExcelImport.importAll(excelFile, db, new SimpleTuple(), false);

			if (dataDir != null)
			{
				List<String> investigationNames = getInvestigationNameFromExcel(excelFile);
				XgapCommonImport.importMatrices(investigationNames, db, false, dataDir, skipWhenDestExists);
			}

			db.commitTx();
		}
		catch (Exception e)
		{
			db.rollbackTx();
			throw(e);
		}

	}

	private List<String> getInvestigationNameFromExcel(File excelFile) throws Exception
	{

		File tmpInvestigation = new File(System.getProperty("java.io.tmpdir") + File.separator + "tmpInvestigation.txt");
		if (tmpInvestigation.exists())
		{
			boolean deleteSuccess = tmpInvestigation.delete();
			if (!deleteSuccess)
			{
				throw new Exception("Deletion of tmp file 'tmpInvestigation.txt' failed, cannot proceed.");
			}
		}
		boolean createSuccess = tmpInvestigation.createNewFile();
		if (!createSuccess)
		{
			throw new Exception("Creation of tmp file 'tmpInvestigation.txt' failed, cannot proceed.");
		}

		Workbook workbook = Workbook.getWorkbook(excelFile);
		for (Sheet sheet : workbook.getSheets())
		{
			if (sheet.getName().toLowerCase().equals("investigation"))
			{
				writeSheetToFile(sheet, tmpInvestigation);
			}
		}

		List<String> names = XgapCommonImport.getInvestigationNameFromFile(tmpInvestigation);

		return names;
	}

	/** NOTE: Copied from InvestigationExcelReader */
	private void writeSheetToFile(Sheet sheet, File file) throws FileNotFoundException
	{
		List<String> headers = new ArrayList<String>();
		Cell[] headerCells = sheet.getRow(0); // assume headers are on first
												// line
		ArrayList<Integer> namelessHeaderLocations = new ArrayList<Integer>(); // allow
																				// for
																				// empty
																				// columns,
																				// also
																				// column
																				// order
																				// does
																				// not
																				// matter
		for (int i = 0; i < headerCells.length; i++)
		{
			if (!headerCells[i].getContents().equals(""))
			{
				headers.add(headerCells[i].getContents());
			}
			else
			{
				headers.add("nameless" + i);
				namelessHeaderLocations.add(i);
			}
		}
		PrintWriter pw = new PrintWriter(file);
		CsvWriter cw = new CsvWriter(pw, headers);
		cw.setMissingValue("");
		cw.writeHeader();
		for (int rowIndex = 1; rowIndex < sheet.getRows(); rowIndex++)
		{
			Tuple t = new SimpleTuple();
			int colIndex = 0;
			for (Cell c : sheet.getRow(rowIndex))
			{
				if (!namelessHeaderLocations.contains(colIndex))
				{
					t.set(headers.get(colIndex), c.getContents());
				}
				colIndex++;
			}
			cw.writeRow(t);
		}
		cw.close();
	}

}
