<#macro MatrixAdmin screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
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

<#if screen.myModel?exists>
	<#assign modelExists = true>
	<#assign model = screen.myModel>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>

<#if model.isHasBackend() == true>
	<br>
	Delete data source <input type="image" src="res/img/delete.png" onclick="if (confirm('You are about to delete the data source for this matrix. Are you sure?')) { document.forms.${screen.name}.__action.value = 'deleteBackend'; } else { return false; }" />
	<br><br>
<#else>
	<br>
	This matrix has no data source attached to it.
	<br><br>
</#if>


<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
