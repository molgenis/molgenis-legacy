<#macro org_molgenis_animaldb_plugins_breeding_ManageLines screen>
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
				<script>$.ctNotify("${message.text}", {type: 'confirmation', delay: 5000});</script>
				<!-- <p class="successmessage">${message.text}</p> -->
			<#else>
				<script>$.ctNotify("${message.text}", {type: 'error', delay: 7000});</script>	        	
				<!-- <p class="errormessage">${message.text}</p> -->
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->	

<div>

	<p><h2><#if screen.lineName?exists>Update<#else>Add</#if> breeding line</h2></p>
	<div class='row'>
		<label for='linename'>Line name:</label>
		<input type='text' class='textbox' name='linename' id='linename' value='<#if screen.lineName?exists>${screen.getLineName()}</#if>' />
	</div>
	<div class='row'>
		<label for='fullname'>Full line name:</label>
		<input type='text' class='textbox' name='fullname' id='fullname' value='<#if screen.fullName?exists>${screen.getFullName()}</#if>' />
	</div>
	<!-- Species -->
	<div class="row">
		<label for="species">Species:</label>
		<select name="species" id="species" class="selectbox">
			<#if screen.speciesList??>
				<#list screen.speciesList as species>
					<option value="${species.name}" <#if species.name == screen.species>selected="selected"</#if>>${species.name}</option>
				</#list>
			</#if>
		</select>
	</div>
	<!-- Source -->
	<div class="row">
		<label for="source">Source:</label>
		<select name="source" id="source" class="selectbox">
			<#if screen.sourceList??>
				<#list screen.sourceList as source>
					<option value="${source.name}" <#if source.name == screen.source>selected="selected"</#if>>${source.name}</option>
				</#list>
			</#if>
		</select>
	</div>
	<!-- Remarks -->
	<div class='row'>
		<label for='remarks'>Remarks:</label>
		<input type='text' class='textbox' name='remarks' id='remarks' value='<#if screen.remarks?exists>${screen.getRemarks()}</#if>' />
	</div>
	<!-- Add button -->
	<div id='buttons_part' class='row'>
		<input type='submit' id='add' class='addbutton' value='<#if screen.lineName?exists>Update<#else>Add</#if>' onclick="__action.value='addLine'" />
	</div>
	
</div>
<br />
<div>
	<p><h2>Existing breeding lines</h2></p>
	<#if screen.lineList??>
		<#if screen.lineList?size gt 0>
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="linestable">
				<thead>
					<tr>
						<th></th>
						<th>Name</th>
						<th>Full name</th>
						<th>Species</th>
						<th>Source</th>
						<th>Remarks</th>
						<th></th>
						
					</tr>
				</thead>
				<tbody>
				<#list screen.lineList as line>
					<#assign lineName = line.getName()>
					<tr>
						<td><a href='molgenis.do?__target=${screen.name}&__action=Edit&id=${line.id?string.computer}'><img id="edit_breedingline" class="edit_button" title="edit current record" alt="Edit" src="generated-res/img/editview.gif"></a></td>
						<td>${lineName}</td>
						<td>${screen.getFullName(lineName)}</td>
						<td>${screen.getSpeciesName(lineName)}</td>
						<td>${screen.getSourceName(lineName)}</td>
						<td>${screen.getRemarksString(lineName)}</td>
						<td><a href='molgenis.do?__target=${screen.name}&__action=Delete&id=${line.id?string.computer}'><img id="delete_breedingline" class="edit_button" title="delete current record" alt="Delete" src="generated-res/img/delete.png"></a></td>
					</tr>
				</#list>
				</tbody>
			</table>
		<#else>
			<p>There are no breeding lines yet.</p>
		</#if>
	<#else>
		<p>There are no breeding lines yet.</p>
	</#if>
</div>


<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script>

	var oTable = jQuery('#linestable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI" : true,
	  "aoColumnDefs": [ 
      	{ "sWidth": "30px", "aTargets": [ 0,6 ] }
    	] 
	  }
	);
	
	
</script>
    
</#macro>
