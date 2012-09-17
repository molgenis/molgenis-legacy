<#macro org_molgenis_animaldb_plugins_system_SetCustomLabelFeaturePlugin screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
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
<#--begin your plugin-->	

<div class='row'>
	<label for="label">Current label:</label>
	<input readonly="readonly" name="label" id="label" value="${screen.getCurrentLabel()}">
</div>
<div id="featureselect" class="row">
	<label for="feature">Observable feature to be used as custom label for animals:</label>
	<select name="feature" id="feature" class="selectbox">
		<option value="-1">name</option>
		<#list screen.measurementList as measurement>
			<option value="${measurement.id?string.computer}">${measurement.name}</option>
		</#list>
	</select>
</div>
<div class='row'>
	<input type="submit" class='addbutton' value="Set as label" onclick="__action.value='setLabel';return true;"/>
</div>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
