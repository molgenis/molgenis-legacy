<#macro org_molgenis_animaldb_plugins_settings_LocationInfoPlugin screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
	
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

<#if screen.action == "Import">

	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>
	
	<div class="row">
		<label for="csv">CSV file:</label>
		<input type="file" name="csv" id="csv" class="textbox" />
	</div>

	<div id='buttons_part' class='row'>
		<input type='submit' class='addbutton' value='Import' onclick="__action.value='importLocations'" />
	</div>
	
<#elseif screen.action == "Add">

	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>

	<div class="row">
		<label for="locname">Name:</label>
		<input type="text" name="locname" id="locname" class="textbox" />
	</div>
	
	<div class="row">
		<label for="superlocation">Sublocation of:</label>
		<select name="superlocation" id="superlocation" class="selectbox">
			<option value="">&nbsp;</option>
			<#list screen.locationList as ll>
				<option value="${ll.name}">${ll.name}</option>
			</#list>
		</select>
	</div>
	
	<div class='row'>
		<input type='submit' class='addbutton' id='addloc' value='Add' onclick="__action.value='addLocation'" />
	</div>

<#else>

	<p>
		<a href="molgenis.do?__target=${screen.name}&__action=Add">Make new location</a>
		<br />
		<a href="molgenis.do?__target=${screen.name}&__action=Import">Import locations</a>
	</p>

	<#if screen.locationList?size gt 0>
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="loctable">
			<thead>
				<tr>
					<th>Name</th>
					<th>Part of</th>
				</tr>
			</thead>
			<tbody>
			<#list screen.locationList as loc>
				<#assign locId = loc.getId()>
				<tr>
					<td>${loc.name}</td>
					<td>${screen.getSuperLocName(locId)}</td>
				</tr>
			</#list>
			</tbody>
		</table>
	<#else>
		<p>There are no locations yet</p>
	</#if>
</#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script>
	var oTable = jQuery('#loctable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI" : true }
	);
</script>

</#macro>
