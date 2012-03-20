<#macro org_molgenis_animaldb_plugins_animal_LocationPlugin screen>
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
	<div>
		<input type='submit' class='addbutton' id='add' value='Add animals' onclick="__action.value='AddAnimals'" />
	</div>
	<br />
	<hr />
	<label for="animalsinlocmatrix">${screen.matrixLabel}</label>
	${screen.renderAnimalsInLocMatrixViewer()}
	<hr />
	<div class='row'>
		<label for="moveto">Move selected to:</label>
		<select name="moveto" id="moveto" class="selectbox">
			<#list screen.locationList as ll>
				<option value="${ll.name}">${ll.name}</option>
			</#list>
		</select>
	</div>
	<div class='row'>
		<script>
			$(function() {
				$( "#startdate" ).datepicker({
					numberOfMonths: 1,
					showButtonPanel: true,
					dateFormat: "yy-mm-dd"
				});
			});
		</script>
		<label for="startdate">Start date:</label>
		<input type='text' id='startdate' name='startdate' value='${screen.currentDate}' />
	</div>
	<div class='row'>
		<label for="move">&nbsp;</label>
		<input type='submit' class='addbutton' id='move' value='Apply' onclick="__action.value='Move'" />
	</div>
	
<#elseif screen.action == "AddAnimals">

	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>
	
	<label for="animalsnotinlocmatrix">All animals:</label>
	${screen.renderAnimalsNotInLocMatrixViewer()}
	<hr />
	<div class='row'>
		<script>
			$(function() {
				$( "#addstartdate" ).datepicker({
					numberOfMonths: 1,
					showButtonPanel: true,
					dateFormat: "yy-mm-dd"
				});
			});
		</script>
		<label for="addstartdate">Start date:</label>
		<input type='text' id='addstartdate' name='addstartdate' value='${screen.currentDate}' />
	</div>
	<div class='row'>
		<label for="applyadd">&nbsp;</label>
		<input type='submit' class='addbutton' id="applyadd" value='Add' onclick="__action.value='ApplyAddAnimals'" />
	</div>
	
<#else>

	<#if screen.locationList?size gt 0>
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="loctable">
			<thead>
				<tr>
					<th></th>
					<th>Name</th>
					<!--<th>Part of</th>-->
				</tr>
			</thead>
			<tbody>
			<#list screen.locationList as loc>
				<#assign locName = loc.getName()>
				<tr>
					<#--<td><a href="molgenis.do?__target=${screen.name}&__action=Manage&locId=${locId?string.computer}">Manage animals in ${loc.name}</a></td> -->
					<td><a href="molgenis.do?__target=${screen.name}&__action=Manage&locName=${locName}"><img id="manage_loc_${locName}" class="edit_button" title="Manage animals in location: ${locName}" alt="Manage animals in location" src="generated-res/img/grid-manage-icon.gif"></a></td>
					<td>${locName}</td>
					<#--<td>${screen.getSuperLocName(locId)}</td>-->
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
	  "bJQueryUI" : true,
	  "aoColumnDefs": [ 
      	{ "sWidth": "30px", "aTargets": [ 0 ] }
    	] 
	  }
	);
</script>

</#macro>
