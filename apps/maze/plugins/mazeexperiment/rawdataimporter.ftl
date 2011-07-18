<#macro plugin_animaldb_mazeexperiment_rawdataimporter screen>
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
<div id ="title">	
	<h1>Import a raw datafile into the mazedata table</h1>
	<em>Caution: duplicate entries are not checked yet.</em>
	<br>
</div>

<div id="animaltablediv" >
<#if !screen.fileList??>
	<fieldset>
    	<legend>File settings:</legend>
		<br>
		<label for="pcid">PcId</label><select name="pcid" id="pcid" class="textbox"><option value="0">pc 0</option>
																					<option value="1">pc 1</option>
																					<option value="2">pc 2</option>
									  </select><br>

		<label for="mazedata">Raw data text file</label><input  name="mazedatafile" id="mazedatafile" class="textbox" type="file" multiple="true" /><br>
		<br><br>	
		<input type='submit' class='addbutton' value='Load data from file(s)' onclick="__action.value='loadMazeDataFile1'" />
	</fieldset>
</div>
<#else>
<div id="filelist" >

	${screen.fileList}
	<input type='submit' class='addbutton' value='Cancel' onclick="__action.value='cancel'" />
	<input type='submit' class='addbutton' value='Load data from file(s)' onclick="__action.value='loadMazeDataFile2'" />
	
</div>

</#if>
<#--<input name="myinput" value="${screen.getMyValue()}">
<input type="submit" value="Change name" onclick="__action.value='do_myaction';return true;"/-->
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
