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

<#if screen.action == "addParentgroupScreen1">

<div>
	<p><h2>Make new parentgroup, step 1/4: set line</h2></p>
	
	<div>
		<label for="line">Breeding line:</label>
		<select name="line" id="line" class="selectbox">
			<#if screen.lineList??>
				<#list screen.lineList as line>
					<option value="${line.id?string.computer}" <#if line.id == screen.line>selected="selected"</#if>>${line.name}</option>
				</#list>
			</#if>
		</select>
	</div>
	
	<div style="clear:both">
		<input type='submit' id='from1to2' class='addbutton' value='Next' onclick="__action.value='addParentgroupScreen2'" />
	</div>
	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>
	
</div>

<#elseif screen.action == "addParentgroupScreen2">

	<p><h2>Make new parentgroup, step 2/4: select mother(s)</h2></p>

	${screen.motherMatrixViewer}<br />
	
	<div style="clear:both">
		<input type='submit' id='from2to1' class='addbutton' value='Previous' onclick="__action.value='addParentgroupScreen1'" />
		<input type='submit' id='from2to3' class='addbutton' value='Next' onclick="__action.value='addParentgroupScreen3'" />
	</div>
	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>

<#elseif screen.action == "addParentgroupScreen3">
	
	<p><h2>Make new parentgroup, step 3/4: select father(s)</h2></p>
	
	${screen.fatherMatrixViewer}<br />
	
	<div style="clear:both">
		<input type='submit' id='from3to2' class='addbutton' value='Previous' onclick="__action.value='addParentgroupScreen2'" />
		<input type='submit' id='from3to4' class='addbutton' value='Next' onclick="__action.value='addParentgroupScreen4'" />
	</div>
	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>

<#elseif screen.action == "addParentgroupScreen4">

	<p><h2>Make new parentgroup, step 4/4: set start date and remarks</h2></p>
	
	<div style='clear:left'>
		<label for='startdate'>Start date:</label>
		<input type='text' class='textbox' id='startdate' name='startdate' value='<#if screen.startdate?exists>${screen.getStartdate()}</#if>' onclick='showDateInput(this)' autocomplete='off' />
	</div>
	
	<div style='clear:left'>
		<label for='remarks'>Remarks:</label>
		<input type='text' class='textbox' id='remarks' name='remarks' />
	</div>
		
	<div>
		<input type='submit' id='from4to3' class='addbutton' value='Previous' onclick="__action.value='addParentgroupScreen3'" />
		<input type='submit' id='addpg' class='addbutton' value='Add' onclick="__action.value='addParentgroup'" />
	</div>
	<p><a href="molgenis.do?__target=${screen.name}&__action=init">Back to overview</a></p>

<#else>

<div>
	<input type='submit' id='createpg' class='addbutton' value='Create new parentgroup' onclick="__action.value='addParentgroupScreen1'" />
	<br />
	<p><h2>Existing parentgroups</h2></p>
	${screen.pgMatrixViewer}

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
