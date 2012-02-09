<#macro plugins_location_LocationPlugin screen>
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

<#if screen.action == "Manage">
	
	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>
	
	<p><a href="molgenis.do?__target=${screen.name}&__action=AddAnimals">Add animals</a></p>

	${screen.renderAnimalsInLocMatrixViewer()}
	
	<div class='row'>
		<label for="moveto">Move selected to:</label>
		<select name="moveto" id="moveto" class="selectbox">
			<#list screen.locationList as ll>
				<option value="${ll.id?string.computer}">${ll.name}</option>
			</#list>
		</select>
		<input type='submit' class='addbutton' value='Apply' onclick="__action.value='Move'" />
	</div>
	
<#elseif screen.action == "AddAnimals">

	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>
	
	${screen.renderAnimalsNotInLocMatrixViewer()}
	
	<input type='submit' class='addbutton' value='Add' onclick="__action.value='ApplyAddAnimals'" />

<#else>

	<#if screen.locationList?size gt 0>
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="loctable">
			<thead>
				<tr>
					<th>Name</th>
					<th>Part of</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
			<#list screen.locationList as loc>
				<#assign locId = loc.getId()>
				<tr>
					<td>${loc.name}</td>
					<td>${screen.getSuperLocName(locId)}</td>
					<td><a href="molgenis.do?__target=${screen.name}&__action=Manage&locId=${locId?string.computer}">Manage animals in ${loc.name}</a></td>
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
