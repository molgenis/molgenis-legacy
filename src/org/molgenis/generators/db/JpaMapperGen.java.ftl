<#include "GeneratorHelper.ftl">
<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* File:        ${model.getName()}/model/${entity.getName()}.java
 * Copyright:   GBIC 2000-${year}, all rights reserved
 * Date:        ${date}
 * Template:	${template}
 * generator:   ${generator} ${version}
 *
 * Jpa Entity Mapper, helper to add, delete and update entities
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package ${package};

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Map;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jpa.JpaMapper;
import org.molgenis.fieldtypes.*;
import org.molgenis.util.CsvReader;
import org.molgenis.util.TupleWriter;

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

import org.molgenis.framework.db.*;
import ${package?replace("mapper", "type")}.*;
import ${package?replace(".db","")}.*;
import org.molgenis.framework.db.jpa.*;

import java.util.Collection;
import javax.annotation.*;
import javax.persistence.*;
import javax.transaction.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.molgenis.util.Tuple;
import java.text.ParseException;
import org.molgenis.framework.db.QueryRule.Operator;

<#--import all xref entities-->
<#foreach field in entity.getAllFields()>
	<#assign type_label = field.getType().toString()>
	<#if type_label == "user" || type_label="xref" || type_label="mref">
			<#assign xref_entity = field.xrefEntity>
import ${xref_entity.namespace}.${JavaName(xref_entity)};
import ${xref_entity.namespace}.db.${JavaName(xref_entity)}JpaMapper;			
	</#if>	
</#foreach>

<#-- inverse relations -->
<#list model.entities as e>
<#if !e.isAssociation()>
	<#list e.implementedFields as f>
		<#if (f.type=="xref" || f.type == "mref") && f.getXrefEntityName() == entity.name>
			 <#assign multipleXrefs = e.getNumberOfReferencesTo(entity)/>
import ${e.namespace}.${JavaName(e)};	
import ${e.namespace}.db.*;
		</#if>
	</#list></#if>
</#list>


public class ${JavaName(entity)}JpaMapper implements JpaMapper<${JavaName(entity)}>
{
	private EntityManager em;
        private Database db;

	public ${JavaName(entity)}JpaMapper() {}

	public ${JavaName(entity)}JpaMapper(EntityManager em) {
		this.em = em;
	}

	@Deprecated
	public ${JavaName(entity)}JpaMapper(Database db) {
            this.db = db;
            this.em = ((JpaDatabase)db).getEntityManager();
        }

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	@Override
	public String createFindSql(QueryRule[] rules) throws DatabaseException
	{
		throw new UnsupportedOperationException();
	}	


	@Override
	public List<${JavaName(entity)}> findAll() {
		List<${JavaName(entity)}> result =
			(List<${JavaName(entity)}>)em.createNamedQuery("${JavaName(entity)}.findAll", ${JavaName(entity)}.class)
									.getResultList();
		return result;
	}

	@Override
	public List<${JavaName(entity)}> find(String qlWhereClause, Integer limit, Integer offset) {
		String ql = "SELECT ${name(entity)} FROM ${JavaName(entity)} ${name(entity)} " + qlWhereClause;
		TypedQuery<${JavaName(entity)}> query = em.createQuery(ql, ${JavaName(entity)}.class);
		if(offset != null) {
			query.setFirstResult(offset);
		}
		if(limit != null) {
			query.setMaxResults(limit);
		}
		return query.getResultList();		
	}

	@Override
	public int count(String qlWhereClause)
	{
		final String QUERY_COUNT = "SELECT count(${name(entity)}) FROM ${JavaName(entity)} ${name(entity)} ";
	
		Long result = new Long(-1);

		if(qlWhereClause == null || qlWhereClause.trim().equals("")) {
			result = (Long)em.createNamedQuery("${JavaName(entity)}.count")
								.getSingleResult();
		} else {
			String qlQuery = QUERY_COUNT + qlWhereClause;
			result = (Long)em.createQuery(qlQuery)
								.getSingleResult();
		}
		return result.intValue();
	}

	/** This method first saves the objects that are being refered to by entity, 
	then the entity itself and 
	finally the objects that refer to this object*/
    public void create(${JavaName(entity)} entity) throws DatabaseException {
        try {


<#foreach field in entity.getAllFields()>
	<#assign type_label = field.getType().toString()>
	<#if type_label == "xref">
			//check if the object refered by '${field.name}' is known in the databse
			if(entity.get${JavaName(field)}() != null)
			{
				//if object has been added as xref, but not yet stored (has no id) -> add the refered object
				if(entity.get${JavaName(field)}().getIdValue() == null)
					new ${JavaName(field.getXrefEntity())}JpaMapper(em).create(entity.get${JavaName(field)}());
				//if object has id (so is stored) but not in this em -> retrieve proper reference reference
				else if (!em.contains(entity.get${JavaName(field)}()) && entity.get${JavaName(field)}().getIdValue() != null)
					entity.set${JavaName(field)}(em.getReference(${JavaName(field.getXrefEntity())}.class, entity.get${JavaName(field)}().getIdValue()));
			} else { //object is reference by xref	
				if(entity.get${JavaName(field)}_${JavaName(field.getXrefField())}() != null) {
					entity.set${JavaName(field)}((${JavaName(field.getXrefEntity())})em.find(${JavaName(field.getXrefEntity())}.class, entity.get${JavaName(field)}_${JavaName(field.getXrefField())}()));
				}
			}
	<#elseif type_label == "mref">
	    List<${JavaName(field.getXrefEntity())}> ${name(field)}List = entity.get${JavaName(field)}();
	    List<Integer> ${name(field)}Ids = entity.get${JavaName(field)}_Id();
	    for(Integer ${name(field)}Id : ${name(field)}Ids) {
		${JavaName(field.getXrefEntity())} ${name(field.getXrefEntity())} = em.getReference(${JavaName(field.getXrefEntity())}.class, ${name(field)}Id);
		if(!${name(field)}List.contains(${name(field.getXrefEntity())}))
		    ${name(field)}List.add(${name(field.getXrefEntity())});
	    }
	    entity.set${JavaName(field)}(${name(field)}List);
	</#if>
</#foreach>

			//prevents uncontrolled recursion call of create (stack overflow)
          
          if(entity.getIdValue() != null) {
            entity = em.merge(entity);            
          } else {
            em.persist(entity);
          }
//inverse association relation
<#list model.entities as e>
    <#if !e.abstract>
	<#list e.fields as f>
	    <#if f.type=="xref" && f.getXrefEntity() == entity.name>
		<#assign multipleXrefs = 0/>
		<#list e.fields as f2>
		    <#if f2.type="xref" && f2.getXrefEntity() == entity.name>
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
						new ${Name(f.entity)}JpaMapper(em).create(${name(entityName)});
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
					${JavaName(entity)} old${JavaName(entity)}Collection = ${name(entityName)}.get${Name(f)}();
					if(!old${JavaName(entity)}Collection.getIdValue().equals(entity.getIdValue()))
					{
						${name(entityName)}.set${Name(f)}(entity);
						${name(entityName)} = em.merge(${name(entityName)});
						if(old${JavaName(entity)}Collection != null)
						{
							old${JavaName(entity)}Collection.get${entityName}Collection().remove(${name(entityName)});
							old${JavaName(entity)}Collection = em.merge(old${JavaName(entity)}Collection);
						}
					}
				}
            }
	    </#if>
	</#list>
    </#if>
</#list>

        } catch (Exception ex) {
            try {
				em.getTransaction().rollback();
            } catch (Exception re) {
                throw new DatabaseException("An error occurred attempting to roll back the transaction: "+re.getMessage());
            }
            throw new DatabaseException(ex);
        }
    }

	public void destroy(${JavaName(entity)} ${name(entity)}, EntityManager em) throws DatabaseException {
		try {
			try {
				${name(entity)} = em.getReference(${JavaName(entity)}.class, ${name(entity)}.getIdValue());
			} catch (EntityNotFoundException enfe) {
				throw new DatabaseException("The ${name(entity)} with id " + ${name(entity)}.getIdField().toString() + " no longer exists: " + enfe.getMessage());
			}

<#list model.entities as e>
<#if !e.abstract && !e.isAssociation()>
	<#list e.implementedFields as f>
		<#if f.type=="mref" && Name(f.getXrefEntityName()) == Name(entity) >
		<#assign multipleXrefs = e.getNumberOfReferencesTo(entity)/>
		//${multipleXrefs}
			if(${name(entity)}.get${Name(f)}<#if multipleXrefs &gt; 1 >${Name(e)}</#if>Collection().contains(${name(entity)})) {	
				${name(entity)}.get${Name(f)}<#if multipleXrefs &gt; 1 >${Name(e)}</#if>Collection().remove(${name(entity)});
			}
		</#if>
	</#list>
</#if>	
</#list>

			em.remove(${name(entity)});
		} catch (Exception ex) {
			try {
				em.getTransaction().rollback();
			} catch (Exception re) {
				throw new DatabaseException("An error occurred attempting to roll back the transaction: "+re.getMessage());
			}
			throw new DatabaseException(ex);
		} 
	}


	public void edit(${JavaName(entity)} ${name(entity)}) throws DatabaseException {
		try {

			//Fixme: getReference??
			${JavaName(entity)} persistent${JavaName(entity)} = em.find(${JavaName(entity)}.class, ${name(entity)}.getIdValue());



<#foreach field in entity.getAllFields()>
	<#assign type_label = field.getType().toString()>

	<#if type_label == "xref" || type_label == "mref">
		<#assign numRef = entity.getNumberOfReferencesTo(field.getXrefEntity())>
			<#assign fieldName = name(field) />
	<#--		
			<#if numRef &gt; 1 >
				<#assign fieldName = fieldName + Name(entity)/>
			</#if>
		-->
			//${numRef}
		<#if type_label == "xref">
			${JavaName(field.getXrefEntity())} ${fieldName}Old = persistent${JavaName(entity)}.get${JavaName(field)}();
			${JavaName(field.getXrefEntity())} ${fieldName}New = ${name(entity)}.get${JavaName(field)}();

			if (${fieldName}New != null) {
				${fieldName}New = em.getReference(${fieldName}New.getClass(), ${fieldName}New.getIdValue());
				${name(entity)}.set${JavaName(field)}(${fieldName}New);
			} else { //object is reference by xref		
                            if(${name(entity)}.get${JavaName(field)}_${JavaName(field.xrefField)}() != null) {
                                ${name(entity)}.set${JavaName(field)}((${JavaName(field.getXrefEntity())})em.find(${JavaName(field.getXrefEntity())}.class, ${name(entity)}.get${JavaName(field)}_${JavaName(field.xrefField)}()));
                            }
			}
    	<#elseif type_label == "mref">
			for(${JavaName(field.getXrefEntity())} m : ${name(entity)}.get${JavaName(field)}()) {
				if(m.get${Name(pkey(field.getXrefEntity()))}() == null) {
					em.persist(m);
				}
				m.get${JavaName(fieldName)}<#if numRef &gt; 1 >${Name(entity)}</#if>Collection().add(${name(entity)});
			}
			
			for(${pkeyJavaType(field.getXrefEntity())} id : ${name(entity)}.get${JavaName(fieldName)}_Id()) {
				${JavaName(field.getXrefEntity())} mref = em.find(${JavaName(field.getXrefEntity())}.class, id);
				if(!${name(entity)}.get${JavaName(fieldName)}().contains(mref)) {
					${name(entity)}.get${JavaName(fieldName)}().add(mref);
				}
			}    
		</#if>
	</#if>
</#foreach>
			if(!em.contains(${name(entity)})) {
				${name(entity)} = em.merge(${name(entity)});
			}
<#--	what does this do? FIXIT		
<#list model.entities as e>
	<#if !e.abstract && !e.isAssociation()>
		<#list e.implementedFields as f>
			<#if f.type=="xref" && entity.isParent(Name(f.getXrefEntity())) >
				<#assign multipleXrefs = e.getNumberOfReferencesTo(entity)/>
			 
			 //${entity.getName()}
			 //${Name(f.getXrefEntity())}
			 
			if (${fieldName}Old != null && !${fieldName}Old.equals(${fieldName}New)) {
				${fieldName}Old.get${f.getXrefEntityName()}Collection().remove(${name(entity)});
				${fieldName}Old = em.merge(${fieldName}Old);
			}

			if (${fieldName}New != null && !${fieldName}New.equals(${fieldName}Old)) {
				${fieldName}New.get${f.getXrefEntityName()}Collection().add(${name(entity)});
				${fieldName}New = em.merge(${fieldName}New);
			}			 
			
			</#if>
		</#list>
	</#if>
</#list>			
-->	
			
<#foreach field in entity.getAllFields()>
	<#assign type_label = field.getType().toString()>
	<#if type_label == "xref">
		<#assign numRef = entity.getNumberOfReferencesTo(field.getXrefEntity())>

			<#assign fieldName = name(field.getXrefEntity()) />
			<#assign methodName = Name(entity) />
			<#if numRef &gt; 1 >
				<#assign fieldName = fieldName + Name(field) />
				<#assign methodName = methodName + Name(field) />
			</#if>

	</#if>
</#foreach>
		} catch (Exception ex) {
			try {
				em.getTransaction().rollback();
			} catch (Exception re) {
				throw new DatabaseException("An error occurred attempting to roll back the transaction: " + re.getMessage());
			}
			throw new DatabaseException(ex);
		} 
	}

	@Override
	public int add(List<${JavaName(entity)}> entities) throws DatabaseException
	{	
		int count = 0;
		
		try {
                    this.resolveForeignKeys(entities);
                    for (${JavaName(entity)} ${name(entity)} : entities) {
                            create(${name(entity)});
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
	public int update(List<${JavaName(entity)}> entities) throws DatabaseException
	{
		int count = 0;
		try {
                    this.resolveForeignKeys(entities);
                    for (${JavaName(entity)} ${name(entity)} : entities) {
                            edit(${name(entity)});
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
	public int remove(List<${JavaName(entity)}> entities) throws DatabaseException
	{
		int count = 0;		
		try {
                    for (${JavaName(entity)} ${name(entity)} : entities) {
                            destroy(${name(entity)}, em);
                            ++count;
                    }
		} catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
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
	public String getTableFieldName(String fieldName)
	{
		<#list viewFields(entity) as f>
		<#assign type= f.type>
		if("${f.name}".equalsIgnoreCase(fieldName)) return "${SqlName(f)}";
		if("${entity.name}_${f.name}".equalsIgnoreCase(fieldName)) return "${SqlName(f)}";
		</#list>	
		<#list viewFields(entity,"xref") as f>	
		if("${f.name}_${f.xrefField.name}".equalsIgnoreCase(fieldName)) return "${SqlName(f)}";
		if("${entity.name}_${f.name}_${f.xrefField.name}".equalsIgnoreCase(fieldName)) return "${SqlName(f)}";
		<#list f.xrefLabelTree.getTreeElements()?values as path><#if path.value.type != "xref">
		if("${path.name}".equalsIgnoreCase(fieldName)) return "${path.getParent().name}.${SqlName(path.value.name)}";	
		if("${entity.name}_${path.name}".equalsIgnoreCase(fieldName)) return "${path.getParent().name}.${SqlName(path.value.name)}";
		</#if></#list></#list>
		<#--
		<#assign xref_entity = f.xrefEntity/> 
		<#assign xref_field = f.xrefField/>
		//alias for query on id field of xref entity
		if("${name(f)}_${name(xref_field)}".equalsIgnoreCase(fieldName)) return "${SqlName(f)}";
		//alias(es) for query on label of the xref entity
			<#list f.xrefLabelNames as label>
		if("${name(f)}_${name(label)}".equalsIgnoreCase(fieldName)) return "xref_${label}.${SqlName(label)}";
			</#list>
		</#list>
		-->		  		
		return fieldName;
	}

	@Override
        @Deprecated
	public Database getDatabase()
	{
            return db;
	}

	@Override
	public int add(CsvReader reader, TupleWriter writer)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(CsvReader reader)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int count(QueryRule... rules)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public List<${JavaName(entity)}> find(QueryRule... rules)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void find(TupleWriter writer, QueryRule[] rules)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void find(TupleWriter writer, List<String> fieldsToExport,	QueryRule[] rules)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int remove(CsvReader reader)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public List<${JavaName(entity)}> toList(CsvReader reader, int limit)
	{
		throw new UnsupportedOperationException();
	}
	
	public ${JavaName(entity)} create()
	{
<#if !entity.abstract>	
		return new ${JavaName(entity)}();
<#else>
		return null; //abstract type, cannot be instantiated
</#if>
	}	
	
	<#include "MapperCommons.java.ftl">
	<#include "MapperFileAttachments.java.ftl">
}
