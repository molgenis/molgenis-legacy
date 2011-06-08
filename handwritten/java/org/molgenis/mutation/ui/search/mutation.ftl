<#assign mutationSummaryVO = vo.mutationSummaryVO>
<table cellpadding="2" cellspacing="2">
<tr>
	<th><a href="molgenis.do?__target=${screen.name}&__action=showFirstMutation#results"><img src="generated-res/img/first.png"/></a></th>
	<th><a href="molgenis.do?__target=${screen.name}&__action=showPrevMutation&mid=${mutationSummaryVO.identifier}#results"><img src="generated-res/img/prev.png"/></a></th>
	<th>${mutationSummaryVO.cdnaNotation}</th>
	<th><a href="molgenis.do?__target=${screen.name}&__action=showNextMutation&mid=${mutationSummaryVO.identifier}#results"><img src="generated-res/img/next.png"/></a></th>
	<th><a href="molgenis.do?__target=${screen.name}&__action=showLastMutation#results"><img src="generated-res/img/last.png"/></a></th>
</tr>
</table>

<table class="listtable">
<tr class="form_listrow1"><th>Mutation ID</th><td>${mutationSummaryVO.identifier}</td></tr>
<tr class="form_listrow0"><th>cDNA change</th><td>${mutationSummaryVO.cdnaNotation}</td></tr>
<tr class="form_listrow1"><th>Chromosomal change</th><td>${mutationSummaryVO.gdnaNotation}</td></tr>
<tr class="form_listrow0"><th>First affected codon number</th><td><#if mutationSummaryVO.aaPosition??>${mutationSummaryVO.aaPosition?c}</#if></td></tr>
<tr class="form_listrow1"><th>Codon change</th><td>${mutationSummaryVO.codonChange}</td></tr>
<tr class="form_listrow0"><th>Protein change</th><td>${mutationSummaryVO.aaNotation}</td></tr>
<tr class="form_listrow1"><th>Exon/intron</th><td><a href="molgenis.do?__target=${screen.name}&__action=showExon&exon_id=${mutationSummaryVO.exonId}#results">${mutationSummaryVO.exonName}</a></td></tr>
<tr class="form_listrow0"><th>Protein domain</th><td><#list mutationSummaryVO.proteinDomainNameList as domainName>${domainName}</#list></td></tr>
<tr class="form_listrow1"><th>Consequence</th><td>${mutationSummaryVO.consequence}</td></tr>
<tr class="form_listrow0"><th>Type of mutation</th><td>${mutationSummaryVO.type}</td></tr>
<tr class="form_listrow1"><th>Inheritance</th><td>${mutationSummaryVO.inheritance}</td></tr>
<tr class="form_listrow0"><th>Reported as SNP?</th><td><#if mutationSummaryVO.reportedSNP??>${mutationSummaryVO.reportedSNP?string("yes", "no")}</#if></td></tr>
<tr class="form_listrow1"><th>Pathogenicity</th><td>${mutationSummaryVO.pathogenicity}</td></tr>
<tr class="form_listrow0"><th>Found in number of patients</th><td><a href="molgenis.do?__target=${screen.name}&__action=findPatients&mid=${mutationSummaryVO.identifier}#results">${mutationSummaryVO.patientSummaryVOList?size}</a></td></tr>
<tr class="form_listrow1"><th>Phenotypes associated with mutation</th><td><#list mutationSummaryVO.phenotypeNameList as phenotypeName>${phenotypeName}<br/></#list></td></tr>
<tr class="form_listrow0"><th>References</th><td><#list mutationSummaryVO.publicationVOList as publicationVO><a href="${mutationSummaryVO.pubmedURL}${publicationVO.pubmed}" target="_new">${publicationVO.title}</a><#--<a href="${publication.pdf}" target="_new"><img src="res/img/pdf.gif"></a>--><br/></#list></td></tr>
<#--
<tr class="form_listrow0"><th>Conserved amino acid?</th><td><#if mutationSummaryVO.mutation.conservedAA??>${mutationSummaryVO.mutation.conservedAA?string("yes", "no")}</#if></td></tr>
<tr class="form_listrow1"><th>Predicted effect on splicing?</th><td><#if mutationSummaryVO.mutation.effectOnSplicing??>${mutationSummaryVO.mutation.effectOnSplicing?string("yes", "no")}</#if></td></tr>
-->
<tr class="form_listrow1"><th>Other changes at nucleotide position</th><td><#if mutationSummaryVO.positionMutations??><#list mutationSummaryVO.positionMutations as positionMutationVO><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${positionMutationVO.identifier}#results">${positionMutationVO.cdnaNotation}</a><br/></#list></#if></td></tr>
<#if mutationSummaryVO.codonMutations??><tr class="form_listrow0"><th>Other changes at codon position</th><td><#list mutationSummaryVO.codonMutations as codonMutationVO><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${codonMutationVO.identifier}#results">${codonMutationVO.cdnaNotation}</a><br/></#list></td></tr></#if>
</table>

<p>
[<a href="javascript:back();">Back to results</a>]
</p>
<p>
[<a href="#">Back to top</a>]
</p>