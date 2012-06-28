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


<div id="geneExpression">
<form action="">
<input type="radio" name="geneExp" value="raw" /> Raw expression<br />
<input type="radio" name="geneExp" value="quanLog" /> Quantile normalized & log2 transformed expression
</form>
</div>
<div id="submit">

<input type='submit' id='jetty' value='Submit' onclick="__action.value='verstuurJetty2'" />

${model.action}

</div>




<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
