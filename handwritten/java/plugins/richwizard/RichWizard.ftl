<#macro RichWizard screen>
<#assign model = screen.model>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">

	<input type="hidden" name="__selectedEntity">
		
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
		
		<#-- Hack to immediatly clear the message so it doesn't "stick". -->
		${screen.clearMessage()}
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->

<h2>RichWizard</h2>

<#if model.state == 'screen1'>
	Step 1 - select the types of ObservationElements you wish to import:
	<br><br>
	
	<#list model.dataTypes as dt>
		<input type="checkbox" name="dt_${dt}">${dt}<br>
	</#list>
	<br>
	<input type="submit" value="Next" onclick="__action.value='toScreen2';return true;"/>

<#elseif model.state == 'screen2'>

	
	Step 2 - select investigation, and upload data files:
	<select>
	<#list model.investigations as i>
		<option value="${i.id?c}">${i.name}</option>
	</#list>
	</select>
	
	<br><br><br>
	Fields displayed in BLACK are nillable, you don't have to fill this in per se.<br>
	<font color="red">Fields displayed in RED are not nillable, you must provide this information.</font>
	<br><br>
	Fields with xref refer to a record of another data type by name. This means you must fill in the specific 'name' here.
	<br><br>
	<h3>Selected datatypes, in import order:</h3>
	<br><br>
	TODO: hide elements, instead make a clickable bar with the import progress.. ie.<br>
	green - green - red - grey - grey - grey<br>
	<br>
	TODO: use database content! ie..<br>
	if user selects entity A with non-nillable XREF of entity B, and does not select the import of B, and there are no instances of B in the database: ERROR, or auto-select the import of B <br>
	<br><br>
	


	
	<#--list model.selectedDataTypes?keys as key-->
	<table>
		<tr>
			<#list model.entities as entity>
			<td id="${entity.name}" style="white-space: nowrap;" onclick="mopen('richwizard_${entity.name}');return true;">[ ${entity.name} ]</td>
			</#list>
		</tr>
	</table>
	
	<div onclick="setHenk('piet');">VERANDER HENK</div>
	
	
	<#list model.entities as entity>
	<div class="wizardfoldout" id="richwizard_${entity.name}">
	<table>
	<tr>
		<td class="matrixTableCell matrixRowColor1">
		<b>${entity.name}</b> has fields:<br>
		<#list entity.allFields as f>
			<#-- hide autofields (id) and Investigation references (selectbox) -->
			<#if f.auto == false && !(f.type == 'xref' && f.xrefLabelNames[0] == 'name' && f.xrefEntityName == 'Investigation')>
				<#if f.isNillable()>
					name: ${f.name}, type: ${f.type}
				<#else>
					<font color="red">name: ${f.name}, type: ${f.type}</font>
				</#if>
				<#if f.defaultValue?exists>default: ${f.defaultValue}</#if>
				<#if f.type == 'xref' && f.xrefLabelNames[0] == 'name'>
				- beware! This field refers to ${f.xrefEntityName} which is
					<#if model.entityNames?seq_contains(f.xrefEntityName) || model.uniqueAncestorsOfEntities?seq_contains(f.xrefEntityName)>
						<#if model.uniqueAncestorsOfEntities?seq_contains(f.xrefEntityName)>
							<font color="green">part of your import through inheritance</font>
						<#else>
							<font color="green">directly part of your import</font>
						</#if>
					<#else>
						<#if f.isNillable()>
							<font color="blue">not part of your import, but nillable</font>
						<#else>
							<font color="red">not part of your import</font>
						</#if>
					</#if>
				</#if>
				<#if f.type == 'enum'>ENUM: <#list f.enumOptions as e>${e}, </#list></#if>
				<br>
			</#if>
		</#list>
		<br><br>
		Example CSV:<br>
		<textarea rows="3" cols="60">${model.exampleCsvs[entity.name]}</textarea>
		<br><br>
		
		Upload by <b>file</b><br>
		<input type="file"><br>
		or <b>text</b><br>
		<textarea rows="3" cols="60" name="textarea_${entity.name}"></textarea>
		<input type="submit" value="Upload" onclick="__action.value='upload_textarea_${entity.name}';return true;"/><br>
		</td></tr>
		</table>
		</div>
	</#list>
	
	
	<br><br><br><br><br><br><br><br><br><br><br><br><br><br>
	<br><br><br><br><br><br><br><br><br><br><br><br><br><br>
	<br><br><br><br><br><br><br><br><br><br><br><br><br><br>
	<br><br><br><br><br><br><br><br><br><br><br><br><br><br>

	<input type="submit" value="Previous" onclick="__action.value='toScreen1';return true;"/>
	<input type="submit" value="Next" onclick="__action.value='toScreen3';return true;"/>
	
	<br>

<#elseif model.state == 'screen3'>

	screen 3

	<input type="submit" value="Previous" onclick="__action.value='toScreen2';return true;"/>
	<input type="submit" value="Next" onclick="__action.value='toScreen4';return true;"/>
	
</#if>


<script type="text/javascript" language="JavaScript">
    mopen('richwizard_${model.entities[0].name}');
</script>

SELECTED ENTITY:



<script type="text/javascript" language="JavaScript">
	var henk;
</script>

<script type="text/javascript" language="JavaScript">
    document.write(henk);
</script>

<script type="text/javascript" language="JavaScript"> 
	function setEntity(ent){
	var selectedEntity = ent;
	document.write('selectedEntity set to ' + selectedEntity);
	document.__selectedEntity.value = ent;
	}
</script>

<script type="text/javascript" language="JavaScript"> 
	function setHenk(jaap){
	var henk = jaap;
	}
</script>

<script type="text/javascript" language="JavaScript">
    document.write(document.__selectedEntity.value);
</script>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
