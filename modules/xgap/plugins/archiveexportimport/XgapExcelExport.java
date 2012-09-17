package plugins.archiveexportimport;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;

public class XgapExcelExport extends ExcelWriterNoExportDataElement
{

	static int BATCH_SIZE = 10000;

	public XgapExcelExport(File directory, Database db) throws Exception
	{
		xgapExcelExportAll(directory, db, null);
	}

	public XgapExcelExport(File directory, Database db, String investigationName) throws Exception
	{
		xgapExcelExportAll(directory, db, investigationName);
	}

	public void xgapExcelExportAll(File directory, Database db, String investigationName) throws Exception
	{
		if (!directory.exists())
		{
			directory.mkdir();
		}
		else
		{
			FileUtils.cleanDirectory(directory);
		}
		
		File excelFile = new File(directory.getAbsolutePath() + File.separator + "xgap.xls");

		// Annotations
		List<Data> dataList;
		if (investigationName == null)
		{
			//checkIfEscapedInvestigationNamesAreAmbiguous(db);
			super.exportAll(excelFile, db, true);
			dataList = db.find(Data.class);
		}
		else
		{
			QueryRule investigationNameRule = new QueryRule("Investigation.name", Operator.EQUALS, investigationName);
			Investigation inv = db.find(Investigation.class, investigationNameRule).get(0);
			QueryRule investigationRefRule = new QueryRule("investigation", Operator.EQUALS, inv.getId());
			super.exportAll(excelFile, db, true, investigationRefRule, investigationNameRule);
			dataList = db.find(Data.class, investigationRefRule);
		}

		XgapMatrixExport.exportMatrix(dataList, investigationName, db, directory);
		
	}

}