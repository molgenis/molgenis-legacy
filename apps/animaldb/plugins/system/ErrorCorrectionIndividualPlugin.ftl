<#macro plugins_system_ErrorCorrectionIndividualPlugin screen>
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
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->

<h3>Animals</h3>
${screen.getIndividualMatrix()}
<p><em>Note: values set on or related to the selected animals will also be flagged as deleted!</em></p>
<input type="submit" class='addbutton' value="Flag as deleted" onclick="__action.value='deleteIndividuals';return true;"/>

<h3>Individuals flagged as deleted</h3>
${screen.getDeletedIndividualMatrix()}
<p><em>Note: values set on or related to the selected individuals, deleted at the same time as the animal, will also be unflagged!</em></p>
<input type="submit" class='addbutton' value="Unflag" onclick="__action.value='undeleteIndividuals';return true;"/>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script>
	var oTable = jQuery('#targettable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI" : true }
	);
	
	var oTable = jQuery('#deltargettable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI" : true }
	);
</script>

</#macro>
