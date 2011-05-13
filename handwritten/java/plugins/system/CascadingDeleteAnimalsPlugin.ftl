<#macro plugins_system_CascadingDeleteAnimalsPlugin screen>
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

<div>
	<p><em>Delete selected targets and their values</em></p>
	<div id="targetselect">
	<label for="target">Target:</label>
	<select name="target" id="target" size='20' multiple='multiple'>
		<#list screen.targetIdList as targetId>
			<#assign name = screen.getTargetName(targetId)>
			<option value="${targetId?string.computer}">${name}</option>
		</#list>
	</select>
	</div>
	<div class='row'>
		<input type="submit" class='addbutton' value="Delete" onclick="__action.value='remove';return true;"/>
	</div>
</div>

<br />
<hr />
<br />

<div>
	<p><em>Delete all animals and their values</em></p>
	<div class='row'>
		<input type="submit" class='addbutton' value="Delete" onclick="__action.value='removeAllAnimals';return true;"/>
	</div>
</div>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
