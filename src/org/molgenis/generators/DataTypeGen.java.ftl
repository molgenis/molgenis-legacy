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

<#if !entity.abstract>
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
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
<#--import all implemented entities-->
<#if entity.hasImplements()>
<#list entity.getImplements() as impl_entity>
import ${impl_entity.namespace}.${JavaName(impl_entity)};
</#list>
</#if>	
 

/**
 * ${Name(entity)}: ${entity.description}.
 * @version ${date} 
 * @author MOLGENIS generator
 */
<#if entity.abstract>
public interface ${JavaName(entity)} extends <#if entity.hasImplements()><#list entity.getImplements() as i> ${JavaName(i)}<#if i_has_next>,</#if></#list><#else>org.molgenis.util.Entity</#if>
<#else>
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
	public ${type(field)} get${JavaName(field)}();
	public void set${JavaName(field)}(${type(field)} _${name(field)});
		<#if type_label == "enum">
	public java.util.List<ValueLabel> get${JavaName(field)}Options();
		<#elseif type_label="xref">			
			<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
	public ${type(field.xrefLabels[label_index])} get${JavaName(field)}_${label}();
	public void set${JavaName(field)}_${label}(${type(field.xrefLabels[label_index])} ${name(field)}_${label});
			</#list></#if>		
		<#elseif type_label == "mref">	
			<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
	public java.util.List<${type(field.xrefLabels[label_index])}> get${JavaName(field)}_${label}();
	public void set${JavaName(field)}_${label}(java.util.List<${type(field.xrefLabels[label_index])}> ${name(field)}_${label}List);	
			</#list></#if>						
		<#elseif type_label == "file" || type_label=="image" >
	public File get${JavaName(field)}File();
	public void set${JavaName(field)}File(File file);
			</#if>
		</#if>	
	</#foreach>	
<#--concrete class has method bodies-->
<#else>
	// fieldname constants
    <#foreach field in entity.getImplementedFields()>
	public final static String ${field.name?upper_case} = "${field.name}";<#if field.type == "xref"><#list field.xrefLabelNames as label>
	public final static String ${field.name?upper_case}_${label?upper_case} = "${field.name}_${label}";</#list></#if>
	</#foreach>
	// member variables (including setters.getters for interface)
	
	<#foreach field in entity.getImplementedFields()>
	
	//${field.description}[type=${field.type}]
	@XmlElement(name="${name(field)}")
		<#assign type_label = field.getType().toString()>
	private ${type(field)} _${name(field)} = ${default(field)};
		<#if type_label == "enum">
	private String _${name(field)}_label = null;
	private java.util.List<ValueLabel> _${name(field)}_options = new ArrayList<ValueLabel>();
		<#elseif type_label == "xref">
			<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
	private ${type(field.xrefLabels[label_index])} _${name(field)}_${label} = null;						
			</#list></#if>
	private  ${JavaName(field.xrefEntity)} _${name(field)}_object = null;				
		<#elseif type_label == "mref">
			<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
	private java.util.List<${type(field.xrefLabels[label_index])}> _${name(field)}_${label} = new java.util.ArrayList<${type(field.xrefLabels[label_index])}>();
			</#list></#if>	
	private java.util.List<${JavaName(field.xrefEntity)}> _${name(field)}_objects= new java.util.ArrayList<${JavaName(field.xrefEntity)}>();						
		<#elseif type_label == "file" || type_label=="image" >
	private File _${name(field)}_file = null;
		</#if>
	</#foreach>	

	//constructors
	public ${JavaName(entity)}()
	{
	<#if entity.isRootAncestor()>
		//set the type for a new instance
		set__Type(this.getClass().getSimpleName());
	</#if>	
	
	<#list entity.getFields() as f>
		<#if f.type == "enum">
		//options for enum ${JavaName(f)}
			<#list f.getEnumOptions() as option>
		_${name(f)}_options.add(new ValueLabel("${option}","${option}"));
			</#list>
		</#if>	
	</#list>
	}
	
<#assign numFields = 0/>

<#if entity.getFields(true,true,false)?size &gt; 0 >
	/**
	 * Constructor with only the required fields
	 */
	public ${JavaName(entity)}(<#list entity.getFields(true,true,false) as f><#if f_index &gt; 0>,</#if>${type(f)} ${name(f)}</#list>)
	{
<#list entity.getFields(true,true,false) as f>
		this.set${JavaName(f)}(${name(f)});
</#list>	
	}
</#if>

<#if entity.getFields(true,true,false)?size &lt; entity.getFields(false,true,false)?size>
   /**
	 * Constructor with all fields
	 */
	public ${JavaName(entity)}(<#list entity.getFields(false,true,false) as f><#if f_index &gt; 0>,</#if>${type(f)} ${name(f)}</#list>)
	{
<#list entity.getFields(false,true,false) as f>
		this.set${JavaName(f)}(${name(f)});
</#list>
	}
</#if>
	
	
	
	
	//static methods
	/**
	 * Shorthand for db.query(${JavaName(entity)}.class).
	 */
	public static Query<? extends ${JavaName(entity)}> query(Database db)
	{
		return db.query(${JavaName(entity)}.class);
	}
	
	/**
	 * Shorthand for db.find(${JavaName(entity)}.class, QueryRule ... rules).
	 */
	public static List<? extends ${JavaName(entity)}> find(Database db, QueryRule ... rules) throws DatabaseException
	{
		return db.find(${JavaName(entity)}.class, rules);
	}

<#foreach key in entity.getAllKeys()>	
	/**
	 * 
	 */
	public static ${JavaName(entity)} findBy<#list key.fields as f>${JavaName(f)}</#list>(Database db<#list key.fields as f>, ${type(f)} ${name(f)}</#list>) throws DatabaseException, ParseException
	{
		Query<${JavaName(entity)}> q = db.query(${JavaName(entity)}.class);
		<#list key.fields as f>q.eq(${JavaName(entity)}.${f.name?upper_case}, ${name(f)});</#list>
		List<${JavaName(entity)}> result = q.find();
		if(result.size()>0) return result.get(0);
		else return null;
	}

</#foreach>
	
	
	//getters and setters
<#--
<#if !entity.isRootAncestor() && entity.hasAncestor()> 
	@Override
	public String getType()
	{
		return this.getClass().getSimpleName();
	}	
</#if>-->
	
	<#foreach field in entity.getImplementedFields()>
		<#assign type_label = field.getType().toString()>
<#--<#if (field.name != typefield()) || !entity.hasAncestor()>-->
	/**
	 * Get the ${field.description}.
	 * @return ${name(field)}.
	 */
	public ${type(field)} get${JavaName(field)}()
	{
		<#if type_label == "xref">
		if(this._${name(field)}_object != null)
			return this._${name(field)}_object.get${JavaName(field.xrefField)}();
		<#elseif type_label == "mref">
		if(this._${name(field)}_objects != null && this._${name(field)}_objects.size() > 0)
		{
			${type(field)} result = ${default(field)};
			for(${JavaName(field.xrefEntity)} o: _${name(field)}_objects) result.add(o.get${JavaName(field.xrefField)}());
			//this should be smarter, like a List that automatically syncs...
			//and this also doesn't give an informative error why it is not modifiable
			return java.util.Collections.unmodifiableList(result);
		}		
		</#if>
		return this._${name(field)};
	}
	
	<#if type_label="xref" || type_label="mref">
	public ${type(field)} get${JavaName(field)}_${JavaName(field.xrefField)}()
	{
		<#if type_label == "xref">
		if(this._${name(field)}_object != null)
			return this._${name(field)}_object.get${JavaName(field.xrefField)}();
		<#elseif type_label == "mref">
		if(this._${name(field)}_objects != null && this._${name(field)}_objects.size() > 0)
		{
			${type(field)} result = ${default(field)};
			for(${JavaName(field.xrefEntity)} o: _${name(field)}_objects) result.add(o.get${JavaName(field.xrefField)}());
			//this should be smarter, like a List that automatically syncs...
			//and this also doesn't give an informative error why it is not modifiable
			return java.util.Collections.unmodifiableList(result);
		}		
		</#if>
		return this._${name(field)};
	}	
	</#if>
	
	
	/**
	 * Set the ${field.description}.
	 * @param _${name(field)}
	 */
	public void set${JavaName(field)}(${type(field)} _${name(field)})
	{
		<#if type_label == "mref">
		//check what type the elements in the list are made off because List<E> has same erasure
		//if ${JavaName(field.xrefEntity)} then tye should go in the object list
		if( _${name(field)} != null && _${name(field)}.size()>0 && _${name(field)} instanceof ${JavaName(field.xrefEntity)})
		{
			// this._${name(field)}_objects = _${name(field)};
			//need to copy ids to this._${name(field)} because get${JavaName(field)} does this.
			//this._${name(field)} = ${default(field)};
			//for(${JavaName(field.xrefEntity)} o: _${name(field)}_objects) result.add(o.get${JavaName(field.xrefField)}());	
		}
		//else make list empty
		else
		{
			this._${name(field)}_objects = new java.util.ArrayList<${JavaName(field.xrefEntity)}>();
		
			if(this._${name(field)} != null)
				this._${name(field)} = _${name(field)};
			else
				this._${name(field)} = ${default(field)};
		}
		<#else>
		this._${name(field)} = _${name(field)};
		<#if type_label == "xref">
		//erases the xref object
		this._${name(field)}_object = null;
		</#if>
		</#if>
	}
	
	<#-- data type specific methods -->
	<#if type_label =="date">
	/**
	 * Set the ${field.description}. Automatically converts string into date;
	 * @param _${name(field)}
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
		return this._${name(field)}_label;
	}
	
	/**
	 * ${JavaName(field)} is enum. This method returns all available enum options.
	 */
	public java.util.List<ValueLabel> get${JavaName(field)}Options()
	{
		return _${name(field)}_options;
	}	
	
	<#elseif type_label == "xref">
	/**
	 * Set the ${field.description}. Automatically calls this.set${JavaName(field)}(${name(field)}.get${JavaName(field.xrefField)});
	 * @param _${name(field)}
	 */
	public void set${JavaName(field)}(${JavaName(field.xrefEntity)} ${name(field)})
	{
		this.set${JavaName(field)}(${name(field)}.get${JavaName(field.xrefField)}());
	}	
	
	public void set${JavaName(field)}_${JavaName(field.xrefField)}(Integer ${name(field)}_${name(field.xrefField)})
	{
		_${name(field)} = ${name(field)}_${name(field.xrefField)};
	}		
	
	 
<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
	/**
	 * Get a pretty label ${label} for cross reference ${JavaName(field)} to ${JavaName(field.xrefEntity)}.${JavaName(field.xrefField)}.
	 */
	public ${type(field.xrefLabels[label_index])} get${JavaName(field)}_${label}()
	{			
		return _${name(field)}_${label};
	}		
	
	/**
	 * Set a pretty label for cross reference ${JavaName(field)} to <a href="${JavaName(field.xrefEntity)}.html#${JavaName(field.xrefField)}">${JavaName(field.xrefEntity)}.${JavaName(field.xrefField)}</a>.
	 */
	public void set${JavaName(field)}_${label}(${type(field.xrefLabels[label_index])} ${name(field)}_${label})
	{
		_${name(field)}_${label} = ${name(field)}_${label};
		//clear the object cache
		_${name(field)}_object = null;
	}		
</#list></#if>
	 
	
	<#elseif type_label="mref">
	public void set${JavaName(field)}(${type(pkey(field.xrefEntity))} ... ${name(field)})
	{
		this.set${JavaName(field)}(java.util.Arrays.asList(${name(field)}));
	}	
	
<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>	
	/**
	 * Get a pretty label for cross reference ${JavaName(field)} to <a href="${JavaName(field.xrefEntity)}.html#${JavaName(field.xrefField)}">${JavaName(field.xrefEntity)}.${JavaName(field.xrefField)}</a>.
	 */
	public java.util.List<${type(field.xrefLabels[label_index])}> get${JavaName(field)}_${label}()
	{
		if(this._${name(field)}_objects != null && this._${name(field)}_objects.size() > 0)
		{
			java.util.List<${type(field.xrefLabels[label_index])}> result = new java.util.ArrayList<${type(field.xrefLabels[label_index])}>();
			for(${JavaName(field.xrefEntity)} o: _${name(field)}_objects) result.add(o.get${JavaName(label)}());
			//this should be smarter, like a List that automatically syncs...
			//and this also doesn't give an informative error why it is not modifiable
			return java.util.Collections.unmodifiableList(result);
		}		
		return  _${name(field)}_${label};
	}
		
	
	public void set${JavaName(field)}_${label}(java.util.List<${type(field.xrefLabels[label_index])}> ${name(field)}_${label})
	{
		_${name(field)}_${label} = ${name(field)}_${label};
		//clear the object cache
		_${name(field)}_objects = null;
	}		
</#list>
public void set${JavaName(field)}_${JavaName(field.xrefField)}(List<${type(field.xrefField)}> ${name(field)}_${name(field.xrefField)})
	{
		_${name(field)} = ${name(field)}_${name(field.xrefField)};
	}
</#if>		
	
	<#elseif type_label == "file"  || type_label=="image" >
	/**
	 * get${JavaName(field)}() is a textual pointer to a file. get${JavaName(field)}AttachedFile() can be used to retrieve the full paht to this file.
	 */
	public File get${JavaName(field)}AttachedFile()
	{
		return _${name(field)}_file;
	}
	
	/**
	 * ${JavaName(field)} is a pointer to a file. Use set${JavaName(field)}AttachedFile() to attach this file so it can be 
	 * retrieved using get${JavaName(field)}AttachedFile().
	 */
	public void set${JavaName(field)}AttachedFile(File file)
	{
		_${name(field)}_file = file;
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
	
	/**
	 * Attempt to create a tuple with all non-nillable fields for this entity type set to some default values.
	 * WORK IN PROGRESS
	 * @throws DatabaseException
	 */
	public Tuple getDummyValues(Database db) throws DatabaseException{
		Tuple t = new SimpleTuple();
		t.set("type", "${JavaName(entity)}");
		<#assign return = true>
		<#list allFields(entity) as field>
			<#if field.nillable == false>
				<#if name(field) != "id" && name(field) != typefield()>
					<#if field.type == "xref" || field.type == "mref">
		//t.set("${name(field)}", db.find(${JavaName(field.xrefEntity)}.class).get(0).getId());
					<#elseif field.getType() == "enum">
		t.set("${name(field)}", "${field.getEnumOptions()[0]}");
					<#elseif settertype(field) == "Int">
		t.set("${name(field)}", 123);
					<#elseif settertype(field) == "String">
		t.set("${name(field)}", "dummy");
					<#else>
		throw new DatabaseException("Could not set dummy tuple for JavaName(entity) because field ${name(field)} of type ${field.type} is not supported (yet)");
					<#assign return = false>
					<#break>
					</#if>
				</#if>
			</#if>
		</#list>
		<#if return>
		return t;
		</#if>
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
		<#if f.type == "xref">			
			<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>		
			//set label ${label} for xref field ${JavaName(f)}
			this.set${JavaName(f)}_${label}(tuple.get${settertype(f.xrefLabels[label_index])}("${name(f)}_${name(label)}"));	
			</#list></#if>			
		</#if>				
		</#if>
	</#list>		
		}
		else if(tuple != null)
		{
	<#list allFields(entity) as f>
		<#assign type_label = f.getType().toString()>
		<#if f.type == "mref">
			//set ${JavaName(f)}
			if( tuple.getObject("${name(f)}")!= null ) 
			{
				java.util.List<${type(f.xrefField)}> values = new java.util.ArrayList<${type(f.xrefField)}>();
				java.util.List<?> mrefs = tuple.getList("${name(f)}");
				if(mrefs != null) for(Object ref: mrefs)
				{
				  	<#if JavaType(f.xrefField) == "String" >
				  		values.add((${JavaType(f.xrefField)})ref);
				  	<#else>
				  		values.add(${type(f.xrefField)}.parse${settertype(f.xrefField)}((ref.toString()));
				  	</#if>
				}							
				this.set${JavaName(f)}( values );			
			}
			<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
			//set labels ${label} for mref field ${JavaName(f)}	
			if( tuple.getObject("${name(f)}_${name(label)}")!= null ) 
			{
				java.util.List<${type(f.xrefLabels[label_index])}> values = new java.util.ArrayList<${type(f.xrefLabels[label_index])}>();
				java.util.List<?> mrefs = tuple.getList("${name(f)}_${name(label)}");
				
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
			if( strict || tuple.get${settertype(f)}("${name(f)}_${name(f.xrefField)}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${name(f)}_${name(f.xrefField)}"));		
			if( tuple.get${settertype(f)}("${name(entity)}.${name(f)}_${name(f.xrefField)}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${name(entity)}.${name(f)}_${name(f.xrefField)}"));
			//alias of xref
			if( tuple.getObject("${name(f)}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${name(f)}"));
			if( tuple.getObject("${name(entity)}.${name(f)}") != null) this.set${JavaName(f)}(tuple.get${settertype(f)}("${name(entity)}.${name(f)}"));
			//set label for field ${JavaName(f)}
			<#if f.xrefLabelNames[0] != f.xrefFieldName><#list f.xrefLabelNames as label>
			if( strict || tuple.getObject("${name(f)}_${name(label)}") != null) this.set${JavaName(f)}_${label}(tuple.get${settertype(f.xrefLabels[label_index])}("${name(f)}_${name(label)}"));			
			if( tuple.getObject("${name(entity)}.${name(f)}_${name(label)}") != null ) this.set${JavaName(f)}_${label}(tuple.get${settertype(f.xrefLabels[label_index])}("${name(entity)}.${name(f)}_${name(label)}"));		
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
		}
		//org.apache.log4j.Logger.getLogger("test").debug("set "+this);
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
			hash = hash * 31 + (_${name(field)}_${label} == null ? 0 : _${name(field)}_${label}.hashCode());			
			</#list>
		}
		else</#if>
		{
    		hash = hash * 31 + (_${name(field)} == null ? 0 : _${name(field)}.hashCode());		
		}
		<#else>
    	hash = hash * 31 + (_${name(field)} == null ? 0 : _${name(field)}.hashCode());	
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
		return "${pkey(entity).name}";
	}
	
	@Override
	public List<String> getLabelFields()
	{
		List<String> result = new ArrayList<String>();
		<#list entity.getXrefLabels() as label>
		result.add("${label}");
		</#list>
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
</#if>

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


	public static ${JavaName(entity)}Factory create()
	{
		return new ${JavaName(entity)}Factory();
	}

	//helper methods for chaining
	public static class ${JavaName(entity)}Factory<#if entity.hasAncestor()> extends ${JavaName(entity.getAncestor())}Factory</#if>
	{
		private ${JavaName(entity)} object = null;
	
		public ${JavaName(entity)}Factory()
		{
	 		this.object = new ${JavaName(entity)}();
		}
		
		public ${JavaName(entity)} create()
		{
			${JavaName(entity)} copy = object;
			object = null;
			return copy;
		}
<#if !entity.abstract><#foreach field in entity.getAllFields()>
<#assign type_label = field.getType().toString()>
		/**
		 * Allows you to chain commands like Person.firstName("x").secondName("y");
		 */
		public ${JavaName(entity)}Factory ${name(field)}(${type(field)} ${name(field)})
		{
			object.set${JavaName(field)}(${name(field)});
			return this;
		}
			
<#if type_label == "xref">
<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
		/**
		 * Allows you to chain commands like Person.firstName("x").secondName("y");
		 */		
		public ${JavaName(entity)}Factory ${name(field)}_${label}(${type(field.xrefLabels[label_index])} ${name(field)}_${label})
		{
			object.set${JavaName(field)}_${label}(${name(field)}_${label});
			return this;
		}	
		
</#list></#if>			
<#elseif type_label == "mref">
		/**
		 * Allows you to chain commands like Person.firstName("x").secondName("y");
		 */
		public ${JavaName(entity)}Factory ${name(field)}(${type(pkey(field.xrefEntity))} ... ${name(field)})
		{
			object.set${JavaName(field)}(java.util.Arrays.asList(${name(field)}));
			return this;
		}	
<#if field.xrefLabelNames[0] != field.xrefFieldName><#list field.xrefLabelNames as label>
		/**
		 * Allows you to chain commands like Person.firstName("x").secondName("y");
		 */
		public ${JavaName(entity)}Factory ${name(field)}_${label}(java.util.List<${type(field.xrefLabels[label_index])}> ${name(field)}_${label})
		{
			object.set${JavaName(field)}_${label}(${name(field)}_${label});
			return this;
		}
		
		/**
		 * Allows you to chain commands like Person.firstName("x").secondName("y");
		 */
		public ${JavaName(entity)}Factory ${name(field)}_${label}(${type(field.xrefLabels[label_index])} ... ${name(field)}_${label})
		{
			object.set${JavaName(field)}_${label}(java.util.Arrays.asList(${name(field)}_${label}));
			return this;
		}
</#list></#if>	
</#if>
</#foreach></#if>
}
</#if>



}

