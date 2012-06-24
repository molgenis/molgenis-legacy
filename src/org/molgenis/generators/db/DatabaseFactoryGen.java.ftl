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
import java.util.Map;
import javax.sql.DataSource;
import java.sql.Connection;
import org.molgenis.MolgenisOptions;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.DataSourceWrapper;


public class DatabaseFactory
{

<#if databaseImp == "jdbc">
		@Deprecated
        public static Database createInsecure(DataSource data_src, File file_src) throws DatabaseException {
            try {
                return new app.JDBCDatabase(data_src, file_src);
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
        }
       
        @Deprecated
        public static Database createInsecure() throws DatabaseException {

            try {
                return new app.JDBCDatabase();
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
        }        

	@Deprecated
	public static Database create(DataSource data_src, File file_source) throws DatabaseException
	{
            try {
                return new ${package}.JDBCDatabase(data_src, file_source);            
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
	}

	@Deprecated
	public static Database create(DataSourceWrapper data_src, File file_src) throws DatabaseException
	{
            try {
                return new ${package}.JDBCDatabase(data_src, file_src);            
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
	}

	@Deprecated
	public static Database create(Properties p) throws DatabaseException
	{
            try {
                return new ${package}.JDBCDatabase(p);            
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
	}

	public static Database create(MolgenisOptions options) throws DatabaseException
	{
            try {
                return new ${package}.JDBCDatabase(options);            
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
	}
	
	public static Database create() throws DatabaseException
	{
            try {
                return new ${package}.JDBCDatabase();            
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
	}

	@Deprecated
	public static Database create(boolean test) throws DatabaseException
	{
            try {
                return new ${package}.JDBCDatabase();            
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
	}  
	
	@Deprecated
	private static Database create(String propertiesFilePath, boolean test) throws DatabaseException
	{
            try {
            	if(test) {
                	new org.molgenis.Molgenis(propertiesFilePath).updateDb(false);
            	} 
            	return new ${package}.JDBCDatabase(propertiesFilePath);
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
	}
	     
</#if>	

<#if databaseImp == "jpa">
	public static Database create(MolgenisOptions options) throws DatabaseException
	{
            return new app.JpaDatabase(options);
	}
	
	public static Database create() throws DatabaseException
	{
            return new app.JpaDatabase();
	}     

	public static Database create(String propertiesFilePath) throws DatabaseException
	{
        return new app.JpaDatabase(propertiesFilePath);
    }

	public static Database create(Map<String, Object> configOverrides) throws DatabaseException {
		return new app.JpaDatabase(configOverrides);
	}
</#if>


}