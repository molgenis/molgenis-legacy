<#include "header.ftl">

<a name="phenotype"></a>
<#-- Observable features -->
<#assign individualDTO = vo.individualDTO>
<#list individualDTO.protocolList as protocolDTO>
<#assign protocolKey = "Protocol" + protocolDTO.protocolId>

<#if individualDTO.observedValues?keys?seq_contains(protocolKey)>

<#list individualDTO.observedValues[protocolKey]?keys as paKey>
<#assign observedValueDTOValList = individualDTO.observedValues[protocolKey][paKey]>
<#assign tmpObservedValueDTO     = observedValueDTOValList?first>

<#if observedValueDTOValList?size &gt; 0>
<h4>${protocolDTO.protocolName}</h4>
<table class="listtable" cellpadding="4">
<#assign even = 1>
<#list observedValueDTOValList as observedValueDTO>
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

</#if>

</#list>

<p>
[<a href="javascript:history.back();" onclick="javascript:history.back();">Back to patient</a>]
</p>
<p>
[<a href="#">Back to top</a>]
</p>

<#-- <#include "displayOptions.ftl"> -->

<#include "footer.ftl">