<#macro plugins_breedingplugin_ManageLines screen>
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

<div id='name_part' class='row' style="width:700px">
	<label for='linename'>Line name:</label>
	<input type='text' class='textbox' name='linename' id='linename' value='<#if screen.lineName?exists>${screen.getLineName()}</#if>' />
</div>

<!-- Source -->
<div id="sourceselect" class="row" style='clear:left'>
	<label for="source">Source:</label>
	<select name="source" id="source" class="selectbox">
		<#if screen.sourceList??>
			<#list screen.sourceList as source>
				<option value="${source.id?string.computer}" <#if source.id == screen.source>selected="selected"</#if>>${source.name}</option>
			</#list>
		</#if>
	</select>
</div>

<!-- Add button -->
<div id='buttons_part' class='row'>
	<input type='submit' class='addbutton' value='Add' onclick="__action.value='addLine'" />
</div>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
