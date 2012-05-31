<#include "header.ftl">

<#assign exonDTO = vo.exonDTO>

<table cellpadding="2" cellspacing="2">
<tr>
	<th><a href="molgenis.do?__target=${screen.name}&__action=showFirstExon#results"><img src="generated-res/img/first.png"/></a></th>
	<th><a href="molgenis.do?__target=${screen.name}&__action=showPrevExon&exon_id=${exonDTO.id}#results"><img src="generated-res/img/prev.png"/></a></th>
	<th>${exonDTO.name}</th>
	<th><a href="molgenis.do?__target=${screen.name}&__action=showNextExon&exon_id=${exonDTO.id}#results"><img src="generated-res/img/next.png"/></a></th>
	<th><a href="molgenis.do?__target=${screen.name}&__action=showLastExon#results"><img src="generated-res/img/last.png"/></a></th>
</tr>
</table>
</p>

<p>
<img id="exonimg${exonDTO.id?c}" src="res/img/open.png" onclick="toggleDiv('exon${exonDTO.id?c}', 'exonimg${exonDTO.id?c}');"> Details
<div id="exon${exonDTO.id?c}" style="display:none;">
<table class="listtable" cellpadding="4">
<tr class="form_listrow1"><th>Number of nucleotides</th><td>${exonDTO.length?c}</td></tr>
<#if !exonDTO.isIntron>
<tr class="form_listrow0"><th>Number of amino acids (fully / partly)</th><td>${exonDTO.numFullAminoAcids?c} / ${exonDTO.numPartAminoAcids?c}</td></tr>
<tr class="form_listrow1"><th>Number of Gly-X-Y repeats</th><td>${exonDTO.numGlyXYRepeats?c}</td></tr>
</#if>
<tr class="form_listrow0"><th>Protein domains</th><td><#list exonDTO.domainName as proteinDomainName>${proteinDomainName}<br/></#list></td></tr>
<tr class="form_listrow1"><th>Alternatively spliced?</th><td></td></tr>
<tr class="form_listrow0"><th>Multiple of 3 nucleotides?</th><td>${exonDTO.multiple3Nucl?string("yes", "no")}</td></tr>
</table>
</div>

${model.mbrowse.createSequencePanel()}

${vo.rawOutput}

<#-- <#include "displayOptions.ftl"> -->

<#include "footer.ftl">