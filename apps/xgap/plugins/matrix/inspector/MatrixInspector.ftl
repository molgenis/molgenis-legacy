<#macro MatrixInspector screen>
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

<br>
<#if model.isHasBackend() == true>
	<#if model.warningsAndErrors.warnings?size gt 0>
		<p class="warningmessage">Warnings:</p>
		
		<#assign cap = model.warningsAndErrors.warnings?size>
		<#assign diff = 0>
		<#if cap gt 10>
			<#assign cap = 10>
			<#assign diff = model.warningsAndErrors.warnings?size-cap>
		</#if>
		
		<ul>
		<#list 0..cap-1 as n>
			<li>${model.warningsAndErrors.warnings[n]}</li>
		</#list>
		</ul>
		
		<#if diff gt 0>
			${diff} more...<br>
		</#if>
	
	<#else>
		<p class="successmessage">No warnings.</p><br>
	</#if>
	
	<#if model.warningsAndErrors.missingRowAnnotations?size gt 0>
		<p class="errormessage">Missing annotations for rows:</p>
		
		<#assign cap = model.warningsAndErrors.missingRowAnnotations?size>
		<#assign diff = 0>
		<#if cap gt 10>
			<#assign cap = 10>
			<#assign diff = model.warningsAndErrors.missingRowAnnotations?size-cap>
		</#if>
		
		<ul>
		<#list 0..cap-1 as n>
			<li>${model.warningsAndErrors.missingRowAnnotations[n]}</li>
		</#list>
		</ul>
		
		<#if diff gt 0>
			${diff} more...<br><br>
		</#if>
		
	<#else>
		<p class="successmessage">No missing row annotations.</p><br>
	</#if>
	
	
	<#if model.warningsAndErrors.missingColumnAnnotations?size gt 0>
		<p class="errormessage">Missing annotations for columns:</p>
		
		<#assign cap = model.warningsAndErrors.missingColumnAnnotations?size>
		<#assign diff = 0>
		<#if cap gt 10>
			<#assign cap = 10>
			<#assign diff = model.warningsAndErrors.missingColumnAnnotations?size-cap>
		</#if>
		
		<ul>
		<#list 0..cap-1 as n>
			<li>${model.warningsAndErrors.missingColumnAnnotations[n]}</li>
		</#list>
		</ul>
		
		<#if diff gt 0>
			${diff} more...<br>
		</#if>
		
	<#else>
		<p class="successmessage">No missing column annotations.</p><br>
	</#if>
	
	<#if model.warningsAndErrors.errors?size gt 0>
		<p class="errormessage">Other errors:</p>
		
		<#assign cap = model.warningsAndErrors.errors?size>
		<#assign diff = 0>
		<#if cap gt 10>
			<#assign cap = 10>
			<#assign diff = model.warningsAndErrors.errors?size-cap>
		</#if>
		
		<ul>
		<#list 0..cap-1 as n>
			<li>${model.warningsAndErrors.errors[n]}</li>
		</#list>
		</ul>
		
		<#if diff gt 0>
			${diff} more...<br>
		</#if>
		
	<#else>
		<p class="successmessage">No other errors.</p><br>
	</#if>
<#else>
	This matrix has no data source attached to it. An error report cannot be generated.
	<br><br>
</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
