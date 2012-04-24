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
${model.individualForm.__action}
${model.individualForm.__target}
<#assign individualDTO = model.individualDTO>

<#list model.protocolDTOList as protocol>
	<#assign protocolKey = "Protocol" + protocol.protocolId>

	<#if individualDTO.observedValues?keys?seq_contains(protocolKey)>

		<h3>${protocol.protocolName}</h3>

		<#list individualDTO.observedValues[protocolKey]?keys as paKey>
			<#assign observedValueDTOValList = individualDTO.observedValues[protocolKey][paKey]>
			<#assign tmpObservedValueDTO     = observedValueDTOValList?first>
	
			<h4>${tmpObservedValueDTO.protocolApplicationName} (${tmpObservedValueDTO.protocolApplicationTime?string.medium_short})</h4>

			<table cellpadding="4">
			<tr><th>Feature</th><th>Value</th></tr>
				
			<#list observedValueDTOValList as observedValueDTO>
				<#assign ovKey = "ObservedValue" + observedValueDTO.observedValueId>
				<#if observedValueDTO.protocolId == protocol.protocolId>
					<tr><th>${observedValueDTO.featureDTO.featureName}</th><td>${model.createIndividualInput(ovKey)}</td></tr>
				</#if>
			</#list>
				
			</table>
		</#list>

	</#if>

</#list>

<@action name="show" label="Back to List mode"/>
<@action name="update" label="Save"/>
<@action name="add" label="Apply Protocol"/>
</form>

			</div>
		</div>
	</div>