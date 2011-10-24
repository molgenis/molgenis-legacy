<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${model.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${model.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${model.getName()}">
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
		
		<div class="screenbody">
			<div class="screenpadding">	
			
<#--begin your plugin-->	
<div id="bla" style="margin-bottom:35px">
	<input type="submit" name="setIndividual" value='Individuals_info' onclick='__action.value="setIndividual"' />
	<input type="submit" name="setPersonal" value='Personal_info' onclick='__action.value="setPersonal"' />
	<input type="submit" name="setMedical" value='Medical_info' onclick='__action.value="setMedical"' />

</div>

${model.getMatrixViewerIndv()}


<input type="submit" name="setSelection" value='Get selected from matrix' onclick='__action.value="setSelection"' />

<#if model.getSelection()??>
	${model.getMatrixViewerSample()}
	<p>You selected from the matrix: ${model.getSelection()}</p>
</#if>
	
	

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
