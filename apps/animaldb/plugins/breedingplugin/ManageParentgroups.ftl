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

	<div id='name_part' class='row' style="width:700px">
		<label for='groupname'>Group name:</label>
		<input type='text' class='textbox' name='groupname' id='groupname' value='<#if screen.groupName?exists>${screen.getGroupName()}</#if>' />
	</div>
		
	<!-- (Possible) mother(s) selector -->
	<div id='motherselect' class='row'>
		<div style='float:left'>
			<label for='mother'>(Possible) mother(s):</label>
			<select name='mother' size='10'>
			<#if screen.selectedMotherIdList?exists>
				<#list screen.selectedMotherIdList as selectedMotherId>
					<#assign name = screen.getAnimalName(selectedMotherId)>
					<option value='${selectedMotherId?string.computer}'>${name}</option>
				</#list>
			</#if>
			</select>
		</div>
		<div>Add...<br />
			<input type='submit' value='&lt;&lt;' onclick='__action.value="addIndMother"' />
			<select name='ind_mother'>
			<#if screen.motherIdList?exists>
				<#list screen.motherIdList as motherId>
					<#assign name = screen.getAnimalName(motherId)>
					<option value='${motherId?string.computer}'>${name}</option>
				</#list>
			</#if>
			</select>
		</div>
		<div>Remove... <br />
			<input type='submit' value='&gt;&gt;' onclick='__action.value="remIndMother"' />
		</div>
	</div>
		
	<!-- (Possible) father(s) selector -->
	<div id='fatherselect' class='row'>
		<div style='float:left'>
			<label for='father'>(Possible) father(s):</label>
			<select name='father' size='10'>
			<#if screen.selectedFatherIdList?exists>
				<#list screen.selectedFatherIdList as selectedFatherId>
					<#assign name = screen.getAnimalName(selectedFatherId)>
					<option value='${selectedFatherId?string.computer}'>${name}</option>
				</#list>
			</#if>
			</select>
		</div>
		<div>Add...<br />
			<input type='submit' value='&lt;&lt;' onclick='__action.value="addIndFather"' />
			<select name='ind_father'>
			<#if screen.fatherIdList?exists>
				<#list screen.fatherIdList as fatherId>
					<#assign name = screen.getAnimalName(fatherId)>
					<option value='${fatherId?string.computer}'>${name}</option>
				</#list>
			</#if>
			</select>
		</div>
		<div>Remove...<br />
			<input type='submit' value='&gt;&gt;' onclick='__action.value="remIndFather"' />
		</div>
	</div>
	
	<!-- Line -->
	<div id="lineselect" class="row" style='clear:left'>
		<label for="line">Breeding line:</label>
		<select name="line" id="line" class="selectbox">
			<#if screen.lineList??>
				<#list screen.lineList as line>
					<option value="${line.id?string.computer}" <#if line.id == screen.line>selected="selected"</#if>>${line.name}</option>
				</#list>
			</#if>
		</select>
	</div>
	
	<!-- Start date -->
	<div id='datevalue_part' class='row'>
		<label for='startdate'>Start date:</label>
		<input type='text' class='textbox' id='startdate' name='startdate' value='<#if screen.startdate?exists>${screen.getStartdate()}</#if>' onclick='showDateInput(this)' autocomplete='off' />
	</div>
		
	<!-- Add button -->
	<div id='buttons_part' class='row'>
		<input type='submit' class='addbutton' value='Add' onclick="__action.value='addParentgroup'" />
	</div>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
