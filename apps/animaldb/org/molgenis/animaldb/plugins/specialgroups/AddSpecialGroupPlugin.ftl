<#macro org_molgenis_animaldb_plugins_specialgroups_AddSpecialGroupPlugin screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
	
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

<div id="name" class="row">
<label for="name">Name:</label>
<input type="text" name="name" id="name" class="textbox" />
</div>

<div id="label" class="row">
<label for="label">Label (optional):</label>
<select name="label" id="label" class="selectbox">
	<option value="0">&nbsp;</option>
	<#if screen.labelList?exists>
		<#list screen.labelList as lbl>
			<option value="${lbl}">${lbl}</option>
		</#list>
	</#if>
</select>
</div>

<div id="newlabel" class="row">
<label for="newlabel">Or, make a new label:</label>
<input type="text" name="newlabel" id="newlabel" class="textbox" />
</div>

<div id='buttons_part' class='row'>
<input type='submit' class='addbutton' value='Add' onclick="__action.value='addSpecialGroup'" />
</div>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
