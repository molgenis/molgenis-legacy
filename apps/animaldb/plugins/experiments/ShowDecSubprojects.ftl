<#macro plugins_experiments_ShowDecSubprojects screen>
	
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

<#if screen.action == "AddEdit">

<p><strong>
<#if screen.listId == 0>Add<#else>Edit</#if> DEC Subproject
</strong></p>

<p><a href="molgenis.do?__target=${screen.name}&__action=Show">Back to overview</a></p>

<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
	
	<#if screen.listId != 0>
		<#assign currentDecSubproject = screen.getDecSubprojectByListId()>
	</#if>
	
	<div class="row">
		<label for="name">Name:</label>
		<input type="text" name="name" id="name" class="textbox" <#if currentDecSubproject??> value="${currentDecSubproject.name}"</#if> />
	</div>
	
	<div class="row">
		<label for="decapp">DEC application:</label>
		<select name="decapp" id="decapp"> 
			<#list screen.decApplicationList as decAppListItem>
				<option 
				<#if currentDecSubproject??><#if currentDecSubproject.decApplicationId = decAppListItem.id>selected="selected"</#if></#if>
				value="${decAppListItem.id}">${decAppListItem.name}</option>
			</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="decnumber">DEC subproject code:</label>
		<input type="text" name="decnumber" id="decnumber" class="textbox" <#if currentDecSubproject??> value="${currentDecSubproject.experimentNr}"</#if> />
	</div>
	
	<div class="row">
		<label for="decapppdf">DEC subproject application PDF:</label>
		<input type="text" name="decapppdf" id="decapppdf" class="textbox" <#if currentDecSubproject??> value="${currentDecSubproject.decSubprojectApplicationPDF}"</#if> />
	</div>
	
	<div class="row">
		<label for="concern">Concern:</label>
		<select name="concern" id="concern">
		<#list screen.concernCodeList as ccl>
			<option value="${ccl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getConcern() == ccl.description>selected="selected"</#if></#if> >${ccl.code} (${ccl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="goal">Goal:</label>
		<select name="goal" id="goal">
		<#list screen.goalCodeList as gcl>
			<option value="${gcl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getGoal() == gcl.description>selected="selected"</#if></#if> >${gcl.code} (${gcl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="specialtechn">Special techniques:</label>
		<select name="specialtechn" id="specialtechn">
		<#list screen.specialTechnCodeList as stcl>
			<option value="${stcl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getSpecialTechn() == stcl.description>selected="selected"</#if></#if> >${stcl.code} (${stcl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="lawdef">Law definition:</label>
		<select name="lawdef" id="lawdef">
		<#list screen.lawDefCodeList as ldcl>
			<option value="${ldcl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getLawDef() == ldcl.description>selected="selected"</#if></#if> >${ldcl.code} (${ldcl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="toxres">Toxic research:</label>
		<select name="toxres" id="toxres">
		<#list screen.toxResCodeList as trcl>
			<option value="${trcl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getToxRes() == trcl.description>selected="selected"</#if></#if> >${trcl.code} (${trcl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="anaesthesia">Anaesthesia:</label>
		<select name="anaesthesia" id="anaesthesia">
		<#list screen.anaesthesiaCodeList as acl>
			<option value="${acl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getAnaesthesia() == acl.description>selected="selected"</#if></#if> >${acl.code} (${acl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="painmanagement">Pain management:</label>
		<select name="painmanagement" id="painmanagement">
		<#list screen.painManagementCodeList as pmcl>
			<option value="${pmcl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getPainManagement() == pmcl.description>selected="selected"</#if></#if> >${pmcl.code} (${pmcl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="endstatus">Expected animal end status:</label>
		<select name="endstatus" id="endstatus">
		<#list screen.animalEndStatusCodeList as aescl>
			<option value="${aescl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getAnimalEndStatus() == aescl.description>selected="selected"</#if></#if> >${aescl.code} (${aescl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="remarks">Remarks:</label>
		<input type="text" name="remarks" id="remarks" class="textbox" <#if currentDecSubproject??><#if currentDecSubproject.getOldAnimalDBRemarks()??>value="${currentDecSubproject.oldAnimalDBRemarks}"</#if></#if> />
	</div>
	
	<div class="row">
		<label for="startdate">Subproject start date:</label>
		<input type='text' class='textbox' id='startdate' name='startdate' <#if currentDecSubproject??><#if currentDecSubproject.getStartDate()??> value="${currentDecSubproject.startDate}"</#if></#if> onclick='showDateInput(this)' autocomplete='off' />
	</div>
	
	<div class="row">
		<label for="enddate">Subproject end date:</label>
		<input type='text' class='textbox' id='enddate' name='enddate' <#if currentDecSubproject??><#if currentDecSubproject.getEndDate()??> value="${currentDecSubproject.endDate}"</#if></#if> onclick='showDateInput(this)' autocomplete='off' />
	</div>
	
	<div class='row'>
		<input type='submit' id='addsubproject' class='addbutton' value='Add' onclick="__action.value='addEditDecSubproject'" />
	</div>
	
</form>

<#else>

<div id="experimentlist">
	<p><strong>DEC Subprojects</strong></p>
	<p><a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=0">Add</a></p>
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="decSubProjectsTable">
		<thead>
			<tr>
				<th>Name</th>
				<th>Start date</th>
				<th>End date</th>
				<th>DEC project (application)</th>
				<th>DEC subproject code</th>
				<th>DEC subproject application PDF</th>
				<th>Concern</th>
				<th>Goal</th>
				<th>Special techniques</th>
				<th>Law definition</th>
				<th>Toxic research</th>
				<th>Anaesthesia</th>
				<th>Pain management</th>
				<th>Expected animal end status</th>
				<th>Remarks</th>
				<th>Nr. of animals currently in</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<#if screen.experimentList?exists>
				<#list screen.experimentList as expl>
					<tr>
						<td>${expl.name}</td>
						<td>${expl.startDate}</td>
						<td>${expl.endDate}</td>
						<td>${expl.decApplication}</td>
						<td>${expl.experimentNr}</td>
						<td>${expl.decSubprojectApplicationPDF}</td>
						<td>${expl.concern}</td>
						<td>${expl.goal}</td>
						<td>${expl.specialTechn}</td>
						<td>${expl.lawDef}</td>
						<td>${expl.toxRes}</td>
						<td>${expl.anaesthesia}</td>
						<td>${expl.painManagement}</td>
						<td>${expl.animalEndStatus}</td>
						<td>${expl.oldAnimalDBRemarks}</td>
						<td>${expl.nrOfAnimals}</td>
						<td><a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=${expl.decExpListId}">Edit</a>&nbsp;</td>
					</tr>
				</#list>
			</#if>
		</tbody>
	</table>
</div>

</#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>

<script>
	var oTable = jQuery('#decSubProjectsTable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false }
	);
</script>
	
</#macro>
