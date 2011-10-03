<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* File:        ${package}/DatabaseFactory
 * Copyright:   GBIC 2000-${year?c}, all rights reserved
 * Date:        ${date}
 * 
 * generator:   ${generator} ${version}
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package ${package};

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;

import org.molgenis.MolgenisOptions;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.DataSourceWrapper;


public class DatabaseFactory
{

<#if databaseImp == "jpa">
    private static Database createJpaDatabase(boolean test) throws DatabaseException {
        try {
            return new app.JpaDatabase(test); 
        } catch (Exception ex) {
            throw new DatabaseException(ex);
        }
    }   
    
    // ignore parameters, everything is declared in persistence.xml
    private static Database createJpaDatabase() throws DatabaseException {
        try {            
            return new app.JpaDatabase(); 
        } catch (Exception ex) {
            throw new DatabaseException(ex);
        }
    }       

    private static Database createJpaDatabase(String propertiesFilePath) throws DatabaseException {
        try {
            return new app.JpaDatabase(propertiesFilePath); 
        } catch (Exception ex) {
            throw new DatabaseException(ex);
        }
    }  
</#if>
        public static Database createInsecure(DataSource data_src, File file_src) throws DatabaseException {
<#if databaseImp == "jdbc">
            try {
                return new app.JDBCDatabase(data_src, file_src);
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
<#elseif databaseImp == "jpa">
            return createJpaDatabase();
<#else>
            throw new UnsupportedOperationException();
</#if>
        }
        
        public static Database createInsecure() throws DatabaseException {
<#if databaseImp == "jdbc">
            try {
                return new app.JDBCDatabase();
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
<#elseif databaseImp == "jpa">
            return createJpaDatabase();
<#else>
            throw new UnsupportedOperationException();
</#if>
        }        

	public static Database create(DataSource data_src, File file_source) throws DatabaseException
	{
<#if databaseImp == "jdbc">
            try {
                return new ${package}.JDBCDatabase(data_src, file_source);            
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
<#elseif databaseImp == "jpa">
            return createJpaDatabase();
<#else>
            throw new UnsupportedOperationException();
</#if>
	}

	public static Database create(DataSourceWrapper data_src, File file_src) throws DatabaseException
	{
<#if databaseImp == "jdbc">
            try {
                return new ${package}.JDBCDatabase(data_src, file_src);            
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
<#elseif databaseImp == "jpa">
            return createJpaDatabase();
<#else>
            throw new UnsupportedOperationException();
</#if>
	}

	public static Database create(Properties p) throws DatabaseException
	{
<#if databaseImp == "jdbc">
            try {
                return new ${package}.JDBCDatabase(p);            
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
<#elseif databaseImp == "jpa">
            return createJpaDatabase();
<#else>
            throw new UnsupportedOperationException();
</#if>
	}

	public static Database create(MolgenisOptions options) throws DatabaseException
	{
<#if databaseImp == "jdbc">
            try {
                return new ${package}.JDBCDatabase(options);            
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
<#elseif databaseImp == "jpa">
            return createJpaDatabase();
<#else>
            throw new UnsupportedOperationException();
</#if>
	}
	
	public static Database create() throws DatabaseException
	{
<#if databaseImp == "jdbc">
            try {
                return new ${package}.JDBCDatabase();            
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
<#elseif databaseImp == "jpa">
            return createJpaDatabase();
<#else>
            throw new UnsupportedOperationException();
</#if>
	}

	public static Database create(boolean test) throws DatabaseException
	{
<#if databaseImp == "jdbc">
            try {
                return new ${package}.JDBCDatabase();            
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
<#elseif databaseImp == "jpa">
            return createJpaDatabase(test);
<#else>
            throw new UnsupportedOperationException();
</#if>
	}       

	public static Database create(String propertiesFilePath) throws DatabaseException
	{
            return create(propertiesFilePath, false);
        }

        public static Database createTest() throws DatabaseException {
            return create(true);
        }

        public static Database createTest(String propertiesFilePath) throws DatabaseException {
            return create(propertiesFilePath, true);
        }

	private static Database create(String propertiesFilePath, boolean test) throws DatabaseException
	{
<#if databaseImp == "jdbc">
            try {
                return new ${package}.JDBCDatabase(propertiesFilePath);
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
<#elseif databaseImp == "jpa">
            return createJpaDatabase(propertiesFilePath);
<#else>
            throw new UnsupportedOperationException();
</#if>
	}


}