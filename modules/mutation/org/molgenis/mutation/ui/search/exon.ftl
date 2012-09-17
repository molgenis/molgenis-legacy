<#include "header.ftl">

<#assign exonSummaryVO      = vo.exonSummaryVO>
<#assign mutationSummaryVOs = vo.mutationSummaryVOs>
<table cellpadding="2" cellspacing="2">
<tr>
	<#if exonSummaryVO.firstExon.getId() != exonSummaryVO.exon.getId()><th><a href="molgenis.do?__target=${screen.name}&select=${screen.name}&__action=showExon&exon_id=${exonSummaryVO.firstExon.getId()?c}#results"><img src="generated-res/img/first.png"/></a></th></#if>
	<#if exonSummaryVO.prevExon??><th><a href="molgenis.do?__target=${screen.name}&select=${screen.name}&__action=showExon&exon_id=${exonSummaryVO.prevExon.getId()?c}#results"><img src="generated-res/img/prev.png"/></a></th></#if>
	<th>${exonSummaryVO.exon.getName()}</th>
	<#if exonSummaryVO.nextExon??><th><a href="molgenis.do?__target=${screen.name}&select=${screen.name}&__action=showExon&exon_id=${exonSummaryVO.nextExon.getId()?c}#results"><img src="generated-res/img/next.png"/></a></th></#if>
	<#if exonSummaryVO.lastExon.getId() != exonSummaryVO.exon.getId()><th><a href="molgenis.do?__target=${screen.name}&select=${screen.name}&__action=showExon&exon_id=${exonSummaryVO.lastExon.getId()?c}#results"><img src="generated-res/img/last.png"/></th></#if>
</tr>
</table>
</p>

<p>
<img id="exonimg${exonSummaryVO.exon.getId()?c}" src="res/img/open.png" onclick="toggleDiv('exon${exonSummaryVO.exon.getId()?c}', 'exonimg${exonSummaryVO.exon.getId()?c}');"> Details
<div id="exon${exonSummaryVO.exon.getId()?c}" style="display:none;">
<table class="listtable" cellpadding="4">
<tr class="form_listrow1"><th>Number of nucleotides</th><td>${exonSummaryVO.exon.getLength()}</td></tr>
<#if !exonSummaryVO.exon.getIsIntron()>
<tr class="form_listrow0"><th>Number of amino acids (fully / partly)</th><td>${exonSummaryVO.numFullAminoAcids} / ${exonSummaryVO.numPartAminoAcids}</td></tr>
</#if>
<#if !exonSummaryVO.exon.getIsIntron()>
<tr class="form_listrow1"><th>Number of Gly-X-Y repeats</th><td>${exonSummaryVO.numGlyXYRepeats}</td></tr>
</#if>
<tr class="form_listrow0"><th>Protein domains</th><td><#list exonSummaryVO.exon.getProteinDomain_Name() as proteinDomainName>${proteinDomainName}<br/></#list></td></tr>
<tr class="form_listrow1"><th>Alternatively spliced?</th><td></td></tr>
<tr class="form_listrow0"><th>Multiple of 3 nucleotides?</th><td>${exonSummaryVO.multiple3Nucl?string("yes", "no")}</td></tr>
</table>
</div>

<#include "mbrowse.ftl">

${vo.rawOutput}

<#-- <#include "displayOptions.ftl"> -->

<#include "footer.ftl">