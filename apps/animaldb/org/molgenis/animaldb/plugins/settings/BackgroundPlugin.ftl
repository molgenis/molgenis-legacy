<#macro plugins_settings_BackgroundPlugin screen>
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

	<div class="row">
		<label for="name">Name:</label>
		<input type="text" name="name" id="name" class="textbox" />
	</div>
	<div class="row">
		<label for="species">Species:</label>
		<select name="species" id="species" class="selectbox">
			<#if screen.speciesList??>
				<#list screen.speciesList as species>
					<option value="${species.id?string.computer}">${species.name}</option>
				</#list>
			</#if>
		</select>
	</div>
	<div class='row'>
		<input type='submit' class='addbutton' value='Add' onclick="__action.value='addBackground'" />
	</div>

<#else>

	<p>
		<a href="molgenis.do?__target=${screen.name}&__action=Add">Make new background</a>
	</p>

	<#if screen.backgroundList?size gt 0>
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="bkgtable">
			<thead>
				<tr>
					<th>Name</th>
					<th>Species</th>
				</tr>
			</thead>
			<tbody>
			<#list screen.backgroundList as bkg>
				<#assign bkgId = bkg.getId()>
				<tr>
					<td>${bkg.name}</td>
					<td><#if screen.getSpecies(bkgId)??>${screen.getSpecies(bkgId)}</#if></td>
				</tr>
			</#list>
			</tbody>
		</table>
	<#else>
		<p>There are no backgrounds yet</p>
	</#if>
</#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script>
	var oTable = jQuery('#bkgtable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI" : true }
	);
</script>

</#macro>
