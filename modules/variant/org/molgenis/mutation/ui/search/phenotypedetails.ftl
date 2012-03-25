<#include "header.ftl">

<#-- Observable features -->
<#assign individualDTO = vo.individualDTO>
<#list individualDTO.protocolList as protocolDTO>
<#assign observedValueDTOs = individualDTO.observedValues["Protocol" + protocolDTO.protocolId]>
<#if observedValueDTOs?size &gt; 0>
<h4>${protocolDTO.protocolName}</h4>
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

<p>
[<a href="javascript:history.back();" onclick="javascript:history.back();">Back to patient</a>]
</p>
<p>
[<a href="#">Back to top</a>]
</p>

<#-- <#include "displayOptions.ftl"> -->

<#include "footer.ftl">