<#macro plugins_fillanimaldb_PreFillDatabasePlugin screen>
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

<h1>Pre-fill database with factory defaults, needed to make the system run</h1>
<em>Caution: all existing database items will be removed first!</em>

<div id='buttons_part1' class='row'>
	<input type='submit' class='addbutton' value='Pre-fill database' onclick="__action.value='fillDB'" />
</div>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
