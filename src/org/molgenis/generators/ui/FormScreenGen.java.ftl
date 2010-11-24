<#--#####################################################################
Generate Table Data Gateway
* One table per concrete class
* One table per class hierarchy root (ensures id's and types)
* Associations map onto the hierarchy root
#####################################################################-->
<#include "GeneratorHelper.ftl">
<#assign entity=Name(form.getRecord())>
<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* File:        ${Name(model)}/screen/${form.getName()}.java
 * Copyright:   GBIC 2000-${year?c}, all rights reserved
 * Date:        ${date}
 * 
 * generator:   ${generator} ${version}
 *
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */
package ${package};

// jdk
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

// molgenis
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.QueryRule;

import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.html.*;


${imports(model, model.getEntity(entity), "")}
${imports(model, model.getEntity(entity), "csv", "CsvReader")}

<#if parent_form?exists>
//imports parent forms
<#assign xrefentity = parent_form.getRecord()>
import ${xrefentity.getNamespace()}.${Name(xrefentity)};
</#if>

/**
 *
 */
public class ${Name(form.className)}Form extends FormModel<${entity}>
{
	private static final long serialVersionUID = 1L;
	
	public ${Name(form.className)}Form()
	{
		this(null);
	}
	
	public ${Name(form.className)}Form(ScreenModel parent)
	{
		super( "${form.getVelocityName()}", parent );
		this.setLabel("${form.label}");
		this.setLimit(${form.limit});

		<#if form.sortby?exists>
		//sort is a bit hacky awaiting redesign of the Form classes
		try
		{
			((FormController)this.getController()).getPager().setOrderByField("${form.sortby}".toLowerCase());
			((FormController)this.getController()).getPager().setOrderByOperator(Operator.SORT${form.sortorder});
			this.setSort("${form.sortby}");
		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		</#if>	
		this.setMode(FormModel.Mode.${form.viewType});
		this.setCsvReader(new ${entity}CsvReader());

<#-- parent form filtering -->
<#assign parent_xref = false>		
<#if parent_form?exists>
<#assign xrefentity = Name(parent_form.getRecord())>		
<#list form.getRecord().getAllFields() as field>
	<#--if subform entity refers to parent form entity: show only records that point to parent record-->
	<#--if multiple references exist, then use union, so 'OR' in query rule-->
	<#if field.getType() == "xref" || field.getType() == "mref">
		<#list superclasses(parent_form.getRecord()) as parent_entity>
			<#if parent_entity.getName() == field.xrefEntityName>
		//filter on <#if field.getType() == "mref">ANY </#if>subform_entity.${name(field)} == parentform_entity.${name(field.xrefField)}
		this.getParentFilters().add(new ParentFilter("${parent_form.name}","${SqlName(field.xrefField)}",Arrays.asList("${csv(field.xrefLabelNames)}".split(",")),"${SqlName(name(field))}"));
			</#if>
		</#list>
	</#if>
</#list>
<#--parent to subform xrefs-->		
<#list parent_form.getRecord().getAllFields() as field>
	<#--if parent entity refers to subform form entity: show only records that are pointed to by parent record-->
	<#--if multiple references exist, then use union, so 'OR' in query rule-->
	<#if field.getType() == "xref" || field.getType() == "mref">
		<#list superclasses(form.getRecord()) as subform_entity>
			<#if subform_entity.getName() == field.xrefEntityName>
		//filter on subform_entity.${name(field.xrefField)} == <#if field.getType() == "mref">ANY </#if> parentform_entity.${name(field)}
		this.getParentFilters().add(new ParentFilter("${parent_form.name}","${SqlName(name(field))}",Arrays.asList("${csv(field.xrefLabelNames)}".split(",")),"${SqlName(field.xrefField)}"));		
			</#if>
		</#list>
	</#if>
</#list>
</#if>	
<#list form.commands as command>
		this.addCommand(new ${command}(this));
</#list>		
<#if form.readOnly>
		this.setReadonly(true);
</#if>

<#list form.getChildren() as subscreen>
		<#assign screentype = Name(subscreen.getType().toString()?lower_case) />
		new ${package}.${JavaName(subscreen)}${screentype}(this);
</#list>		
	}
	
	@Override
	public Class<${entity}> getEntityClass()
	{
		return ${entity}.class;
	}
	
	@Override
	public Vector<String> getHeaders()
	{
		Vector<String> headers = new Vector<String>();
<#list form.getRecord().getAllFields() as field>
		headers.add("${field.getLabel()}");
</#list>
		return headers;
	}	
	
	@Override
	public HtmlForm getInputs(${entity} entity, boolean newrecord)
	{
		HtmlForm form = new HtmlForm();
		form.setNewRecord(newrecord);
		form.setReadonly(isReadonly());
		
		List<HtmlInput> inputs = new ArrayList<HtmlInput>();			
<#list allFields(model.getEntity(entity)) as field>
	<#assign inputtype = Name(field.getType().toString())>
		//${JavaName(field)}: ${field}
		{
		    <#if field.type == "xref" >
		    	//xref
				${inputtype}Input input = new ${inputtype}Input("${name(field)}",entity.get${JavaName(field)}_${Name(field.getXrefField())}());
			<#else>
				${inputtype}Input input = new ${inputtype}Input("${name(field)}",entity.get${JavaName(field)}());
			</#if>
			
			input.setLabel("${field.label}");
			input.setDescription("${escapeXml(field.description)}");
			<#if field.isNillable() && field.type != "file"><#--whether files are filled in is only checked in the db-->
			input.setNillable(true);
			<#else>
			input.setNillable(false);
			</#if>		
			<#if field.length?exists>
			input.setSize(${field.length?c});
			</#if>
			<#if field.readOnly && field.auto>
			input.setReadonly(true); //automatic fields that are readonly, are also readonly on newrecord
			<#elseif field.readOnly>
			if(!newrecord || isReadonly() || entity.isReadonly()) input.setReadonly(true); //readonly if not new record
			<#else>
			input.setReadonly( isReadonly() || entity.isReadonly());
			</#if>
			<#if inputtype = "Enum">
			input.setOptions(entity.get${JavaName(field)}Options());
			</#if>	
			<#if inputtype = "Xref" || inputtype = "Mref">
			<#assign xref_entity = field.xrefEntity>
			input.setXrefEntity("${xref_entity.getNamespace()}.${JavaName(field.xrefEntity)}");
			input.setXrefField("${name(field.xrefField)}");
			input.setXrefLabels(Arrays.asList("${csv(field.xrefLabelNames)}".split(",")));
			//initialize the ${field.xrefEntityName}.${csv(field.xrefLabelNames)} of current record
			<#if field.xrefLabelNames[0] != field.xrefFieldName>
				<#list field.xrefLabelNames as label>
			input.setValueLabel<#if inputtype = "Mref">s</#if>("${label}", entity.get${JavaName(field)}_${label}()); 
				</#list>
			<#else>
			input.setValueLabel<#if inputtype = "Mref">s</#if>("${field.xrefField.name}", entity.get${JavaName(field)}());
			</#if>
			</#if>
			<#if field.hidden || form.hideFields?seq_contains(field.name)>		
			input.setHidden(<#if (field.auto && field.readOnly) || (field.defaultValue?exists)>true<#else>!newrecord</#if>);
			</#if>			
			<#if form.getCompactView()?size &gt; 0 && !form.getCompactView()?seq_contains(field.name)>
			input.setCollapse(true);
			</#if>
			inputs.add(input);
		}
</#list>	
		form.setInputs(inputs);
		return form;
	}
	
	public void resetSystemHiddenColumns()
	{
<#list form.getRecord().getAllFields() as field>
	<#if field.isHidden() || field.hidden>
		this.systemHiddenColumns.add("${name(field)}");
	</#if>
</#list>
	}

	@Override	
	public String getSearchField(String fieldName)
	{
<#list form.getRecord().getAllFields() as field>
	<#if field.type="xref" || field.type="mref">
		<#list field.xrefLabelNames?reverse as label>
		if(fieldName.equals("${name(field)}")) return "${name(field)}_${name(label)}";
		</#list>
	</#if>
</#list>	
		return fieldName;
	}	
	
	@Override
	public void resetCompactView()
	{
		this.compactView = new ArrayList<String>();
<#list form.getCompactView() as field_name>
		this.compactView.add("${field_name}");
</#list>	
	}
}


