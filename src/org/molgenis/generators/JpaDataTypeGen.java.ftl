<#--helper functions-->
<#include "GeneratorHelper.ftl">

<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* File:        ${model.getName()}/model/${entity.getName()}.java
 * Copyright:   GBIC 2000-${year?c}, all rights reserved
 * Date:        ${date}
 * Generator:   ${generator} ${version}
 *
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */
 

package ${package};

import javax.persistence.*;

<#if !entity.abstract>
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.io.StringWriter;
import org.molgenis.util.Tuple;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.ResultSetTuple;
import java.text.ParseException;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

<#--import parent class if extends-->
<#list entity.getImplements() as i>
import ${i.namespace}.${JavaName(i)};
</#list>
<#if entity.hasAncestor()>
import ${entity.getAncestor().namespace}.${JavaName(entity.getAncestor())};
</#if>
<#--import Dateformater if there are Date or Timestamp fields-->
<#list allFields(entity) as f>
		<#if f.type == "datetime">
import java.text.SimpleDateFormat;
import java.util.Locale;
			<#break>
		</#if>
</#list>
</#if>
<#--import ValueLabel and Array if there are xref fields-->
<#list entity.getImplementedFields() as f>
	<#if f.type == "mref" || f.type="xref" || f.type="enum">
import org.molgenis.util.ValueLabel;
		<#if !entity.abstract>
import java.util.ArrayList;
</#if>
<#break>
</#if>
</#list>
<#list allFields(entity) as f>
	<#if f.type == "mref">
import java.util.StringTokenizer;
		<#break>
	</#if>
</#list>
<#--import File if there are file fields-->
<#list entity.fields as f>
	<#if f.type == "file" || f.type == "image" >
import java.io.File;
		<#break>
	</#if>
</#list>
<#--import all xref entities-->
<#foreach field in entity.getImplementedFields()>
	<#assign type_label = field.getType().toString()>
	<#if type_label == "user" || type_label="xref" || type_label="mref">
			<#assign xref_entity = field.xrefEntity>
import ${xref_entity.namespace}.${JavaName(xref_entity)};			
	</#if>	
</#foreach>	

<#-- inverse relations -->
<#list model.entities as e><#if !e.abstract && !e.isAssociation()>
	<#list e.implementedFields as f>
		<#if (f.type=="xref" || f.type == "mref") && f.getXrefEntityName() == entity.name>
			 <#assign multipleXrefs = e.getNumberOfReferencesTo(entity)/>
import ${e.namespace}.${JavaName(e)};	
		</#if>
	</#list></#if>
</#list>

/**
 * ${Name(entity)}: ${entity.description}.
 * @version ${date} 
 * @author MOLGENIS generator
 */
<#if entity.abstract>
public interface ${JavaName(entity)} extends <#if entity.hasImplements()><#list entity.getImplements() as i> ${JavaName(i)}<#if i_has_next>,</#if></#list><#else>org.molgenis.util.Entity</#if>
<#else>

@Entity
@Table(name = "${Name(entity)}"<#list entity.keys as uniqueKeys ><@compress single_line=true>
<#if uniqueKeys_index = 0 >, uniqueConstraints={
	@UniqueConstraint( columnNames={<#else>), @UniqueConstraint( columnNames={</#if>
    <#list key_fields(uniqueKeys) as uniqueFields >
	"${Name(uniqueFields)}"<#if uniqueFields_has_next>,</#if>
    </#list>
	}
    <#if !uniqueKeys_has_next>
    )
   }
    </#if>
</@compress>
</#list>
)
<#if !entity.hasAncestor() && entity.hasDescendants() >
@Inheritance(strategy=InheritanceType.JOINED)
</#if>
@XmlRootElement(name="${name(entity)}")
@XmlAccessorType(XmlAccessType.FIELD)
public class ${JavaName(entity)} extends <#if entity.hasAncestor()>${JavaName(entity.getAncestor())}<#else>org.molgenis.util.AbstractEntity</#if> <#if entity.hasImplements()>implements<#list entity.getImplements() as i> ${JavaName(i)}<#if i_has_next>,</#if></#list></#if>
</#if>
{
<#if entity.abstract>
<#--interface only has method signatures-->
	<#foreach field in entity.getImplementedFields()>
		<#assign type_label = field.getType().toString()>
		<#if (field.name != typefield()) || !entity.hasAncestor()>
	public <#if field.type = "xref">${JavaName(field.xrefEntity)}<#else>${type(field)}</#if> get${JavaName(field)}();
	public void set${JavaName(field)}(<#if field.type = "xref">${JavaName(field.xrefEntity)}<#else>${type(field)}</#if> ${name(field)});
		<#if type_label == "enum">
	public java.util.List<ValueLabel> get${JavaName(field)}Options();
		<#elseif type_label="xref">			
			<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
	public String get${JavaName(field)}_${label}();
	public void set${JavaName(field)}_${label}(String ${name(field)}_${label});
			</#list></#if>		
		<#elseif type_label == "mref">	
	public List<${type(f.xrefField)}> get${JavaName(field)}_${JavaName(f.xrefField)}();	
	public void set${JavaName(field)}_${JavaName(f.xrefField)}(List<${type(f.xrefField)}> ${JavaName(field)}_${JavaName(f.xrefField)}List);	
			<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
	public java.util.List<String> get${JavaName(field)}_${label}();
	public void set${JavaName(field)}_${label}(java.util.List<String> ${name(field)}_${label}List);	
			</#list></#if>						
		<#elseif type_label == "file" || type_label=="image" >
	public File get${JavaName(field)}File();
	public void set${JavaName(field)}File(File file);
			</#if>
		</#if>	
	</#foreach>	
<#--concrete class has method bodies-->
<#else>
	// member variables (including setters.getters for interface)
	<#foreach field in entity.getImplementedFields()>
	
	//${field.description}[type=${field.type}]
	<#if !isPrimaryKey(field,entity) || !entity.hasAncestor()>
 			<#if isPrimaryKey(field,entity) && !entity.hasAncestor()>
    @Id
    			<#if field.auto = true>
    @GeneratedValue(strategy = GenerationType.AUTO)   			
    			</#if>
    		</#if>
		</#if>	
        <#if field.type == "date">
    @Temporal(TemporalType.DATE)
    	<#elseif field.type == "datetime">
    @Temporal(TemporalType.TIMESTAMP)
    	</#if>
        <#if field.type == "mref">
	@ManyToMany(/*cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}*/)
	@JoinColumn(name="${SqlName(field)}", insertable=true, updatable=true, nullable=${field.isNillable()?string})
       	<#elseif field.type == "xref">
    @ManyToOne(/*cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}*/)
    @JoinColumn(name="${SqlName(field)}"<#if !field.nillable>, nullable=false</#if>)   	
       	<#else>
			<#if isPrimaryKey(field,entity)>
				<#if !entity.hasAncestor()>
    @Column(name="${SqlName(field)}"<#if !field.nillable>, nullable=false</#if>)
	@XmlElement(name="${name(field)}")
				</#if>
			<#else>
				<#if field.type == "text" >			
	@Lob()
	@Column(name="${SqlName(field)}"<#if !field.nillable>, nullable=false</#if>)
				<#else>
	@Column(name="${SqlName(field)}"<#if !field.nillable>, nullable=false</#if>)
	@XmlElement(name="${name(field)}")
				</#if>
			</#if>   	
       	</#if>
	
		<#assign type_label = field.getType().toString()>
			<#if isPrimaryKey(field,entity)>
				<#if !entity.hasAncestor()>
	private <#if field.type="xref">${JavaName(field.xrefEntity)}<#elseif field.type="mref">List<${JavaName(field.xrefEntity)}><#else>${type(field)}</#if> ${name(field)} = <#if field.type == "mref">new ArrayList<${JavaName(field.xrefEntity)}>()<#else> ${default(field)}</#if>;				
				</#if>
			<#else>
	private <#if field.type="xref">${JavaName(field.xrefEntity)}<#elseif field.type="mref">List<${JavaName(field.xrefEntity)}><#else>${type(field)}</#if> ${name(field)} = <#if field.type == "mref">new ArrayList<${JavaName(field.xrefEntity)}>()<#else> ${default(field)}</#if>;
			</#if>
		<#if type_label == "enum">
	@Transient
	private String ${name(field)}_label = null;
	@Transient
	private java.util.List<ValueLabel> ${name(field)}_options = new ArrayList<ValueLabel>();
		<#elseif type_label == "xref">
	@Transient
	private ${type(field.xrefField)} ${name(field)}_${name(field.xrefField)} = null;	
			<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
	@Transient
	private String ${name(field)}_${label} = null;						
			</#list></#if>
			<#elseif type_label == "mref">
	@Transient
	private List<${type(field.xrefField)}> ${name(field)}_${name(field.xrefField)} = null;		
			<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
	@Transient
	private java.util.List<String> ${name(field)}_${label} = new java.util.ArrayList<String>();
			</#list></#if>	
			<#elseif type_label == "file" || type_label=="image" >
	@Lob
	private File ${name(field)}_file = null;
		</#if>
	</#foreach>	

	//constructors
	public ${JavaName(entity)}()
	{
	<#if entity.hasAncestor() || entity.hasDescendants()>
		//set the type for a new instance
		set${typefield()}(this.getClass().getSimpleName());
	</#if>	
	
	<#list entity.getFields() as f>
		<#if f.type == "enum">
		//options for enum ${JavaName(f)}
			<#list f.getEnumOptions() as option>
		${name(f)}_options.add(new ValueLabel("${option}","${option}"));
			</#list>
		</#if>	
	</#list>
	}
	
	//static methods
	/**
	 * Shorthand for db.query(${JavaName(entity)}.class).
	 */
	public static Query query(Database db)
	{
		return db.query(${JavaName(entity)}.class);
	}
	
	/**
	 * Shorthand for db.findById(${JavaName(entity)}.class, id).
	 */
	public static ${JavaName(entity)} get(Database db, Object id) throws DatabaseException
	{
		return db.findById(${JavaName(entity)}.class, id);
	}
	
	/**
	 * Shorthand for db.find(${JavaName(entity)}.class, QueryRule ... rules).
	 */
	public static List find(Database db, QueryRule ... rules) throws DatabaseException
	{
		return db.find(${JavaName(entity)}.class, rules);
	}
	
	//getters and setters
	<#foreach field in entity.getImplementedFields()>
		<#assign type_label = field.getType().toString()>
			<#if isPrimaryKey(field,entity)>
				<#if !entity.hasAncestor()>
	/**
	 * Get the ${field.description}.
	 * @return ${name(field)}.
	 */
	public <#if field.type =="xref">${JavaName(field.xrefEntity)}<#else>${type(field)}</#if> get${JavaName(field)}()
	{
		return this.${name(field)};
	}	
				</#if>
			<#else>
	/**
	 * Get the ${field.description}.
	 * @return ${name(field)}.
	 */

	public <#if field.type =="xref">${JavaName(field.xrefEntity)}<#elseif field.type == "mref">List<${JavaName(field.xrefEntity)}><#else>${type(field)}</#if> get${JavaName(field)}()
	{
		return this.${name(field)};
	}
			</#if>
	
			<#if isPrimaryKey(field,entity)>
				<#if !entity.hasAncestor()>
	/**
	 * Set the ${field.description}.
	 * @param ${name(field)}
	 */
	public void set${JavaName(field)}( <#if field.type =="xref">${JavaName(field.xrefEntity)}<#elseif field.type == "mref">List<${JavaName(field.xrefEntity)}><#else>${type(field)}</#if> ${name(field)})
	{
		this.${name(field)} = ${name(field)};
	}
				</#if>
			<#else>
	/**
	 * Set the ${field.description}.
	 * @param ${name(field)}
	 */
	public void set${JavaName(field)}( <#if field.type =="xref">${JavaName(field.xrefEntity)}<#elseif field.type == "mref">List<${JavaName(field.xrefEntity)}><#else>${type(field)}</#if> ${name(field)})
	{
		this.${name(field)} = ${name(field)};
	}			
			</#if>

	
	<#-- data type specific methods -->
	<#if type_label =="date">
	/**
	 * Set the ${field.description}. Automatically converts string into date;
	 * @param ${name(field)}
	 */	
	public void set${JavaName(field)}(String datestring) throws ParseException
	{
		this.set${JavaName(field)}(string2date(datestring));
	}	
	<#elseif type_label == "enum" >	 
	/**
	 * Get tha label for enum ${JavaName(field)}.
	 */
	public String get${JavaName(field)}Label()
	{
		return this.${name(field)}_label;
	}
	
	/**
	 * ${JavaName(field)} is enum. This method returns all available enum options.
	 */
	public java.util.List<ValueLabel> get${JavaName(field)}Options()
	{
		return ${name(field)}_options;
	}	
	
	<#elseif type_label == "xref">
	
	/**
	 * Set foreign key for field ${name(field)}.
	 * This will erase any foreign key objects currently set.
	 * FIXME: can we autoload the new object?
	 */
	public void set${JavaName(field)}_${JavaName(field.xrefField)}(Integer ${name(field)}_${name(field.xrefField)})
	{
		this.${name(field)}_${name(field.xrefField)} = ${name(field)}_${name(field.xrefField)};
	}	
	
	public ${type(field.xrefField)} get${JavaName(field)}_${JavaName(field.xrefField)}()
	{
		
		if(${name(field)} != null) 
		{
			return ${name(field)}.get${JavaName(field.xrefField)}();
		}
		else
		{
			return ${name(field)}_${name(field.xrefField)};
		}
	}	
	 
<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
	/**
	 * Get a pretty label ${label} for cross reference ${JavaName(field)} to ${JavaName(field.xrefEntity)}.${JavaName(field.xrefField)}.
	 */
	public String get${JavaName(field)}_${label}()
	{		
		//FIXME should we auto-load based on get${JavaName(field)}()?	
		if(${name(field)} != null) {
			return ${name(field)}.get${JavaName(label)}();
		} else {
			return ${name(field)}_${label};
		}
	}		
	
	/**
	 * Set a pretty label for cross reference ${JavaName(field)} to <a href="${JavaName(field.xrefEntity)}.html#${JavaName(field.xrefField)}">${JavaName(field.xrefEntity)}.${JavaName(field.xrefField)}</a>.
	 * Implies set${JavaName(field)}(null) until save
	 */
	public void set${JavaName(field)}_${label}(String ${name(field)}_${label})
	{
		this.${name(field)}_${label} = ${name(field)}_${label};
	}		
</#list></#if>
	 
	
	<#elseif type_label="mref">
	/**
	 * Set foreign key for field ${name(field)}.
	 * This will erase any foreign key objects currently set.
	 * FIXME: can we autoload the new object?
	 */
	public void set${JavaName(field)}_${JavaName(field.xrefField)}(List<${type(field.xrefField)}> ${name(field)}_${name(field.xrefField)})
	{
		this.${name(field)}_${name(field.xrefField)} = ${name(field)}_${name(field.xrefField)};
	}	
	
	public List<${type(field.xrefField)}> get${JavaName(field)}_${JavaName(field.xrefField)}()
	{
		
		if(${name(field)} != null) 
		{
			List<${type(field.xrefField)}> result = new ArrayList<${type(field.xrefField)}>();
			for(${JavaName(field.xrefEntity)} xref: ${name(field)})
			{
				result.add(xref.get${JavaName(field.xrefField)}());
			}
			return result;
		}
		else
		{
			return ${name(field)}_${name(field.xrefField)};
		}
	}	
	
<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>	
	/**
	 * Get a pretty label for cross reference ${JavaName(field)} to <a href="${JavaName(field.xrefEntity)}.html#${JavaName(field.xrefField)}">${JavaName(field.xrefEntity)}.${JavaName(field.xrefField)}</a>.
	 */
	public java.util.List<String> get${JavaName(field)}_${label}()
	{
		if(this.${name(field)} != null && this.${name(field)}.size() > 0)
		{
			java.util.List<String> result = new java.util.ArrayList<String>();
			for(${JavaName(field.xrefEntity)} o: ${name(field)}) result.add(o.get${JavaName(label)}().toString());
			return java.util.Collections.unmodifiableList(result);
		}	
		else
		{	
			return ${name(field)}_${label};
		}
	}
	
	/**
	 * Update the foreign key ${JavaName(field)}
	 * This sets ${name(field)} to null until next database transaction.
	 */
	public void set${JavaName(field)}_${label}(java.util.List<String> ${name(field)}_${label})
	{
		this.${name(field)}_${label} = ${name(field)}_${label};
	}		
</#list></#if>		
	
	<#elseif type_label == "file"  || type_label=="image" >
	/**
	 * get${JavaName(field)}() is a textual pointer to a file. get${JavaName(field)}AttachedFile() can be used to retrieve the full paht to this file.
	 */
	public File get${JavaName(field)}AttachedFile()
	{
		return ${name(field)}_file;
	}
	
	/**
	 * ${JavaName(field)} is a pointer to a file. Use set${JavaName(field)}AttachedFile() to attach this file so it can be 
	 * retrieved using get${JavaName(field)}AttachedFile().
	 */
	public void set${JavaName(field)}AttachedFile(File file)
	{
		${name(field)}_file = file;
	}
	</#if>

</#foreach>	

	/**
	 * Generic getter. Get the property by using the name.
	 */
	public Object get(String name)
	{
		name = name.toLowerCase();
		<#foreach field in allFields(entity)>
		if (name.toLowerCase().equals("${name(field)?lower_case}"))
			return get${JavaName(field)}();
		<#if field.type == "enum" >	
		if(name.toLowerCase().equals("${name(field)?lower_case}_label"))
			return get${JavaName(field)}Label();
		<#elseif field.type == "xref" || field.type == "mref">
		if(name.toLowerCase().equals("${name(field)?lower_case}_${name(field.xrefField)?lower_case}"))
			return get${JavaName(field)}_${JavaName(field.xrefField)}();
<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>	
		if(name.toLowerCase().equals("${name(field)?lower_case}_${label?lower_case}"))
			return get${JavaName(field)}_${label}();
</#list></#if>			
		</#if>
	</#foreach>		
		return "";
	}	
	
	public void validate() throws DatabaseException
	{
	<#list allFields(entity) as field><#if field.nillable == false>
		if(this.get${JavaName(field)}() == null) throw new DatabaseException("required field ${name(field)} is null");
	</#if></#list>
	}
	
	//@Implements
	public void set( Tuple tuple, boolean strict )  throws ParseException
	{
		//optimization :-(
		if(tuple instanceof ResultSetTuple)
		{
	<#list allFields(entity) as f>
		<#assign type_label = f.getType().toString()>
		<#if f.type == "mref">
			//mrefs can not be directly retrieved
			//set ${JavaName(f)}			
		<#--elseif f.name!= typefield() || !entity.hasAncestor()-->
		<#elseif f.type == "xref">
			this.set${JavaName(f)}_${JavaName(f.xrefField)}(tuple.get${settertype(f)}("${name(f)}_${name(f.xrefField)}"));
			<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>		
			//set label ${label} for xref field ${JavaName(f)}
			this.set${JavaName(f)}_${label}(tuple.getString("${name(f)}_${name(label)}"));	
			</#list></#if>				
		<#else>
			//set ${JavaName(f)}
			<#if f.type == "nsequence">
			this.set${JavaName(f)}(tuple.getNSequence("${name(f)}"));
			<#elseif f.type == "onoff">
			this.set${JavaName(f)}(tuple.getOnoff("${name(f)}"));
			<#else>
			this.set${JavaName(f)}(tuple.get${settertype(f)}("${name(f)}"));
		</#if>
		<#if f.type == "file"  || type_label=="image" >
		</#if>						
		</#if>
	</#list>		
		}
		else if(tuple != null)
		{
	<#list allFields(entity) as f>
		<#assign type_label = f.getType().toString()>
		<#if f.type == "mref">
			if( tuple.getObject("${name(f)}")!= null ) 
			{
				java.util.List<${type(xrefField(model,f))}> values = new java.util.ArrayList<${type(xrefField(model,f))}>();
				java.util.List<Object> mrefs = tuple.getList("${name(f)}");
				if(mrefs != null) for(Object ref: mrefs)
				{
					if(ref instanceof String)
						values.add(${type(xrefField(model,f))}.parse${settertype(xrefField(model,f))}((String)ref));
					else
						values.add((${type(xrefField(model,f))})ref);
				}							
				this.set${JavaName(f)}_${JavaName(f.xrefField)}( values );			
			}
			<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
			//set labels ${label} for mref field ${JavaName(f)}	
			if( tuple.getObject("${name(f)}_${name(label)}")!= null ) 
			{
				java.util.List<String> values = new java.util.ArrayList<String>();
				java.util.List<Object> mrefs = tuple.getList("${name(f)}_${name(label)}");
				if(mrefs != null) 
					for(Object ref: mrefs)
					{
					<#if type(f.xrefLabels[label_index]) == "String">
						<#-- values.add(${type(f.xrefLabels[label_index])}.parse${settertype(f.xrefLabels[label_index])}(ref.toString())); -->
						String[] refs = ref.toString().split("\\|");
						for(String r : refs) {
							values.add(r);	
						}						
					<#else>
						values.add(ref.toString());
					</#if>
					}						
				this.set${JavaName(f)}_${label}( values );			
			}	
			</#list></#if>					
		<#elseif f.name != typefield() || !entity.hasAncestor()>
			//set ${JavaName(f)}
			<#if f.type == "xref">	
			if( strict || tuple.get${settertype(f)}("${name(f)}_${name(f.xrefField)}") != null) this.set${JavaName(f)}_${JavaName(f.xrefField)}(tuple.get${settertype(f)}("${name(f)}_${name(f.xrefField)}"));		
			if( tuple.get${settertype(f)}("${name(entity)}.${name(f)}_${name(f.xrefField)}") != null) this.set${JavaName(f)}_${JavaName(f.xrefField)}(tuple.get${settertype(f)}("${name(entity)}.${name(f)}_${name(f.xrefField)}"));
			//alias of xref
			if( tuple.getObject("${name(f)}") != null) 
				this.set${JavaName(f)}_${JavaName(f.xrefField)}(tuple.get${settertype(f)}("${name(f)}"));
			if( tuple.getObject("${name(entity)}.${name(f)}") != null) 
				this.set${JavaName(f)}_${JavaName(f.xrefField)}(tuple.get${settertype(f)}("${name(entity)}.${name(f)}_${name(f.xrefField)}"));
			//set label for field ${JavaName(f)}
			<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
			if( strict || tuple.getObject("${name(f)}_${name(label)}") != null) this.set${JavaName(f)}_${label}(tuple.getString("${name(f)}_${name(label)}"));			
			if( tuple.getObject("${name(entity)}.${name(f)}_${name(label)}") != null ) this.set${JavaName(f)}_${label}(tuple.getString("${name(entity)}.${name(f)}_${name(label)}"));		
			</#list></#if>
			<#elseif f.type == "nsequence">
			if( strict || tuple.getNSequence("${name(f)}") != null)this.set${JavaName(f)}(tuple.getNSequence("${name(f)}"));
			if(tuple.getNSequence("${name(entity)}.${name(f)}") != null) this.set${JavaName(f)}(tuple.getNSequence("${name(entity)}.${name(f)}"));
			<#elseif f.type == "onoff">
			if( strict || tuple.getOnoff("${name(f)}") != null) this.set${JavaName(f)}(tuple.getOnoff("${name(f)}"));
			if( tuple.getOnoff("${name(entity)}.${name(f)}") != null) this.set${JavaName(f)}(tuple.getOnoff("${name(entity)}.${name(f)}"));
			<#else>
			if( strict || tuple.get${settertype(f)}("${name(f)}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${name(f)}"));
			if( tuple.get${settertype(f)}("${name(entity)}.${name(f)}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${name(entity)}.${name(f)}"));
			</#if>
			<#if f.type == "file" || f.type=="image">
			this.set${JavaName(f)}AttachedFile(tuple.getFile("filefor_${name(f)}"));
			if(tuple.getFile("filefor_${name(entity)}.${name(f)}") != null) this.set${JavaName(f)}AttachedFile(tuple.getFile("filefor_${name(entity)}.${name(f)}"));
			</#if>						
		</#if>
	</#list>
	<#--if the label itself is not (completely) set it can use the value of another field as default-->
<#--
	<#list allFields(entity) as f>
		<#if (f.type == "xref" || f.type == "mref") && f.xrefLabelNames[0] != f.xrefFieldName && f.xrefLabelNames?size &gt; 1>
			<#assign all_labels = f.allPossibleXrefLabels()/>
			//MAGIC guessing of xref_labels:
			//if a some parts of the secondary key for '${f.name}' are set and some not it will search if it can use another label to complete it
			//e.g if protocol_name is set, but protocol_investigation_name is not set it will look for investigation_name in the other labels to copy
			//caveat: it may be left empty on purpose, hence tuple headers should be checked and not null constraints
			if( (<#list f.xrefLabelNames as label><#if label_index &gt; 0>||</#if> this.get${JavaName(f)}_${label}() == null</#list>) && (<#list f.xrefLabelNames as label><#if label_index &gt; 0>||</#if> this.get${JavaName(f)}_${label}() != null</#list>) )
			{
				<#list f.xrefLabelNames as label>
				//guess the value for ${label} from other labels, if not set to null on purpose in the tuple
				if( this.get${JavaName(f)}_${label}() == null && !tuple.getFields().contains("${name(f)}_${name(label)}") )
				{
					<#list f.labelsToSameEndpoint(label) as otherLabel>
						//${otherLabel}
					</#list>
				}
				</#list>
			}	
		</#if>		
	</#list>
-->
		}
		//org.apache.log4j.LogFactory.getLog("test").debug("set "+this);
	}	

	@Override
	public String toString()
	{
		return this.toString(false);
	}
	
	public String toString(boolean verbose)
	{
		String result = "${JavaName(entity)}(";
<#list allFields(entity) as field>
	<#assign type_label = field.getType().toString()>
		<#if field.type.toString() == "datetime">
		result+= "${name(field)}='" + (get${JavaName(field)}() == null ? "" : new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US).format(get${JavaName(field)}()))+"'<#if field_has_next> </#if>";
		result+= "${name(field)}='" + (get${JavaName(field)}() == null ? "" : new SimpleDateFormat("MMMM d, yyyy", Locale.US).format(get${JavaName(field)}()))+"'<#if field_has_next> </#if>";		
		<#else>
		result+= "${name(field)}='" + get${JavaName(field)}()+"'<#if field_has_next> </#if>";
			<#if field.type == "xref" || field.type == "mref">
				<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
		result+= " ${name(field)}_${name(label)}='" + get${JavaName(field)}_${label}()+"' ";
				</#list></#if>
			</#if>
		</#if>
</#list>
		result += ");";
		return result;
	}
		
	@Override
	public boolean equals(Object other)
	{
		if (!${JavaName(entity)}.class.equals(other.getClass()))
			return false;
		${JavaName(entity)} e = (${JavaName(entity)}) other;
		
	<#list allFields(entity) as field>
		<#if (field.type = "int" && field.auto)>
		//ignoring automatic primary key ${field.name}		
		<#elseif field.type = "xref" || field.type = "mref">
		//compare on xref labels if they are set
		<#if field.xrefLabelNames[0] != field.xrefFieldName>if(<#list field.xrefLabelNames as label>get${JavaName(field)}_${label}() != null <#if label_has_next> && </#if></#list>)
		{
			<#list field.xrefLabelNames as label>
			if ( get${JavaName(field)}_${label}() == null ? e.get${JavaName(field)}_${label}()!= null : !get${JavaName(field)}_${label}().equals( e.get${JavaName(field)}_${label}()))
				return false;			
			</#list>
		}
		else</#if>
		{
			if ( get${JavaName(field)}() == null ? e.get${JavaName(field)}()!= null : !get${JavaName(field)}().equals( e.get${JavaName(field)}()))
				return false;		
		}
		<#else>
		if ( get${JavaName(field)}() == null ? e.get${JavaName(field)}()!= null : !get${JavaName(field)}().equals( e.get${JavaName(field)}()))
			return false;		
		</#if>
	</#list>
		
		return true;
	}	
	
	@Override
 	public int hashCode() 
 	{ 
    	int hash = <#if entity.hasAncestor()>super.hashCode()<#else>1</#if>;
 <#list entity.fields as field>
 		<#if (field.type = "int" && field.auto)>
		//ignoring automatic primary key ${field.name}		
		<#elseif field.type = "xref" || field.type = "mref">
		//hash on xref labels if they are set
		<#if field.xrefLabelNames[0] != field.xrefFieldName>if(<#list field.xrefLabelNames as label>get${JavaName(field)}_${label}() != null <#if label_has_next> && </#if></#list>)
		{
			<#list field.xrefLabelNames as label>
			hash = hash * 31 + (${name(field)}_${label} == null ? 0 : ${name(field)}_${label}.hashCode());			
			</#list>
		}
		else</#if>
		{
    		hash = hash * 31 + (${name(field)} == null ? 0 : ${name(field)}.hashCode());		
		}
		<#else>
    	hash = hash * 31 + (${name(field)} == null ? 0 : ${name(field)}.hashCode());	
		</#if>
</#list>
    	return hash;
  	}
	
	/**
	 * Get the names of all public properties of ${JavaName(entity)}.
	 */
	public Vector<String> getFields(boolean skipAutoIds)
	{
		Vector<String> fields = new Vector<String>();
	<#list allFields(entity) as field>
		<#if (field.auto && field.type = "int")>
		if(!skipAutoIds)
		</#if>
		{
			fields.add("${name(field)}");
		}
		<#if field.type="xref" || field.type="mref">
			<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
		fields.add("${name(field)}_${name(label)}");
			</#list></#if>
		</#if>
	</#list>		
		return fields;
	}	

	public Vector<String> getFields()
	{
		return getFields(false);
	}

	@Override
	public String getIdField()
	{
		return "${name(pkey(entity))}";
	}
	

	
	@Override
	public List<String> getLabelFields()
	{
		List<String> result = new ArrayList<String>();
		<#if entity.getXrefLabels()?exists><#list entity.getXrefLabels() as label>
		result.add("${label}");
		</#list></#if>
		return result;
	}

	@Deprecated
	public String getFields(String sep)
	{
		return (""
	<#list allFields(entity) as field>
		+ "${name(field)}" <#if field_has_next>+sep</#if>
	</#list>
		);
	}

<#if !entity.abstract>	
	@Override
	public Object getIdValue()
	{
		return get(getIdField());
	}		
	
	
    public String getXrefIdFieldName(String fieldName) {
        <#list allFields(entity) as field>
        	<#if field.type = 'xref' >
        if (fieldName.equalsIgnoreCase("${name(field)}")) {
            return "${name(field.getXrefEntity().getPrimaryKey())}";
        }
        	</#if>
        </#list>
        
        <#if !(superclasses(entity)??) >
        return super.getXrefIdFieldName(fieldName);
        <#else>
        return null;
        </#if>
    }	
</#if>

	@Deprecated
	public String getValues(String sep)
	{
		StringWriter out = new StringWriter();
	<#list allFields(entity) as field>
		{
			Object valueO = get${JavaName(field)}();
			String valueS;
			if (valueO != null)
				valueS = valueO.toString();
			else 
				valueS = "";
			valueS = valueS.replaceAll("\r\n"," ").replaceAll("\n"," ").replaceAll("\r"," ");
			valueS = valueS.replaceAll("\t"," ").replaceAll(sep," ");
			out.write(valueS<#if field_has_next>+sep</#if>);
		}
	</#list>
		return out.toString();
	}
	
	@Override
	public ${JavaName(entity)} create(Tuple tuple) throws ParseException
	{
		${JavaName(entity)} e = new ${JavaName(entity)}();
		e.set(tuple);
		return e;
	}
	
<#list model.entities as e><#if !e.abstract && !e.isAssociation()>
	<#list e.implementedFields as f>
		<#if f.type=="xref" && f.getXrefEntityName() == entity.name>
			 <#assign multipleXrefs = e.getNumberOfReferencesTo(entity)/>
	@OneToMany(mappedBy="${name(f)}" /*, cascade={CascadeType.REFRESH, CascadeType.MERGE} */)
    private Collection<${Name(f.entity)}> ${name(f.entity)}<#if multipleXrefs &gt; 1 >${Name(f)}</#if>Collection = new ArrayList<${Name(f.entity)}>();

	@XmlTransient
	public Collection<${Name(f.entity)}> get${Name(f.entity)}<#if multipleXrefs &gt; 1 >${Name(f)}</#if>Collection()
	{
            return ${name(f.entity)}<#if multipleXrefs &gt; 1 >${Name(f)}</#if>Collection;
	}

    public void set${Name(f.entity)}<#if multipleXrefs &gt; 1 >${Name(f)}</#if>Collection(Collection<${Name(f.entity)}> collection)
    {
        for (${Name(f.entity)} ${name(f.entity)} : collection) {
            ${name(f.entity)}.set${Name(f)}(this);
        }
        ${name(f.entity)}<#if multipleXrefs &gt; 1 >${Name(f)}</#if>Collection = collection;
    }	
		</#if>
	</#list></#if>
</#list>
<#list model.entities as e><#if !e.abstract && !e.isAssociation()>
	<#list e.implementedFields as f>
		<#if f.type=="mref" && f.getXrefEntityName() == entity.name>
			 <#assign multipleXrefs = e.getNumberOfReferencesTo(entity)/>
	@ManyToMany(mappedBy="${name(f)}" /*, cascade={CascadeType.REFRESH, CascadeType.MERGE} */)
    private Collection<${Name(f.entity)}> ${name(f.entity)}<#if multipleXrefs &gt; 1 >${Name(f)}</#if>Collection = new ArrayList<${Name(f.entity)}>();

	@XmlTransient
	public Collection<${Name(f.entity)}> get${Name(f.entity)}<#if multipleXrefs &gt; 1 >${Name(f)}</#if>Collection()
	{
        return ${name(f.entity)}<#if multipleXrefs &gt; 1 >${Name(f)}</#if>Collection;
	}

    public void set${Name(f.entity)}<#if multipleXrefs &gt; 1 >${Name(f)}</#if>Collection(Collection<${Name(f.entity)}> collection)
    {
    	${name(f.entity)}<#if multipleXrefs &gt; 1 >${Name(f)}</#if>Collection.addAll(collection);
    }	
		</#if>
	</#list></#if>
</#list>
	
	
</#if>
}

