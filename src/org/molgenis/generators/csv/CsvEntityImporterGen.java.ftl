<#--helper functions-->
<#include "GeneratorHelper.ftl">

<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* Date:        ${date}
 * 
 * generator:   ${generator} ${version}
 *
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package ${package};

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.molgenis.auth.db.MolgenisGroupEntityImporter;
import org.molgenis.auth.db.MolgenisPermissionEntityImporter;
import org.molgenis.auth.db.MolgenisRoleEntityImporter;
import org.molgenis.auth.db.MolgenisRoleGroupLinkEntityImporter;
import org.molgenis.auth.db.MolgenisUserEntityImporter;
import org.molgenis.core.db.MolgenisEntityEntityImporter;
import org.molgenis.core.db.MolgenisFileEntityImporter;
import org.molgenis.core.db.RuntimePropertyEntityImporter;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.EntityImportReport;
import org.molgenis.framework.db.EntityImporter;
import org.molgenis.framework.db.CsvEntityImporter;
import org.molgenis.io.csv.CsvReader;

<#list entities as entity><#if !entity.abstract>
import ${entity.namespace}.db.${JavaName(entity)}EntityImporter;
</#if></#list>

public class CsvEntityImporterImpl implements CsvEntityImporter
{
    /** importable entity names (lowercase) */
	private static final Map<String, EntityImporter> ENTITIES_IMPORTABLE;
	
	static {
		// entities added in import order
		ENTITIES_IMPORTABLE = new LinkedHashMap<String, EntityImporter>();
	<#list entities as entity><#if !entity.abstract>
		ENTITIES_IMPORTABLE.put("${entity.name?lower_case}", new ${JavaName(entity)}EntityImporter());
	</#if></#list>
	}
	
	private final Database db;
	
	public CsvEntityImporterImpl(Database db) {
		if(db == null) throw new IllegalArgumentException("db is null");
		this.db = db;
	}
	
	public int importData(Reader reader, String entityName, Database db, DatabaseAction dbAction) throws IOException,
			DatabaseException
	{
		EntityImporter entityImporter = ENTITIES_IMPORTABLE.get(entityName.toLowerCase());
		if (entityImporter == null) throw new IllegalArgumentException("unknown entity: " + entityName);

		CsvReader csvReader = new CsvReader(reader);
		int nrImportedEntities = 0;

		boolean doTx = !db.inTx();
		try
		{
			if (doTx) db.beginTx();
			nrImportedEntities = entityImporter.importData(csvReader, db, dbAction);
			if (doTx) db.commitTx();
		}
		catch (IOException e)
		{
			if (doTx) db.rollbackTx();
			throw e;
		}
		catch (DatabaseException e)
		{
			if (doTx) db.rollbackTx();
			throw e;
		}
		finally
		{

			reader.close();
		}
		return nrImportedEntities;
	}
}