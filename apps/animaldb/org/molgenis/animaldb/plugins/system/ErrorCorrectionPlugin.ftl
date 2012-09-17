<#macro org_molgenis_animaldb_plugins_system_ErrorCorrectionPlugin screen>
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

	<img class="edit_button" src="generated-res/img/first.png" alt="" onclick="$(this).closest('form').find('input[name=__action]').val('moveUpEnd');; $(this).closest('form').submit();" title="" id="moveUpEnd" style="null"  />
	<img class="edit_button" src="generated-res/img/prev.png" alt="" onclick="$(this).closest('form').find('input[name=__action]').val('moveUp');; $(this).closest('form').submit();" title="" id="moveUp" style="null"  />
	<#assign lowest = screen.offset + 1>
	<#assign highest = screen.offset + screen.limit>
	&nbsp;Showing ${lowest} - ${highest} of ${screen.nrOfValues}&nbsp;
	<img class="edit_button" src="generated-res/img/next.png" alt="" onclick="$(this).closest('form').find('input[name=__action]').val('moveDown');; $(this).closest('form').submit();" title="" id="moveDown" style="null"  />
	<img class="edit_button" src="generated-res/img/last.png" alt="" onclick="$(this).closest('form').find('input[name=__action]').val('moveDownEnd');; $(this).closest('form').submit();" title="" id="moveDownEnd" style="null"  />
	
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="valuetable">
		<thead>
			<tr>
				<th>Select</th>
				<th>Target</th>
				<th>Measurement</th>
				<th>Value</th>
				<th>Relation</th>
				<th>Start date-time</th>
				<th>End date-time</th>
			</tr>
		</thead>
		<tbody>
		<#assign i = 0>
		<#list screen.valueList as value>
			<tr>
				<td><input type="checkbox" id="${i}" name="${i}" />
				<td>${value.target_Name}</td>
				<td>${value.feature_Name}</td>
				<td><#if value.value??>${value.value}</#if></td>
				<td><#if value.relation_Name??>${value.relation_Name}</#if></td>
				<td><#if value.time??>${value.time}</#if></td>
				<td><#if value.endtime??>${value.endtime}</#if></td>
			</tr>
			<#assign i = i + 1>
		</#list>
		</tbody>
	</table>
</#if>

<input type="submit" class='addbutton' value="Flag as deleted" onclick="__action.value='deleteValues';return true;"/>

<#if screen.deletedValueList?size gt 0>

<h3>Observed values flagged as deleted</h3>
	<table cellpadding="0" cellspacing="0" border="0" class="display" id="delvaluetable">
		<thead>
			<tr>
				<th>Select</th>
				<th>Deleted on</th>
				<th>Deleted by</th>
				<th>Target</th>
				<th>Measurement</th>
				<th>Value</th>
				<th>Relation</th>
				<th>Start date-time</th>
				<th>End date-time</th>
			</tr>
		</thead>
		<tbody>
		<#assign i = 0>
		<#list screen.deletedValueList as value>
			<tr>
				<td><input type="checkbox" id="${i}" name="${i}" />
				<td><#if value.deletionTime??>${value.deletionTime}</#if></td>
				<td><#if value.deletedBy_Name??>${value.deletedBy_Name}</#if></td>
				<td><#if value.deletedTarget_Name??>${value.deletedTarget_Name}<#else>${value.target_Name}</#if></td>
				<td>${value.feature_Name}</td>
				<td><#if value.value??>${value.value}</#if></td>
				<td><#if value.deletedRelation_Name??>${value.deletedRelation_Name}<#else><#if value.relation_Name??>${value.relation_Name}</#if></#if></td>
				<td><#if value.time??>${value.time}</#if></td>
				<td><#if value.endtime??>${value.endtime}</#if></td>
			</tr>
			<#assign i = i + 1>
		</#list>
		</tbody>
	</table>

<input type="submit" class='addbutton' value="Unflag" onclick="__action.value='undeleteValues';return true;"/>

</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script>
	var oTable = jQuery('#valuetable').dataTable(
	{ "bProcessing": true,
	  "bFilter" : false,
	  "bLengthChange": false,
	  "bServerSide": false,
	  "bInfo" : false,
	  "sPaginate": "false",
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
