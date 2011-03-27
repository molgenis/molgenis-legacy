<#--
<#list screen.children as subscreen>
<div class="subscreen">
subscreen.name==${subscreen.name}
<@layout subscreen/>
</div>
</#list>
-->
<#assign proteinDomainSummaryVO = vo.proteinDomainSummaryVO>
<#assign exons                  = proteinDomainSummaryVO.exons> <#--screen.exonService.findExonsByProteinDomainId(screen.proteinDomain.id, false)-->
<#assign allExons               = proteinDomainSummaryVO.allExons>
<#if exons?size &gt; 0>
<#assign exon = exons?first>
<label>Browse the ${exon.getGene_Name()} gene: ${proteinDomainSummaryVO.proteinDomain.getName()}</label>
<#if proteinDomainSummaryVO.proteinDomain.getName() == 'Triple helix domain'>
	<#assign imgsrc = "protdom0.png">
<#else>
	<#assign imgsrc = "protdom1.png">
</#if>
<div style="overflow-x: auto; margin-right: 10px;">
<table border="0" cellpadding="0" cellspacing="0">
<tr>
	<td align="center" valign="bottom" colspan="${exons?size}">${proteinDomainSummaryVO.proteinDomain.getName()}</td>
</tr>
<tr>
	<#list exons as exon>
	<td class="exonbox"><a href="molgenis.do?__target=${screen.name}&__action=showProteinDomain&domain_id=${proteinDomainSummaryVO.proteinDomain.getId()}&snpbool=1#exon${exon.getId()}"><img src="res/img/col7a1/${imgsrc}" width="${exon.getLength() / 10}" height="26px" alt="${exon.getName()}" title="${exon.getName()}"></a></td>
	</#list>
</tr>
</table>
</div>

<br/>

<div class="scrollable">
<table border="0" cellpadding="0" cellspacing="0">
<#if queryParametersVO.showNames>
<tr>
<#list allExons as exon>
<#if !exon.getIsIntron() || (exon.getIsIntron() && queryParametersVO.showIntrons)>
	<td width="${exon.getLength()}px" align="center"><a name="exon${exon.getId()}"><p style="text-align:center;">${exon.getName()}</p></a></td>
</#if>
</#list>
</tr>
</#if>
<tr>
<#list allExons as exon>
<#if exon.getIsIntron()>
<#if queryParametersVO.showIntrons>
	<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr><td><a href="molgenis.do?__target=${screen.name}&__action=showExon&exon_id=${exon.getId()}"><img src="res/img/col7a1/intron.png" width="${exon.getLength()?c}px" height="30px"/></a>
		</td></tr>
		</table>
	</td>
</#if>
<#else>
	<td>
		<table border="1" cellpadding="0" cellspacing="0" style="border-color:#ff0000; border-width:2px;">
		<tr><td><a href="molgenis.do?__target=${screen.name}&__action=showExon&exon_id=${exon.getId()}"><img src="res/img/col7a1/exon.png" width="${exon.getLength()}px" height="26px"/></a>
		</td></tr>
		</table>
	</td>
</#if>
</#list>
</tr>
<#if queryParametersVO.showNumbering>
<tr>
<#list allExons as exon>
<#if exon.getIsIntron()>
<#if queryParametersVO.showIntrons>
	<td width="${exon.getLength()}px" align="left"></td>
</#if>
<#else>
	<td width="${exon.getLength()}px" align="left"><span style="font-size:6pt;">${exon.getCdna_Position()?c}</span></td>
</#if>
</#list>
</tr>
</#if>
</table>

<br/>

</div>
</#if>