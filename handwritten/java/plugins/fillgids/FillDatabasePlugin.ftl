<#macro plugins_fillgids_FillDatabasePlugin screen>
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

<h1>Load data from legacy systems</h1>
<em>Caution: this might interfere with existing database items!</em>

<div>
    <br />
    <hr />
    <br />
</div>

<h2>GIDS</h2>
<div id="loaddirectorydiv" class="row">
<label for="loadingdirectories">Choose your directory with the files:</label>
	<input type="text" name="loadingdirectories" id="loadingdirectories" class="textbox" value="/Users/roankanninga/Documents/NewMolgenis/molgenis_apps/handwritten/java/convertors/gids/importFiles" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input type='submit' class='addbutton' value='Loading' onclick="loadingdirectories.getText()" />
</div>

<#--<div id="ulilinetablediv" class="row">
	<label for="ulilinetable">'Linie' (Line) table CSV export file:</label>
	<input type="file" name="ulilinetable" id="ulilinetable" class="textbox" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input type='submit' class='addbutton' value='Load lines from old version' onclick="__action.value='loadUliLines'" />
</div>-->


<div>
    <br />
    <hr />
    <br />
</div>

	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
