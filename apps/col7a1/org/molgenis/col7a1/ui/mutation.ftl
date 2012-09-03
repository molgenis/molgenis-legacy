<#include "/org/molgenis/mutation/ui/search/header.ftl">

<#assign mutationSummaryDTO = model.mutationSummaryVO>
<table cellpadding="2" cellspacing="2">
<tr>
	<th><a href="molgenis.do?__target=${screen.name}&__action=showFirstMutation#results"><img src="generated-res/img/first.png"/></a></th>
	<th><a href="molgenis.do?__target=${screen.name}&__action=showPrevMutation&mid=${mutationSummaryDTO.identifier}#results"><img src="generated-res/img/prev.png"/></a></th>
	<th>${mutationSummaryDTO.cdnaNotation}</th>
	<th><a href="molgenis.do?__target=${screen.name}&__action=showNextMutation&mid=${mutationSummaryDTO.identifier}#results"><img src="generated-res/img/next.png"/></a></th>
	<th><a href="molgenis.do?__target=${screen.name}&__action=showLastMutation#results"><img src="generated-res/img/last.png"/></a></th>
</tr>
</table>

<table class="listtable">
<tr class="form_listrow1"><th width="50%">Mutation ID</th><td>${mutationSummaryDTO.identifier}</td></tr>
<tr class="form_listrow0"><th width="50%">cDNA change</th><td>${mutationSummaryDTO.cdnaNotation}</td></tr>
<tr class="form_listrow1"><th width="50%">mRNA change</th><td>${mutationSummaryDTO.mrnaNotation}</td></tr>
<tr class="form_listrow0"><th width="50%">Chromosomal change</th><td>${mutationSummaryDTO.gdnaNotation}</td></tr>
<#if mutationSummaryDTO.codonChange != "">
<tr class="form_listrow1"><th width="50%">First affected codon number</th><td>${mutationSummaryDTO.aaStart?c}</td></tr>
<tr class="form_listrow0"><th width="50%">Codon change</th><td>${mutationSummaryDTO.codonChange}</td></tr>
</#if>
<tr class="form_listrow1"><th width="50%">Protein change</th><td>${mutationSummaryDTO.aaNotation}</td></tr>
<tr class="form_listrow0"><th width="50%">Exon/intron</th><td><a href="molgenis.do?__target=${screen.name}&__action=showExon&exon_id=${mutationSummaryDTO.exonId}#results">${mutationSummaryDTO.exonName}</a></td></tr>
<tr class="form_listrow1"><th width="50%">Protein domain</th><td><#list mutationSummaryDTO.proteinDomainNameList as domainName>${domainName}</#list></td></tr>

<#-- Observable features -->
<#list mutationSummaryDTO.protocolDTOList as protocolDTO>
<#assign even = 1>
<#assign observedValueDTOList = mutationSummaryDTO.observedValueDTOHash["Protocol" + protocolDTO.protocolId]>
<#list observedValueDTOList as observedValueDTO>
<#if even == 1>
  <#assign class = "form_listrow0">
  <#assign even = 0>
<#else>
  <#assign class = "form_listrow1">
  <#assign even = 1>
</#if>
<tr class="${class}"><th width="50%">${observedValueDTO.featureDTO.featureName}</th><td>${observedValueDTO.value}</td></tr>
</#list>
</#list>

<#--
<tr class="form_listrow0"><th width="50%">Consequence</th><td>${mutationSummaryDTO.consequence}</td></tr>
<tr class="form_listrow1"><th width="50%">Type of mutation</th><td>${mutationSummaryDTO.type}</td></tr>
<tr class="form_listrow0"><th width="50%">Inheritance</th><td>${mutationSummaryDTO.inheritance}</td></tr>
<tr class="form_listrow1"><th width="50%">Reported as SNP?</th><td>${mutationSummaryDTO.reportedSNP?string("yes", "no")}</td></tr>
<tr class="form_listrow0"><th width="50%">Pathogenicity</th><td>${mutationSummaryDTO.pathogenicity}</td></tr>
-->
<tr class="form_listrow1"><th width="50%">Found in number of patients</th><td><a href="molgenis.do?__target=${screen.name}&__action=findPatients&mid=${mutationSummaryDTO.identifier}#results">${mutationSummaryDTO.patientSummaryDTOList?size}</a></td></tr>
<tr class="form_listrow0"><th width="50%">Phenotypes associated with mutation</th><td><#list mutationSummaryDTO.phenotypeNameList as phenotypeName>${phenotypeName}<br/></#list></td></tr>
<tr class="form_listrow1"><th width="50%">References</th><td><#list mutationSummaryDTO.publicationDTOList as publicationDTO><a href="${mutationSummaryDTO.pubmedURL}${publicationDTO.pubmedId}" target="_new">${publicationDTO.title}</a><#--<a href="${publication.pdf}" target="_new"><img src="res/img/pdf.gif"></a>--><br/></#list></td></tr>
<#--
<tr class="form_listrow0"><th width="50%">Conserved amino acid?</th><td><#if mutationSummaryDTO.mutation.conservedAA??>${mutationSummaryDTO.mutation.conservedAA?string("yes", "no")}</#if></td></tr>
<tr class="form_listrow1"><th width="50%">Predicted effect on splicing?</th><td><#if mutationSummaryDTO.mutation.effectOnSplicing??>${mutationSummaryDTO.mutation.effectOnSplicing?string("yes", "no")}</#if></td></tr>
-->
<tr class="form_listrow0"><th width="50%">Other changes at nucleotide position</th><td><#if model.positionMutations??><#list model.positionMutations as positionMutationVO><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${positionMutationVO.identifier}#results">${positionMutationVO.cdnaNotation}</a><br/></#list></#if></td></tr>
<#if mutationSummaryDTO.codonChange != "">
<#if model.codonMutations??><tr class="form_listrow1"><th width="50%">Other changes at codon position</th><td><#list model.codonMutations as codonMutationVO><a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${codonMutationVO.identifier}#results">${codonMutationVO.cdnaNotation}</a><br/></#list></td></tr></#if>
</#if>
</table>


<p>
[<a href="javascript:history.back();" onclick="javascript:history.back();">Back to results</a>]
</p>
<p>
[<a href="#">Back to top</a>]
</p>

<#include "/org/molgenis/mutation/ui/search/footer.ftl">