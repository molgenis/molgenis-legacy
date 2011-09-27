<#macro plugins_breedingplugin_ManageParentgroups screen>
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

<#if screen.action == "showAddParentgroup">

<div>

	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>

	<!-- Line -->
	<div>
		<label for="line">Breeding line:</label>
		<select name="line" id="line" class="selectbox">
			<#if screen.lineList??>
				<#list screen.lineList as line>
					<option value="${line.id?string.computer}" <#if line.id == screen.line>selected="selected"</#if>>${line.name}</option>
				</#list>
			</#if>
		</select>
		<input type='submit' id='updateline' value='Update' onclick='__action.value="updateLine"' />
	</div>
	
	<hr />
	
	<div>
		<div style='float:left'>
			<label for='mother'>(Possible) mother(s):</label>
			<select id='mother' name='mother' size='10'>
			<#if screen.selectedMotherIdList?exists>
				<#list screen.selectedMotherIdList as selectedMotherId>
					<#assign name = screen.getAnimalName(selectedMotherId)>
					<option value='${selectedMotherId?string.computer}'>${name}</option>
				</#list>
			</#if>
			</select>
		</div>
		<div>Remove<br />
			<input type='submit' id='remmother' value='&gt;&gt;' onclick='__action.value="remIndMother"' />
		</div>
	</div>
	<!-- (Possible) mother(s) selector -->
	<div style='clear:left'>
		${screen.renderMotherMatrixViewer()}<br />
		<input type='submit' id='addmothersfrommatrix' value='&lt;&lt; Add selected mother(s)' onclick='__action.value="addMothersFromMatrix"' />
	</div>
	
	<hr />
	
	<div>
		<div style='float:left'>
			<label for='father'>(Possible) father(s):</label>
			<select id='father' name='father' size='10'>
			<#if screen.selectedFatherIdList?exists>
				<#list screen.selectedFatherIdList as selectedFatherId>
					<#assign name = screen.getAnimalName(selectedFatherId)>
					<option value='${selectedFatherId?string.computer}'>${name}</option>
				</#list>
			</#if>
			</select>
		</div>
		<div>Remove<br />
			<input type='submit' id='remfather' value='&gt;&gt;' onclick='__action.value="remIndFather"' />
		</div>
	</div>
	
	<!-- (Possible) father(s) selector -->
	<div style='clear:left'>
		${screen.renderFatherMatrixViewer()}<br />
		<input type='submit' id='addfathersfrommatrix' value='&lt;&lt; Add selected father(s)' onclick='__action.value="addFathersFromMatrix"' />
	</div>
	
	<hr />
	
	<!-- Start date -->
	<div style='clear:left'>
		<label for='startdate'>Start date:</label>
		<input type='text' class='textbox' id='startdate' name='startdate' value='<#if screen.startdate?exists>${screen.getStartdate()}</#if>' onclick='showDateInput(this)' autocomplete='off' />
	</div>
	
	<!-- Remarks -->
	<div>
		<label for='remarks'>Remarks:</label>
		<input type='text' class='textbox' id='remarks' name='remarks' />
	</div>
		
	<!-- Add button -->
	<div>
		<input type='submit' id='addpg' class='addbutton' value='Add' onclick="__action.value='addParentgroup'" />
	</div>
	
</div>

<#else>

<div>
	<p><a href="molgenis.do?__target=${screen.name}&__action=showAddParentgroup">Make new parent group</a></p>
	
	<#if screen.pgList?size gt 0>
		<p><h2>Existing parent groups</h2></p>
		<table cellpadding="0" cellspacing="0" border="0" class="display" id="pgstable">
			<thead>
				<tr>
					<th>Name</th>
					<th>Start date</th>
					<th>Remarks</th>
				</tr>
			</thead>
			<tbody>
			<#list screen.pgList as pg>
				<#assign pgId = pg.getId()>
				<tr>
					<td>${pg.name}</td>
					<td>${screen.getPgStartDate(pgId)}</td>
					<td>${screen.getPgRemarks(pgId)}</td>
				</tr>
			</#list>
			</tbody>
		</table>
	<#else>
		<p>There are no parent groups yet.</p>
	</#if>

</div>

</#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script>
	var oTable = jQuery('#pgstable').dataTable(
	{ "bProcessing": true,
	  "bServerSide": false,
	  "sPaginationType": "full_numbers",
	  "bSaveState": true,
	  "bAutoWidth": false,
	  "bJQueryUI" : true }
	);
</script>

</#macro>
