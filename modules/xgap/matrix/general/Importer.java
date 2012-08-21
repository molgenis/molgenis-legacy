package matrix.general;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import matrix.implementations.binary.BinaryDataMatrixWriter;
import matrix.implementations.csv.CSVDataMatrixWriter;
import matrix.implementations.database.DatabaseDataMatrixWriter;

import org.molgenis.core.MolgenisFile;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.util.Tuple;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jpa.JpaDatabase;

import decorators.NameConvention;

import java.util.HashMap;
import filehandling.generic.PerformUpload;
import matrix.implementations.binary.BinaryDataMatrixInstance;

public class Importer
{

	public static void performImport(Tuple request, Data data, Database db) throws Exception
	{

		File importFile = null;
		
		//special: upload existing xQTL binary matrix
		if (request.getString("__action").equals("uploadBinary"))
		{
			importFile = request.getFile("uploadBinaryFile");
			
			//update this 'Data' with the info from the binary file
			// but not name/investigationname
			// additionally, set storage to Binary :)
			BinaryDataMatrixInstance bmi = new BinaryDataMatrixInstance(importFile);
			data.setFeatureType(bmi.getData().getFeatureType());
			data.setTargetType(bmi.getData().getTargetType());
			data.setValueType(bmi.getData().getValueType());
			data.setStorage("Binary");
			
			db.update(data);
			
			// code from /molgenis_apps/modules/xgap/matrix/implementations/binary/BinaryDataMatrixWriter.java
			// upload as a MolgenisFile, type 'BinaryDataMatrix'
            HashMap<String, String> extraFields = new HashMap<String, String>();
            extraFields.put("data_" + Data.ID, data.getId().toString());
            extraFields.put("data_" + Data.NAME, data.getName());
			
			PerformUpload.doUpload(db, true, data.getName()+".bin", "BinaryDataMatrix", importFile, extraFields, false);
			
			return;
		}

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
			pm.prependUnderscoreToRowNames();
		}
		if (request.getString("prependToCols") != null)
		{
			pm.prependUnderscoreToColNames();
		}
		if (request.getString("escapeRows") != null)
		{
			pm.escapeRowNames();
		}
		if (request.getString("escapeCols") != null)
		{
			pm.escapeColNames();
		}
		if (request.getString("trimTextElements") != null)
		{
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
			if (importFile == null || !importFile.exists())
			{
				throw new Exception("No valid import file provided");
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
			boolean realFilePresent;
			try
			{
				String type = data.getStorage() + "DataMatrix";
				dmh.getFileDirectly(NameConvention.escapeFileName(data.getName()), dmh.getExtension(data.getStorage()), type, db);
				realFilePresent = true;
			}
			catch(FileNotFoundException fnfe)
			{
				realFilePresent = false;
			}
			
			
			// original check here: dmh.findSourceFile(data, db) != null which failed when there is a leftover MF but no actual file
			// so now there's this check instead
			if (realFilePresent)
			{
				throw new DatabaseException("There is already a storage file named '" +NameConvention.escapeFileName(data.getName()) +"' which is used when escaping the name '" + data.getName() + "'. Please rename your Data matrix or contact your admin.");
			}
			
			//no file present, remove the MF link if there is one
			//this is needed when you throw away the file on the file system 'without telling the database'
			//and subsequently try to upload a new matrix, because otherwise the names would clash
			MolgenisFile mf = dmh.findMolgenisFile(data, db);
			if(mf != null)
			{
				db.remove(mf);
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
