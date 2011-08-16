<#macro plugins_animal_RemAnimalPlugin screen>
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

<div id="animaltermform">

<div id="animalselect" class="row">
<label for="animal">Animal:</label>
<select name="animal" id="animal" class="selectbox" onchange="getExperiment(this);">
	<option value="0">&nbsp;</option>
	<#list screen.animalIdList as animalId>
		<#assign name = screen.getAnimalName(animalId)>
		<option value="${animalId?string.computer}">${name}</option>
	</#list>
</select>
</div>

<div class='row'>
	<label for='removal'>Death or kind of removal:</label>
	<select name='removal' id='removal'>
	<#list screen.removalCodeList as rcl>
		<option value="${rcl.description}">${rcl.code} (${rcl.description})</option>
	</#list>
	</select>
</div>

<div class="row">
	<label for="deathdate">Date of death or removal:</label>
	<input type='text' class='textbox' id='deathdate' name='deathdate' value='' onclick='showDateInput(this)' autocomplete='off' />
</div>

<div id="experimentfields">
<!-- This box is filled dynamically by the TerminateAnimalsServlet (Ajax-style) -->
</div>

</div>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
