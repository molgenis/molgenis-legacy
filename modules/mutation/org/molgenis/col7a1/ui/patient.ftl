<#assign patientSummaryVO = vo.patientSummaryVO>
<table class="listtable">
<tr class="form_listrow0"><th>Patient ID</th><td>${patientSummaryVO.patientIdentifier}</td></tr>
<tr class="form_listrow1"><th>Genotype</th><td>
<#list patientSummaryVO.variantSummaryVOList as variantSummaryVO>
${variantSummaryVO.cdnaNotation}<#if variantSummaryVO.aaNotation??> (${variantSummaryVO.aaNotation})</#if>
</#list>
</td></tr>
<tr class="form_listrow0"><th>Phenotype</th><td>${patientSummaryVO.getPhenotypeMajor()}<#if patientSummaryVO.getPhenotypeSub() != "">, ${patientSummaryVO.getPhenotypeSub()}</#if><#if patientSummaryVO.patientConsent != "no"> [<a href="molgenis.do?__target=${screen.name}&__action=showPhenotypeDetails&pid=${patientSummaryVO.patientIdentifier}#phenotype">Details</a>]</#if></td></tr>
<#--
<tr class="form_listrow1"><th>Immunofluorescence: type VII collagen</th><td><#if patientSummaryVO.if_??>${patientSummaryVO.if_.getValue()}</#if><#if patientSummaryVO.patient.getConsent() != "no">  [<a href="molgenis.do?__target=${screen.name}&__action=showPhenotypeDetails&pid=${patientSummaryVO.patient.getIdentifier()}#%20Immunofluorescence">Details</a>]</#if></td></tr>
<tr class="form_listrow0"><th>Electron Microscopy: anchoring fibrils</th><td><#if patientSummaryVO.em_??>${patientSummaryVO.em_.getNumber()}</#if><#if patientSummaryVO.patient.getConsent() != "no">  [<a href="molgenis.do?__target=${screen.name}&__action=showPhenotypeDetails&pid=${patientSummaryVO.patient.getIdentifier()}#%20Electron">Details</a>]</#if></td></tr>
-->
<tr class="form_listrow1"><th>Patient material available?</th><td><#if patientSummaryVO.patientMaterialList?size &gt; 0>yes [<a href="molgenis.do?__target=${screen.name}&__action=showPhenotypeDetails&pid=${patientSummaryVO.patientIdentifier}#material">Details</a>]<#else>unknown</#if></td></tr>
<tr class="form_listrow0"><th>Local patient no</th><td>${patientSummaryVO.patientNumber}</td></tr>
<tr class="form_listrow1"><th>Reference</th><td>
<#if patientSummaryVO.publicationVOList?? && patientSummaryVO.publicationVOList?size &gt; 0>
<#list patientSummaryVO.publicationVOList as publicationVO>
<a href="${patientSummaryVO.pubmedURL}${publicationVO.pubmedId}" target="_new">${publicationVO.title}</a><br/>
</#list>
<#if patientSummaryVO.submitterDepartment??>
First submitted as unpublished case by
${patientSummaryVO.submitterDepartment}, ${patientSummaryVO.submitterInstitute}, ${patientSummaryVO.submitterCity}, ${patientSummaryVO.submitterCountry}
</#if>
<#elseif patientSummaryVO.submitter??>
Unpublished<br/>
${patientSummaryVO.submitterDepartment}, ${patientSummaryVO.submitterInstitute}, ${patientSummaryVO.submitterCity}, ${patientSummaryVO.submitterCountry}
</#if></td></tr>
</table>

<p>
[<a href="javascript:back();">Back to results</a>]
</p>
<p>
[<a href="#">Back to top</a>]
</p>