<#macro org_molgenis_dsleditor_DSLEditor screen>
<#assign model = screen.VO>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
	<input type="hidden" name="__selectName">
	<input type="hidden" name="__selectFieldEntity">
	<input type="hidden" name="__selectFieldIndex">
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
		${screen.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list screen.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	

<#--BAD!!! MOVE TO CSS -->
<#assign submitStyle = "
background: transparent;
border-top: 1;
border-right: 1;
border-bottom: 1;
border-left: 1;
color: #0000FF;
display: inline;
margin: 0;
padding: 2;
width: 90px;
text-align: left;
">

<#--BAD!!! how to fix?? (These strings are the spaces in front of the element buttons in the treeview) -->
<#assign moduleNbsp=" - - -">
<#assign entityNbsp=" - - - - - -">
<#assign fieldNbsp=" - - - - - - - - -">

<h2>Build model</h2>


<table cellpadding="5"><tr>

<td>
<h3>View</h3>
<div style="background: #DEDEDE; width: 300px;">
<br>


<input type="submit" style="${submitStyle} <#if model.selectType == "molgenis" && model.selectName == model.molgenisModel.name>background: lightblue;</#if>" onclick="__action.value='editMolgenis';__selectName.value='${model.molgenisModel.name}';return true;" value="${model.molgenisModel.name}"><br>
<#list model.molgenisModel.modules as module>
${moduleNbsp}<input type="submit" style="${submitStyle} <#if model.selectType == "module" && model.selectName == module.name>background: lightblue;</#if>" onclick="__action.value='editModule';__selectName.value='${module.name}';return true;" value="${module.name}"><br>

	<#list module.entities as entity>
	${entityNbsp}<input type="submit" style="${submitStyle} <#if model.selectType == "entity" && model.selectName == entity.name>background: lightblue;</#if>" onclick="__action.value='editEntity';__selectName.value='${entity.name}';return true;" value="${entity.name}"><#--input type="image" src="res/img/cancel.png" onclick="__action.value='removeEntity';__selectName.value='${entity.name}';return true;"/--><br>
	
		<#list entity.fields as field>
		${fieldNbsp}<input type="submit" style="${submitStyle} <#if model.selectFieldEntity?exists && model.selectFieldIndex?exists && model.selectType == "field"  && model.selectFieldEntity == entity.name && model.selectFieldIndex == field_index>background: lightblue;</#if>" onclick="__action.value='editField';__selectName.value='${field.name}';__selectFieldEntity.value='${entity.name}';__selectFieldIndex.value='${field_index}';return true;" value="${field.name}"><br>
		</#list>
		
	</#list>

</#list>

<font color="red">- - Below: entities not in modules - -</font><br>

<#list model.molgenisModel.entities as entity>
${moduleNbsp}<input type="submit" style="${submitStyle} <#if model.selectType == "entity" && model.selectName == entity.name>background: lightblue;</#if>" onclick="__action.value='editEntity';__selectName.value='${entity.name}';return true;" value="${entity.name}"><br>

	<#list entity.fields as field>
	${entityNbsp}<input type="submit" style="${submitStyle} <#if model.selectFieldEntity?exists && model.selectFieldIndex?exists && model.selectType == "field"  && model.selectFieldEntity == entity.name && model.selectFieldIndex == field_index>background: lightblue;</#if>" onclick="__action.value='editField';__selectName.value='${field.name}';__selectFieldEntity.value='${entity.name}';__selectFieldIndex.value='${field_index}';return true;" value="${field.name}"><br>
	</#list>

</#list>

<br>
</div>

</td>


<td>
<h3>Modify</h3>
<div style="background: #DEDEDE; width: 300px;">

<#-- 	
Depending on the type of element that is selected, the modify panel will adapt to the proper behaviour.
This information is stored in the selectType variable, which is in the XMLBuilderModel.java	
-->


Selected: '${model.selectType}' <b>${model.selectName}</b><#if model.selectType == 'field'><br>(field #${model.selectFieldIndex+1}</b> in <i>${model.selectFieldEntity}</i>)</#if><br><br>

<#if model.selectType == "molgenis">
	<input type="submit" value="Add new module" onclick="__action.value='addModuleToMolgenis';return true;"/><br><br>
	<input type="submit" value="Add new entity" onclick="__action.value='addEntityToMolgenis';return true;"/><br><br>
	<input type="submit" value="Reset" onclick="if (confirm('You are about to start with a new EMPTY model. Are you sure?')) { __action.value = 'resetModel'; } else { return false; }"/><br><br>
	
	<b>Edit this molgenis:</b><br>
	<table>
		<tr>
			<td>Name</td><td><input type="text" name="molgenisName" value="${model.molgenisModel.name}"></td>
		</tr>
		<tr>
			<td>Label</td><td><input type="text" name="molgenisLabel" value="<#if model.molgenisModel.label?exists>${model.molgenisModel.label}</#if>"></td>
		</tr>
		<tr>
			<td>Version</td><td><input type="text" name="molgenisVersion" value="<#if model.molgenisModel.version?exists>${model.molgenisModel.version}</#if>"></td>
		</tr>
		<tr>
			<td>&nbsp;</td><td align="right"><input type="submit" value="Save" onclick="__action.value='saveMolgenis';return true;"/></td>
		</tr>
	</table>
	
<#elseif model.selectType == "module">
	<#assign module = model.molgenisModel.getModule(model.selectName)>
	<input type="submit" value="Add new entity" onclick="__action.value='addEntityToModule';__selectName.value='${module.name}';return true;"/><br><br>
	<input type="submit" value="Delete this module" onclick="__action.value='removeModuleFromMolgenis';__selectName.value='${module.name}';return true;"/><br><br>
	
	<b>Edit this module:</b><br>
	<table>
		<tr>
			<td>Name</td><td><input type="text" name="moduleName" value="${module.name}"></td>
		</tr>
		<tr>
			<td>&nbsp;</td><td align="right"><input type="submit" value="Save" onclick="__action.value='saveModule';__selectName.value='${module.name}';return true;"/></td>
		</tr>
	</table>
<#elseif model.selectType == "entity">
	<#assign entity = model.molgenisModel.findEntity(model.selectName)>
	<input type="submit" value="Add new field" onclick="__action.value='addFieldToEntity';__selectName.value='${entity.name}';return true;"/><br><br>
	<input type="submit" value="Delete this entity" onclick="__action.value='removeEntity';__selectName.value='${entity.name}';return true;"/><br><br>
	
	<b>Edit this entity:</b><br>
	<table>
		<tr>
			<td>Name</td><td><input type="text" name="entityName" value="${entity.name}"></td>
		</tr>
		<tr>
			<td>Label</td><td><input type="text" name="entityLabel" value="<#if entity.getLabel()?exists>${entity.getLabel()}<#else></#if>"></td>
		</tr>
		<tr>
			<td>Extends</td><td><input type="text" name="entityExtends" value="<#if entity.extends?exists>${entity.extends}<#else></#if>"></td>
		</tr>
		<tr>
			<td>Implements</td><td><input type="text" name="entityImplements" value="<#if entity.implements?exists>${entity.implements}<#else></#if>"></td>
		</tr>
		<tr>
			<td>Decorator</td><td><input type="text" name="entityDecorator" value="<#if entity.decorator?exists>${entity.decorator}<#else></#if>"></td>
		</tr>
		<tr>
			<td>Abstract</td>
			
			<td>
			<input type="radio" name="abstract" value="true" <#if entity.isAbstract()?exists><#if entity.isAbstract()>checked</#if></#if>>True <input type="radio" name="abstract" value="false" <#if entity.isAbstract()?exists><#if !entity.isAbstract()>checked</#if><#else>checked</#if>>False
			</td>
		</tr>
			
		<tr>
			<td>&nbsp;</td><td align="right"><input type="submit" value="Save" onclick="__action.value='saveEntity';__selectName.value='${entity.name}';return true;"/></td>
		</tr>
	</table>

<#elseif model.selectType == "field">
	<#assign field = model.molgenisModel.findEntity(model.selectFieldEntity).fields[model.selectFieldIndex]>
	<input type="submit" value="Delete this field" onclick="__action.value='removeField';__selectFieldEntity.value='${model.selectFieldEntity}';__selectFieldIndex.value='${model.selectFieldIndex}';return true;"/><br><br>
	
	<b>Edit this field:</b><br>
	<table>
		<tr>
			<td>Name</td><td><input type="text" name="fieldName" value="${model.selectName}"></td>
		</tr>
		<tr>
			<td>Type</td>
			<td>
			<select name="fieldTypeSelect">
				<#list model.fieldTypes as ft>
					<option <#if field.type?exists && field.type == ft>SELECTED</#if> value="${ft}">${ft}</option>
				</#list>
				</select>
			</td>
		</tr>
		<tr>
			<td>Label</td><td><input type="text" name="fieldLabel" value="<#if field.label?exists>${field.label}</#if>"></td>
		</tr>
		
		<tr>
			<td>Unique</td>
			<td>
				<input type="radio" name="fieldUnique" value="true" <#if field.getUnique()?exists><#if field.getUnique()>checked</#if></#if>/>True <input type="radio" name="fieldUnique" value="false" <#if field.getUnique()?exists><#if !field.getUnique()>checked</#if><#else>checked</#if>>False
			</td>
		</tr>			
		
		<tr>
			<td>Nillable</td>
			<td>
				<input type="radio" name="fieldNillable" value="true" <#if field.getNillable()?exists><#if field.getNillable()>checked</#if></#if>/>True <input type="radio" name="fieldNillable" value="false" <#if field.getNillable()?exists><#if !field.getNillable()>checked</#if><#else>checked</#if>>False
			</td>
		</tr>
		
		<tr>
			<td>Readonly</td>
			<td>
				<input type="radio" name="fieldReadonly" value="true" <#if field.getReadonly()?exists><#if field.getReadonly()>checked</#if></#if>/>True <input type="radio" name="fieldReadonly" value="false" <#if field.getReadonly()?exists><#if !field.getReadonly()>checked</#if><#else>checked</#if>>False
			</td>
		</tr>
		
		<tr>
			<td>Length</td><td><input type="text" name="fieldLength" value="<#if field.getLength()?exists>${field.getLength()}</#if>"></td>
		</tr>
		
		<tr>
			<td>Xref_field</td><td><input type="text" name="xref_field" value="<#if field.getXrefField()?exists>${field.getXrefField()}</#if>"></td>
		</tr>
		
		<tr>
			<td>Xref_label</td><td><input type="text" name="xref_label" value="<#if field.getXrefLabel()?exists>${field.getXrefLabel()}</#if>"></td>
		</tr>
			
				
		<#--TODO: add xref_cascade: DEFAULT VALUE???: xref_cascade="true|false": this will enable cascading deletes which means that is the related element is deleted this entity will be deleted as well. -->
			
		<tr>
			<td>Enum options</td><td><input type="text" name="enum_options" value="<#if field.getEnumoptions()?exists>${field.getEnumoptions()}</#if>"></td>
		</tr>	
			
		<tr>
			<td>Description</td><td><input type="text" name="fieldDescription" value="<#if field.getDescription()?exists>${field.getDescription()}</#if>"></td>
		</tr>
					
		<tr>
			<td>&nbsp;</td><td align="right"><input type="submit" value="Save" onclick="__action.value='saveField';__selectFieldEntity.value='${model.selectFieldEntity}';__selectFieldIndex.value='${model.selectFieldIndex}';return true;"/></td>
		</tr>
	</table>

</#if>


	</div>
</td>

</tr>

</table>
<br>

<#-- This will show the table which contains the preview window and the buttons for showing the xml and loading xml into the model  -->

<h2>Validate & make XML</h2>

<div style="background: #DEDEDE; width: 600px; height: 400px;">
<br>
<table cellpadding="5"><tr>
<td>
<input type="submit" value="Validate model & show as XML" onclick="__action.value='toXml';return true;"/>
<input type="submit" value="Parse XML & show as model" onclick="__action.value='fromXml';return true;"/>
</td>
</tr>
<tr><td>
<#-- The next line writes the model into the textarea -->
<textarea rows="25" cols="88" name="xmlWindow"><#if model.xmlPreview?exists>${model.xmlPreview}</#if></textarea>
</td>
</tr>
</table>
</div>


	
			</div>
		</div>
	</div>
</form>
</#macro>