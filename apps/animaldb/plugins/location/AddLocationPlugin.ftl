<#macro plugins_location_AddLocationPlugin screen>
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

<div id="superlocation" class="row">
<label for="superlocation">Sublocation of:</label>
<select name="superlocation" id="superlocation" class="selectbox">
	<option value="0">&nbsp;</option>
	<#list screen.locationList as ll>
		<option value="${ll.id?string.computer}">${ll.name}</option>
	</#list>
</select>
</div>

<div id='buttons_part' class='row'>
<input type='submit' class='addbutton' value='Add' onclick="__action.value='addLocation'" />
</div>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
