<#macro org_molgenis_animaldb_plugins_administration_ShowDecProjects screen>
	
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
	
	<#if currentDecProject?? && currentDecProject.getName()??>
	<div class="row">
		<label for="name">Name:</label>
		<input disabled type="text" name="name" id="name" class="textbox" 
		 value="${currentDecProject.name}"
		/>
	</div>
	</#if>
	
	<div class="row">
		<label for="decnumber">DEC number:</label>
		<input type="text" name="decnumber" id="decnumber" class="textbox" 
		<#if currentDecProject?? && currentDecProject.getDecNr()??> value="${currentDecProject.decNr}"</#if>
		/>
	</div>
	
	<div class="row">
		<label for="dectitle">DEC title:</label>
		<input type="text" name="dectitle" id="dectitle" class="textbox" 
		<#if currentDecProject?? && currentDecProject.getDecTitle()??> value="${currentDecProject.decTitle}"</#if>
		/>
	</div>
	
	<!-- <div class="row">
		<label for="decapplicant">DEC Applicant:</label>
		<select name="decapplicant" id="decapplicant"> 
			<!#list screen.decApplicantList as decApplicant>
				<option 
				<!#if currentDecProject??><!#if currentDecProject.decApplicantName = decApplicant.name>selected="selected"<!/#if><!/#if>
				value="{decApplicant.id?string.computer}">{decApplicant.name}</option>
			<!/#list>
		</select>
	</div> -->
	
	<div class="row">
		<label for="decapplicationpdf">DEC application PDF:</label>
		<#if currentDecProject?? && currentDecProject.pdfDecApplication??>File already present! <a href="downloadfile?name=${currentDecProject.pdfDecApplication}">${currentDecProject.pdfDecApplication}</a> </#if>
		<input type="file" name="decapplicationpdf" id="decapppdf" class="textbox" <#if currentDecProject?? && currentDecProject.getPdfDecApplication()??> value="${currentDecProject.pdfDecApplication}"</#if> />
	</div>
	
	<div class="row">
		<label for="decapprovalpdf">DEC approval PDF:</label>
		<#if currentDecProject?? && currentDecProject.pdfDecApproval??>File already present! <a href="downloadfile?name=${currentDecProject.pdfDecApproval}">${currentDecProject.pdfDecApproval}</a> </#if>
		<input type="file" name="decapprovalpdf" id="decapprovalpdf" class="textbox" <#if currentDecProject?? && currentDecProject.getPdfDecApproval()??> value="${currentDecProject.pdfDecApproval}"</#if> />
	</div>
	
	<div class="row">
		<label for="startdate">Project start date:</label>
		<script>
			$(function() {
				$( "#startdate" ).datepicker({
					numberOfMonths: 2,
					showButtonPanel: true,
					dateFormat: "yy-mm-dd"
				});
			});
		</script>			
		<input type='text' id='startdate' name='startdate' <#if currentDecProject?? && currentDecProject.getStartDate()??> value="${currentDecProject.startDate}"</#if> />
	</div>
	
	<div class="row">
		<script>
			$(function() {
				$( "#enddate" ).datepicker({
					numberOfMonths: 2,
					showButtonPanel: true,
					dateFormat: "yy-mm-dd"
				});
			});
		</script>
		<label for="enddate">Project end date:</label>
		<input type='text' id='enddate' name='enddate' <#if currentDecProject?? && currentDecProject.getEndDate()??> value="${currentDecProject.endDate}"</#if> />
	</div>
	
	<div class="row">
        <label for="decbudget">Animal budget:</label>
        <input type="text" name="decbudget" id="decbudget" class="textbox" 
        <#if currentDecProject?? && currentDecProject.getDecBudget()??> value="${currentDecProject.decBudget}"</#if>
        />
    </div>
	
	<div class='row'>
		<input type='submit' id="addproject" class='addbutton' value='<#if screen.listId == 0>Add<#else>Update</#if>' onclick="__action.value='addEditDecProject'" />
	</div>
	
</form>

<#else>

<div id="decapplist">
	<p><a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=0"><img id="add_decproject" class="add_button" title="add new decproject" alt="Add new DEC project" src="generated-res/img/new.png"></a></p>
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="decProjectsTable">
		<thead>
			<tr>
				<th></th>
				<th>Name</th>
				<th>DEC number</th>
				<th>DEC title</th>
				<th>Start date</th>
				<th>End date</th>
				<th>DEC applicant</th>
				<th>DEC application PDF</th>
				<th>DEC approval PDF</th>
				<th>Animal budget</th>
			</tr>
		</thead>
		<tbody>
		<#if screen.decappList?exists>
			<#list screen.decappList as decl>
				<tr>
					<td><a href="molgenis.do?__target=${screen.name}&__action=AddEdit&id=${decl.decAppListId}"><img id="edit_breedingline" class="edit_button" title="edit current record" alt="Edit" src="generated-res/img/editview.gif"></a></td>
					<td>${decl.name}</td>
					<td>${decl.decNr}</td>
					<td>${decl.decTitle}</td>
					<td>${decl.startDate}</td>
					<td>${decl.endDate}</td>					
					<td>${decl.decApplicantName}</td>
					<td><#if decl.pdfDecApplication??><a href="downloadfile?name=${decl.pdfDecApplication}">${decl.pdfDecApplication}</a></#if></td>
					<td><#if decl.pdfDecApproval??><a href="downloadfile?name=${decl.pdfDecApproval}">${decl.pdfDecApproval}</a></#if></td>
					<td><#if decl.decBudget??>${decl.decBudget}</#if></td>
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
	  "bJQueryUI": true,
	  "aoColumnDefs": [ 
      	{ "sWidth": "30px", "aTargets": [ 0 ] }
    	] 
	  }
	);
	
	
</script>

</#macro>
