<#include "GeneratorHelper.ftl">
<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* File:        ${model.getName()}/model/${entity.getName()}.java
 * Copyright:   GBIC 2000-${year?c}, all rights reserved
 * Date:        ${date}
 * Template:	${template}
 * generator:   ${generator} ${version}
 *
 * Using "subclass per table" strategy (MultiQueryMapper)
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package ${package};

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jpa.JpaMapper;
import org.molgenis.framework.db.jdbc.ColumnInfo.Type;

<#list allFields(entity) as f><#if f.type == "file">
import org.apache.commons.io.FileUtils;
<#break>
</#if></#list>

<#list allFields(entity) as f><#if f.type == "mref" || f.type="xref">
import org.molgenis.util.ValueLabel;
<#break>
</#if></#list>
//${model.getName()}
//import ${package}.data.JPAUtils;

import ${package?replace("mapper", "type")}.*;

import java.util.Collection;
import javax.annotation.*;
import javax.persistence.*;
import javax.transaction.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.molgenis.util.Tuple;
import java.text.ParseException;

public class ${Name(entity)}Mapper implements JpaMapper<${Name(entity)}>
{
	@Override
	public List<${Name(entity)}> findAll(EntityManager em) {
		List<${Name(entity)}> result =
			(List<${Name(entity)}>)em.createNamedQuery("${Name(entity)}.findAll")
									.getResultList();
		return result;
	}

	@Override
	public List<${Name(entity)}> find(String qlWhereClause, Integer limit, Integer offset, EntityManager em) {
		String ql = "SELECT ${name(entity)} FROM ${Name(entity)} ${name(entity)} " + qlWhereClause;
		
		if(offset != null && limit != null) {
			return (List<${Name(entity)}>)em
							.createQuery(ql)
							.setFirstResult(offset)
							.setMaxResults(limit)
							.getResultList();
		} else if(offset != null) {
			return (List<${Name(entity)}>)em
							.createQuery(ql)
							.setFirstResult(offset)
							.getResultList(); 
		} else if(limit != null) {
			return (List<${Name(entity)}>)em
							.createQuery(ql)
							.setMaxResults(limit)
							.getResultList();
		} else { 
			return (List<${Name(entity)}>)em
							.createQuery(ql)
							.getResultList();		
		}
	}

	@Override
	public int count(String qlWhereClause, EntityManager em)
	{
		final String QUERY_COUNT = "SELECT count(${name(entity)}) FROM ${Name(entity)} ${name(entity)} ";
	
		Long result = new Long(-1);

		if(qlWhereClause == null || qlWhereClause.trim().equals("")) {
			result = (Long)em.createNamedQuery("${Name(entity)}.count")
								.getSingleResult();
		} else {
			String qlQuery = QUERY_COUNT + qlWhereClause;
			result = (Long)em.createQuery(qlQuery)
								.getSingleResult();
		}
		return result.intValue();
	}

	public void create(${Name(entity)} entity) throws DatabaseException {
		create(entity, null);
	}

	/** This method first saves the objects that are being refered to by entity, 
	then the entity itself and 
	finally the objects that refer to this object*/
    public void create(${Name(entity)} entity, EntityManager em) throws DatabaseException {
        try {
//			em.persist(entity);

<#foreach field in entity.getImplementedFields()>
	<#assign type_label = field.getType().toString()>
	<#if type_label == "xref">
			//check if the object refered by '${field.name}' is known in the databse
			if(entity.get${JavaName(field)}() != null)
			{
				//if object has been added as xref, but not yet stored (has no id) -> add the refered object
				if(entity.get${JavaName(field)}().getIdValue() == null)
					new ${JavaName(field.getXRefEntity())}Mapper().create(entity.get${JavaName(field)}(), em);
				//if object has id (so is stored) but not in this em -> retrieve proper reference reference
				else if (!em.contains(entity.get${JavaName(field)}()) && entity.get${JavaName(field)}().getIdValue() != null)
					entity.set${JavaName(field)}(em.getReference(${JavaName(field.getXRefEntity())}.class, entity.get${JavaName(field)}().getIdValue()));
			} else { //object is reference by xref		
				entity.set${JavaName(field)}((${JavaName(field.getXRefEntity())})em.find(${JavaName(field.getXRefEntity())}.class, entity.get${JavaName(field)}Xref()));
			}
	</#if>
</#foreach>

			//prevents uncontrolled recursion call of create (stack overflow)
            em.persist(entity);

//inverse association relation
<#list model.entities as e><#if !e.abstract>
	<#list e.fields as f>
		<#if f.type=="xref" && f.getXRefEntity() == entity.name>

		 <#assign multipleXrefs = 0/>
		 <#list e.fields as f2>
		 	<#if f2.type="xref" && f2.getXRefEntity() == entity.name>
		 			<#assign multipleXrefs = multipleXrefs+1>
	 		</#if>
		 </#list>

            <#assign entityName = "${Name(f.entity)}" >
            <#assign entityType = "${Name(f.entity)}" >
            <#if multipleXrefs &gt; 1 >
                <#assign entityName = "${entityName}${Name(f)}" >
            </#if>
            Collection<${Name(f.entity)}> attached${entityName}Collection = new ArrayList<${Name(f.entity)}>();
            if(entity.get${entityName}Collection() != null) {
				for (${entityType} ${name(entityName)} : entity.get${entityName}Collection()) {
					if(${name(entityName)}.getIdValue() == null) {
						if(${name(entityName)}.get${Name(f)}().getIdValue() == null) {
							${name(entityName)}.set${Name(f)}(entity);
						}
						new ${Name(f.entity)}Mapper().create(${name(entityName)}, em);
					} else {
						//check if the object realy exists!
						${Name(f.entity)} db${Name(entityName)} = em.getReference(${name(entityName)}.getClass(), ${name(entityName)}.getIdValue());
					}
					attached${entityName}Collection.add(${name(entityName)});
				}
			}
            entity.set${entityName}Collection( attached${entityName}Collection);
            em.persist(entity);

			//remove object references that link to a different object than entity
            if (entity.get${entityName}Collection() != null) {
				for(${entityType} ${name(entityName)} : entity.get${entityName}Collection())
				{
					${Name(entity)} old${Name(entity)}Collection = ${name(entityName)}.get${Name(f)}();
					if(!old${Name(entity)}Collection.getIdValue().equals(entity.getIdValue()))
					{
						${name(entityName)}.set${Name(f)}(entity);
						${name(entityName)} = em.merge(${name(entityName)});
						if(old${Name(entity)}Collection != null)
						{
							old${Name(entity)}Collection.get${entityName}Collection().remove(${name(entityName)});
							old${Name(entity)}Collection = em.merge(old${Name(entity)}Collection);
						}
					}
				}
            }
		</#if>
	</#list></#if>
</#list>

        } catch (Exception ex) {
            try {
				if(em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
            } catch (Exception re) {
                throw new DatabaseException("An error occurred attempting to roll back the transaction: "+re.getMessage());
            }
            throw new DatabaseException(ex);
        }
    }

	public void destroy(${Name(entity)} ${name(entity)}, EntityManager em) throws DatabaseException {
		try {
			try {
				${name(entity)} = em.getReference(${Name(entity)}.class, ${name(entity)}.getIdValue());
				//${name(entityType)}.getIdField(); //Fixme: is dit nodig?
			} catch (EntityNotFoundException enfe) {
				throw new DatabaseException("The ${name(entity)} with id " + ${name(entity)}.getIdField().toString() + " no longer exists: " + enfe.getMessage());
			}

<#foreach field in entity.getImplementedFields()>
	<#assign type_label = field.getType().toString()>
	<#if type_label == "xref">
		<#assign numRef = entity.getNumberOfReferencesTo(model.getEntity(field.getXRefEntity()))>
			${Name(field.getXRefEntity())} ${name(field)} = ${name(entity)}.get${Name(field)}();
			if (${name(field)} != null) {
				${name(field)}.get${Name(entity)}<#if numRef &gt; 1 >${Name(field)}</#if>Collection().remove(${name(entity)});
				${name(field)} = em.merge(${name(field)});
			}
	</#if>
</#foreach>

			em.remove(${name(entity)});
		} catch (Exception ex) {
			try {
				if(em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
			} catch (Exception re) {
				throw new DatabaseException("An error occurred attempting to roll back the transaction: "+re.getMessage());
			}
			throw new DatabaseException(ex);
		} 
	}


	public void edit(${Name(entity)} ${name(entity)}, EntityManager em) throws DatabaseException {
		try {

			//Fixme: getReference??
			${Name(entity)} persistent${Name(entity)} = em.find(${Name(entity)}.class, ${name(entity)}.getIdValue());



<#foreach field in entity.getImplementedFields()>
	<#assign type_label = field.getType().toString()>
	<#if type_label == "xref">
		<#assign numRef = entity.getNumberOfReferencesTo(model.getEntity(field.getXRefEntity()))>
			
			<#assign fieldName = name(field.getXRefEntity()) />
			<#assign methodName = Name(field.getXRefEntity()) />

			<#if numRef &gt; 1 >
				<#assign fieldName = fieldName + Name(field) />
				<#assign methodName = Name(field) />
			</#if>

			${Name(field.getXRefEntity())} ${fieldName}Old = persistent${Name(entity)}.get${Name(field)}();
			${Name(field.getXRefEntity())} ${fieldName}New = ${name(entity)}.get${Name(field)}();


			if (${fieldName}New != null) {
				${fieldName}New = em.getReference(${fieldName}New.getClass(), ${fieldName}New.getIdValue());
				${name(entity)}.set${Name(field)}(${fieldName}New);
			} else { //object is reference by xref		
				${name(entity)}.set${JavaName(field)}((${JavaName(field.getXRefEntity())})em.find(${JavaName(field.getXRefEntity())}.class, ${name(entity)}.get${JavaName(field)}Xref()));
			}


	</#if>
</#foreach>
			if(!em.contains(${name(entity)})) {
				${name(entity)} = em.merge(${name(entity)});
			}
<#foreach field in entity.getImplementedFields()>
	<#assign type_label = field.getType().toString()>
	<#if type_label == "xref">
		<#assign numRef = entity.getNumberOfReferencesTo(model.getEntity(field.getXRefEntity()))>

			<#assign fieldName = name(field.getXRefEntity()) />
			<#assign methodName = Name(entity) />
			<#if numRef &gt; 1 >
				<#assign fieldName = fieldName + Name(field) />
				<#assign methodName = methodName + Name(field) />
			</#if>

			if (${fieldName}Old != null && !${fieldName}Old.equals(${fieldName}New)) {
				${fieldName}Old.get${methodName}Collection().remove(${name(entity)});
				${fieldName}Old = em.merge(${fieldName}Old);
			}

			if (${fieldName}New != null && !${fieldName}New.equals(${fieldName}Old)) {
				${fieldName}New.get${methodName}Collection().add(${name(entity)});
				${fieldName}New = em.merge(${fieldName}New);
			}
	</#if>
</#foreach>
		} catch (Exception ex) {
			try {
				if(em.getTransaction().isActive()) {
					em.getTransaction().rollback();
				}
			} catch (Exception re) {
				throw new DatabaseException("An error occurred attempting to roll back the transaction: " + re.getMessage());
			}
			throw new DatabaseException(ex);
		} 
	}

	Logger logger = Logger.getLogger(this.getClass());

	@Override
	public int add(List<${Name(entity)}> entities, EntityManager em) throws DatabaseException
	{	
		int count = 0;
		
		try {
			for (${Name(entity)} ${name(entity)} : entities) {
				create(${name(entity)}, em);
				++count;
			}
		} catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new DatabaseException( "An error occurred attempting to roll back the transaction: "+  re.getMessage() );
            }
            throw new DatabaseException(ex);
        }
		return count;
	}

	@Override
	public int update(List<${Name(entity)}> entities, EntityManager em) throws DatabaseException
	{
		int count = 0;
		try {
			for (${Name(entity)} ${name(entity)} : entities) {
				edit(${name(entity)}, em);
				++count;
			}
		} catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new DatabaseException( "An error occurred attempting to roll back the transaction: " + re.getMessage());
            }
            throw new DatabaseException(ex);
        } 
		return count;
	}

	@Override
	public int remove(List<${Name(entity)}> entities, EntityManager em) throws DatabaseException
	{
		int count = 0;		
		try {
			for (${Name(entity)} ${name(entity)} : entities) {
				destroy(${name(entity)}, em);
				++count;
			}
		} catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
            	re.printStackTrace();
                throw new DatabaseException( "An error occurred attempting to roll back the transaction.");
            }
            throw new DatabaseException(ex);
        }
		return count;
	}
	
<#--	public ${JavaName(entity)} create()
	{
<#if !entity.abstract>	
		return new ${JavaName(entity)}();
<#else>
		return null; //abstract type, cannot be instantiated
</#if>
	}-->
	
	@Override
	public Type getFieldType(String fieldName)
	{
		<#list viewFields(entity) as f>
		<#assign type= f.type>
		<#if type == "user" || type == "xref" || type == "mref">		
		<#assign type = model.getEntity(f.getXRefEntity()).getField(f.getXRefField()).getType()/>
		if("${name(f)}".equalsIgnoreCase(fieldName)) return Type.${type?upper_case};
		if("${name(f)}_${name(xref_label)}".equalsIgnoreCase(fieldName)) return Type.STRING;
		<#else>		
		if("${name(f)}".equalsIgnoreCase(fieldName)) return Type.${type?upper_case};
		</#if>
		</#list>
		return Type.STRING;
	}		
	
	@Override
	public String getTableFieldName(String fieldName)
	{
		<#list viewFields(entity) as f>
		<#assign type= f.type>
		if("${name(f)}".equalsIgnoreCase(fieldName)) return "${SqlName(f)}";
		</#list>	
		<#list viewFields(entity,"xref") as f>		
		<#assign xref_entity = model.getEntity(f.getXRefEntity())/> 
		<#assign xref_field = xref_entity.getField(f.getXRefField())/>
		<#assign xref_label = xref_entity.getAllField(f.getXRefLabelString()) /><#--can be from supertype!!-->
		//alias for query on id field of xref entity
		if("${name(f)}_${name(xref_field)}".equalsIgnoreCase(fieldName)) return "${SqlName(f)}";
		//alias for query on label of the xref entity
		if("${name(f)}_${name(xref_label)}".equalsIgnoreCase(fieldName)) return "xref${f_index}.${SqlName(xref_label)}";
		</#list>		  		
		return fieldName;
	}	
	
	public ${JavaName(entity)} create()
	{
<#if !entity.abstract>	
		return new ${JavaName(entity)}();
<#else>
		return null; //abstract type, cannot be instantiated
</#if>
	}
	
	<#include "MapperFileAttachments.java.ftl">
}
