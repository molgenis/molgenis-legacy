<#macro plugins_system_database_Settings screen>
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

<br><br>
<a href="loadexampledata_4EE1D7A3E73C504183B69F7D20108853">Load example data</a><br><br>
<a href="resetdatabase_4EE1D7A3E73C504183B69F7D20108853">Reset database</a><br><br>
<a href="resetdatabase_loadexampledata_4EE1D7A3E73C504183B69F7D20108853">Reset database & Load example data</a><br><br>
	
<br><br>
TODO: Allow admin user to setup a database connection! merge with some kind of larger ADMIN panel.<br>
<br>
TODO: Allow admin to run some informative queries about the database.<br>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
