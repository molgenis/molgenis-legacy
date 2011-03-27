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
	
	<div id="name" class="row">
	<label for="name">Name:</label>
	<input type="text" name="name" id="name" class="textbox" 
	<#if currentDecSubproject??> value="${currentDecSubproject.name}"</#if>
	/>
	</div>
	
	<div id="decapp" class="row">
	<label for="decapp">DEC Application:</label>
	<select name="decapp" id="decapp"> 
		<#list screen.decApplicationList as decAppListItem>
			<option 
			<#if currentDecSubproject??><#if currentDecSubproject.decApplicationId = decAppListItem.id>selected="selected"</#if></#if>
			value="${decAppListItem.id}">${decAppListItem.name}</option>
		</#list>
	</select>
	</div>
	
	<div id="decnumber" class="row">
	<label for="decnumber">DEC Subproject Code:</label>
	<input type="text" name="decnumber" id="decnumber" class="textbox" 
	<#if currentDecSubproject??> value="${currentDecSubproject.experimentNr}"</#if>
	/>
	</div>
	
	<div id="decapppdf" class="row">
	<label for="decapppdf">DEC Subproject Application PDF:</label>
	<input type="text" name="decapppdf" id="decapppdf" class="textbox" 
	<#if currentDecSubproject??> value="${currentDecSubproject.decSubprojectApplicationPDF}"</#if>
	/>
	</div>
	
	<div id="concern" class="row">
	<label for="concern">Concern:</label>
	<select name="concern" id="concern">
	<#list screen.concernCodeList as ccl>
		<option value="${ccl.description}" 
		<#if currentDecSubproject??><#if currentDecSubproject.getConcern() == ccl.description>selected="selected"</#if></#if> 
		>${ccl.code} (${ccl.description})</option>
	</#list>
	</select>
	</div>
	
	<div id="goal" class="row">
	<label for="goal">Goal:</label>
	<select name="goal" id="goal">
	<#list screen.goalCodeList as gcl>
		<option value="${gcl.description}" 
		<#if currentDecSubproject??><#if currentDecSubproject.getGoal() == gcl.description>selected="selected"</#if></#if> 
		>${gcl.code} (${gcl.description})</option>
	</#list>
	</select>
	</div>
	
	<div id="specialtechn" class="row">
	<label for="specialtechn">Special Techniques:</label>
	<select name="specialtechn" id="specialtechn">
	<#list screen.specialTechnCodeList as stcl>
		<option value="${stcl.description}" 
		<#if currentDecSubproject??><#if currentDecSubproject.getSpecialTechn() == stcl.description>selected="selected"</#if></#if> 
		>${stcl.code} (${stcl.description})</option>
	</#list>
	</select>
	</div>
	
	<div id="lawdef" class="row">
	<label for="lawdef">Law Definition:</label>
	<select name="lawdef" id="lawdef">
	<#list screen.lawDefCodeList as ldcl>
		<option value="${ldcl.description}" 
		<#if currentDecSubproject??><#if currentDecSubproject.getLawDef() == ldcl.description>selected="selected"</#if></#if> 
		>${ldcl.code} (${ldcl.description})</option>
	</#list>
	</select>
	</div>
	
	<div id="toxres" class="row">
	<label for="toxres">Toxic Research:</label>
	<select name="toxres" id="toxres">
	<#list screen.toxResCodeList as trcl>
		<option value="${trcl.description}" 
		<#if currentDecSubproject??><#if currentDecSubproject.getToxRes() == trcl.description>selected="selected"</#if></#if> 
		>${trcl.code} (${trcl.description})</option>
	</#list>
	</select>
	</div>
	
	<div id="anaesthesia" class="row">
	<label for="anaesthesia">Anaesthesia:</label>
	<select name="anaesthesia" id="anaesthesia">
	<#list screen.anaesthesiaCodeList as acl>
		<option value="${acl.description}" 
		<#if currentDecSubproject??><#if currentDecSubproject.getAnaesthesia() == acl.description>selected="selected"</#if></#if> 
		>${acl.code} (${acl.description})</option>
	</#list>
	</select>
	</div>
	
	<div id="painmanagement" class="row">
	<label for="painmanagement">Pain Management:</label>
	<select name="painmanagement" id="painmanagement">
	<#list screen.painManagementCodeList as pmcl>
		<option value="${pmcl.description}" 
		<#if currentDecSubproject??><#if currentDecSubproject.getPainManagement() == pmcl.description>selected="selected"</#if></#if> 
		>${pmcl.code} (${pmcl.description})</option>
	</#list>
	</select>
	</div>
	
	<div id="endstatus" class="row">
	<label for="endstatus">Expected Animal End Status:</label>
	<select name="endstatus" id="endstatus">
	<#list screen.animalEndStatusCodeList as aescl>
		<option value="${aescl.description}" 
		<#if currentDecSubproject??><#if currentDecSubproject.getAnimalEndStatus() == aescl.description>selected="selected"</#if></#if> 
		>${aescl.code} (${aescl.description})</option>
	</#list>
	</select>
	</div>
	
	<div id="remarks" class="row">
	<label for="remarks">Remarks:</label>
	<input type="text" name="remarks" id="remarks" class="textbox" 
	<#if currentDecSubproject??><#if currentDecSubproject.getOldAnimalDBRemarks()??>value="${currentDecSubproject.oldAnimalDBRemarks}"</#if></#if>
	/>
	</div>
	
	<div id="starttime" class="row">
	<label for="starttime">Subproject start date:</label>
	<input type='text' class='textbox' id='starttime' name='starttime' 
	<#if currentDecSubproject??><#if currentDecSubproject.getStartDate()??> value="${currentDecSubproject.startDate}"</#if></#if>
	onclick='showDateInput(this,true)' autocomplete='off' />
	</div>
	
	<div id="endtime" class="row">
	<label for="endtime">Subproject end date:</label>
	<input type='text' class='textbox' id='endtime' name='endtime' 
	<#if currentDecSubproject??><#if currentDecSubproject.getEndDate()??> value="${currentDecSubproject.endDate}"</#if></#if>
	onclick='showDateInput(this,true)' autocomplete='off' />
	</div>
	
	<div id='buttons_part' class='row'>
	<input type='submit' class='addbutton' value='Add' onclick="__action.value='addEditDecSubproject'" />
	</div>
	
</form>

<#else>

<div id="experimentlist">
	<p><strong>DEC Subprojects</strong></p>
	<p><a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=0">Add</a></p>
	<table cellpadding="10" cellspacing="2" border="1">
	<tr>
		<th>Name</th>
		<th>Start Date</th>
		<th>End Date</th>
		<th>DEC Project (Application)</th>
		<th>DEC Subproject Code</th>
		<th>DEC Subproject Application PDF</th>
		<th>Concern</th>
		<th>Goal</th>
		<th>Special Techniques</th>
		<th>Law Definition</th>
		<th>Toxic Research</th>
		<th>Anaesthesia</th>
		<th>Pain Management</th>
		<th>Expected Animal End Status</th>
		<th>Remarks</th>
		<th>Nr. of animals currently in</th>
		<th></th>
	</tr>
	<#if screen.experimentList?exists>
		<#list screen.experimentList as expl>
			<tr>
				<td style='padding:5px'>${expl.name}</td>
				<td style='padding:5px'>${expl.startDate}</td>
				<td style='padding:5px'>${expl.endDate}</td>
				<td style='padding:5px'>${expl.decApplication}</td>
				<td style='padding:5px'>${expl.experimentNr}</td>
				<td style='padding:5px'>${expl.decSubprojectApplicationPDF}</td>
				<td style='padding:5px'>${expl.concern}</td>
				<td style='padding:5px'>${expl.goal}</td>
				<td style='padding:5px'>${expl.specialTechn}</td>
				<td style='padding:5px'>${expl.lawDef}</td>
				<td style='padding:5px'>${expl.toxRes}</td>
				<td style='padding:5px'>${expl.anaesthesia}</td>
				<td style='padding:5px'>${expl.painManagement}</td>
				<td style='padding:5px'>${expl.animalEndStatus}</td>
				<td style='padding:5px'>${expl.oldAnimalDBRemarks}</td>
				<td style='padding:5px'>${expl.nrOfAnimals}</td>
				<td style='padding:5px'><a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=${expl.decExpListId}">Edit</a>&nbsp;</td>
			</tr>
		</#list>
	</#if>
	</table>
</div>

</#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
	
</#macro>
