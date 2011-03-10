<#include "GeneratorHelper.ftl">
<#setting number_format="#"/>
<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* File:        app/JUnitTest.java
 * Copyright:   GBIC 2000-${year?c}, all rights reserved
 * Date:        ${date}
 * 
 * generator:   ${generator} ${version}
 *
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package ${package};

<#if databaseImp != 'jpa'>	
import app.JDBCDatabase;
<#else>
import javax.persistence.*;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.framework.db.jpa.JpaUtil;
</#if>

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import org.molgenis.Molgenis;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.DatabaseException;

import static  org.testng.AssertJUnit.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

<#list model.entities as entity>
import ${entity.namespace}.${JavaName(entity)};
</#list>

public class TestDatabase
{
	private static int total = 10;
	private static Database db;
	public static final transient Logger logger = Logger.getLogger(TestDatabase.class);
	DateFormat dateFormat = new SimpleDateFormat(SimpleTuple.DATEFORMAT, Locale.US);
	DateFormat dateTimeFormat = new SimpleDateFormat(SimpleTuple.DATETIMEFORMAT, Locale.US);	 

	/*
	 * Create a database to use
	 */
	@BeforeClass
	public static void oneTimeSetUp()   
	{
		try
		{		
		<#if databaseImp = 'jpa'>		
			db = new app.JpaDatabase(true);
			((JpaDatabase)db).getEntityManager().setFlushMode(FlushModeType.AUTO);
		<#else>
		db = new JDBCDatabase("molgenis.test.properties");	
			//create the database
			new Molgenis("molgenis.test.properties").updateDb();
			//get it
			db = new JDBCDatabase("molgenis.test.properties");
		</#if>			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		logger.info("Database created");
	}
		
		
<#list entities as entity><#if !entity.abstract && !entity.association>
<#assign dependson = entity.getDependencies()/>
<#if dependson?size &gt; 0>
	@Test(dependsOnMethods = {<#list dependson as d><#if d_index &gt; 0>,</#if>"test${JavaName(d)}"</#list>})
<#else>
	@Test
</#if>
	<#assign dependson = "test" + JavaName(entity)/>
	public void test${JavaName(entity)}() throws DatabaseException, IOException, ParseException
	{
		//create entities
		List<${JavaName(entity)}> entities = new ArrayList<${JavaName(entity)}>();

		//retrieve xref entity candidates
<#list entity.allFields as f><#if !f.auto>
	<#if f.type == "xref" || f.type == "mref">
		List<${JavaName(f.xrefEntity)}> ${name(f)}Xrefs = db.query(${JavaName(f.xrefEntity)}.class)<#if f.xrefEntity.hasAncestor()>.eq("__Type",${JavaName(f.xrefEntity)}.class.getSimpleName())</#if>.find();	
	</#if></#if>
</#list>		

		for(Integer i = 0; i < total; i++)
		{
			${JavaName(entity)} e = new ${JavaName(entity)}();
			<#list entity.allFields as f><#if !f.auto>
			<#if f.type == "xref">
			if(${name(f)}Xrefs.size() > 0) e.set${JavaName(f)}_${JavaName(f.xrefField)}( ${name(f)}Xrefs.get(i).get${JavaName(f.xrefField)}() );
			<#elseif f.type == "mref">
			if(${name(f)}Xrefs.size() > 0)
			{
				e.get${JavaName(f)}_${JavaName(f.xrefField)}().add( ${name(f)}Xrefs.get(i).get${JavaName(f.xrefField)}() );
				//e.get${JavaName(f)}().add( random(${name(f)}Xrefs).get${JavaName(f.xrefField)}() );
			}
			<#elseif f.type=="bool">
			e.set${JavaName(f)}(randomBool(i));
			<#elseif f.type=="date">
			e.set${JavaName(f)}(new java.sql.Date(new java.util.Date().getTime()));
			<#elseif f.type=="datetime">
			e.set${JavaName(f)}(new java.sql.Timestamp(new java.util.Date().getTime()));
			<#elseif f.type=="enum">
			e.set${JavaName(f)}(randomEnum(new String[]{<#list f.enumOptions as option><#if option_index &gt; 0>,</#if>"${option}"</#list>}));
			<#elseif f.type=="decimal">
			e.set${JavaName(f)}(i.doubleValue());
			<#elseif f.type == "int">
			e.set${JavaName(f)}(i);
			<#elseif f.type == "long">
			e.set${JavaName(f)}(i.longValue());
			<#elseif f.type == "string">
			e.set${JavaName(f)}(truncate("${entity.name?lower_case}_${f.name?lower_case}_"+i, ${f.length?c}));
			<#else>
			e.set${JavaName(f)}("${entity.name?lower_case}_${f.name?lower_case}_"+i);
			</#if></#if></#list>	
				
			entities.add(e);
		}
		
		//add entities and check counts
		db.add(entities);
		Query<${JavaName(entity)}> q = db.query(${JavaName(entity)}.class)<#if entity.hasAncestor() || entity.hasDescendants()>.eq("${typefield()}",${JavaName(entity)}.class.getSimpleName())</#if>;
		assertEquals(total, q.count());
		List<${JavaName(entity)}> entitiesDb = q.sortASC("${pkey(entity).name}").find();
		assertEquals(total, entitiesDb.size());
		
		//compare entities against insert (assumes sorting by id)
		for(int i = 0; i < total; i++)
		{
			assertNotNull(entities.get(i).get${JavaName(pkey(entity))}());
<#list entity.allFields as f><#if pkey(entity).name != f.name && !f.auto><#if f.type == "date">
			//check formatted because of milliseconds rounding
			assertEquals(dateFormat.format(entities.get(i).get${JavaName(f)}()), dateFormat.format(entitiesDb.get(i).get${JavaName(f)}()));
<#elseif f.type == "datetime">
			//check formatted because of milliseconds rounding
			assertEquals(dateTimeFormat.format(entities.get(i).get${JavaName(f)}()),dateTimeFormat.format(entitiesDb.get(i).get${JavaName(f)}()));
<#else>
			assertEquals(entities.get(i).get${JavaName(f)}(), entitiesDb.get(i).get${JavaName(f)}());
</#if>
</#if></#list>		
		}	
		
		//test the query capabilities by finding on all fields
		for(${JavaName(entity)} entity: entitiesDb)
		{
<#list entity.allFields as f><#if f.type == "int">		
			//test field '${f.name}'
			{
				Query<${JavaName(entity)}> q2 = db.query(${JavaName(entity)}.class);
				q2.equals("${name(f)}",entity.get${JavaName(f)}());
				List<${JavaName(entity)}> results = q2.find();
<#if pkey(entity) == f>
				assertEquals(results.size(),1);
</#if>			
				for(${JavaName(entity)} r: results)
				{
					assertEquals(r.get${JavaName(f)}(),entity.get${JavaName(f)}());
				}
			}
</#if></#list>
		}
	}

</#if></#list>	
	
	/** Helper to get random element from a list */
	public <E extends Entity> E random(List<E> entities)
	{
		return entities.get( Long.valueOf( Math.round( Math.random() * (entities.size() - 1) )).intValue() );
	}
	
	public Boolean randomBool(int i)
	{
		return i % 2 == 0 ? true : false;
	}
	
	public String randomEnum(String[] options)
	{
		Integer index = Long.valueOf(Math.round(Math.random() * (options.length - 1) )).intValue();
		return options[index];
	}
	
	public String truncate(String value, int length)
	{
	   if (value != null && value.length() > length)
          value = value.substring(0, length-1);
       return value;
	}
	
	/*
	 * Cleanup all database stuff
	 */
	 @AfterClass
     public static void oneTimeTearDown() throws DatabaseException, IOException, ParseException
	 {
<#--list entities?reverse as entity><#if !entity.abstract>
		db.remove(db.query(${JavaName(entity)}.class).find());
</#if></#list-->	 
	 }
	 
	 
}