<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${model.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${model.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">

<!-- this shows a title and border -->
	<div class="formscreen" style="border:0px">
	
		<div class="form_header" id="${model.getName()}"">
		${model.label}
		</div>

		
		
		<#--optional: mechanism to show messages-->
		<#list model.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		

			
			
	<#--begin your plugin-->	
${model.getMatrixViewerIndv()}
		<#--end of your plugin-->	
	</div>
	
	
</form>
