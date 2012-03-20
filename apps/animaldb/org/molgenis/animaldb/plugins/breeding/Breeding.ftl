<#macro org_molgenis_animaldb_plugins_breeding_Breeding screen>
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

<div style="float:left">
	<label for="line">Breeding line:</label>
	<select name="line" id="line" class="selectbox">
		<#if screen.lineList??>
			<#list screen.lineList as line>
				<option value="${line.name}" <#if line.name == screen.line>selected="selected"</#if>>${line.name}</option>
			</#list>
		</#if>
	</select>
</div>
<div style="float:left">
	<input type="image" onclick="__action.value='changeLine'" src="generated-res/img/update.gif" />
</div>
<br /><br /><br />
<div style="clear:both; margin-bottom:2px">
	<div id='menuParentgroups' class='<#if screen.entity == "Parentgroups">navigationSelected<#else>navigationNotSelected</#if>' onclick="window.location.href='molgenis.do?__target=${screen.name}&__action=switchParentgroups'">Parentgroups</div>
	<div id='menuLitters' class='<#if screen.entity == "Litters">navigationSelected<#else>navigationNotSelected</#if>' onclick="window.location.href='molgenis.do?__target=${screen.name}&__action=switchLitters'">Litters</div>
</div>

<#if screen.action == "createParentgroup">

<div class="form_header">Create parentgroup, step 1/3: select mother(s)</div>
<br />
${screen.motherMatrixViewer}<br />
<div style="clear:both">
	<input type='submit' id='cancel2' class='addbutton' value='Cancel' onclick="__action.value='init'" />
	<input type='submit' id='from2to3' class='addbutton' value='Next' onclick="__action.value='addParentgroupScreen3'" />
</div>

<#elseif screen.action == "addParentgroupScreen3">

<div class="form_header">Create parentgroup, step 2/3: select father(s)</div>
<br />
${screen.fatherMatrixViewer}<br />
<div style="clear:both">
	<input type='submit' id='cancel3' class='addbutton' value='Cancel' onclick="__action.value='init'" />
	<input type='submit' id='from3to2' class='addbutton' value='Previous' onclick="__action.value='createParentgroup'" />
	<input type='submit' id='from3to4' class='addbutton' value='Next' onclick="__action.value='addParentgroupScreen4'" />
</div>

<#elseif screen.action == "addParentgroupScreen4">

<div class="form_header">Create parentgroup, step 3/3: set start date and remarks</div>
<br />
<div style="clear:both; display:block">
	<label style="width:16em;float:left;" for="startdate">Start date:</label>
	<input type="text" id="startdate" name="startdate" value="<#if screen.startdate?exists>${screen.getStartdate()}</#if>" onclick='showDateInput(this)' autocomplete='off'  />
</div>
<div style="clear:both; display:block">
	<label style="width:16em;float:left;" for="remarks">Remarks:</label>
	<input type="text" id="remarks" name="remarks" />
</div>
<br />
<div>
	<input type='submit' id='cancel4' class='addbutton' value='Cancel' onclick="__action.value='init'" />
	<input type='submit' id='from4to3' class='addbutton' value='Previous' onclick="__action.value='addParentgroupScreen3'" />
	<input type='submit' id='addpg' class='addbutton' value='Add' onclick="__action.value='addParentgroup'" />
</div>

<#elseif screen.action == "createLitter">

<div class="form_header">Create litter from parentgroup ${screen.getSelectedParentgroup()}</div>
<div style="clear:both; display:block">
	<label style="width:16em;float:left;" for='birthdate'>Birth date:</label>		
	<input type='text' id='birthdate' name='birthdate' <#if screen.getBirthdate??> value="${screen.getBirthdate()}"</#if> />
</div>
<div style="clear:both; display:block">
	<label style="width:16em;float:left;" for='littersize'>Litter size:</label>
	<input type='text' class='textbox' name='littersize' id='littersize' value='<#if screen.litterSize?exists>${screen.getLitterSize()}</#if>' />
</div>
<div style="clear:both; display:block">
	<label style="width:16em;float:left;" for="sizeapp_toggle">Size approximate:</label>
	<input type="checkbox" id="sizeapp_toggle" name="sizeapp_toggle" value="sizeapp" checked="yes" />
</div>
<div style="clear:both; display:block">
	<label style="width:16em;float:left;" for='litterremarks'>Remarks:</label>
	<input type='text' class='textbox' name='litterremarks' id='litterremarks' value='<#if screen.litterRemarks?exists>${screen.getLitterRemarks()}</#if>' />
</div>
<div style="clear:both; display:block">
	<input type='submit' id='addlitter' value='Add' onclick="__action.value='addLitter'" />
</div>

<#else>

	<#if screen.entity == "Parentgroups">
	
	<div class="form_header">Parentgroups</div>
	<div>
		<br />
		<a href="molgenis.do?__target=${screen.name}&__action=createParentgroup"><img id="createParentgroup" title="Create parentgroup" alt="Create parentgroup" src="generated-res/img/new.png"></a>
		<br /><br />
		${screen.pgMatrixViewer}
		<br />
		<input type='submit' id='createlitter' value='Create new litter from selected parentgroup' onclick="__action.value='createLitter'" />
		<br />
		<input type='submit' id='deactivate' value='(De)activate selected parentgroup' onclick="__action.value='deActivate'" />
	</div>
	
	<#else>
	
	<div style="clear:both" class="form_header">Litters</div>
	<div>
		<br />
		${screen.litterMatrixViewer}
		<br />
		<input type='submit' id='deactivate' value='(De)activate selected litter' onclick="__action.value='deActivateLitter'" />
		<br />
		<input type='submit' id='wean' value='Wean selected litter' onclick="__action.value='weanLitter'" />
		<br />
		<input type='submit' id='genotype' value='Genotype selected litter' onclick="__action.value='genotypeLitter'" />
		<br />
		<input type='submit' id='label' value='Make cage labels for selected litter' onclick="__action.value='makeLabels'" />
	</div>
	
	</#if>

</#if>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script>
	jQuery('#createlitter').button();
	jQuery('#from2to3').button();
	jQuery('#from3to4').button();
	jQuery('#from4to3').button();
	jQuery('#from3to2').button();
	jQuery('#cancel2').button();
	jQuery('#cancel3').button();
	jQuery('#cancel4').button();
	jQuery('#addpg').button();
	jQuery('#deactivate').button();
	jQuery('#wean').button();
	jQuery('#genotype').button();
	jQuery('#label').button();
	jQuery('#addlitter').button();
	
	$(function() {
		$("#birthdate").datepicker({
			numberOfMonths: 1,
			showButtonPanel: true,
			dateFormat: "yy-mm-dd"
		});
	});

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
