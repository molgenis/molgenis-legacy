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
			<h1>Import data</h1>
			<em>Caution: this might interfere with existing database items!</em>
			<div style="margin-top:18px"id="deleteAllFromDatabase" class="row">
				<input type='submit' class='addbutton' value='emptyDB' onclick="__action.value='emptyDB'" />
			</div>
		<div>
	    <br />
	    <hr />
	    <br />
	</div>

	<#if screen.state = "pipeline">

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
		
		<#elseif screen.state ="start">
			<h2>Convert csv to phenomodel</h2>
			
			<div id="investigationdiv" class="row">
				<label for="investigation">Investigation:</label>
				<select name="investigation" id="investigation"> 
					<option value="select investigation"</option>
					<#list screen.investigations as investigation>
						<option value="${investigation.name}">${investigation.name}</option>
						
					</#list>
				</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <label for="createNewInvest">or create a new investigation</label>
				<input name='createNewInvest' id='createNewInvest'type='text'><br /><br /><br /> 
				
			</div>
			
			<label for="convertData"> Upload here the file you want to prepare for the phenomodel in Molgenis:</label><br />
			<input style="margin-right:50px" type="file" name="convertData" id="convertData"/><br / ><br / ><br / >
			<input type='submit' class='addbutton' value='next' onclick="__action.value='update'; createNew.getText()"/>
			<input type='submit' class='addbutton' value='skip this step' onclick="__action.value='skip'"/>
		<#elseif screen.state = "updated">	
			<br />
			<label for="target"> Select the target:</label><br />
			<select name="target" id="target"> 
			<option value="select target"</option>
				<#list screen.arrayMeasurements as target>
					<option value="${target}">${target}</option>			
				</#list>
			</select>
			<br /><br />
			
			<div style="margin-top:6px; border-style:outset; border-width:3px; width:300px" >
				<label>*Optional</label><br />
				<div> 
					<label for="mother"> Select the mother:</label>
					<select name="mother" id="mother" style="margin-right:10px">
						<option value="select mother"</option>
						<#list screen.arrayMeasurements as mother>
							<option value="${mother}">${mother}</option>			
						</#list>
					
					</select>
				</div>
				<br />
				<div style="margin-bottom:10px">
					<label for="father"> Select the father:&nbsp;&nbsp;</label>
					<select name="father" id="father" style="margin-right:5px"> 
						<option value="select father"</option>
						<#list screen.arrayMeasurements as father>
							<option value="${father}">${father}</option>			
						</#list>
					</select>
				</div>
			</div>
			<br />
			<div style="margin-top:20px" id="converter" class="row" >			
				<input type='submit' class='addbutton' value='run pipeline' onclick="__action.value='pipeline'"/>	
				<input type='submit' class='addbutton' value='download files' onclick="__action.value='downloads'"/>
				
				<br />
			</div>	
			<#if screen.status="downloaded">
				<div style="margin-top:22px">
					<#if screen.gc.listSizeTargets gt 0>
						<a href="tmpfile/individual.txt">Download individuals</a><br />
					</#if>
					<#if screen.gc.listSizeMeasurements gt 0>
						<a href="tmpfile/measurement.txt">Download measurements</a><br />
					</#if>
					<#if screen.gc.listSizeValues gt 0>
						<a href="tmpfile/observedvalue.txt">Download observed values</a>
					</#if>
				</div>
				<br />
				<input type='submit' class='addbutton' value='next' onclick="__action.value='step3'"/>
			</#if>
			
			
	</#if>
	    <br />
	    <hr />
	    <br />
	</div>
	<#if screen.state !="start">
		<div id="resetMe">
			<input type='submit' class='addbutton' value='return to mainscreen' onclick="__action.value='reset'"/>
		</div>
	</#if>
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
