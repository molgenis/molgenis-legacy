<#assign mbrowse = screen.MBrowseVO>

<#if vo.action?starts_with("showProteinDomain")>

<#assign proteinDomainSummaryVO = vo.proteinDomainSummaryVO>
<#assign exons                  = proteinDomainSummaryVO.exons>
<#if exons?size &gt; 0>
<h4>Browse the ${exons?first.getGene_Name()} gene: ${proteinDomainSummaryVO.proteinDomain.getName()}</h4>
<#if proteinDomainSummaryVO.proteinDomain.getName() == 'Triple helix domain'>
	<#assign colour = "#6dcbfe">
<#else>
	<#assign colour = "#d95a14">
</#if>
<div style="overflow: hidden; margin-right: 10px; padding-top: 10px; padding-bottom: 10px;">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td align="center" valign="bottom" colspan="${exons?size}">${proteinDomainSummaryVO.proteinDomain.getName()}</td>
</tr>
<tr>
	<#list exons as exon>
	<td><div style="background-color: ${colour}; display: block; width: ${exon.getLength() / 10}px; height: 26px;"><a style="display: block; height: 100%; width: 100%;" href="molgenis.do?__target=${screen.name}&select=${screen.name}&__action=showProteinDomain&domain_id=${proteinDomainSummaryVO.proteinDomain.getId()?c}&snpbool=1#exon${exon.getId()?c}" alt="${exon.getName()}" title="${exon.getName()}"></a></div></td>
	</#list>
</tr>
</table>
</div>
<br/>

<div class="scrollable">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
<#assign allExons = mbrowse.exonList>
<#list allExons as exon>
	<td id="exon${exon.getId()}" width="${exon.getLength()?c}px" align="center">${exon.getName()}</td>
</#list>
</tr>
<tr>
<#assign prevDomain = 0>
<#assign colour = "#d95a14">
<#list allExons as exon>
<#if exon.getIsIntron()>
  <#if queryParametersVO.showIntrons>
	<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr><td><a href="molgenis.do?__target=${screen.name}&select=${screen.name}&__action=showExon&exon_id=${exon.getId()}#results" alt="[]" title="Go to ${exon.getName()}"><img src="res/img/col7a1/intron.png" width="${exon.getLength()?c}px" height="30px"/></a>
		</td></tr>
		</table>
	</td>
  </#if>
<#else>
	<#if exon.getProteinDomain_Id()?? && prevDomain != exon.getProteinDomain_Id()?first>
		<#if colour == "#d95a14">
			<#assign colour = "#6dcbfe">
		<#else>
			<#assign colour = "#d95a14">
		</#if>
	</#if>
	<td>
		<div style="background-color: ${colour}; display: block; width: ${exon.getLength()?c}px; height: 26px; border-color:#000000; border-width:2px; border-style:solid;"><a style="display: block; height: 100%; width: 100%;" href="molgenis.do?__target=${screen.name}&select=${screen.name}&__action=showExon&exon_id=${exon.getId()}#results" alt="[]" title="Go to ${exon.getName()}"></a></div>
	</td>
</#if>
<#assign prevDomain = exon.getProteinDomain_Id()?first>
</#list>
</tr>
<tr>
<#list allExons as exon>
	<td width="${exon.getLength()?c}px" align="left"><#if !exon.getIsIntron()><span style="font-size:6pt;">${exon.getCdna_Position()?c}</span></#if></td>
</#list>
</tr>
</table>
</div>

<br/>
</#if>

<#elseif vo.action?starts_with("showExon")>

<#assign searchPluginUtils  = screen.searchPluginUtils>

<div class="scrollable">
<pre>
<#-- for Orientation == "R" -->
${exonSummaryVO.nuclSequenceFlankLeft}<span class="seq"><#list exonSummaryVO.exon.getGdna_Position()..exonSummaryVO.exon.getGdna_Position() - exonSummaryVO.exon.getLength() + 1 as i><#assign hasMutation = 0><#list mutationSummaryVOs as mutationSummaryVO><#assign tooltip = mutationSummaryVO.getNiceNotation()><#if mutationSummaryVO.mutation.getGdna_Position() == i><#assign hasMutation = 1><span class="mut"><a class="mut" href="molgenis.do?__target=${screen.name}&select=${screen.name}&__action=showMutation&mid=${mutationSummaryVO.mutation.getIdentifier()}#results" alt="${tooltip}" title="${tooltip}"><#break></#if></#list>${exonSummaryVO.nuclSequence?substring(exonSummaryVO.exon.getGdna_Position() - i, exonSummaryVO.exon.getGdna_Position() - i + 1)}<#if hasMutation == 1></a></span></#if></#list></span>${exonSummaryVO.nuclSequenceFlankRight}
<#-- for Orientation == "F" -->
<#--
${exonSummaryVO.nuclSequenceFlankLeft}<span class="seq"><#list 0..exonSummaryVO.exon.getLength() - 1 as i><#assign hasMutation = 0><#list mutationSummaryVOs as mutationSummaryVO><#assign tooltip = mutationSummaryVO.getNiceNotation()><#if mutationSummaryVO.mutation.getGdna_Position() == i><#assign hasMutation = 1><span class="mut"><a class="mut" href="molgenis.do?__target=${screen.name}&select=${screen.name}&__action=showMutation&mid=${mutationSummaryVO.mutation.getIdentifier()}#results" alt="${tooltip}" title="${tooltip}"><#break></#if></#list>${exonSummaryVO.nuclSequence?substring(i, i + 1)}<#if hasMutation == 1></a></span></#if></#list></span>${exonSummaryVO.nuclSequenceFlankRight}
-->
<#if !exonSummaryVO.exon.getIsIntron()><#list 1..exonSummaryVO.nuclSequenceFlankLeft?length as i> </#list><span class="seq">${exonSummaryVO.aaSequence}</span></#if>
<#if !exonSummaryVO.exon.getIsIntron()><#list 1..exonSummaryVO.nuclSequenceFlankLeft?length as i> </#list><span>${searchPluginUtils.printBaseNumbers(exonSummaryVO.exon)}</span></#if>
</pre>
</div>

</#if>