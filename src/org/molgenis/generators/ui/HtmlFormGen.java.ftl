<#--helper functions-->
<#include "GeneratorHelper.ftl">

<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* File:        ${model.getName()}/html/${entity.getName()}Form.java
 * Copyright:   GBIC 2000-${year?c}, all rights reserved
 * Date:        ${date}
 * 
 * generator:   ${generator} ${version}
 *
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package ${package};

import java.util.Vector;

import org.molgenis.framework.ui.html.*;
${imports(model,entity,"")};

public class ${JavaName(entity)}HtmlForm extends HtmlForm<${JavaName(entity)}>
{
	public ${JavaName(entity)}HtmlForm(${JavaName(entity)} entity)
	{
		super(entity);
	}

	@Override
	public Vector<HtmlInput> getInputs()
	{
		Vector<HtmlInput> inputs = new Vector<HtmlInput>();
		${JavaName(entity)} entity = this.getEntity();			
<#list allFields(entity) as field>
	<#assign inputtype = Name(field.getType().toString())>
		//${JavaName(field)}: ${field}
		{
			${inputtype}Input input = new ${inputtype}Input("${name(field)}",entity.get${JavaName(field)}());
			input.setLabel("${field.label}");
			input.setDescription("${escapeXml(field.description)}");
			<#if field.isNillable() && field.type != "file"><#--whether files are filled in is only checked in the db-->
			input.setNillable(true);
			<#else>
			input.setNillable(false);
			</#if>		
			<#if field.readOnly && field.auto>
			input.setReadonly(true); //automatic fields that are readonly, are also readonly on newrecord
			<#elseif field.readOnly>
			if(!isNewRecord() || isReadonly() || entity.isReadonly()) input.setReadonly(true); //readonly if not new record
			<#else>
			input.setReadonly( isReadonly() || entity.isReadonly());
			</#if>
			<#if inputtype = "Enum">
			input.setOptions(entity.get${JavaName(field)}Options());
			</#if>	
			<#if inputtype = "Xref" || inputtype = "Mref">
			<#assign xref_entity = model.getEntity(field.XRefEntity)>
			input.setXrefEntity("${xref_entity.getNamespace()}.${field.XRefEntity}");
			input.setXrefField("${name(field.XRefField)}");
			input.setXrefLabel("${name(field.XRefLabelString)}");
			//initialize the ${field.XRefEntity}.${field.XRefLabelString} of current record
			input.setValueLabel<#if inputtype = "Mref">s</#if>(entity.get${JavaName(field)}Label<#if inputtype = "Mref">s</#if>()); 
			</#if>
			<#if field.hidden>		
			input.setHidden(<#if (field.auto && field.readOnly) || (field.defaultValue?exists)>true<#else>!isNewRecord()</#if>);
			</#if>			
			inputs.add(input);
		}
</#list>	
		return inputs;
	}
}