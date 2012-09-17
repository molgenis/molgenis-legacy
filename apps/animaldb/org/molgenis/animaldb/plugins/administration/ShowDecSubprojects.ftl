<#macro org_molgenis_animaldb_plugins_administration_ShowDecSubprojects screen>
	
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
	<#if screen.listId == -1>Add<#else>Edit</#if> DEC subproject
	</strong></p>

	<p><a href="molgenis.do?__target=${screen.name}&__action=Show">Back to overview</a></p>
	
	<#if screen.listId != -1>
		<#assign currentDecSubproject = screen.getSelectedDecSubproject()>
	</#if>
	
	<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
	
	<div class="row">
		<label for="decapp">DEC application:</label>
		<select name="decapp" id="decapp"> 
			<#list screen.decApplicationList as decAppListItem>
				<option 
				<#if currentDecSubproject??><#if currentDecSubproject.decExpListId == decAppListItem.id>selected="selected"</#if></#if>
				value="${decAppListItem.id?string.computer}">${decAppListItem.name}</option>
			</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="expnumber">DEC subproject code:</label>
		<!--<input type="text" name="expnumber" id="expnumber" class="textbox" <#if currentDecSubproject??> value="${currentDecSubproject.experimentNr}"</#if> />-->
		<select name="expnumber" id="expnumber">
		<#list screen.experimentNrCodeList as encl>
			<option value="${encl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getExperimentNr() == encl.description>selected="selected"</#if></#if> >${encl.code_string}</option>
		</#list>
		</select>
	
	</div>
	
	<div class="row">
		<label for="experimenttitle">DEC title:</label>
		<input type="text" name="experimenttitle" id="experimenttitle" class="textbox" <#if currentDecSubproject??> value="${currentDecSubproject.experimentTitle}"</#if> />
	</div>
	
	<div class="row">
		<label for="decapppdf">DEC subproject application PDF:</label>
		<input type="text" name="decapppdf" id="decapppdf" class="textbox" <#if currentDecSubproject??> value="${currentDecSubproject.decSubprojectApplicationPDF}"</#if> />
	</div>
	
	<div class="row">
		<label for="concern">Concern:</label>
		<select name="concern" id="concern">
		<#list screen.concernCodeList as ccl>
			<option value="${ccl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getConcern() == ccl.description>selected="selected"</#if></#if> >${ccl.code_string} (${ccl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="goal">Goal:</label>
		<select name="goal" id="goal">
		<#list screen.goalCodeList as gcl>
			<option value="${gcl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getGoal() == gcl.description>selected="selected"</#if></#if> >${gcl.code_string} (${gcl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="specialtechn">Special techniques:</label>
		<select name="specialtechn" id="specialtechn">
		<#list screen.specialTechnCodeList as stcl>
			<option value="${stcl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getSpecialTechn() == stcl.description>selected="selected"</#if></#if> >${stcl.code_string} (${stcl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="lawdef">Law definition:</label>
		<select name="lawdef" id="lawdef">
		<#list screen.lawDefCodeList as ldcl>
			<option value="${ldcl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getLawDef() == ldcl.description>selected="selected"</#if></#if> >${ldcl.code_string} (${ldcl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="toxres">Toxic research:</label>
		<select name="toxres" id="toxres">
		<#list screen.toxResCodeList as trcl>
			<option value="${trcl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getToxRes() == trcl.description>selected="selected"</#if></#if> >${trcl.code_string} (${trcl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="anaesthesia">Anaesthesia:</label>
		<select name="anaesthesia" id="anaesthesia">
		<#list screen.anaesthesiaCodeList as acl>
			<option value="${acl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getAnaesthesia() == acl.description>selected="selected"</#if></#if> >${acl.code_string} (${acl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="painmanagement">Pain management:</label>
		<select name="painmanagement" id="painmanagement">
		<#list screen.painManagementCodeList as pmcl>
			<option value="${pmcl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getPainManagement() == pmcl.description>selected="selected"</#if></#if> >${pmcl.code_string} (${pmcl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="endstatus">Expected animal end status:</label>
		<select name="endstatus" id="endstatus">
		<#list screen.animalEndStatusCodeList as aescl>
			<option value="${aescl.description}" <#if currentDecSubproject??><#if currentDecSubproject.getAnimalEndStatus() == aescl.description>selected="selected"</#if></#if> >${aescl.code_string} (${aescl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class="row">
		<label for="remarks">Remarks:</label>
		<input type="text" name="remarks" id="remarks" class="textbox" <#if currentDecSubproject??><#if currentDecSubproject.getRemarks()??>value="${currentDecSubproject.remarks}"</#if></#if> />
	</div>
	
	<div class="row">
		<label for="startdate">Subproject start date:</label>
		<input type='text' id='startdate' name='startdate' <#if currentDecSubproject??><#if currentDecSubproject.getStartDate()??> value="${currentDecSubproject.startDate}"</#if></#if> />
	    <script>
            $(function() {
                $( "#startdate" ).datepicker({
                    numberOfMonths: 1,
                    showButtonPanel: true,
                    dateFormat: "yy-mm-dd",
                    changeYear: true,
                    changeMonth: true,
                                        
                });
            });
        </script>
	
	</div>
	
	<div class="row">
		<label for="enddate">Subproject end date:</label>
		<input type='text' id='enddate' name='enddate' <#if currentDecSubproject??><#if currentDecSubproject.getEndDate()??> value="${currentDecSubproject.endDate}"</#if></#if>  />
	    <script>
            $(function() {
                $( "#enddate" ).datepicker({
                    numberOfMonths: 1,
                    showButtonPanel: true,
                    dateFormat: "yy-mm-dd",
                    changeYear: true,
                    changeMonth: true
                });
            });
        </script>
	</div>
	
	<div class="row">
        <label for="decsubprojectbudget">Animal budget:</label>
        <input type="text" name="decsubprojectbudget" id="decsubprojectbudget" class="textbox" 
        <#if currentDecSubproject??><#if currentDecSubproject.getDecSubprojectBudget()??> value="${currentDecSubproject.decSubprojectBudget}"</#if></#if> />
    </div>
    	
	<div class='row'>
		<input type='submit' id='addsubproject' class='addbutton' value='Save' onclick="__action.value='addEditDecSubproject'" />
	</div>
	
	</form>

<#elseif screen.action == "EditAnimals">

	<p><a href="molgenis.do?__target=${screen.name}&__action=Show">Back to overview</a></p>
	
	<#assign currentDecSubproject = screen.getSelectedDecSubproject()>
	<h3>Manage animals in ${currentDecSubproject.name}</h3>

	<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />

	
	
	<input type='submit' id='startadd' class='addbutton' value='Add animals' onclick="__action.value='AddAnimalToSubproject'" />
	<br /><hr /><br />
	
	${screen.renderRemAnimalsMatrixViewer()}
	<input type='submit' id='dorem' class='addbutton' value='Remove selected animals' onclick="__action.value='RemoveAnimalsFromSubproject'" />
	
	</form>

<#elseif screen.action == "RemoveAnimalsFromSubproject">

	<p><a href="molgenis.do?__target=${screen.name}&__action=EditAnimals&id=${screen.listId?string.computer}">Back to overview</a></p>
	
	<h3>Removing&nbsp;
	<#list screen.getAnimalRemoveIdList() as animalId>
		<#assign name = screen.getAnimalName(animalId)>
		${name}&nbsp;
	</#list>
	<#assign currentDecSubproject = screen.getSelectedDecSubproject()>
	from ${currentDecSubproject.name}</h3>
	
	<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
	
	<div class="row">
		<label for="subprojectremovaldate">Date of removal from DEC subproject:</label>
		<input type='text' class='textbox' id='subprojectremovaldate' name='subprojectremovaldate' value='${screen.currentDate}'  autocomplete='off' />
	   <script>
            $(function() {
                $( "#subprojectremovaldate" ).datepicker({
                    numberOfMonths: 1,
                    showButtonPanel: true,
                    dateFormat: "yy-mm-dd"
                    changeMonth: true,
                    changeYear: true
                });
            });
        </script>
	</div>
	
	<div class='row'>
		<label for='discomfort'>Actual discomfort:</label>
		<select name='discomfort' id='discomfort'>
		<#list screen.actualDiscomfortCodeList as dcl>
			<option value="${dcl.description}">${dcl.code_string} (${dcl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class='row'>
		<label for='endstatus'>Actual animal end status:</label>
		<select name='endstatus' id='endstatus' onchange="showDeathDatetime(this.value);">
		<#list screen.actualEndstatusCodeList as ecl>
			<option value="${ecl.description}">${ecl.code_string} (${ecl.description})</option>
		</#list>
		</select>
	</div>
	
	<div id="deathdatediv" class="row" style="display:block">
		<label for="deathdate">Date of death:</label>
		<input type='text' class='textbox' id='deathdate' name='deathdate' value='${screen.currentDate}' autocomplete='off' />
	    <script>
            $(function() {
                $( "#deathdate" ).datepicker({
                    numberOfMonths: 1,
                    showButtonPanel: true,
                    dateFormat: "yy-mm-dd",
                    changeYear: true
                });
            });
        </script>
	</div>
	
	<div class='row'>
		<input type='submit' id='dorem' class='addbutton' value='Apply' onclick="__action.value='ApplyRemoveAnimalsFromSubproject'" />
	</div>
	
	</form>

<#elseif screen.action == "ApplyRemoveAnimalsFromSubproject">

	<p><a href="molgenis.do?__target=${screen.name}&__action=EditAnimals&id=${screen.listId?string.computer}">Back to overview</a></p>

<#elseif screen.action == "ApplyAddAnimalToSubproject">

	<p><a href="molgenis.do?__target=${screen.name}&__action=EditAnimals&id=${screen.listId?string.computer}">Back to overview</a></p>

<#elseif screen.action == "AddAnimalToSubproject">

	<p><a href="molgenis.do?__target=${screen.name}&__action=EditAnimals&id=${screen.listId?string.computer}">Back to overview</a></p>
	
	<#assign currentDecSubproject = screen.getSelectedDecSubproject()>
	<p><h3>Adding animal(s) to ${currentDecSubproject.name}</h3></p>
	
	<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
		
	<!--div style='float:left'>
		<label for='animal'>Animal(s):</label>
		<select name='animal' id='animal' size='20' multiple='multiple'>
		<list screen.allAnimalIdList as animalId>
			<assign name = screen.getAnimalName(animalId)>
			<option value="{animalId?string.computer}">{name}</option>
		</list>
		</select>
	</div>
	
	<div>
		<label for='groupname'>Batch(es):</label>
		<select name='groupname' id='groupname' size='20' multiple='multiple'>
			<list screen.batchList as batch>
				<option value="{batch.id?string.computer}">{batch.name}</option>
			</list>
		</select>
	</div-->
	
	${screen.renderAddAnimalsMatrixViewer()}
	
	<hr />
	
	<div class="row" style='clear:left'>
		<label for="subprojectadditiondate">Date of entry into DEC subproject:</label>
		<input type='text' class='textbox' id='subprojectadditiondate' name='subprojectadditiondate' value='${screen.currentDate}' autocomplete='off' />
	    <script>
            $(function() {
                $( "#subprojectadditiondate" ).datepicker({
                    numberOfMonths: 1,
                    showButtonPanel: true,
                    dateFormat: "yy-mm-dd",
                    changeYear: true,
                    changeMonth: true
                });
            });
        </script>
	</div>
	
	<div class='row'>
		<label for='painmanagement'>Pain management:</label>
		<select name='painmanagement' id='painmanagement'>
		<#list screen.painManagementCodeList as pml>
			<option value="${pml.description}">${pml.code_string} (${pml.description})</option>
		</#list>
		</select>
	</div>
	
	<div class='row'>
		<label for='anaesthesia'>Anaesthesia:</label>
		<select name='anaesthesia' id='anaesthesia'>
		<#list screen.anaesthesiaCodeList as al>
			<option value="${al.description}">${al.code_string} (${al.description})</option>
		</#list>
		</select>
	</div>
	
	<div class='row'>
		<label for='discomfort'>Expected discomfort:</label>
		<select name='discomfort' id='discomfort'>
		<#list screen.expectedDiscomfortCodeList as dcl>
			<option value="${dcl.description}">${dcl.code_string} (${dcl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class='row'>
		<label for='endstatus'>Expected animal end status:</label>
		<select name='endstatus' id='endstatus'>
		<#list screen.expectedEndstatusCodeList as ecl>
			<option value="${ecl.description}">${ecl.code_string} (${ecl.description})</option>
		</#list>
		</select>
	</div>
	
	<div class='row'>
		<input type='submit' id='doadd' class='addbutton' value='Apply' onclick="__action.value='ApplyAddAnimalToSubproject'" />
	</div>
	
	</form>

<#else>

	<div id="experimentlist">
		<p><a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=-1"><img id="add_subproject" class="add_button" title="add new subproject" alt="Add new DEC subproject" src="generated-res/img/new.png"></a></p>
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="decSubProjectsTable">
			<thead>
				<tr>
					<th></th>
					<th>Animals</th>
					<th>Name</th>
					<th>Start date</th>
					<th>End date</th>
					<th>Animal Budget</th>
					<th>DEC application</th>
					<th>DEC subproject code</th>
					<th>DEC subproject title</th>
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
				</tr>
			</thead>
			<tbody>
				<#if screen.experimentList?exists>
					<#assign i = 0>
					<#list screen.experimentList as expl>
						<tr>
							<td><a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=${i}"><img id="edit_decsubproject" class="edit_button" title="edit current record" alt="Edit" src="generated-res/img/editview.gif"></a></td>
							<td> <a href='molgenis.do?__target=${screen.name}&__action=EditAnimals&id=${i}'><img id="manage_animals_in_subproject" class="edit_button" title="add/remove animals to/from subproject" alt="Manage" src="generated-res/img/grid-manage-icon.gif"></a>  [${expl.nrOfAnimals}]</td>
							<td>${expl.name}</td>
							<td><#if expl.startDate??>${expl.startDate}</#if></td>
							<td><#if expl.endDate??>${expl.endDate}</#if></td>
							<td><#if expl.decSubprojectBudget??>${expl.decSubprojectBudget}</#if></td> <! TODO remove this checkafter adding data to db, only for upgrade (2012-06-06) -->
							<td><#if expl.decApplication??>${expl.decApplication}</#if></td>
							<td><#if expl.experimentNr??>${expl.experimentNr}</#if></td>
							<td><#if expl.experimentTitle??>${expl.experimentTitle}</#if></td>
							<td><#if expl.decSubprojectApplicationPDF??>${expl.decSubprojectApplicationPDF}</#if></td>
							<td><#if expl.concern??>${expl.concern}</#if></td>
							<td><#if expl.goal??>${expl.goal}</#if></td>
							<td><#if expl.specialTechn??>${expl.specialTechn}</#if></td>
							<td><#if expl.lawDef??>${expl.lawDef}</#if></td>
							<td><#if expl.toxRes??>${expl.toxRes}</#if></td>
							<td><#if expl.anaesthesia??>${expl.anaesthesia}</#if></td>
							<td><#if expl.painManagement??>${expl.painManagement}</#if></td>
							<td><#if expl.animalEndStatus??>${expl.animalEndStatus}</#if></td>
							<td><#if expl.remarks??>${expl.remarks}</#if></td>
						</tr>
						<#assign i = i + 1>
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
	  "bAutoWidth": false,
	  "bJQueryUI": true,
	  "aoColumnDefs": [ 
      	{ "sWidth": "30px", "aTargets": [ 0 ] }
    	] 
	  }
	  
	);
</script>
	
</#macro>
