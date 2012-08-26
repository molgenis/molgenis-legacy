package plugins.archiveexportimport;

import java.io.File;
import java.io.IOException;
import java.util.List;

import matrix.AbstractDataMatrixInstance;
import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.util.TarGz;

public class XgapMatrixExport {

	public static void exportMatrix(List<Data> dataList, String investigationName, Database db, File directory) throws IllegalArgumentException, DatabaseException, IOException, Exception{
		
		DataMatrixHandler dmh = new DataMatrixHandler(db);
		
		// Data matrices
		if (dataList.size() > 0)
		{
			// null indicates all investigations, but only do this if there are
			// actually more than 1 investigation in the db
			if (investigationName == null && db.find(Investigation.class).size() > 1)
			{
				for (Data data : dataList)
				{
					//Investigation inv = db.find(Investigation.class, new QueryRule("id", Operator.EQUALS, data.getInvestigation())).get(0);
					
					//File dataDir = new File(directory + File.separator + NameConvention.escapeFileName(inv.getName())+ "_data");
					
					File dataDir = new File(directory + File.separator + "data");
					
					if (!dataDir.exists())
					{
						dataDir.mkdir();
					}
					
					
					DataMatrixInstance instance = dmh.createInstance(data, db);
					File matrixFile = instance.getAsFile();
					File newLoc = new File(dataDir.getAbsolutePath() + File.separator + matrixFile.getName());
//					boolean createDestSuccess = newLoc.createNewFile();
//					if(!createDestSuccess){
//						throw new Exception("Creation if destination file " + newLoc.getAbsolutePath() + " failed.");
//					}
					TarGz.fileCopy(matrixFile, newLoc, false);
					
					//File f = new File(dataDir.getAbsolutePath() + File.separator + data.getName() + ".txt");
					//PrintWriter out = new PrintWriter(new FileOutputStream(f));
					//MatrixViewPlugin.downloadAll(db, data, BATCH_SIZE, out);

				}
			}
			else
			{
				File dataDir = new File(directory + File.separator + "data");

				if (!dataDir.exists())
				{
					dataDir.mkdir();
				}

				for (Data data : dataList)
				{
					
					DataMatrixInstance instance = dmh.createInstance(data, db);
					File matrixFile = instance.getAsFile();
					File newLoc = new File(dataDir.getAbsolutePath() + 
							File.separator + matrixFile.getName());
					TarGz.fileCopy(matrixFile, newLoc, false);
					
//					File f = new File(dataDir + File.separator + data.getName() + ".txt");
//					PrintWriter out = new PrintWriter(new FileOutputStream(f));
//					MatrixViewPlugin.downloadAll(db, data, BATCH_SIZE, out);

				}
			}

		}
	}
	
}
