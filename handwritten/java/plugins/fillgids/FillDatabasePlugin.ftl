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

<div id="readInvestigations" class="row">
<label for="readinv">Choose your directory with the investigation files:</label>

	<input style="margin-right:50px" type="file" name="readinv" id="readinv"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input type='submit' class='addbutton' value='load investigations' onclick="__action.value='loadinv'" />
</div>

<div id="readIndividuals" class="row">
	<label for="readind">Choose your directory with the individual files:</label>
	<input style="margin-right:50px" type="file" name="readind" id="readind"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input type='submit' class='addbutton' value='load individuals' onclick="__action.value='loadind'" />
</div>

<div id="readMeasurement" class="row">
	<label for="readmeas">Choose your directory with the measurement files:</label>&nbsp;&nbsp;&nbsp;
	<input type="file" name="readmeas" id="readmeas" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input type='submit' class='addbutton' value='load measurement' onclick="__action.value='loadmeas'" />
</div>

<div id="readValues" class="row">
	<label for="readval">Choose your directory with the observedvalues files:</label>
	<input type="file" name="readval" id="readval"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input type='submit' class='addbutton' value='load values' onclick="__action.value='loadval'" />
</div>

<div id="submitbutton" class="row">
	<label for="submittie">Load the files into the database: </label>
	<input type='submit' name='submittie' class='addbutton' value='load files into db' onclick="__action.value='loadAll'" />
</div>

<div id="deleteAllFromDatabase" class="row">
	<label for="loadingdirectories">Choose your directory with the files:</label>
	<input type='submit' class='addbutton' value='deleteDB' onclick="__action.value='deleteDB'" />
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
