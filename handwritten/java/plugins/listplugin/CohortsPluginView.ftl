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
			
<#--begin your plugin-->	

<table cellpadding="0" cellspacing="0" border="0" class="display" id="listtable">
	<thead>
		<tr>
			<th>Name</th>
			<th>Category</th>
			<th>Topic</th>
			<th>Coordinators</th>
		</tr>
	</thead>
	<tbody>
		<#list model.cohorts as cohort>
		<tr>
			<td>${cohort.name}</td>
			<td>${cohort.Category}</td>
			<td>${cohort.Topic}</td>
			<td>${cohort.Coordinators}</td>
		</tr>
		</#list>
	</tbody>
</table>
	
<#--end of your plugin-->	
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
