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

<form method="post" action="molgenis.do">
${model.individualForm.__action}
${model.individualForm.__target}
<#assign individualDTO = model.individualDTO>
<#list individualDTO.protocolList as protocol>
<#assign protocolId = "Protocol" + protocol.protocolId>
<h4>${protocol.protocolName}</h4>
<table class="listtable" cellpadding="4">
<#assign even = 1>
<#list protocol.featureDTOList as featureDTO>
<#if even == 1>
  <#assign class = "form_listrow0">
  <#assign even = 0>
<#else>
  <#assign class = "form_listrow1">
  <#assign even = 1>
</#if>
<tr class="${class}"><th width="50%">${featureDTO.featureName}</th><td>${model.createInput(featureDTO.featureKey)}</td></tr>
</#list>
</table>
</#list>
<@action name="show" label="Back to List mode"/>
<@action name="save" label="Save"/>
</form>

			</div>
		</div>
	</div>