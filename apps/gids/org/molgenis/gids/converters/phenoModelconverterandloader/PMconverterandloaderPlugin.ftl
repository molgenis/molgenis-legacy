<#macro org_molgenis_gids_converters_phenoModelconverterandloader_PMconverterandloaderPlugin screen>
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
		
		<#-- select investigation and inputfile -->
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
			<input style="margin-right:50px" type="file" name="convertData" id="convertData"/>
			<label for="delimeter"> Choose the delimeter:</label>
				<select name="delimeter" id="delimeter" style="margin-right:10px">
					<option value="choose the delimeter"</option>
					<#list screen.arrayDelimeter as delimeter>
						<option value="${delimeter}">${delimeter}</option>			
					</#list>		
				</select>
			<label>(default is semicolon) </label>
			<br / ><br / ><br / >
			<input type='submit' class='addbutton' value='go to step2' onclick="__action.value='goToStep2'; createNew.getText()"/>
			<input type='submit' class='addbutton' value='upload your files' onclick="__action.value='skip'"/>
		
		<#-- select individual, father and mother -->
		<#elseif screen.state = "inStep2">	
			<br />
			<table>
				<tr>
					<td width="250px"><label for="target"> Select the target for Individual:</label></td><td><label for="sample"> Select the target for Sample:</label>
					</td>
				</tr>
				<tr>
					<td>
						<select name="individual" id="individual"> 
							<#if screen.arrayMeasurements?seq_contains('id_individual')>
								<option value="id_individual">id_individual</option>
							<#else>
								<#list screen.arrayMeasurements as individual>
									<option value="${individual}">${individual}</option>			
								</#list>
							</#if>
						</select>
						<#if screen.arrayMeasurements?seq_contains('id_individual')>
							<a style="color:green; font-size:9px">individual found</a>
							<#else>
								<br>
								<a style="color:red; font-size:9px">&nbsp; individual not found, please select from dropdown</a>
						</#if>
					</td>
					<td>
						<select name="sample" id="sample"> 
							<#if screen.arrayMeasurements?seq_contains('id_sample')>
								<option value="id_sample">id_sample</option>
								
							<#else>
								<#list screen.arrayMeasurements as sample>
									<option value="${sample}">${sample}</option>			
								</#list>
							</#if>
						</select>
						<#if screen.arrayMeasurements?seq_contains('id_sample')>
							<a style="color:green; font-size:9px">sample found</a>
							<#else>
								<br>
								<a style="color:red; font-size:9px">&nbsp; sample not found, please select from dropdown</a>
						</#if>
					</td>
				</tr>
			</table>
			<br /><br />
			
			<div style="margin-top:6px; border-style:outset; border-width:3px; width:330px" >
				<label>*Optional</label><br />
				<div> 
					<label for="mother"> Select the mother:</label>
					<select name="mother" id="mother" style="margin-right:10px">
						<#if screen.arrayMeasurements?seq_contains('id_mother')>
							<option value="id_mother">id_mother</option>
						<#else>
						<option value="select father"</option>
							<#list screen.arrayMeasurements as mother>
								<option value="${mother}">${mother}</option>			
							</#list>
						</#if>
					</select>
					<#if screen.arrayMeasurements?seq_contains('id_mother')>
						<a style="color:green; font-size:9px">mother found</a>
						<#else>
						<br>
						<a style="color:red; font-size:9px">&nbsp; mother not found, please select from dropdown or leave it blank</a>
					</#if>
				</div>
				<br />
				<div style="margin-bottom:10px">
					<label for="father"> Select the father:&nbsp;&nbsp;</label>
					<select name="father" id="father" style="margin-right:5px"> 
						<#if screen.arrayMeasurements?seq_contains('id_father')>
							<option value="id_father">id_father</option>
						<#else>
						<option value="select father"</option>
							<#list screen.arrayMeasurements as father>
								<option value="${father}">${father}</option>			
							</#list>
							
						</#if>
					</select>
					<#if screen.arrayMeasurements?seq_contains('id_father')>
						<a style="color:green; font-size:9px">&nbsp; father found</a>
						<#else>
						<br>
						<a style="color:red; font-size:9px">&nbsp; father not found, please select from dropdown or leave it blank</a>
					</#if>
				</div>
			</div>
			<br />
			<div style="margin-top:20px" id="converter" class="row" >			
				<input type='submit' class='addbutton' value='next' onclick="__action.value='goToStep3'"/>
				
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
				<input type='submit' class='addbutton' value='next' onclick="__action.value='step4'"/>
			</#if>
			
		<#--select Individuals or Samples -->
		<#elseif screen.state = "inStep3">	
			<table border="1">
				
				<#if screen.listNewMeas?has_content>
				
				<tr>
					<td><b>Measurement</b></td>
					<td><b>Individuals</b></td>
					<td><b>Samples</b></td>
					<td><b>Already in database?</b></td>
					
				</tr>
					<#list screen.listNewMeas as target>
					<tr> 		
	 					<td>${target}</td> 	 		
				 		<#if target?contains("sample") || 
				 			 target?contains("isolat") ||
				 			 target?contains("hla") ||
				 			 target?contains("rna") ||
				 			 target?contains("dna") ||
				 			 target?contains("serum") ||
				 			 target?contains("plasma") ||
				 			 target?contains("biopsy") ||
				 			 target?contains("sscp") ||
				 			 target?contains("mix") ||
				 			 target?contains("storage") ||
				 			 target?contains("box") ||
				 			 target?contains("arrival") ||
				 			 target?contains("picogreen") ||
				 			 target?contains("nanodrop") ||
				 			 target?contains("serology") ||
				 			 target?contains("biopsies") ||
				 			 target?contains("biopt") ||
				 			 target?contains("physician") ||
				 			 target?matches('rs[0-9-]+') ||
				 			 target?contains("hemolytic")> 
					 		<td align="center"><input type="radio" name="${target}" value="Individuals"></td>
					 		<td align="center"><input type="radio" name="${target}" value="Samples" checked></td>
					 			 		
				 		<#else>
				 			<td align="center"><input type="radio" name="${target}" id="radiob${target_index}" value="Individuals" checked></td>
					 		<td align="center"><input type="radio" name="${target}" id="radioc${target_index}" value="Samples" ></td>
				 		</#if>
				 		<div id="radio">
					 	<td><input type="checkbox" name="checker${target_index}" id="checker${target_index}" value="check" onclick="controleer('${target_index}');"></td>
					 	</div>	
					 		<td>
					 		<select disabled="true" name="dropbox${target_index}" id="dropbox${target_index}" style="margin-right:5px">
							
					 		<option value="select measurement">select measurement</option>	
					 		<#list screen.measInDb as measInDb>
								<option value="${measInDb}">${measInDb}</option>			
							</#list>
							</select></td>
							
					</tr>
					</#list>
				<#else>
					<p><b>There are no new measurements, please continue</b></p>
				
				</#if>			
			</table>
			<input type='submit' class='addbutton' value='next' onclick="__action.value='run pipeline'"/>
		<br /><br />
			
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
	
	<script>
		function controleer(targetId)
		{
			//alert(targetId);
			var element = document.getElementById('dropbox' +targetId);
			var disabled = !element.disabled;
			
			element.disabled = disabled;			
			document.getElementById('radiob' +targetId).disabled = !disabled;
			document.getElementById('radioc' +targetId).disabled = !disabled;
		}
	</script>
	
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
