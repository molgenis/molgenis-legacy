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

<#-- Observable features -->
<#assign individualDTO = model.individualDTO>
<#list individualDTO.protocolList as protocol>
<#assign protocolId = "Protocol" + protocol.protocolId>
<#assign observedValueDTOs = individualDTO.observedValues[protocolId]>
<#if observedValueDTOs?size &gt; 0>
<h4>${protocol.protocolName}</h4>
<table class="listtable" cellpadding="4">
<#assign even = 1>
<#list observedValueDTOs as observedValueDTO>
<#if even == 1>
  <#assign class = "form_listrow0">
  <#assign even = 0>
<#else>
  <#assign class = "form_listrow1">
  <#assign even = 1>
</#if>
<tr class="${class}"><th width="50%">${observedValueDTO.featureDTO.featureName}</th><td>${observedValueDTO.value}</td></tr>
</#list>
</table>
</#if>
</#list>
<#if model.isEditable()>
<form method="post" action="molgenis.do">
${model.individualForm.__action}
${model.individualForm.__target}
<@action name="edit" label="Edit"/>
</form>
</#if>

			</div>
		</div>
	</div>