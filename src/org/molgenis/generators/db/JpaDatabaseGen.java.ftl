<#include "GeneratorHelper.ftl">

package app;

import java.util.Map;

import org.molgenis.MolgenisOptions;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

public class JpaDatabase extends org.molgenis.framework.db.jpa.JpaDatabase
{
	public JpaDatabase(Map<String, Object> configOverrides) throws DatabaseException
	{
		super(new JDBCMetaDatabase(), configOverrides);
	}

	public JpaDatabase(MolgenisOptions options) throws DatabaseException
	{
		super(new JDBCMetaDatabase(), options);
	}

	public JpaDatabase() throws DatabaseException
	{
		super(new JDBCMetaDatabase());
	}

	public JpaDatabase(String propertiesFilePath) throws DatabaseException
	{
		super(new JDBCMetaDatabase(), propertiesFilePath);
	}
	
	public void initMappers(Database db)
	{
		<#list model.entities as entity><#if !entity.isAbstract()>
			<#if disable_decorators>
				this.putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.namespace}.db.${JavaName(entity)}JpaMapper(db));			
			<#elseif entity.decorator?exists>
				<#if auth_loginclass?ends_with("SimpleLogin")>
		this.putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.decorator}(new ${entity.namespace}.db.${JavaName(entity)}JpaMapper(db)));
				<#else>
		this.putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.decorator}(new ${entity.namespace}.db.${JavaName(entity)}SecurityDecorator(new ${entity.namespace}.db.${JavaName(entity)}JpaMapper(db))));
				</#if>	
			<#else>
				<#if auth_loginclass?ends_with("SimpleLogin")>
		this.putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.namespace}.db.${JavaName(entity)}JpaMapper(db));
				<#else>
		this.putMapper(${entity.namespace}.${JavaName(entity)}.class, new ${entity.namespace}.db.${JavaName(entity)}SecurityDecorator(new ${entity.namespace}.db.${JavaName(entity)}JpaMapper(db)));
				</#if>
			</#if>
		</#if></#list>	
	}
}
