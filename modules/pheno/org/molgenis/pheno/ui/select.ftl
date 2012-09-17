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

<form method="post" action="molgenis.do">
${model.selectProtocolForm.__action}
${model.selectProtocolForm.__target}
<p>
${model.selectProtocolForm.Protocol}
</p>
<@action name="show" label="Back to List mode"/>
<@action name="add" label="Apply Protocol"/>
</form>

			</div>
		</div>
	</div>