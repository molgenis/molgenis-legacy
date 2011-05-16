<#macro plugins_convertergids_ConvertDataIntoPhenoPlugin screen>
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

<h2>Convert csv to phenomodel</h2>
<div id="investigationdiv" class="row">
	<label for="investigation">Investigation:</label>
	<select name="investigation" id="investigation"> 
		<option value="select investigation"</option>
		<#list screen.investigations as investigation>
			<option value="${investigation.name}">${investigation.name}</option>
			
		</#list>
	</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <label for="createNew">or create a new investigation</label>
	<input name='createNew' id='createNew'type='text'>
</div>

<div style="margin-top:20px" id="converter" class="row">
<label for="convertData"> Upload here the file you want to prepare for the phenomodel in Molgenis:</label><br />

	<input style="margin-right:50px" type="file" name="convertData" id="convertData"/><br / ><br / ><br / >
	
	<input type='submit' class='addbutton' value='  convert  ' onclick="__action.value='convertMe'; createNew.getText()"/>
	
	<h3>output will be in the same folder as the input</h3>
</div>
<#if screen.finished??>
<div id="bal">
	
		<a href="tmpfile/individual.txt">Download individuals</a><br />
		<a href="tmpfile/measurement.txt">Download measurements</a><br />
		<a href="tmpfile/observedvalue.txt">Download observed values</a>
	
<div>
</#if>
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
