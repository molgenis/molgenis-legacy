<#-- TODO: make this a Servlet/JspView and move PrintWriter to render() methods -->

${rawOutput}

<#--

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

<tr class="tableheader">
<th>No.</th>
<th>Patient ID</th>
<th>Phenotype</th>
<th>Mutation</th>
<th>cDNA change</th>
<th>Protein change</th>
<th>Exon</th>
<th>Consequence</th>
<th>Reference</th>
</tr>

<#assign count = 0>
<#list patientSummaryVOs as patientSummaryVO>

<#assign count = count + 1>
<#if count % 2 == 1>
	<#assign clazz = "form_listrow1">
<#else>
	<#assign clazz = "form_listrow0">
</#if>
<tr class="${clazz}">
<td rowspan="2">${pager.offset + count}</td>
<td rowspan="2"><a href="molgenis.do?__target=${screen.name}&__action=showPatient&pid=${patientSummaryVO.patientIdentifier}#results">${patientSummaryVO.patientIdentifier}</a></td>
<td rowspan="2">${patientSummaryVO.phenotypeMajor}, ${patientSummaryVO.phenotypeSub}</td>
<td>First Mutation</td>
<#assign variantSummaryVO1 = patientSummaryVO.variantSummaryVOList?first>
<td><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${variantSummaryVO1.identifier}#results">${variantSummaryVO1.cdnaNotation}</a></td>
<td>${variantSummaryVO1.aaNotation}</td>
<td>${variantSummaryVO1.exonName}</td>
<td>${variantSummaryVO1.consequence}</td>
<td rowspan="2">
<#if patientSummaryVO.publicationVOList?? && patientSummaryVO.publicationVOList?size &gt; 0>
<#list patientSummaryVO.publicationVOList as publicationVO>
	<a href="${patientSummaryVO.pubmedURL}${publicationVO.pubmed}" target="_new">${publicationVO.title}</a><br/>
</#list>
<#if patientSummaryVO.submitterDepartment??>
First submitted as unpublished case by
${patientSummaryVO.submitterDepartment}, ${patientSummaryVO.submitterInstitute}, ${patientSummaryVO.submitterCity}, ${patientSummaryVO.submitterCountry}
</#if>
<#elseif patientSummaryVO.submitter??>
Unpublished<br/>
${patientSummaryVO.submitterDepartment}, ${patientSummaryVO.submitterInstitute}, ${patientSummaryVO.submitterCity}, ${patientSummaryVO.submitterCountry}
</#if>
</td>
</tr>
<tr class="${clazz}">
<td>Second Mutation</td>
<#if patientSummaryVO.variantSummaryVOList?size &gt; 1>
<#assign variantSummaryVO2 = patientSummaryVO.variantSummaryVOList?last>
<td><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${variantSummaryVO2.identifier}#results">${variantSummaryVO2.cdnaNotation}</a></td>
<td>${variantSummaryVO2.aaNotation}</td>
<td>${variantSummaryVO2.exonName}</td>
<td>${variantSummaryVO2.consequence}</td>
<#else>
<td colspan="4">${patientSummaryVO.variantComment}</td>
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

-->