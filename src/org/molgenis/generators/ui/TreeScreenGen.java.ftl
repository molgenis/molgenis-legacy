<#--#####################################################################
Generate Table Data Gateway
* One table per concrete class
* One table per class hierarchy root (ensures id's and types)
* Associations map onto the hierarchy root
#####################################################################-->
<#include "GeneratorHelper.ftl">
<#assign entity=Name(tree.getRecord())>
<#assign screenpackage = tree.getPackageName() />
<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* File:        ${Name(model)}/screen/${tree.getName()}.java
 * Copyright:   GBIC 2000-${year?c}, all rights reserved
 * Date:        ${date}
 * 
 * generator:   ${generator} ${version}
 *
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */


<#if screenpackage = "">
package ${package};
<#else>
package ${package}.${screenpackage};
</#if>

// jdk
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;

// molgenis
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.screen.Screen;
import org.molgenis.framework.screen.tree.TreeScreen;
import org.molgenis.framework.screen.FormScreen;
import org.molgenis.framework.util.html.*;
import org.molgenis.framework.util.Pair;
import org.molgenis.framework.util.ValueLabel;

import ${name(model)}.data.types.*;



/**
 *
 */
public class ${Name(tree.className)}Tree extends TreeScreen<${entity}>
{
	private static final long serialVersionUID = 1L;
	public ${Name(tree.className)}Tree(Screen parent)
	{
		super( "${tree.getVelocityName()}", parent );
		this.setLabel("${tree.label}");
		this.setMode(FormScreen.Mode.RECORD_VIEW);
		this.resetSystemHiddenColumns();
		this.resetUserHiddenColumns();
		
<#list tree.getChildren() as subscreen>
<#assign screentype = Name(subscreen.getType().toString()?lower_case) />
		new ${package}.${subscreen.getCanonicalClassName()}${screentype}(this);
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
<#list tree.getRecord().getAllFields() as field>
		headers.add("${field.getLabel()}");
</#list>
		return headers;
	}	
	
	@Override
	public Vector<HtmlInput> getInputs(${entity} entity, boolean newrecord)
	{
		Vector<HtmlInput> inputs = new Vector<HtmlInput>();	
<#list tree.getRecord().getAllFields() as field>
	<#assign inputtype = Name(field.getType().toString())>
		//${Name(field)}
		{			
			${inputtype}Input input = new ${inputtype}Input("${name(field)}",entity.get${Name(field)}());
			input.setLabel("${field.label}");
			input.setDescription("${field.description}");
	<#if !field.isNillable()>
			input.setRequired(true);
	</#if>		
	<#if field.isHidden() || field.hidden>
			input.setHidden(true);
	</#if>
	<#if field.readOnly & field.auto>
			input.setReadonly(true); //automatic and readonly
	<#elseif field.readOnly>
			if(!newrecord || isReadonly()) input.setReadonly(true); //readonly
	<#elseif field.auto>
			if(newrecord || isReadonly()) input.setReadonly(true); //automatic
	<#else>
			input.setReadonly(isReadonly());
	</#if>
	<#if inputtype = "Enum" || inputtype = "Xref" || inputtype = "Mref">
		<#if Name(field) == Name(parentfield)>
			input.setOptions(this.setParentColumnOptions(entity));		 
		<#else>
			input.setOptions(entity.get${Name(field)}Options());
		</#if>
	</#if>
	<#if inputtype = "Xref">
		<#assign xref_entity = field.getXRefEntity()/>
		<#assign xref_field = field.getXRefField()/>
		<#assign count = 0/>
		<#foreach tree in model.getUserinterface().getCompleteSchema()>
			<#if tree.getClass().getSimpleName() != "Menu">
				<#if tree.getClass().getSimpleName() != "Plugin">
					<#if tree.getRecord().getName() == xref_entity && count == 0>
			input.setTarget("${tree.getName()}");
			input.setTargetfield("${xref_field}");
						<#assign count = 1/>
					</#if>
				<#elseif tree.getPluginType() != "org.molgenis.framework.screen.plugin.PluginScreen">
					<#if tree.getRecord().getName() == xref_entity && count == 0>
			input.setTarget("${tree.getName()}");
			input.setTargetfield("${xref_field}");
						<#assign count = 1/>
					</#if>
				</#if>
			</#if>
		</#foreach>
		</#if>
	<#if inputtype = "Xref" && parent_form?exists && parent_form.getRecord().getName() == field.getXRefEntity()>
	<#assign entity = Name(field.getXRefEntity())>
			if(newrecord)
			{
				FormScreen<${entity}> parent = (FormScreen<${entity}>)this.get("${parent_form.getVelocityName()}");
				List<${entity}> records = parent.getRecords();
				if(records.size()>0)
				{
					input.setValue(records.get(0).get${Name(field.getXRefField())}().toString());
				}			
			}
	</#if>
			inputs.add(input);
		}
</#list>	
		return inputs;
	}
	
	@Override
	public List<QueryRule> getSystemRules()
	{
		List<QueryRule> rules = new ArrayList<QueryRule>();
<#if parent_form?exists>
	<#list tree.getRecord().getAllFields() as field>	
		<#if field.getType().toString() = "xref" && parent_form.getRecord().getName() == field.getXRefEntity()>
			<#assign entity = Name(field.getXRefEntity())>
		TreeScreen<${entity}> parent = (TreeScreen<${entity}>)this.get("${parent_form.getVelocityName()}");
		List<${entity}> records = parent.getRecords();
		if(records.size()>0)
		{
			QueryRule rule = new QueryRule("${name(field)}",QueryRule.Operator.EQUALS, records.get(0).get${Name(field.getXRefField())}());
			rules.add(rule);
		}
		<#break>
		</#if>
	</#list>	
</#if>
		return rules;	
	}
	
	@Override
	public String getId(${Name(tree.getRecord())} entity) {
		return entity.get${Name(idfield)}().toString();
	}


	@Override
	public String getIdColumn() {
		return "${name(idfield)}";
	}


	@Override
	public String getLabel(${Name(tree.getRecord())} entity) {
		return entity.get${Name(labelfield)}().toString();
	}


	@Override
	public String getLabelColumn() {
		return "${name(labelfield)}";
	}


	@Override
	public String getParentColumn() {
		return "${name(parentfield)}";
	}
		
	@Override
	public TreeMap<String, Pair<String,String>> getRecords(List<${Name(tree.getRecord())}> records) {
		TreeMap<String, Pair<String,String>> allRecords = new TreeMap<String, Pair<String,String>>();
		for (${Name(tree.getRecord())} record : records) {
			if (record.getParent() == null)
				allRecords.put(record.get${Name(idfield)}().toString(), new Pair(record.get${Name(labelfield)}(), record.get${Name(parentfield)}()));
			else
				allRecords.put(record.get${Name(idfield)}().toString(), new Pair(record.get${Name(labelfield)}(), record.get${Name(parentfield)}().toString()));
		}
		return allRecords;
	}	
	
	public void resetSystemHiddenColumns()
	{
<#list tree.getRecord().getAllFields() as field>
	<#if field.isHidden() || field.hidden>
		this.systemHiddenColumns.add("${name(field)}");
	</#if>
</#list>
	}

	@Override
	public List<ValueLabel> setParentColumnOptions(${Name(tree.getRecord())} entity)
	{
		List<ValueLabel> oldOptions = entity.get${Name(parentfield)}Options();
		List<ValueLabel> newOptions = new ArrayList<ValueLabel>();
		
		for (ValueLabel vl : oldOptions)
		{
			newOptions.add(new ValueLabel(vl.getValue(),super.getParentString(vl.getValue().toString(),"")));
		}
		
		return newOptions;
		
	}
}


