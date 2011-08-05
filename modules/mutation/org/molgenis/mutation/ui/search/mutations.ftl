<#if vo.geneName == "CHD7">

<#assign rawOutput = vo.rawOutput>
${rawOutput}

<#else>

<#assign pager                  = vo.pager>
<#if pager.count &gt; 0>
	<#assign mutationSummaryVOs = pager.page>
<#else>
	<#assign mutationSummaryVOs = pager.entities>
</#if>

<#if mutationSummaryVOs?size &gt; 0>

<#if pager.count &gt; pager.limit>
<p align="center">
<a href="molgenis.do?__target=${screen.name}&__action=mutationsFirstPage${pager.offset}#results"><img class="navigation_button" src="res/img/first.png" title="go to first record"/></a>
<a href="molgenis.do?__target=${screen.name}&__action=mutationsPrevPage${pager.offset}#results"><img class="navigation_button" src="res/img/prev.png" title="go to previous record"/></a>
<label>${pager.label}</label>
<a href="molgenis.do?__target=${screen.name}&__action=mutationsNextPage${pager.offset}#results"><img class="navigation_button" src="res/img/next.png" title="go to next record"/></a>
<a href="molgenis.do?__target=${screen.name}&__action=mutationsLastPage${pager.offset}#results"><img class="navigation_button" src="res/img/last.png" title="go to last record"/></a>
</p>
</#if>

<p>
<table class="listtable" cellpadding="4" border="1">
<#list mutationSummaryVOs as mutationSummaryVO>
<tr class="tableheader">
<th>Mutation ID</th>
<th>cDNA change</th>
<th>Protein change</th>
<th>Exon/Intron</th>
<th>Consequence</th>
<th>Inheritance</th>
<th>Patient ID</th>
<th>Phenotype</th>
</tr>
<tr class="form_listrow1">
<td><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${mutationSummaryVO.identifier}#results">${mutationSummaryVO.identifier}</a></td>
<td>${mutationSummaryVO.cdnaNotation}</td>
<td>${mutationSummaryVO.aaNotation}</td>
<td><a href="molgenis.do?__target=${screen.name}&__action=showExon&exon_id=${mutationSummaryVO.exonId}#results">${mutationSummaryVO.exonName}</a></td>
<td>${mutationSummaryVO.consequence}</td>
<td>${mutationSummaryVO.inheritance}</td>
<td></td>
<td></td>
</tr>

<#--
<tr class="tableheader">
</tr>
-->
<#list mutationSummaryVO.patientSummaryVOList as patientSummaryVO>

<#if patientSummaryVO.variantSummaryVOList?size != 0>
<#list patientSummaryVO.variantSummaryVOList as variantSummaryVO>
<tr class="form_listrow1">
<td>+ <a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${variantSummaryVO.identifier}">${variantSummaryVO.identifier}</a></td>
<td>${variantSummaryVO.cdnaNotation}</td>
<td>${variantSummaryVO.aaNotation}</td>
<td><a href="molgenis.do?__target=${screen.name}&__action=showExon&exon_id=${variantSummaryVO.exonId}&snpbool=1">${variantSummaryVO.exonName}</a></td>
<td>${variantSummaryVO.consequence}</td>
<td>${variantSummaryVO.inheritance}</td>
<td><a href="molgenis.do?__target=${screen.name}&__action=showPatient&pid=${patientSummaryVO.patientIdentifier}">${patientSummaryVO.patientIdentifier}</a></td>
<td>${patientSummaryVO.phenotypeMajor}<#if patientSummaryVO.phenotypeSub != "">, ${patientSummaryVO.phenotypeSub}</#if></td>
</tr>
</#list>
<#else>
<tr class="form_listrow1">
<td>+</td>
<td><#if patientSummaryVO.variantComment??>${patientSummaryVO.variantComment}<#else>Error</#if></td>
<td></td>
<td></td>
<td></td>
<td></td>
<td><a href="molgenis.do?__target=${screen.name}&__action=showPatient&pid=${patientSummaryVO.patientIdentifier}">${patientSummaryVO.patientIdentifier}</a></td>
<td>${patientSummaryVO.phenotypeMajor}<#if patientSummaryVO.phenotypeSub != "">, ${patientSummaryVO.phenotypeSub}</#if></td>
</tr>
</#if>

<tr class="form_listrow1">
<td colspan="5"></td>
<td colspan="3">
<#if patientSummaryVO.publicationVOList?? && patientSummaryVO.publicationVOList?size &gt; 0>
<#list patientSummaryVO.publicationVOList as publicationVO>
<a href="${mutationSummaryVO.pubmedURL}${publicationVO.pubmed}" target="_new">${publicationVO.title}</a></br>
</#list>
<#if patientSummaryVO.submitterDepartment??>
First submitted as unpublished case by
${patientSummaryVO.submitterDepartment}, ${patientSummaryVO.submitterInstitute}, ${patientSummaryVO.submitterCity}, ${patientSummaryVO.submitterCountry}
</#if>
<#elseif patientSummaryVO.submitterDepartment??>
Unpublished<br/>
${patientSummaryVO.submitterDepartment}, ${patientSummaryVO.submitterInstitute}, ${patientSummaryVO.submitterCity}, ${patientSummaryVO.submitterCountry}
</#if>
</td>
</tr>
</#list>
</#list>
</table>
</p>

<#if pager.count &gt; pager.limit>
<p align="center">
<a href="molgenis.do?__target=${screen.name}&__action=mutationsFirstPage${pager.offset}#results"><img class="navigation_button" src="generated-res/img/first.png" title="go to first record"/></a>
<a href="molgenis.do?__target=${screen.name}&__action=mutationsPrevPage${pager.offset}#results"><img class="navigation_button" src="generated-res/img/prev.png" title="go to previous record"/></a>
<label>${pager.label}</label>
<a href="molgenis.do?__target=${screen.name}&__action=mutationsNextPage${pager.offset}#results"><img class="navigation_button" src="generated-res/img/next.png" title="go to next record"/></a>
<a href="molgenis.do?__target=${screen.name}&__action=mutationsLastPage${pager.offset}#results"><img class="navigation_button" src="generated-res/img/last.png" title="go to last record"/></a>
</p>
</#if>

<p>
[<a href="#">Back to top</a>]
</p>
</#if>
<#--
<script type="text/javascript" language="javascript">
alert(navigator.appName);
alert(navigator.appVersion);
</script>
-->

</#if>