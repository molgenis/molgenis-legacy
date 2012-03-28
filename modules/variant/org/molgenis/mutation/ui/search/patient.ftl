<#include "header.ftl">

<#assign patientSummaryVO = vo.patientSummaryVO>
<table class="listtable">
<tr class="form_listrow0"><th>Patient ID</th><td>${patientSummaryVO.patientIdentifier}</td></tr>
<tr class="form_listrow1"><th>Genotype</th><td>
<#list patientSummaryVO.variantDTOList as variantDTO>
${variantDTO.cdnaNotation}<#if variantDTO.aaNotation??> (${variantDTO.aaNotation})</#if>
</#list>
</td></tr>
<tr class="form_listrow0"><th>Phenotype</th><td>${patientSummaryVO.getPhenotypeMajor()}<#if patientSummaryVO.getPhenotypeSub() != "">, ${patientSummaryVO.getPhenotypeSub()}</#if><#if patientSummaryVO.patientConsent != "no"> [<a href="molgenis.do?__target=${screen.name}&__action=showPhenotypeDetails&pid=${patientSummaryVO.patientIdentifier}#results">Details</a>]</#if></td></tr>
<tr class="form_listrow1"><th>Local patient no</th><td>${patientSummaryVO.patientLocalId}</td></tr>
<tr class="form_listrow0"><th>Reference</th><td>
<#if patientSummaryVO.publicationDTOList?? && patientSummaryVO.publicationDTOList?size &gt; 0>
<#list patientSummaryVO.publicationDTOList as publicationDTO>
<a href="${patientSummaryVO.pubmedURL}${publicationDTO.pubmedId}" target="_new">${publicationDTO.title}</a><br/>
</#list>
<#else>
Unpublished<br/>
</#if>
</td></tr>
</table>

<p>
[<a href="javascript:history.back();" onclick="javascript:history.back();">Back to results</a>]
</p>
<p>
[<a href="#">Back to top</a>]
</p>

<#-- <#include "displayOptions.ftl"> -->

<#include "footer.ftl">