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

<h2>Reset & loading</h2>

<@action name="loadExampleData" label="load example data (may take a minute)"/>

<#-->a href="loadexampledata_4EE1D7A3E73C504183B69F7D20108853">Load example data</a--><br><br>

<@action name="resetDatabase" label="reset database (all data will be deleted!!!)"/>
<#-->a href="resetdatabase_4EE1D7A3E73C504183B69F7D20108853">Reset database</a--><br><br>
<#-->a href="resetdatabase_loadexampledata_4EE1D7A3E73C504183B69F7D20108853">Reset database & Load example data</a><br><br-->

<#if screen.console?exists>
<div style="border: 1px black; background: white; width: 100%">
${screen.console}
</div>
</#if>	
	

<h2>Current connection & settings</h2>
<table cellpadding="4" border="1">
	<tr>
		<td><h3>Option</h3></td>
		<td><h3>Value</h3></td>
	</tr>
	<#list screen.info?keys as key>
	<tr>
		<td style="font-family: Courier, 'Courier New', monospace">${key}</td>
		<td style="font-family: Courier, 'Courier New', monospace">${screen.info[key]}</td>
	</tr>
	</#list>
</table>


<h2>Manage connection & database</h2>

TODO: Allow admin user to setup a database connection! merge with some kind of larger ADMIN panel.<br>
<br>
TODO: Allow admin to run some informative queries about the database.<br>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
