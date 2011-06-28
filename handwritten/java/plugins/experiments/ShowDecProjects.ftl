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
	
	<div id="name" class="row">
	<label for="name">Name:</label>
	<input type="text" name="name" id="name" class="textbox" 
	<#if currentDecProject?? && currentDecProject.getName()??> value="${currentDecProject.name}"</#if>
	/>
	</div>
	
	<div id="decnumber" class="row">
	<label for="decnumber">DEC Number:</label>
	<input type="text" name="decnumber" id="decnumber" class="textbox" 
	<#if currentDecProject?? && currentDecProject.getDecNr()??> value="${currentDecProject.decNr}"</#if>
	/>
	</div>
	
	<!--
	<div id="decapplicant" class="row">
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
	
	<div id="decapplicationpdf" class="row">
	<label for="decapplicationpdf">DEC Application PDF:</label>
	<input type="text" name="decapplicationpdf" id="decapppdf" class="textbox" 
	<#if currentDecProject?? && currentDecProject.getPdfDecApplication()??> value="${currentDecProject.pdfDecApplication}"</#if>
	/>
	</div>
	
	<div id="decapprovalpdf" class="row">
	<label for="decapprovalpdf">DEC Approval PDF:</label>
	<input type="text" name="decapprovalpdf" id="decapprovalpdf" class="textbox" 
	<#if currentDecProject?? && currentDecProject.getPdfDecApproval()??> value="${currentDecProject.pdfDecApproval}"</#if>
	/>
	</div>
	
	<div id="starttime" class="row">
	<label for="starttime">Project Start Date:</label>
	<input type='text' class='textbox' id='starttime' name='starttime' 
	<#if currentDecProject?? && currentDecProject.getStartDate()??> value="${currentDecProject.startDate}"</#if>
	onclick='showDateInput(this,true)' autocomplete='off' />
	</div>
	
	<div id="endtime" class="row">
	<label for="endtime">Project End Date:</label>
	<input type='text' class='textbox' id='endtime' name='endtime' 
	<#if currentDecProject?? && currentDecProject.getEndDate()??> value="${currentDecProject.endDate}"</#if>
	onclick='showDateInput(this,true)' autocomplete='off' />
	</div>
	
	<div id='buttons_part' class='row'>
	<input type='submit' class='addbutton' value='Add' onclick="__action.value='addEditDecProject'" />
	</div>
	
</form>

<#else>

<div id="decapplist">
	<p><strong>DEC Projects</strong></p>
	<p><a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=0">Add</a></p>
	<table cellpadding="10" cellspacing="2" border="1">
	<tr>
		<th>Name</th>
		<th>Start Date</th>
		<th>End Date</th>
		<th>DEC Number</th>
		<th>DEC Applicant</th>
		<th>DEC Application PDF</th>
		<th>DEC Approval PDF</th>
		<th></th>
	</tr>
	<#if screen.decappList?exists>
		<#list screen.decappList as decl>
			<tr>
				<td style='padding:5px'>${decl.name}</td>
				<td style='padding:5px'>${decl.startDate}</td>
				<td style='padding:5px'>${decl.endDate}</td>
				<td style='padding:5px'>${decl.decNr}</td>
				<td style='padding:5px'>${decl.decApplicantName}</td>
				<td style='padding:5px'>${decl.pdfDecApplication}</td>
				<td style='padding:5px'>${decl.pdfDecApproval}</td>
				<td style='padding:5px'><a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=${decl.decAppListId}">Edit</a></td>
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
