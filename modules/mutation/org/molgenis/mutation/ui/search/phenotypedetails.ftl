<#include "header.ftl">

<#-- Patient details that are not considered to be phenotypic details -->
<#assign patientSummaryVO = vo.patientSummaryVO>
<h4><a name="phenotype">Characteristics</a></h4>
<table class="listtable" cellpadding="4">
<tr class="form_listrow1"><th width="50%">Age</th><td>${patientSummaryVO.patientAge}</td></tr>
<tr class="form_listrow0"><th width="50%">Gender</th><td>${patientSummaryVO.patientGender}</td></tr>
<tr class="form_listrow1"><th width="50%">Ethnicity</th><td>${patientSummaryVO.patientEthnicity}</td></tr>
<tr class="form_listrow0"><th width="50%">Deceased</th><td>${patientSummaryVO.patientDeceased}</td></tr>
<tr class="form_listrow1"><th width="50%">Cause of death</th><td><#if patientSummaryVO.patientDeathCause??>${patientSummaryVO.patientDeathCause}</#if></td></tr>
<#if vo.gene == "COL7A1">
<tr class="form_listrow0"><th width="50%">MMP1 allele 1</th><td><#if patientSummaryVO.patientMmp1Allele1??>${patientSummaryVO.patientMmp1Allele1}</#if></td></tr>
<tr class="form_listrow1"><th width="50%">MMP1 allele 2</th><td><#if patientSummaryVO.patientMmp1Allele2??>${patientSummaryVO.patientMmp1Allele2}</#if></td></tr>
</#if>
</table>

<#-- Observable features -->
<#assign phenotypeDetailsVO = vo.phenotypeDetailsVO>
<#list phenotypeDetailsVO.protocolNames as protocolName>
<h4>${protocolName}</h4>
<table class="listtable" cellpadding="4">
<#assign even = 1>
<#assign observedValueVOs = phenotypeDetailsVO.observedValues[protocolName]>
<#list observedValueVOs as observedValueVO>
<#if even == 1>
  <#assign class = "form_listrow0">
  <#assign even = 0>
<#else>
  <#assign class = "form_listrow1">
  <#assign even = 1>
</#if>
<tr class="${class}"><th width="50%">${observedValueVO.featureName}</th><td>${observedValueVO.value}</td></tr>
</#list>
</table>
</#list>

<#-- Patient material -->
<#if vo.gene == "COL7A1">
<h4><a name="material">Patient material</a></h4>
<table class="listtable" cellpadding="4">
<tr class="form_listrow1"><th width="50%">Patient material available?</th><td><#if patientSummaryVO.patientMaterialList?size &gt; 0><#list patientSummaryVO.patientMaterialList as material>${material}<br/></#list><#else>unknown</#if></td></tr>
</table>
</#if>

<p>
[<a href="javascript:back();">Back to patient</a>]
</p>
<p>
[<a href="#">Back to top</a>]
</p>

<#-- <#include "displayOptions.ftl"> -->

<#include "footer.ftl">