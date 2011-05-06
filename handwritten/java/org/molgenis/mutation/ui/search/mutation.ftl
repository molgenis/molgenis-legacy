<#assign mutationSummaryVO = vo.mutationSummaryVO>
<table cellpadding="2" cellspacing="2">
<tr>
	<#if mutationSummaryVO.firstMutation.getId() != mutationSummaryVO.mutation.getId()><th><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${mutationSummaryVO.firstMutation.getIdentifier()}#results"><img src="generated-res/img/first.png"/></a></th></#if>
	<#if mutationSummaryVO.prevMutation??><th><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${mutationSummaryVO.prevMutation.getIdentifier()}#results"><img src="generated-res/img/prev.png"/></a></th></#if>
	<th>${mutationSummaryVO.mutation.getCdna_Notation()}</th>
	<#if mutationSummaryVO.nextMutation??><th><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${mutationSummaryVO.nextMutation.getIdentifier()}#results"><img src="generated-res/img/next.png"/></a></th></#if>
	<#if mutationSummaryVO.lastMutation.getId() != mutationSummaryVO.mutation.getId()><th><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${mutationSummaryVO.lastMutation.getIdentifier()}#results"><img src="generated-res/img/last.png"/></a></th></#if>
</tr>
</table>

<table class="listtable">
<tr class="form_listrow1"><th>Mutation ID</th><td>${mutationSummaryVO.mutation.getIdentifier()}</td></tr>
<tr class="form_listrow0"><th>cDNA change</th><td>${mutationSummaryVO.mutation.getCdna_Notation()}</td></tr>
<tr class="form_listrow1"><th>Chromosomal change</th><td>${mutationSummaryVO.mutation.getGdna_Notation()}</td></tr>
<tr class="form_listrow0"><th>First affected codon number</th><td><#if mutationSummaryVO.mutation.getAa_Position()??>${mutationSummaryVO.mutation.getAa_Position()?c}</#if></td></tr>
<tr class="form_listrow1"><th>Codon change</th><td>${mutationSummaryVO.codonChange}</td></tr>
<tr class="form_listrow0"><th>Protein change</th><td>${mutationSummaryVO.mutation.getAa_Notation()}</td></tr>
<tr class="form_listrow1"><th>Exon/intron</th><td><a href="molgenis.do?__target=${screen.name}&__action=showExon&exon_id=${mutationSummaryVO.mutation.getExon_Id()}#results">${mutationSummaryVO.mutation.getExon_Name()}</a></td></tr>
<tr class="form_listrow0"><th>Protein domain</th><td>${mutationSummaryVO.proteinDomain.getName()}</td></tr>
<tr class="form_listrow1"><th>Consequence</th><td>${mutationSummaryVO.mutation.getConsequence()}</td></tr>
<tr class="form_listrow0"><th>Type of mutation</th><td>${mutationSummaryVO.mutation.getType()}</td></tr>
<tr class="form_listrow1"><th>Inheritance</th><td>${mutationSummaryVO.mutation.getInheritance()}</td></tr>
<tr class="form_listrow0"><th>Reported as SNP?</th><td><#if mutationSummaryVO.mutation.getReportedSNP()??>${mutationSummaryVO.mutation.getReportedSNP()?string("yes", "no")}</#if></td></tr>
<tr class="form_listrow1"><th>Pathogenicity</th><td>${mutationSummaryVO.mutation.getPathogenicity()}</td></tr>
<tr class="form_listrow0"><th>Found in number of patients</th><td><a href="molgenis.do?__target=${screen.name}&__action=findPatients&mid=${mutationSummaryVO.mutation.getIdentifier()}#results">${mutationSummaryVO.patients?size}</a></td></tr>
<tr class="form_listrow1"><th>Phenotypes associated with mutation</th><td><#list mutationSummaryVO.phenotypes as phenotype>${phenotype.getMajortype()}, ${phenotype.getSubtype()}<br/></#list></td></tr>
<tr class="form_listrow0"><th>References</th><td><#list mutationSummaryVO.publications as publication><a href="${mutationSummaryVO.pubmedURL}${publication.getPubmedID_Name()}" target="_new">${publication.getTitle()}</a><#--<a href="${publication.pdf}" target="_new"><img src="res/img/pdf.gif"></a>--><br/></#list></td></tr>
<#--
<tr class="form_listrow0"><th>Conserved amino acid?</th><td><#if mutationSummaryVO.mutation.conservedAA??>${mutationSummaryVO.mutation.conservedAA?string("yes", "no")}</#if></td></tr>
<tr class="form_listrow1"><th>Predicted effect on splicing?</th><td><#if mutationSummaryVO.mutation.effectOnSplicing??>${mutationSummaryVO.mutation.effectOnSplicing?string("yes", "no")}</#if></td></tr>
-->
<tr class="form_listrow1"><th>Other changes at nucleotide position</th><td><#if mutationSummaryVO.positionMutations??><#list mutationSummaryVO.positionMutations as positionMutation><#if mutationSummaryVO.mutation.getId() != positionMutation.getId()><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${positionMutation.getIdentifier()}#results">${positionMutation.getCdna_Notation()}</a><br/></#if></#list></#if></td></tr>
<#if mutationSummaryVO.codonMutations??><tr class="form_listrow0"><th>Other changes at codon position</th><td><#list mutationSummaryVO.codonMutations as codonMutation><#if mutationSummaryVO.mutation.getId() != codonMutation.getId()><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${codonMutation.getIdentifier()}#results">${codonMutation.getCdna_Notation()}</a><br/></#if></#list></td></tr></#if>
</table>

<p>
[<a href="javascript:back();">Back to results</a>]
</p>
<p>
[<a href="#">Back to top</a>]
</p>