package plugins.archiveexportimport;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;

public class XgapCsvExport extends CsvWriterNoExportDataElement
{

	static int BATCH_SIZE = 10000;

	public XgapCsvExport(File directory, Database db) throws Exception
	{
		xgapCsvExportAll(directory, db, null);
	}

	public XgapCsvExport(File directory, Database db, String investigationName) throws Exception
	{
		xgapCsvExportAll(directory, db, investigationName);
	}

	public void xgapCsvExportAll(File directory, Database db, String investigationName) throws Exception
	{
		if (!directory.exists())
		{
			directory.mkdir();
		}
		else
		{
			FileUtils.cleanDirectory(directory);
		}
		
		//File excelFile = new File(directory.getAbsolutePath() + File.separator + "xgap.xls");

		// Annotations
		List<Data> dataList;
		if (investigationName == null)
		{
			//checkIfEscapedInvestigationNamesAreAmbiguous(db);
			super.exportAll(directory, db, true);
			dataList = db.find(Data.class);
		}
		else
		{
			QueryRule investigationNameRule = new QueryRule("Study.name", Operator.EQUALS, investigationName);
			Investigation inv = db.find(Investigation.class, investigationNameRule).get(0);
			QueryRule investigationRefRule = new QueryRule("study", Operator.EQUALS, inv.getId());
			super.exportAll(directory, db, true, investigationRefRule, investigationNameRule);
			dataList = db.find(Data.class, investigationRefRule);
		}

		XgapMatrixExport.exportMatrix(dataList, investigationName, db, directory);
		
	}

}