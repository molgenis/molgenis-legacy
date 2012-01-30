<#macro plugins_system_ErrorCorrectionPlugin screen>
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

<h3>Observed values</h3>
<#if screen.valueList?size gt 0>
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="valuetable">
		<thead>
			<tr>
				<th>Select</th>
				<th>Start date-time</th>
				<th>End date-time</th>
				<th>Value</th>
				<th>Relation</th>
				<th>Observation target</th>
				<th>Measurement</th>
			</tr>
		</thead>
		<tbody>
		<#assign i = 0>
		<#list screen.valueList as value>
			<tr>
				<td><input type="checkbox" id="${i}" name="${i}" />
				<td>${value.time}</td>
				<td><#if value.endtime??>${value.endtime}</#if></td>
				<td><#if value.value??>${value.value}</#if></td>
				<td><#if value.relation_Name??>${value.relation_Name}</#if></td>
				<td>${value.target_Name}</td>
				<td>${value.feature_Name}</td>
			</tr>
			<#assign i = i + 1>
		</#list>
		</tbody>
	</table>
</#if>

<input type="submit" class='addbutton' value="Flag as deleted" onclick="__action.value='deleteValues';return true;"/>

<h3>Observed values flagged as deleted</h3>
<#if screen.deletedValueList?size gt 0>
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="delvaluetable">
		<thead>
			<tr>
				<th>Select</th>
				<th>Deleted on</th>
				<th>Deleted by</th>
				<th>Start date-time</th>
				<th>End date-time</th>
				<th>Value</th>
				<th>Relation</th>
				<th>Observation target</th>
				<th>Measurement</th>
			</tr>
		</thead>
		<tbody>
		<#assign i = 0>
		<#list screen.deletedValueList as value>
			<tr>
				<td><input type="checkbox" id="${i}" name="${i}" />
				<td><#if value.deletionTime??>${value.deletionTime}</#if></td>
				<td><#if value.deletedBy_Name??>${value.deletedBy_Name}</#if></td>
				<td>${value.time}</td>
				<td><#if value.endtime??>${value.endtime}</#if></td>
				<td><#if value.value??>${value.value}</#if></td>
				<td><#if value.relation_Name??>${value.relation_Name}</#if></td>
				<td>${value.target_Name}</td>
				<td>${value.feature_Name}</td>
			</tr>
			<#assign i = i + 1>
		</#list>
		</tbody>
	</table>
</#if>

<input type="submit" class='addbutton' value="Unflag" onclick="__action.value='undeleteValues';return true;"/>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script>
	var oTable = jQuery('#valuetable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI" : true }
	);
	
	var oTable = jQuery('#delvaluetable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI" : true }
	);
</script>

</#macro>
