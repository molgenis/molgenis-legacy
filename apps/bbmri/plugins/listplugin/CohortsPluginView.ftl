<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${model.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${model.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${model.getName()}">
		${model.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list model.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	
			
				
				<table cellpadding="0" cellspacing="0" border="0" class="display" id="listtable">
					<thead>
						<tr>
							<th>Id</th>
							<th>Category</th>
							<th>SubCategory</th>
							<th>Topic</th>
							<th>Institutions</th>
							<th>Coordinators</th>
							<th>Biodata</th>
							<th>Number of Gwa Data</th>
							<th>Gwa Platform</th>
							<th>Gwa Comments</th>
							<th>General Comments</th>
							<th>Publications</th>
						</tr>
					</thead>
					<tbody>
						<#list model.cohorts as cohort>
						<tr>
							<td>${cohort.name}</td>
							<td>${cohort.Category_Name}</td>
							<td>${cohort.SubCategory_Name}</td>
							<td>${cohort.Topic_Name}</td>
							<td>${cohort.Institutes_Name}</td>
							<td>${cohort.Coordinators_LastName}</td>
							<td>${cohort.Biodata_name}</td>
							<#if cohort.GwaDataNum??>
								<td>${cohort.GwaDataNum}</td>
							<#else>
								<td></td>
							</#if>
							<#if cohort.GwaPlatform??>
								<td>${cohort.GwaPlatform}</td>
							 <#else>
							 	<td></td>
							 </#if>
							<#if cohort.GwaComments??>
								<td>${cohort.GwaComments}</td>
							 <#else>
							 	<td></td>
							 </#if>
							 <#if cohort.GeneralComments??>
								<td>${cohort.GeneralComments}</td>
							<#else>
								<td></td>
							</#if>
							<#if cohort.Publications??>
								<td>${cohort.Publications}</td>
							<#else>
								<td></td>
							</#if>
							 
						</tr>
						</#list>
					</tbody>
				</table>
	
			</div>
		</div>
	</div>
</form>

<script>

<!-- Initialization of 'listtable' as DataTable -->
var oTable = jQuery('#listtable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false }
);

</script>
