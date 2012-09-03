/* Date:        March 22, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.dsleditor;

import java.util.List;

import org.molgenis.model.jaxb.Model;

public class DSLEditorModel
{

	Model molgenisModel;

	String selectType;
	String selectName;

	String selectFieldEntity;
	Integer selectFieldIndex;

	String xmlPreview;
	List<String> fieldTypes;

	public Model getMolgenisModel()
	{
		return molgenisModel;
	}

	public void setMolgenisModel(Model molgenisModel)
	{
		this.molgenisModel = molgenisModel;
	}

	public String getSelectType()
	{
		return selectType;
	}

	public void setSelectType(String selectType)
	{
		this.selectType = selectType;
	}

	public String getSelectName()
	{
		return selectName;
	}

	public void setSelectName(String selectName)
	{
		this.selectName = selectName;
	}

	public String getSelectFieldEntity()
	{
		return selectFieldEntity;
	}

	public void setSelectFieldEntity(String selectFieldEntity)
	{
		this.selectFieldEntity = selectFieldEntity;
	}

	public Integer getSelectFieldIndex()
	{
		return selectFieldIndex;
	}

	public void setSelectFieldIndex(Integer selectFieldIndex)
	{
		this.selectFieldIndex = selectFieldIndex;
	}

	public String getXmlPreview()
	{
		return xmlPreview;
	}

	public void setXmlPreview(String xmlPreview)
	{
		this.xmlPreview = xmlPreview;
	}

	public List<String> getFieldTypes()
	{
		return fieldTypes;
	}

	public void setFieldTypes(List<String> fieldTypes)
	{
		this.fieldTypes = fieldTypes;
	}

}