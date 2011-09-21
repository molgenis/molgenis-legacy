<#macro plugins_experiments_ShowDecProjects screen>
	
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
<#if screen.listId == 0>Add<#else>Edit</#if> DEC Project
</strong></p>

<p><a href="molgenis.do?__target=${screen.name}&__action=Show">Back to overview</a></p>

<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
	
	<#if screen.listId != 0>
		<#assign currentDecProject = screen.getDecProjectByListId()>
	</#if>
	
	<div class="row">
		<label for="name">Name:</label>
		<input type="text" name="name" id="name" class="textbox" 
		<#if currentDecProject?? && currentDecProject.getName()??> value="${currentDecProject.name}"</#if>
		/>
	</div>
	
	<div class="row">
		<label for="decnumber">DEC number:</label>
		<input type="text" name="decnumber" id="decnumber" class="textbox" 
		<#if currentDecProject?? && currentDecProject.getDecNr()??> value="${currentDecProject.decNr}"</#if>
		/>
	</div>
	
	<!--
	<div class="row">
		<label for="decapplicant">DEC Applicant:</label>
		<select name="decapplicant" id="decapplicant"> 
			<!#list screen.decApplicantList as decApplicant>
				<option 
				<!#if currentDecProject??><!#if currentDecProject.decApplicantName = decApplicant.name>selected="selected"<!/#if><!/#if>
				value="{decApplicant.id?string.computer}">{decApplicant.name}</option>
			<!/#list>
		</select>
	</div>
	-->
	
	<div class="row">
		<label for="decapplicationpdf">DEC application PDF:</label>
		<input type="text" name="decapplicationpdf" id="decapppdf" class="textbox" <#if currentDecProject?? && currentDecProject.getPdfDecApplication()??> value="${currentDecProject.pdfDecApplication}"</#if> />
	</div>
	
	<div class="row">
		<label for="decapprovalpdf">DEC approval PDF:</label>
		<input type="text" name="decapprovalpdf" id="decapprovalpdf" class="textbox" <#if currentDecProject?? && currentDecProject.getPdfDecApproval()??> value="${currentDecProject.pdfDecApproval}"</#if> />
	</div>
	
	<div class="row">
		<label for="startdate">Project start date:</label>
		<input type='text' class='textbox' id='startdate' name='startdate' <#if currentDecProject?? && currentDecProject.getStartDate()??> value="${currentDecProject.startDate}"</#if> onclick='showDateInput(this)' autocomplete='off' />
	</div>
	
	<div class="row">
		<label for="enddate">Project end date:</label>
		<input type='text' class='textbox' id='enddate' name='enddate' <#if currentDecProject?? && currentDecProject.getEndDate()??> value="${currentDecProject.endDate}"</#if> onclick='showDateInput(this)' autocomplete='off' />
	</div>
	
	<div class='row'>
		<input type='submit' id="addproject" class='addbutton' value='Add' onclick="__action.value='addEditDecProject'" />
	</div>
	
</form>

<#else>

<div id="decapplist">
	<p><strong>DEC Projects</strong></p>
	<p><a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=0">Add</a></p>
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="decProjectsTable">
		<thead>
			<tr>
				<th>Name</th>
				<th>Start date</th>
				<th>End date</th>
				<th>DEC number</th>
				<th>DEC applicant</th>
				<th>DEC application PDF</th>
				<th>DEC approval PDF</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
		<#if screen.decappList?exists>
			<#list screen.decappList as decl>
				<tr>
					<td>${decl.name}</td>
					<td>${decl.startDate}</td>
					<td>${decl.endDate}</td>
					<td>${decl.decNr}</td>
					<td>${decl.decApplicantName}</td>
					<td>${decl.pdfDecApplication}</td>
					<td>${decl.pdfDecApproval}</td>
					<td><a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=${decl.decAppListId}">Edit</a></td>
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
	var oTable = jQuery('#decProjectsTable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI": true }
	);
	
	
</script>

</#macro>
