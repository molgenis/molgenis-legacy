<#macro plugins_phenoModelconverterandloader_PMconverterandloaderPlugin screen>
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

<#if screen.wait = "yes">
	<div id="waiter" style="cursor:wait"/> 
	<#else>
		<div id="waiter2" style="cursor:wait"/> 
			
		
</#if>
	
<div id="waitornot">
	<h1>Import data</h1>
	<em>Caution: this might interfere with existing database items!</em>
	
	<div>
	    <br />
	    <hr />
	    <br />
	</div>
	
	<div id="deleteAllFromDatabase" class="row">
		<input type='submit' class='addbutton' value='emptyDB' onclick="__action.value='emptyDB'" />
	</div>
	
	

	<#if screen.state = "downloaded">
	<div id="bal">
		<#if screen.cgtp.listSizeTargets gt 0>
			<a href="tmpfile/individual.txt">Download individuals</a><br />
		</#if>
		<#if screen.cgtp.listSizeMeasurements gt 0>
			<a href="tmpfile/measurement.txt">Download measurements</a><br />
		</#if>
		<#if screen.cgtp.listSizeValues gt 0>
			<a href="tmpfile/observedvalue.txt">Download observed values</a>
		</#if>
		<input style="margin-left:100px" type='submit' class='addbutton' value='  clean downloads  ' onclick="__action.value='clean'"/>
	<div>
	<#elseif screen.state = "pipeline">
	
	
	<#elseif screen.state = "skip">
	
	
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
	<#else>
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
		<h4>Run the converter and immediately load the files into the database &nbsp;&nbsp;&nbsp;&nbsp;<input type='submit' class='addbutton' value='run pipeline' onclick="__action.value='pipeline'; createNew.getText()"/></h4>
		
		<br />
		<h5>Run the converter and create downloadable links to the files       <input type='submit' class='addbutton' value='download' onclick="__action.value='downloads'; createNew.getText()"/></h5>
		
		<br />
		<p> Go to the load files into database screen   <input type='submit' class='addbutton' value='load db screen' onclick="__action.value='skip'"/></p>
		
		
	
	</div>
	</#if>
	
	
	    <br />
	    <hr />
	    <br />
	</div>
	<input type='submit' class='addbutton' value='  reset ' onclick="__action.value='reset'"/>
</div>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
