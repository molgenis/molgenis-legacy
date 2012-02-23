<#macro plugins_settings_SpeciesPlugin screen>
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

	Import

<#elseif screen.action == "Add">

	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>

	<div id="name" class="row">
	<label for="name">Name:</label>
	<input type="text" name="name" id="name" class="textbox" />
	</div>
	
	<div id="superlocation" class="row">
	<label for="superlocation">Sublocation of:</label>
	<select name="superlocation" id="superlocation" class="selectbox">
		<option value="0">&nbsp;</option>
		<#list screen.locationList as ll>
			<option value="${ll.id?string.computer}">${ll.name}</option>
		</#list>
	</select>
	</div>
	
	<div id='buttons_part' class='row'>
	<input type='submit' class='addbutton' value='Add' onclick="__action.value='addSpecies'" />
	</div>

<#else>

	<!--p>
		<a href="molgenis.do?__target={screen.name}&__action=Add">Make new species</a>
	</p-->

	<#if screen.speciesList?size gt 0>
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="spectable">
			<thead>
				<tr>
					<th>Name</th>
					<th>Latin name</th>
					<th>Dutch name</th>
					<th>NVWA category</th>
				</tr>
			</thead>
			<tbody>
			<#list screen.speciesList as spec>
				<#assign specId = spec.getId()>
				<tr>
					<td>${spec.name}</td>
					<td>${screen.getLatinName(specId)}</td>
					<td>${screen.getDutchName(specId)}</td>
					<td>${screen.getVwaName(specId)}</td>
				</tr>
			</#list>
			</tbody>
		</table>
	<#else>
		<p>There are no species yet</p>
	</#if>
</#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script>
	var oTable = jQuery('#spectable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI" : true }
	);
</script>

</#macro>
