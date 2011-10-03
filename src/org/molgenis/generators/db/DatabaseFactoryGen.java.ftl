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
import org.molgenis.framework.security.Login;
import org.molgenis.framework.security.SimpleLogin;

public class DatabaseFactory
{
    private static class SecurityFactory {
        public static Login create() throws Exception {
            return create(false);
        }
        
        public static Login create(boolean test) throws Exception {
            if(test) {
                return new SimpleLogin();
            } else {
                Login login = ${auth_loginclass}.class.newInstance();
               	<#if auth_redirect??>
               	login.setRedirect("${auth_redirect}");
               	</#if>
                return login;
            }
        }
        
        public static Login create(Class<? extends  Login> loginClass) throws Exception {
            Login login = loginClass.newInstance();
           	<#if auth_redirect??>
           	login.setRedirect("${auth_redirect}");
           	</#if>
            return login;
        }
    }



<#if databaseImp == "jpa">
    private static Database createJpaDatabase(boolean test) throws DatabaseException {
        try {
            return new app.JpaDatabase(test, SecurityFactory.create(test)); 
        } catch (Exception ex) {
            throw new DatabaseException(ex);
        }
    }   
    
    // ignore parameters, everything is declared in persistence.xml
    private static Database createJpaDatabase() throws DatabaseException {
        try {            
            return new app.JpaDatabase(SecurityFactory.create()); 
        } catch (Exception ex) {
            throw new DatabaseException(ex);
        }
    }       

    private static Database createJpaDatabase(String propertiesFilePath) throws DatabaseException {
        try {
            return new app.JpaDatabase(propertiesFilePath, SecurityFactory.create()); 
        } catch (Exception ex) {
            throw new DatabaseException(ex);
        }
    } 

    private static Database createJpaDatabase(Class<? extends Login> loginClass) throws DatabaseException {
        try {
            return new app.JpaDatabase(false, SecurityFactory.create(loginClass)); 
        } catch (Exception ex) {
            throw new DatabaseException(ex);
        }
    }   
</#if>
        public static Database createInsecure(DataSource data_src, File file_src) throws DatabaseException {
<#if databaseImp == "jdbc">
            try {
                return new app.JDBCDatabase(data_src, file_src, SecurityFactory.create(SimpleLogin.class));
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
<#elseif databaseImp == "jpa">
            return createJpaDatabase(SimpleLogin.class);
<#else>
            throw new UnsupportedOperationException();
</#if>
        }
        
        public static Database createInsecure() throws DatabaseException {
<#if databaseImp == "jdbc">
            try {
                return new app.JDBCDatabase(SecurityFactory.create(SimpleLogin.class));
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
<#elseif databaseImp == "jpa">
            return createJpaDatabase(SimpleLogin.class);
<#else>
            throw new UnsupportedOperationException();
</#if>
        }        

	public static Database create(DataSource data_src, File file_source) throws DatabaseException
	{
<#if databaseImp == "jdbc">
            try {
                return new ${package}.JDBCDatabase(data_src, file_source, SecurityFactory.create());            
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
                return new ${package}.JDBCDatabase(data_src, file_src, SecurityFactory.create());            
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
                return new ${package}.JDBCDatabase(p, SecurityFactory.create());            
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
                return new ${package}.JDBCDatabase(options, SecurityFactory.create());            
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
                return new ${package}.JDBCDatabase(SecurityFactory.create());            
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
                return new ${package}.JDBCDatabase(SecurityFactory.create());            
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
                return new ${package}.JDBCDatabase(propertiesFilePath, SecurityFactory.create(test));
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
<#elseif databaseImp == "jpa">
            return createJpaDatabase(propertiesFilePath);
<#else>
            throw new UnsupportedOperationException();
</#if>
	}

        public static Database create(Class<? extends Login> loginClass) throws DatabaseException{
<#if databaseImp == "jdbc">
            try {
                return new ${package}.JDBCDatabase(SecurityFactory.create(loginClass));            
            } catch (Exception ex) {
                throw new DatabaseException(ex);
            }
<#elseif databaseImp == "jpa">
            return createJpaDatabase(loginClass);
<#else>
            throw new UnsupportedOperationException();
</#if>
        }
}