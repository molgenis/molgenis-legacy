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

<h1>Import data</h1>
<em>Caution: this might interfere with existing database items!</em>

<div>
    <br />
    <hr />
    <br />
</div>

<h2>GIDS</h2>

<div id="deleteAllFromDatabase" class="row">
	<input type='submit' class='addbutton' value='emptyDB' onclick="__action.value='emptyDB'" />
</div>

<div style="margin-top:20px" id="readIndividuals" class="row">
	<label for="readind">Choose your directory with the individual files:</label>
	<input style="margin-left:34px" type="file" name="readind" id="readind"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</div>

<div id="readMeasurement" class="row">
	<label for="readmeas">Choose your directory with the measurement files:</label>&nbsp;&nbsp;&nbsp;
	<input  type="file" name="readmeas" id="readmeas" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

</div>

<div id="readValues" class="row">
	<label for="readval">Choose your directory with the observedvalues files:</label>
	<input style="margin-left:3px" type="file" name="readval" id="readval"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

</div>

<div style="padding-left:418px; margin-top:10px" id="submitbutton" class="row">

	<input style="background-color:#5B82A4; color:white" type='submit' name='submittie' class='addbutton' value='load files into db' onclick="__action.value='loadAll'" />
</div>


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
