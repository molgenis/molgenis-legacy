<#include "GeneratorHelper.ftl">
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

<#if databaseImp = 'jpa'>
import app.JpaDatabase;
import org.molgenis.framework.db.jpa.JpaMapper;
<#else>	
import app.JDBCDatabase;
</#if>

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
<#if !entity.isAbstract()>
	<#if entity.decorator?exists >
import ${entity.decorator};		
        <#else>
import ${entity.namespace}.db.${JavaName(entity)}<#if databaseImp = 'jpa'>Jpa</#if>Mapper;
	</#if>
</#if>
import ${entity.namespace}.${JavaName(entity)};
</#list>


/**
 * This class produces a random data set
 */
public class TestDataSet
{
	//private static Database db;
	public static final transient Logger logger = Logger.getLogger(TestCsv.class);
	DateFormat dateFormat = new SimpleDateFormat(SimpleTuple.DATEFORMAT, Locale.US);
	DateFormat dateTimeFormat = new SimpleDateFormat(SimpleTuple.DATETIMEFORMAT, Locale.US);	 
	
	/**
	 * An empty set
	 */	
	public TestDataSet()
	{	
	}
		
	
        <#if databaseImp = 'jpa'>
            public TestDataSet(int size, int mrefSize, app.JpaDatabase db)  
            throws org.molgenis.framework.db.DatabaseException
        <#else>
            public TestDataSet(int size, int mrefSize)  
        </#if>
	{
		<#list entities as entity><#if !entity.abstract && !entity.system && !entity.association>
		//generating ${entity.name} data:
		for(Integer i = 0; i < size; i++)
		{
			${JavaName(entity)} e = new ${JavaName(entity)}();
			<#list entity.allFields as f><#if !f.auto>
			//assign field ${f.name}
			<#if f.type == "xref">
			if( this.${name(f.xrefEntity)}.size() > 0  && i < this.${name(f.xrefEntity)}.size())
			{ 
				${JavaName(f.xrefEntity)} ref = this.${name(f.xrefEntity)}.get(i);
				<#list f.xrefLabelNames as label>
				e.set${JavaName(f)}_${JavaName(label)}(ref.get${JavaName(label)}() );
				</#list>
			}
			<#elseif f.type == "mref">
			if( this.${name(f.xrefEntity)}.size() > 0)
			{
				//get a set of unique entity indexes
				Set<Integer> indexes = new LinkedHashSet<Integer>();
				for(int j = 0; j < mrefSize; j++)
				{	
					indexes.add(j < this.${name(f.xrefEntity)}.size() ? j : this.${name(f.xrefEntity)}.size()-1);
				}
				<#list f.xrefLabelNames as label><#--FIXME not alway string-->
				List<${type(f.xrefLabels[label_index])}> ${label}List = new ArrayList<${type(f.xrefLabels[label_index])}>();
				</#list>
				for(Integer index: indexes)
				{
					<#list f.xrefLabelNames as label>
					${label}List.add( this.${name(f.xrefEntity)}.get(index).get${JavaName(label)}() );
					</#list>
				}
				<#list f.xrefLabelNames as label><#if label != pkey(f.xrefEntity).name >
				e.set${JavaName(f)}_${JavaName(label)}( ${label}List );
				</#if></#list>
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
			e.set${JavaName(f)}("${entity.name?lower_case}_${f.name?lower_case}"+i);
			</#if></#if></#list>
			this.${name(entity)}.add(e);
		}



<#if databaseImp = 'jpa'>

	<#if entity.decorator?exists >
            ${entity.decorator} ${name(entity)}Save = (${entity.decorator})db
	<#else> 
			JpaMapper ${name(entity)}Save = (JpaMapper)db
	</#if>
		.getMapper("${JavaName(entity)}");	
        
            //((JpaDatabase)db).getEntityManager().getTransaction().begin();
            db.add(this.${name(entity)});
            //((JpaDatabase)db).getEntityManager().getTransaction().commit();
</#if>		
		
		</#if></#list>
	}	 
	
	public String truncate(String value, int length)
	{
	   if (value != null && value.length() > length)
          value = value.substring(0, length-1);
       return value;
	}	
	 
	 /** Helper to get random element from a list */
	private int random(int max)
	{
		return new Long(Math.round(Math.floor( Math.random() * max ))).intValue();
	}
	
	private Boolean randomBool(int i)
	{
		return i % 2 == 0 ? true : false;
	}
	
	private String randomEnum(String[] options)
	{
		Integer index = Long.valueOf(Math.round(Math.random() * (options.length - 1) )).intValue();
		return options[index];
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!this.getClass().equals(other.getClass()))
			return false;
		TestDataSet set = (TestDataSet) other;
		
<#list model.entities as entity>
		if ( this.${name(entity)} == null ? set.${name(entity)} != null : !this.${name(entity)}.equals( set.${name(entity)} ) )
			return false;
</#list>
		
		return true;
	}
	
	@Override
 	public int hashCode() 
 	{ 
    	int hash = 1;
<#list model.entities as entity>
    	hash = hash * 31 + (this.${name(entity)} == null ? 0 : ${name(entity)}.hashCode());
</#list>
    	return hash;
  	}
	
<#list model.entities as entity>
	public List<${JavaName(entity)}> ${name(entity)} = new ArrayList<${JavaName(entity)}>();
</#list>
}