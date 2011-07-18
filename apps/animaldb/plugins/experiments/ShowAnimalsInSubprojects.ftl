<#macro plugins_experiments_ShowAnimalsInSubprojects screen>
	
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

<#if screen.getSubproject()??>
	<#assign strf = screen.getSubproject()>
</#if>

<#if screen.action == "ShowAnimalsInSubproject">

<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>

<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
	
	<table cellpadding="10" cellspacing="2" border="1">
		<tr>
			<th>Name</th>
			<th><input type='submit' class='addbutton' value='Remove selected' onclick="__action.value='RemoveAnimalsFromSubproject'" /></th>
		</tr>
		<#assign i = 0>
		<#list screen.getAnimalIdList() as animalId>
			<#assign name = screen.getAnimalName(animalId)>
			<tr>
				<td style='padding:5px'>${name}</td>
				<td style='padding:5px'><input type="checkbox" id="rem${i}" name="rem${i}" value="rem${i}" /></td>
			</tr>
			<#assign i = i + 1>
		</#list>
	</table>

</form>

<br />
<input type='submit' class='addbutton' value='Add' onclick="window.location='molgenis.do?__target=${screen.name}&__action=AddAnimalToSubproject'" />

<#elseif screen.action == "RemoveAnimalsFromSubproject">

<p><a href="molgenis.do?__target=${screen.name}&__action=ShowAnimalsInSubproject&id=${strf.id?string.computer}">Back to overview</a></p>

<em>Removing&nbsp;
<#list screen.getAnimalRemoveIdList() as animalId>
	<#assign name = screen.getAnimalName(animalId)>
	${name}&nbsp;
</#list>
from ${strf.name}</em>

<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
	
	<div class="row">
		<label for="subprojectremovaldatetime">Date and time of removal from DEC subproject:</label>
		<input type='text' class='textbox' id='subprojectremovaldatetime' name='subprojectremovaldatetime' value='' onclick='showDateInput(this,true)' autocomplete='off' />
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
	
	<div class="row" id="deathdatetimebox" style="display:block">
		<label for="deathdatetime">Date and time of death:</label>
		<input type='text' class='textbox' id='deathdatetime' name='deathdatetime' value='' onclick='showDateInput(this,true)' autocomplete='off' />
	</div>
	
	<div id='buttons_part' class='row'>
		<input type='submit' class='addbutton' value='Apply' onclick="__action.value='ApplyRemoveAnimalsFromSubproject'" />
	</div>
	
</form>

<#elseif screen.action == "ApplyRemoveAnimalsFromSubproject">

<p><a href="molgenis.do?__target=${screen.name}&__action=ShowAnimalsInSubproject&id=${strf.id?string.computer}">Back to overview</a></p>

<#elseif screen.action == "ApplyAddAnimalToSubproject">

<p><a href="molgenis.do?__target=${screen.name}&__action=ShowAnimalsInSubproject&id=${strf.id?string.computer}">Back to overview</a></p>

<#elseif screen.action == "AddAnimalToSubproject">

<p><a href="molgenis.do?__target=${screen.name}&__action=ShowAnimalsInSubproject&id=${strf.id?string.computer}">Back to overview</a></p>

<em>Adding animal(s) to ${strf.name}</em><br />

<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
	
	<div style='float:left'>
		<label for='animal'>Animal(s):</label>
		<select name='animal' id='animal' size='20' multiple='multiple'>
		<#list screen.allAnimalIdList as animalId>
			<#assign name = screen.getAnimalName(animalId)>
			<option value="${animalId?string.computer}">${name}</option>
		</#list>
		</select>
	</div>
	
	<div>
		<label for='groupname'>Batch(es):</label>
		<select name='groupname' id='groupname' size='20' multiple='multiple'>
			<#list screen.batchList as batch>
				<option value="${batch.id?string.computer}">${batch.name}</option>
			</#list>
		</select>
	</div>
	
	<div class="row" style='clear:left'>
		<label for="subprojectadditiondatetime">Date and time of entry into DEC subproject:</label>
		<input type='text' class='textbox' id='subprojectadditiondatetime' name='subprojectadditiondatetime' value='' onclick='showDateInput(this,true)' autocomplete='off' />
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
	
	<div id='buttons_part' class='row'>
		<input type='submit' class='addbutton' value='Apply' onclick="__action.value='ApplyAddAnimalToSubproject'" />
	</div>
	
</form>

<#else>

<div id="subprojectselect" class="row">
<label for="subproject">DEC Subproject:</label>
<select name="subproject" id="subproject" class="selectbox" onchange="callScreenWithSubprojectId(this.value);">
	<option value="0">&nbsp;</option>
	<#list screen.subprojectList as sl>
		<option value="${sl.id?string.computer}">${sl.name}</option>
	</#list>
</select>
</div>

</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>

<script type="text/javascript">

function callScreenWithSubprojectId(value) {
	window.location='molgenis.do?__target=${screen.name}&__action=ShowAnimalsInSubproject&id=' + value;
}

function showDeathDatetime(value) {
	if (value == "A. Dood in het kader van de proef" || value == "B. Gedood na beeindiging van de proef") {
		document.getElementById('deathdatetimebox').style.display = 'block';
	} else {
		document.getElementById('deathdatetimebox').value = '';
		document.getElementById('deathdatetimebox').style.display = 'none';
	}
}

</script>

</#macro>
