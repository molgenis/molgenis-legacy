<#macro org_molgenis_animaldb_plugins_breeding_ManageLitters screen>
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
				<script>$.ctNotify("${message.text}", {type: 'confirmation', delay: 5000});</script>
				<!-- <p class="successmessage">${message.text}</p> -->
			<#else>
				<script>$.ctNotify("${message.text}", {type: 'error', delay: 7000});</script>	        	
				<!-- <p class="errormessage">${message.text}</p> -->
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->

<#if screen.action == "ShowLitters">

	<input type='submit' id='addlitter' class='addbutton' value='Create new litter' onclick="__action.value='AddLitter'" />
	<br />
	<#if screen.litterList?exists>
		<#if screen.litterList?size gt 0>
			<h2>Unweaned litters</h2>
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="unweanedlitterstable">
				<thead>
					<tr>
						<th>Name</th>
						<th>Parentgroup</th>
						<th>Line</th>
						<th>Birth date</th>
						<th>Size</th>
						<th>Size approximate?</th>
						<th>Remarks</th>
						<th>Status</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
				<#list screen.litterList as litter>
					<tr>
						<td>${litter.name}</td>
						<td>${litter.parentgroup}</td>
						<td>${litter.line}</td>
						<td>${litter.birthDate}</td>
						<td>${litter.size}</td>
						<td>${litter.isSizeApproximate}</td>
						<td>${litter.remarks}</td>
						<td>${litter.status}</td>
						<td><a href="molgenis.do?__target=${screen.name}&__action=ShowWean&id=${litter.id?string.computer}">Wean</a></td>
					</tr>
				</#list>
				</tbody>
			</table>
		</#if>
	</#if>
	
	<#if screen.genoLitterList?exists>
		<#if screen.genoLitterList?size gt 0>
			<h2>Weaned litters that have not been genotyped yet</h2>
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="ungenotypedlitterstable">
				<thead>
					<tr>
						<th>Name</th>
						<th>Parentgroup</th>
						<th>Line</th>
						<th>Birth date</th>
						<th>Wean date</th>
						<th>Size at birth</th>
						<th>Size at weaning</th>
						<th>Remarks</th>
						<th>Status</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
				<#list screen.genoLitterList as litter>
					<tr>
						<td>${litter.name}</td>
						<td>${litter.parentgroup}</td>
						<td>${litter.line}</td>
						<td>${litter.birthDate}</td>
						<td>${litter.weanDate}</td>
						<td>${litter.size}</td>
						<td>${litter.weanSize}</td>
						<td>${litter.remarks}</td>
						<td>${litter.status}</td>
						<td><a href="molgenis.do?__target=${screen.name}&__action=MakeTmpLabels&id=${litter.id?string.computer}">Create temporary cage labels</a></td>
						<td><a href="molgenis.do?__target=${screen.name}&__action=ShowGenotype&id=${litter.id?string.computer}">Genotype</a></td>
					</tr>
				</#list>
				</tbody>
			</table>
		</#if>
	</#if>
	
	<h2>Weaned and genotyped litters</h2>
	${screen.renderDoneLitterMatrix()}
	<div class='row'>
		<input type='submit' id='makedeflabels' class='addbutton' value='Make definitive cage labels' onclick="__action.value='MakeDefLabels'" />
	</div>
	
<#elseif screen.action == "MakeTmpLabels">

	<#if screen.labelDownloadLink??>
		<p>${screen.labelDownloadLink}</p>
	</#if>
	
	<p><a href="molgenis.do?__target=${screen.name}&__action=ShowLitters">Back to overview</a></p>
	
<#elseif screen.action == "MakeDefLabels">

	<#if screen.labelDownloadLink??>
		<p>${screen.labelDownloadLink}</p>
	</#if>
	
	<p><a href="molgenis.do?__target=${screen.name}&__action=ShowLitters">Back to overview</a></p>

<#elseif screen.action == "AddLitter">

	<p><a href="molgenis.do?__target=${screen.name}&__action=ShowLitters">Back to overview</a></p>
	
	<label for="matrix">Parentgroup:</label>
	${screen.renderMatrixViewer()}
	<hr />
	
	<!-- Date of birth -->
	<div id='newlitter_datevalue_part' class='row'>
		<label for='birthdate'>Birth date:</label>
		<!-- input type='text' class='textbox' id='birthdate' name='birthdate' value='<#if screen.birthdate?exists>${screen.getBirthdate()}</#if>' onclick='showDateInput(this)' autocomplete='off' / -->
		<script>
			$(function() {
				$( "#birthdate" ).datepicker({
					numberOfMonths: 1,
					showButtonPanel: true,
					dateFormat: "yy-mm-dd"
				});
			});
		</script>			
		<input type='text' id='birthdate' name='birthdate' <#if screen.getBirthdate??> value="${screen.getBirthdate()}"</#if> />
	</div>
	
	<!-- Size -->
	<div id='newlitter_size_part' class='row'>
		<label for='littersize'>Litter size:</label>
		<input type='text' class='textbox' name='littersize' id='littersize' value='<#if screen.litterSize?exists>${screen.getLitterSize()}</#if>' />
	</div>
	
	<!-- Size approximate? -->
	<div id="sizeapp_div" class="row">
		<label for="sizeapp_toggle">Size approximate:</label>
		<input type="checkbox" id="sizeapp_toggle" name="sizeapp_toggle" value="sizeapp" checked="yes" />
	</div>
	
	<!-- Remarks -->
	<div class='row'>
		<label for='remarks'>Remarks:</label>
		<input type='text' class='textbox' name='remarks' id='remarks' value='<#if screen.remarks?exists>${screen.getRemarks()}</#if>' />
	</div>
	
	<!-- Add button -->
	<div id='newlitter_buttons_part' class='row'>
		<input type='submit' id='addlitter' class='addbutton' value='Add' onclick="__action.value='ApplyAddLitter'" />
	</div>
	
<#elseif screen.action == "ShowWean">

	<p><a href="molgenis.do?__target=${screen.name}&__action=ShowLitters">Back to overview</a></p>
	
	<h2>Weaning litter ${screen.getLitterName()}</h2>
	
	<!-- Date and time of weaning -->
	<div id='weandatediv' class='row'>
		<label for='weandate'>Wean date:</label>
		<!-- <input type='text' class='textbox' id='weandate' name='weandate' value='<#if screen.weandate?exists>${screen.getWeandate()}</#if>' onclick='showDateInput(this)' autocomplete='off' / -->
		<script>
			$(function() {
				$( "#weandate" ).datepicker({
					numberOfMonths: 1,
					showButtonPanel: true,
					dateFormat: "yy-mm-dd"
				});
			});
		</script>			
		<input type='text' id='weandate' name='weandate' <#if screen.weandate??> value="${screen.getWeandate()}"</#if> />
	</div>
	<!-- Size -->
	<div id='weansize_part1' class='row'>
		<label for='weansizefemale'>Nr. of females:</label>
		<input type='text' class='textbox' name='weansizefemale' id='weansizefemale' value='<#if screen.weanSizeFemale?exists>${screen.getWeanSizeFemale()}</#if>' />
	</div>
	<div id='weansize_part2' class='row'>
		<label for='weansizemale'>Nr. of males:</label>
		<input type='text' class='textbox' name='weansizemale' id='weansizemale' value='<#if screen.weanSizeMale?exists>${screen.getWeanSizeMale()}</#if>' />
	</div>
	<div id='weansize_part3' class='row'>
		<label for='weansizeunknown'>Nr. of unknowns:</label>
		<input type='text' class='textbox' name='weansizeunknown' id='weansizeunknown' value='<#if screen.weanSizeUnknown?exists>${screen.getWeanSizeUnknown()}</#if>' />
	</div>
	<p>Name:</p>
	<div id="divnamebase" class="row">
		<label for="namebase">Name prefix (may be empty):</label>
		<select id="namebase" name="namebase" onchange="updateStartNumberAndNewNameBase(this.value)">
			<option value=""></option>
			<option value="New">New (specify below)</option>
			<#list screen.bases as base>
				<option value="${base}">${base}</option>
			</#list>
		</select>
	</div>
	<input id="startnumberhelper" type="hidden" value="${screen.getStartNumberHelperContent()}">
	<div id="divnewnamebasePanel" class="row" style="display:none">
		<label for="newnamebase">New name prefix:</label>
		<input type="text" name="newnamebase" id="newnamebase" class="textbox" />
	</div>
	<div id="divstartnumber" class="row">
		<label for="startnumber">Start numbering at:</label>
		<input type="text" readonly="true" name="startnumber" id="startnumber" class="textbox" value="${screen.getStartNumberForEmptyBase()?string.computer}" />
	</div>
	<!-- Remarks -->
	<div class='row'>
		<label for='remarks'>Weaning remarks:</label>
		<input type='text' class='textbox' name='remarks' id='remarks' />
	</div>
	<!-- Responsible researcher -->
	<div class='row'>
		<label for='respres'>Responsible researcher:</label>
		<input type='text' class='textbox' name='respres' id='respres' value='<#if screen.responsibleResearcher?exists>${screen.getResponsibleResearcher()}</#if>' />
	</div>
	<!-- Optional location -->
	<div class="row">
		<label for="location">Location (optional):</label>
		<select id="location" name="location">
			<option value="-1"></option>
			<#list screen.locationList as loc>
				<option value="${loc.id}">${loc.name}</option>
			</#list>
		</select>
	</div>
	<!-- Add button -->
	<div id='addlitter' class='row'>
		<input type='submit' id='wean' class='addbutton' value='Wean' onclick="__action.value='Wean'" />
	</div>

<#elseif screen.action == "ShowGenotype" || screen.action == "AddGenoCol" || screen.action == "RemGenoCol">
	
	<p><a href="molgenis.do?__target=${screen.name}&__action=ShowLitters">Back to overview</a></p>
	
	<h2>Genotype litter</h2>
	
	<p>${screen.parentInfo}</p>
	
	${screen.getGenotypeTable()}
	
	<input type='submit' id='addgenocol' class='addbutton' value='Add Gene modification + state' onclick="__action.value='AddGenoCol'" />
	<input type='submit' id='remgenocol' class='addbutton' value='Remove Gene modification + state' onclick="__action.value='RemGenoCol'" />
	<!-- Remarks -->
	<div class='row'>
		<label for='genodate'>Genotyping date:</label>
		<input type='text' class='textbox' name='genodate' id='genodate' value='<#if screen.genodate?exists>${screen.getGenodate()}</#if>' onclick='showDateInput(this)' autocomplete='off' />
	</div>
	<!-- Remarks -->
	<div class='row'>
		<label for='remarks'>Genotyping remarks:</label>
		<input type='text' class='textbox' name='remarks' id='remarks' />
	</div>
	<!-- Save button -->
	<div class='row'>
		<input type='submit' id='save' class='addbutton' value='Save' onclick="__action.value='Genotype'" />
	</div>
	
</#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script>
	var oTable = jQuery('#donelitterstable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI" : true }
	);
	
	var oTable = jQuery('#unweanedlitterstable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bJQueryUI" : true }
	);
	
	var oTable = jQuery('#ungenotypedlitterstable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bJQueryUI" : true }
	);
</script>

</#macro>
