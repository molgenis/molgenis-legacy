<#include "header.ftl">

<#assign patientSummaryVO = vo.patientSummaryVO>
<table class="listtable">
<tr class="form_listrow0"><th>Patient ID</th><td>${patientSummaryVO.patientIdentifier}</td></tr>
<tr class="form_listrow1"><th>Genotype</th><td>
<#list patientSummaryVO.variantSummaryVOList as variantSummaryVO>
${variantSummaryVO.cdnaNotation}<#if variantSummaryVO.aaNotation??> (${variantSummaryVO.aaNotation})</#if>
</#list>
</td></tr>
<tr class="form_listrow0"><th>Phenotype</th><td>${patientSummaryVO.getPhenotypeMajor()}<#if patientSummaryVO.getPhenotypeSub() != "">, ${patientSummaryVO.getPhenotypeSub()}</#if><#if patientSummaryVO.patientConsent != "no"> [<a href="molgenis.do?__target=${screen.name}&__action=showPhenotypeDetails&pid=${patientSummaryVO.patientIdentifier}#phenotype">Details</a>]</#if></td></tr>
<tr class="form_listrow1"><th>Local patient no</th><td>${patientSummaryVO.patientNumber}</td></tr>
<tr class="form_listrow0"><th>Reference</th><td>
<#if patientSummaryVO.publicationVOList?? && patientSummaryVO.publicationVOList?size &gt; 0>
<#list patientSummaryVO.publicationVOList as publicationVO>
<a href="${patientSummaryVO.pubmedURL}${publicationVO.pubmedId}" target="_new">${publicationVO.title}</a><br/>
</#list>
<#else>
Unpublished<br/>
</#if>
</td></tr>
</table>

<p>
[<a href="javascript:back();">Back to results</a>]
</p>
<p>
[<a href="#">Back to top</a>]
</p>

<#-- <#include "displayOptions.ftl"> -->

<#include "footer.ftl">