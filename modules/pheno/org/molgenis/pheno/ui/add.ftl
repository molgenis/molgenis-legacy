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
${model.applyProtocolForm.__action}
${model.applyProtocolForm.__target}
<p>
<table cellpadding="4">
<tr><td>Name of ProtocolApplication</td><td>${model.applyProtocolForm.paName}</td></tr>
<tr><td>Time of ProtocolApplication</td><td>${model.applyProtocolForm.paTime}</td></tr>
<tr><td>Performer</td><td>${model.applyProtocolForm.paPerformer}</td>
<#list model.protocolDTO.featureDTOList as featureDTO>
<tr><td>${featureDTO.featureName}</td><td>${model.createProtocolInput(featureDTO.featureKey)}</td></tr>
</#list>
</table>
</p>
<@action name="show" label="Back to List mode"/>
<@action name="insert" label="Save"/>
</form>

			</div>
		</div>
	</div>