<#assign pager = vo.pager>
<#if pager.count &gt; 0>
	<#assign patientSummaryVOs = pager.page>
<#else>
	<#assign patientSummaryVOs = pager.entities>
</#if>

<#if pager.count &gt; pager.limit>
<p align="center">
<a href="molgenis.do?__target=${screen.name}&__action=patientsFirstPage${pager.offset}#results"><img class="navigation_button" src="generated-res/img/first.png" title="go to first record"/></a>
<a href="molgenis.do?__target=${screen.name}&__action=patientsPrevPage${pager.offset}#results"><img class="navigation_button" src="generated-res/img/prev.png" title="go to previous record"/></a>
<label>${pager.label}</label>
<a href="molgenis.do?__target=${screen.name}&__action=patientsNextPage${pager.offset}#results"><img class="navigation_button" src="generated-res/img/next.png" title="go to next record"/></a>
<a href="molgenis.do?__target=${screen.name}&__action=patientsLastPage${pager.offset}#results"><img class="navigation_button" src="generated-res/img/last.png" title="go to last record"/></a>
</p>
</#if>

<p>
<table class="listtable" cellpadding="4">

<#--
<tr class="tableheader">
<th colspan="3">Patient</th>
<th colspan="4">First Mutation</th>
<th colspan="4">Second Mutation</th>
<th></th>
</tr>
-->

<tr class="tableheader">
<th>No.</th>
<th>Patient ID</th>
<th>Phenotype</th>
<th>Mutation</th>
<th>cDNA change</th>
<th>Protein change</th>
<th>Exon</th>
<th>Consequence</th>

<#--
<th>cDNA change</th>
<th>Protein change</th>
<th>Exon</th>
<th>Consequence</th>
-->

<th>Reference</th>
</tr>
<#list patientSummaryVOs as patientSummaryVO>
<#if patientSummaryVOs?seq_index_of(patientSummaryVO) % 2 == 0>
	<#assign clazz = "form_listrow1">
<#else>
	<#assign clazz = "form_listrow0">
</#if>
<tr class="${clazz}">
<td rowspan="2">${pager.offset + patientSummaryVOs?seq_index_of(patientSummaryVO) + 1}</td>
<td rowspan="2"><a href="molgenis.do?__target=${screen.name}&__action=showPatient&pid=${patientSummaryVO.patient.getIdentifier()}#results">${patientSummaryVO.patient.getIdentifier()}</a></td>
<td rowspan="2">${patientSummaryVO.phenotype.getMajortype()}, ${patientSummaryVO.phenotype.getSubtype()}</td>
<td>First Mutation</td>
<td><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${patientSummaryVO.mutation1.getIdentifier()}#results">${patientSummaryVO.mutation1.getCdna_Notation()}</a></td>
<td>${patientSummaryVO.mutation1.getAa_Notation()}</td>
<td>${patientSummaryVO.mutation1.getExon_Name()}</td>
<td>${patientSummaryVO.mutation1.getConsequence()}</td>
<#--
<td><#if patientSummaryVO.mutation2??><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${patientSummaryVO.mutation2.identifier}">${patientSummaryVO.mutation2.cdna_notation}</a></#if></td>
<td><#if patientSummaryVO.mutation2??>${patientSummaryVO.mutation2.aa_notation}</#if></td>
<td><#if patientSummaryVO.mutation2??>${patientSummaryVO.mutation2.exon_name}</#if></td>
<td><#if patientSummaryVO.mutation2??>${patientSummaryVO.mutation2.consequence}</#if></td>
-->
<td rowspan="2">
<#if patientSummaryVO.publications?? && patientSummaryVO.publications?size &gt; 0>
<#list patientSummaryVO.publications as publication>
	<a href="${publication.getPubmedID_Name()}" target="_new">${publication.getTitle()}</a><br/>
</#list>
<#elseif patientSummaryVO.submitter??>
Unpublished<br/>
${patientSummaryVO.submitter.getDepartment()}, ${patientSummaryVO.submitter.getInstitute()}, ${patientSummaryVO.submitter.getCity()}, ${patientSummaryVO.submitter.getCountry()}
</#if>
</td>
</tr>
<tr class="${clazz}">
<td>Second Mutation</td>
<#if patientSummaryVO.mutation2??>
<td><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${patientSummaryVO.mutation2.getIdentifier()}#results">${patientSummaryVO.mutation2.getCdna_Notation()}</a></td>
<td>${patientSummaryVO.mutation2.getAa_Notation()}</td>
<td>${patientSummaryVO.mutation2.getExon_Name()}</td>
<td>${patientSummaryVO.mutation2.getConsequence()}</td>
<#else>
<td colspan="4">${patientSummaryVO.patient.getMutation2remark()}</td>
</#if>
</tr>
</#list>
</table>
</p>

<#if pager.count &gt; pager.limit>
<p align="center">
<a href="molgenis.do?__target=${screen.name}&__action=patientsFirstPage${pager.offset}#results"><img class="navigation_button" src="generated-res/img/first.png" title="go to first record"/></a>
<a href="molgenis.do?__target=${screen.name}&__action=patientsPrevPage${pager.offset}#results"><img class="navigation_button" src="generated-res/img/prev.png" title="go to previous record"/></a>
<label>${pager.label}</label>
<a href="molgenis.do?__target=${screen.name}&__action=patientsNextPage${pager.offset}#results"><img class="navigation_button" src="generated-res/img/next.png" title="go to next record"/></a>
<a href="molgenis.do?__target=${screen.name}&__action=patientsLastPage${pager.offset}#results"><img class="navigation_button" src="generated-res/img/last.png" title="go to last record"/></a>
</p>
</#if>

<p>
[<a href="#">Back to top</a>]
</p>