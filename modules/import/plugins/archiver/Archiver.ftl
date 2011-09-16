<#macro Archiver screen>
<#assign model = screen.myModel>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
		
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
		${screen.label}
		</div>

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

<h1>Archiver</h1>
<h3>Export</h3>
A simple, but complete export of database records into CSV files packaged in a GZipped tarball.<br>
<b><i>Beware</b> that special data such as files or system tables are not included in this archive.</i><br>
Press 'Export' and wait for your download to be prepared.<br><br>

<input type="submit" value="Export" onclick="__action.value='export';return true;"/>

<#if model.download?exists>
	<a href="tmpfile/${model.download}">Download</a> (*.tar.gz format)
<#else>
	Not available.
</#if>

<br><br><hr>

<h3>Import</h3>
The reverse of export. Upload an existing archive and attempt to insert the records back into the database.<br>
<i>Remember that all regular database constraints are in effect and may prevent success.</i><br>
Select an archive, press 'Import' and wait for your data to be uploaded.<br><br>
Archive (*.tar.gz format):
<input type="file" name="importFile">
<input type="submit" value="Import" onclick="__action.value='import';return true;"/>

<br>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
