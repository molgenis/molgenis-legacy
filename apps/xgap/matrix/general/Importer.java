package matrix.general;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import matrix.implementations.binary.BinaryDataMatrixWriter;
import matrix.implementations.csv.CSVDataMatrixWriter;
import matrix.implementations.database.DatabaseDataMatrixWriter;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.Tuple;

public class Importer
{

	public static void performImport(Tuple request, Data data, Database db) throws Exception
	{

		File importFile = null;

		if (request.getString("__action").equals("uploadTextArea"))
		{
			String content = request.getString("inputTextArea");
			File inputTextAreaContent = new File(System.getProperty("java.io.tmpdir") + File.separator
					+ "tmpTextAreaInput" + System.nanoTime() + ".txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(inputTextAreaContent));
			out.write(content);
			out.close();
			importFile = inputTextAreaContent;
		}
		else
		{
			importFile = request.getFile("upload");
		}

		// apply preprocessing if called for
		PreProcessMatrix pm = null;
		if (request.getString("prependToRows") != null || request.getString("prependToCols") != null
				|| request.getString("escapeRows") != null || request.getString("escapeCols") != null
				|| request.getString("trimTextElements") != null)
		{
			pm = new PreProcessMatrix(importFile);
		}
		if (request.getString("prependToRows") != null)
		{
			System.out.println("prependToRows");
			pm.prependUnderscoreToRowNames();
		}
		if (request.getString("prependToCols") != null)
		{
			System.out.println("prependToCols");
			pm.prependUnderscoreToColNames();
		}
		if (request.getString("escapeRows") != null)
		{
			System.out.println("escapeRows");
			pm.escapeRowNames();
		}
		if (request.getString("escapeCols") != null)
		{
			System.out.println("escapeCols");
			pm.escapeColNames();
		}
		if (request.getString("trimTextElements") != null)
		{
			System.out.println("trimTextElements");
			pm.trimTextElements();
		}

		if (request.getString("prependToRows") != null || request.getString("prependToCols") != null
				|| request.getString("escapeRows") != null || request.getString("escapeCols") != null
				|| request.getString("trimTextElements") != null)
		{
			File result = pm.getResult();
			if (!result.exists())
			{
				throw new Exception("Import file '" + result.getAbsolutePath() + " does not exist");
			}
			performImport(result, data, db);
		}
		else
		{
			if (!importFile.exists())
			{
				throw new Exception("Import file '" + importFile.getAbsolutePath() + " does not exist");
			}
			performImport(importFile, data, db);
		}
	}

	public static void performImport(File uploadedFile, Data data, Database db) throws Exception
	{

		DataMatrixHandler dmh = new DataMatrixHandler(db);

		// check if uploaded file is there
		if (uploadedFile == null || !uploadedFile.exists())
		{
			throw new DatabaseException("No file selected for upload.");
		}

		// check if the matrix elements exists in another source of data first
		if (data.getStorage().equals("Database"))
		{
			if (dmh.isDataMatrixStoredInDatabase(data, db))
			{
				throw new DatabaseException("Database source already exists for source type '" + data.getStorage()
						+ "'");
			}
		}
		else
		{
			if (dmh.findSourceFile(data, db) != null)
			{
				throw new DatabaseException("File source already exists for source type '" + data.getStorage() + "'");
			}
		}

		// do import
		if (data.getStorage().equals("Database"))
		{
			// new DatabaseDataMatrixWriter(data, uploadedFile, db, false);
			new DatabaseDataMatrixWriter(data, uploadedFile, db);
		}
		else if (data.getStorage().equals("Binary"))
		{
			new BinaryDataMatrixWriter(data, uploadedFile, db);
		}
		else if (data.getStorage().equals("CSV"))
		{
			new CSVDataMatrixWriter(data, uploadedFile, db);
		}
		else
		{
			throw new DatabaseException("Unknown matrix source: " + data.getStorage() + "");
		}

	}

}
