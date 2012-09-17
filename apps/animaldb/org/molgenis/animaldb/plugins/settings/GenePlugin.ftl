<#macro org_molgenis_animaldb_plugins_settings_GenePlugin screen>
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
	
	<div id='buttons_part' class='row'>
	<input type='submit' class='addbutton' value='Add' onclick="__action.value='addGene'" />
	</div>

<#else>

	<p>
		<a href="molgenis.do?__target=${screen.name}&__action=Add">Make new gene modification</a>
		<!--
		<br />
		<a href="molgenis.do?__target={screen.name}&__action=Import">Import gene modifications</a>
		-->
	</p>

	<#if screen.geneList?size gt 0>
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="spectable">
			<thead>
				<tr>
					<th>Name</th>
				</tr>
			</thead>
			<tbody>
			<#list screen.geneList as gene>
				<tr>
					<td>${gene}</td>
				</tr>
			</#list>
			</tbody>
		</table>
	<#else>
		<p>There are no genes yet</p>
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
