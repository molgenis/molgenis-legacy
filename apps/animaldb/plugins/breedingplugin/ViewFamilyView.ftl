<#macro plugins_breedingplugin_ViewFamily screen>
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

<#if screen.action == "init">

<div id="animalselect" class="row">
<label for="animal">Animal:</label>
<select name="animal" id="animal" class="selectbox">
	<#list screen.animalIdList as animalId>
		<#assign name = screen.getAnimalName(animalId)>
		<option value="${animalId?string.computer}">${name}</option>
	</#list>
</select>
</div>

<div id='buttons_part' class='row'>
	<input type='submit' class='addbutton' value='Show family info' onclick="__action.value='reqInfo'" />
</div>

<#else>

<p>${screen.info}</p>

<div id='buttons_part' class='row'>
	<input type='submit' class='addbutton' value='Reset' onclick="__action.value='init'" />
</div>

</#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
