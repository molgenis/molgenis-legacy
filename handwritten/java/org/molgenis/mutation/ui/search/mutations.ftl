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
<td><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${mutationSummaryVO.mutation.getIdentifier()}#results">${mutationSummaryVO.mutation.getIdentifier()}</a></td>
<td>${mutationSummaryVO.mutation.getCdna_Notation()}</td>
<td>${mutationSummaryVO.mutation.getAa_Notation()}</td>
<td><a href="molgenis.do?__target=${screen.name}&__action=showExon&exon_id=${mutationSummaryVO.mutation.getExon_Id()}#results">${mutationSummaryVO.mutation.getExon_Name()}</a></td>
<td>${mutationSummaryVO.mutation.getConsequence()}</td>
<td>${mutationSummaryVO.mutation.getInheritance()}</td>
<td></td>
<td></td>
</tr>

<tr class="tableheader">
</tr>
<#list mutationSummaryVO.patients as patientSummaryVO>
<#assign secondMutation = "empty">
<#if patientSummaryVO.mutation1.getId() == mutationSummaryVO.mutation.getId()>
	<#if patientSummaryVO.mutation2??>
		<#assign secondMutation = patientSummaryVO.mutation2>
	</#if>
<#elseif patientSummaryVO.mutation2.getId() == mutationSummaryVO.mutation.getId()>
	<#if patientSummaryVO.mutation1??>
		<#assign secondMutation = patientSummaryVO.mutation1>
	</#if>
</#if>
<tr class="form_listrow1">
<#if secondMutation != "empty">
<td>+ <a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${secondMutation.getIdentifier()}">${secondMutation.getIdentifier()}</a></td>
<td>${secondMutation.getCdna_Notation()}</td>
<td>${secondMutation.getAa_Notation()}</td>
<td><a href="molgenis.do?__target=${screen.name}&__action=showExon&exon_id=${secondMutation.getExon_Id()}&snpbool=1">${secondMutation.getExon_Name()}</a></td>
<td>${secondMutation.getConsequence()}</td>
<td>${secondMutation.getInheritance()}</td>
<#else>
<td>+</td>
<td>${patientSummaryVO.patient.getMutation2remark()}</td>
<td></td>
<td></td>
<td></td>
<td></td>
</#if>
<td><a href="molgenis.do?__target=${screen.name}&__action=showPatient&pid=${patientSummaryVO.patient.getIdentifier()}">${patientSummaryVO.patient.getIdentifier()}</a></td>
<td>${patientSummaryVO.phenotype.getMajortype()}, ${patientSummaryVO.phenotype.getSubtype()}</td>
</tr>
<tr class="form_listrow1">
<td colspan="5"></td>
<td colspan="3">
<#if patientSummaryVO.publications?? && patientSummaryVO.publications?size &gt; 0>
<#list patientSummaryVO.publications as publication>
<a href="${patientSummaryVO.pubmedURL}${publication.getPubmedID_Name()}" target="_new">${publication.getTitle()}</a></br>
</#list>
<#if patientSummaryVO.submitter?? && !patientSummaryVO.submitter.getSuperuser()>
First submitted as unpublished case by
${patientSummaryVO.submitter.getDepartment()}, ${patientSummaryVO.submitter.getInstitute()}, ${patientSummaryVO.submitter.getCity()}, ${patientSummaryVO.submitter.getCountry()}
</#if>
<#elseif patientSummaryVO.submitter??>
Unpublished<br/>
${patientSummaryVO.submitter.getDepartment()}, ${patientSummaryVO.submitter.getInstitute()}, ${patientSummaryVO.submitter.getCity()}, ${patientSummaryVO.submitter.getCountry()}
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