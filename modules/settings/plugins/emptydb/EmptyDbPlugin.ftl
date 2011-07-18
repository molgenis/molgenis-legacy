<#macro plugins_emptydb_EmptyDbPlugin screen>
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
		<#-- MOVED DOWN -->
		
		<div class="screenbody">
			<div class="screenpadding">	
	<br><br>
	
	WARNING: this will remove all entries from the current database<br><input type="submit" value="Empty the database" onclick="if (confirm('Do you really wish to wipe the database?')) { document.forms.${screen.name}.__action.value = 'emptyDatabase'; document.forms.${screen.name}.submit(); } else { return false; }"/>
	
	<br><br>
	
</form>
</#macro>