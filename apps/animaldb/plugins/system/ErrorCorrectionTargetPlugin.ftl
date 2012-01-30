<#macro plugins_system_ErrorCorrectionTargetPlugin screen>
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

<h3>Observation targets</h3>
<#if screen.targetList?size gt 0>
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="targettable">
		<thead>
			<tr>
				<th>Select</th>
				<th>Name</th>
			</tr>
		</thead>
		<tbody>
		<#assign i = 0>
		<#list screen.targetList as target>
			<tr>
				<td><input type="checkbox" id="${i}" name="${i}" />
				<td>${target.name}</td>
			</tr>
			<#assign i = i + 1>
		</#list>
		</tbody>
	</table>
</#if>

<p><em>Note: observed values set on the selected observation targets will also be flagged as deleted!</em></p>

<input type="submit" class='addbutton' value="Flag as deleted" onclick="__action.value='deleteTargets';return true;"/>

<#if screen.deletedTargetList?size gt 0>

<h3>Observation targets flagged as deleted</h3>
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="deltargettable">
		<thead>
			<tr>
				<th>Select</th>
				<th>Deleted on</th>
				<th>Deleted by</th>
				<th>Name</th>
			</tr>
		</thead>
		<tbody>
		<#assign i = 0>
		<#list screen.deletedTargetList as target>
			<tr>
				<td><input type="checkbox" id="${i}" name="${i}" />
				<td><#if target.deletionTime??>${target.deletionTime}</#if></td>
				<td><#if target.deletedBy_Name??>${target.deletedBy_Name}</#if></td>
				<td>${target.name}</td>
			</tr>
			<#assign i = i + 1>
		</#list>
		</tbody>
	</table>

<p><em>Note: observed values set on the selected observation targets will not be unflagged! You have to do this yourself in the Correct errors Plugin for Observed values.</em></p>

<input type="submit" class='addbutton' value="Unflag" onclick="__action.value='undeleteTargets';return true;"/>

</#if>

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
