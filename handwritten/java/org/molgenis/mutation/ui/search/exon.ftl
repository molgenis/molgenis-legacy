<#assign exonSummaryVO      = vo.exonSummaryVO>
<#assign mutationSummaryVOs = vo.mutationSummaryVOs>
<#assign searchPluginUtils  = screen.searchPluginUtils>
<table cellpadding="2" cellspacing="2">
<tr>
	<#if exonSummaryVO.firstExon.getId() != exonSummaryVO.exon.getId()><th><a href="molgenis.do?__target=${screen.name}&__action=showExon&exon_id=${exonSummaryVO.firstExon.getId()?c}#results"><img src="generated-res/img/first.png"/></a></th></#if>
	<#if exonSummaryVO.prevExon??><th><a href="molgenis.do?__target=${screen.name}&__action=showExon&exon_id=${exonSummaryVO.prevExon.getId()?c}#results"><img src="generated-res/img/prev.png"/></a></th></#if>
	<th>${exonSummaryVO.exon.getName()}</th>
	<#if exonSummaryVO.nextExon??><th><a href="molgenis.do?__target=${screen.name}&__action=showExon&exon_id=${exonSummaryVO.nextExon.getId()?c}#results"><img src="generated-res/img/next.png"/></a></th></#if>
	<#if exonSummaryVO.lastExon.getId() != exonSummaryVO.exon.getId()><th><a href="molgenis.do?__target=${screen.name}&__action=showExon&exon_id=${exonSummaryVO.lastExon.getId()?c}#results"><img src="generated-res/img/last.png"/></th></#if>
</tr>
</table>
</p>

<p>
<img id="exonimg${exonSummaryVO.exon.getId()?c}" src="res/img/open.png" onclick="toggleDiv('exon${exonSummaryVO.exon.getId()?c}', 'exonimg${exonSummaryVO.exon.getId()?c}');"> Details
<div id="exon${exonSummaryVO.exon.getId()?c}" style="display:none;">
<table class="listtable" cellpadding="4">
<tr class="form_listrow1"><th>Number of nucleotides</th><td>${exonSummaryVO.exon.getLength()}</td></tr>
<#if !exonSummaryVO.exon.getIsIntron()><tr class="form_listrow0"><th>Number of amino acids (fully / partly)</th><td>${exonSummaryVO.numFullAminoAcids} / ${exonSummaryVO.numPartAminoAcids}</td></tr></#if>
<#if !exonSummaryVO.exon.getIsIntron()><tr class="form_listrow1"><th>Number of Gly-X-Y repeats</th><td>${exonSummaryVO.numGlyXYRepeats}</td></tr></#if>
<tr class="form_listrow0"><th>Protein domains</th><td><#list exonSummaryVO.exon.getProteinDomain_Name() as proteinDomainName>${proteinDomainName}<br/></#list></td></tr>
<tr class="form_listrow1"><th>Alternatively spliced?</th><td></td></tr>
<tr class="form_listrow0"><th>Multiple of 3 nucleotides?</th><td>${exonSummaryVO.multiple3Nucl?string("yes", "no")}</td></tr>
</table>
</div>

<div class="scrollable">
<table cellspacing="0" cellpadding="2">
<tr>
<td>
<pre>
${exonSummaryVO.nuclSequenceFlankLeft}<span class="seq"><#list exonSummaryVO.exon.getGdna_Position()..exonSummaryVO.exon.getGdna_Position() - exonSummaryVO.exon.getLength() + 1 as i><#assign hasMutation = 0><#list mutationSummaryVOs as mutationSummaryVO><#assign tooltip = mutationSummaryVO.mutation.getCdna_Notation()?xml + " (" + mutationSummaryVO.mutation.getAa_Notation()?xml + ")"><#if mutationSummaryVO.mutation.getGdna_Position() == i><#assign hasMutation = 1><span class="mut"><a class="mut" href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${mutationSummaryVO.mutation.getIdentifier()}#results" alt="${tooltip}" title="${tooltip}"><#break></#if></#list>${exonSummaryVO.nuclSequence?substring(exonSummaryVO.exon.getGdna_Position() - i, exonSummaryVO.exon.getGdna_Position() - i + 1)}<#if hasMutation == 1></a></span></#if></#list></span>${exonSummaryVO.nuclSequenceFlankRight}
<#if !exonSummaryVO.exon.getIsIntron()><#list 1..exonSummaryVO.nuclSequenceFlankLeft?length as i> </#list><span class="seq">${exonSummaryVO.aaSequence}</span></#if>
<#if !exonSummaryVO.exon.getIsIntron()><#list 1..exonSummaryVO.nuclSequenceFlankLeft?length as i> </#list><span>${searchPluginUtils.printBaseNumbers(exonSummaryVO.exon)}</span></#if>
<#--
<#if !exonSummaryVO.exon.isIntron>
<#if queryParametersVO.showMutations>
<#list mutationSummaryVOs as mutationSummaryVO>
<#if exonSummaryVO.exon.id == mutationSummaryVO.mutation.exon>
<#list 1..exonSummaryVO.nuclSequenceFlankLeft?length as i> </#list>${searchPluginUtils.printMutationMark(exonSummaryVO.exon, mutationSummaryVO.mutation)}<a href="molgenis.do?__target=${screen.name}&__action=showMutation&mid=${mutationSummaryVO.mutation.identifier}&snpbool=1">${mutationSummaryVO.mutation.cdna_notation} <#if mutationSummaryVO.mutation.aa_notation?length &gt; 0>(${mutationSummaryVO.mutation.aa_notation})</#if></a>
</#if>
</#list>
</#if>
</#if>
-->
</pre>
</td>
</tr>
</table>
</div>

<#if queryParametersVO.showMutations && mutationSummaryVOs?size &gt; 0>
	<#include "mutations.ftl">
</#if>