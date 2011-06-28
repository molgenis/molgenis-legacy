<#include "GeneratorHelper.ftl">

<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->
/* File:        ${Name(model)}/html/${entity.getName()}.java
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
import org.molgenis.framework.ui.html.*;


${imports(model, entity, "")}
${imports(model, entity, "csv", "CsvReader")}

/**
 * A HtmlForm that is preloaded with all inputs for entity ${JavaName(entity)}
 * @see EntityForm
 */
public class ${JavaName(entity)}Form extends EntityForm<${JavaName(entity)}>
{
	private static final long serialVersionUID = 1L;
	
	public ${JavaName(entity)}Form()
	{
		super();
	}
	
	public ${JavaName(entity)}Form(${JavaName(entity)} entity)
	{
		super(entity);
	}
	
	
	@Override
	public Class<${JavaName(entity)}> getEntityClass()
	{
		return ${JavaName(entity)}.class;
	}
	
	@Override
	public Vector<String> getHeaders()
	{
		Vector<String> headers = new Vector<String>();
<#list entity.getAllFields() as field>
		headers.add("${field.getLabel()}");
</#list>
		return headers;
	}	
	
	@Override
	public List<HtmlInput> getInputs()
	{	
		List<HtmlInput> inputs = new ArrayList<HtmlInput>();			
<#list allFields(entity) as field>
	<#assign inputtype = Name(field.getType().toString())>
		//${JavaName(field)}: ${field}
		{
		    <#if field.type == "xref" >
		    	//xref
				${inputtype}Input input = new ${inputtype}Input("${entity.name}_${field.name}",getEntity().get${JavaName(field)}_${JavaName(field.getXrefField())}());
			<#else>
				${inputtype}Input input = new ${inputtype}Input("${entity.name}_${field.name}",getEntity().get${JavaName(field)}());
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
			//FIXME: this should be moved to login?
			//readonly, except when new record without default, unless whole entity is readonly
			if( !(isNewRecord() && "".equals(input.getValue())) || getEntity().isReadonly()) input.setReadonly(true); 
			<#else>
			input.setReadonly( isReadonly() || getEntity().isReadonly());
			</#if>
			<#if inputtype = "Enum">
			input.setOptions(getEntity().get${JavaName(field)}Options());
			</#if>	
			<#if inputtype = "Xref" || inputtype = "Mref">
			<#assign xref_entity = field.xrefEntity>
			input.setXrefEntity("${xref_entity.getNamespace()}.${JavaName(field.xrefEntity)}");
			input.setXrefField("${name(field.xrefField)}");
			input.setXrefLabels(Arrays.asList("${csv(field.xrefLabelNames)}".split(",")));
			//initialize the ${field.xrefEntityName}.${csv(field.xrefLabelNames)} of current record
			<#if field.xrefLabelNames[0] != field.xrefFieldName>
				<#list field.xrefLabelNames as label>
			input.setValueLabel<#if inputtype = "Mref">s</#if>("${label}", getEntity().get${JavaName(field)}_${JavaName(label)}()); 
				</#list>
			<#else>
			input.setValueLabel<#if inputtype = "Mref">s</#if>("${field.xrefField.name}", getEntity().get${JavaName(field)}_${JavaName(field.getXrefField())}());
			</#if>
			ActionInput addButton = new ActionInput("add", "Add", "Add"); // ${field.label}
			addButton.setIcon("generated-res/img/new.png");
			addButton.setJavaScriptAction("if( window.name == '' ){ window.name = 'molgenis'+Math.random();}document.forms.${JavaName(entity)}_form.__target.value='${JavaName(entity)}';document.forms.${JavaName(entity)}_form.__action.value='${entity.name}_${field.name}';molgenis_window = window.open('','molgenis_edit_new_xref','height=800,width=600,location=no,status=no,menubar=no,directories=no,toolbar=no,resizable=yes,scrollbars=yes');document.forms.${JavaName(entity)}_form.target='molgenis_edit_new_xref';document.forms.${JavaName(entity)}_form.__show.value='popup';document.forms.${JavaName(entity)}_form.submit();molgenis_window.focus();");
			input.setIncludeAddButton(true);
			input.setAddButton(addButton);
			</#if>
			<#if field.hidden>
			input.setHidden(<#if (field.auto && field.readOnly) || (field.defaultValue?exists)>true<#else>!isNewRecord()</#if>);
			<#else>
			if(this.getHiddenColumns().contains(input.getName()))
			{	
				input.setHidden(<#if (field.auto && field.readOnly) || (field.defaultValue?exists)>true<#else>!isNewRecord()</#if>);
			}
			</#if>
			if(this.getCompactView().size() > 0 && !this.getCompactView().contains(input.getName()))
			{
				input.setCollapse(true);
			}

			inputs.add(input);
		}
</#list>	

		return inputs;
	}
}


