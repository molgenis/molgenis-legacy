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

<#assign individualDTO = model.individualDTO>

<#list model.protocolDTOList as protocol>
	<#assign protocolKey = "Protocol" + protocol.protocolId>

	<#if individualDTO.observedValues?keys?seq_contains(protocolKey)>

		<h3>${protocol.protocolName}</h3>

		<#list individualDTO.observedValues[protocolKey]?keys as paKey>
			<#assign observedValueDTOValList = individualDTO.observedValues[protocolKey][paKey]>
			<#assign tmpObservedValueDTO     = observedValueDTOValList?first>
	
			<h4>${tmpObservedValueDTO.protocolApplicationName}
			(${tmpObservedValueDTO.protocolApplicationTime?string.medium_short})
			<#if tmpObservedValueDTO.performerNameList?size &gt; 0>Performers: <#list tmpObservedValueDTO.performerNameList as performerName>${performerName} </#list></#if>
			</h4>

			<table class="listtable" cellpadding="4">
			<tr><th width="50%">Feature</th><th width="50%">Value</th></tr>
				
			<#list observedValueDTOValList as observedValueDTO>
				<#assign even = 1>
				<#if observedValueDTO.protocolId == protocol.protocolId>
					<#if even == 1>
						<#assign class = "form_listrow0">
						<#assign even = 0>
					<#else>
						<#assign class = "form_listrow1">
						<#assign even = 1>
					</#if>
					<tr class="${class}"><th width="50%">${observedValueDTO.featureDTO.featureName}</th><td width="50%">${observedValueDTO.value}</td></tr>
				</#if>
			</#list>
				
			</table>
		</#list>

	</#if>

</#list>

<#if model.isEditable()>
<form method="post" action="molgenis.do">
${model.individualForm.__action}
${model.individualForm.__target}
<@action name="edit" label="Edit"/>
<@action name="select" label="Apply Protocol"/>
</form>
</#if>

			</div>
		</div>
	</div>