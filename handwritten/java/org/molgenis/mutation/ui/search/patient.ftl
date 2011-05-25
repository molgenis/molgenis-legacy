<#assign patientSummaryVO = vo.patientSummaryVO>
<table class="listtable" cellpadding="4">
<tr class="form_listrow0"><th>Patient ID</th><td>${patientSummaryVO.patient.getIdentifier()}</td></tr>
<tr class="form_listrow1"><th>Genotype</th><td>
<#if patientSummaryVO.mutation1??>${patientSummaryVO.mutation1.getCdna_Notation()}<#if patientSummaryVO.mutation1.getAa_Notation() != ""> (${patientSummaryVO.mutation1.getAa_Notation()})</#if></#if>
<#if patientSummaryVO.mutation2??> and ${patientSummaryVO.mutation2.getCdna_Notation()}<#if patientSummaryVO.mutation2.getAa_Notation() != ""> (${patientSummaryVO.mutation2.getAa_Notation()})</#if></#if></th></tr>
<tr class="form_listrow0"><th>Phenotype</th><td>${patientSummaryVO.phenotype.getMajortype()}, ${patientSummaryVO.phenotype.getSubtype()}<#if patientSummaryVO.patient.getConsent() != "no"> [<a href="molgenis.do?__target=${screen.name}&__action=showPhenotypeDetails&pid=${patientSummaryVO.patient.getIdentifier()}#phenotype">Details</a>]</#if></td></tr>
<tr class="form_listrow1"><th>Immunofluorescence: type VII collagen</th><td><#if patientSummaryVO.if_??>${patientSummaryVO.if_.getValue()}</#if><#if patientSummaryVO.patient.getConsent() != "no">  [<a href="molgenis.do?__target=${screen.name}&__action=showPhenotypeDetails&pid=${patientSummaryVO.patient.getIdentifier()}#if">Details</a>]</#if></td></tr>
<tr class="form_listrow0"><th>Electron Microscopy: anchoring fibrils</th><td><#if patientSummaryVO.em_??>${patientSummaryVO.em_.getNumber()}</#if><#if patientSummaryVO.patient.getConsent() != "no">  [<a href="molgenis.do?__target=${screen.name}&__action=showPhenotypeDetails&pid=${patientSummaryVO.patient.getIdentifier()}#em">Details</a>]</#if></td></tr>
<tr class="form_listrow1"><th>Patient material available?</th><td><#if patientSummaryVO.material?size &gt; 0>yes [<a href="molgenis.do?__target=${screen.name}&__action=showPhenotypeDetails&pid=${patientSummaryVO.patient.getIdentifier()}#material">Details</a>]<#else>unknown</#if></td></tr>
<tr class="form_listrow0"><th>Local patient no</th><td>${patientSummaryVO.patient.getNumber()}</td></tr>
<tr class="form_listrow1"><th>Reference</th><td>
<#if patientSummaryVO.publications?? && patientSummaryVO.publications?size &gt; 0>
<#list patientSummaryVO.publications as publication>
<a href="${patientSummaryVO.pubmedURL}${publication.getPubmedID_Name()}" target="_new">${publication.getTitle()}</a><br/>
</#list>
<#if patientSummaryVO.submitter?? && !patientSummaryVO.submitter.getSuperuser()>
First submitted as unpublished case by
${patientSummaryVO.submitter.getDepartment()}, ${patientSummaryVO.submitter.getInstitute()}, ${patientSummaryVO.submitter.getCity()}, ${patientSummaryVO.submitter.getCountry()}
</#if>
<#elseif patientSummaryVO.submitter??>
Unpublished<br/>
${patientSummaryVO.submitter.getDepartment()}, ${patientSummaryVO.submitter.getInstitute()}, ${patientSummaryVO.submitter.getCity()}, ${patientSummaryVO.submitter.getCountry()}
</#if></td></tr>
<#--<tr class="form_listrow1"><th>Patient consent</th><td>${patientSummaryVO.patient.consent}</td></tr>-->
</table>

<p>
[<a href="javascript:back();">Back to results</a>]
</p>
<p>
[<a href="#">Back to top</a>]
</p>