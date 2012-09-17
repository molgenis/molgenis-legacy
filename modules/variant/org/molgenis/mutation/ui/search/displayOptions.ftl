<#assign form = vo.displayOptionsForm>
<#if !vo.action?starts_with("init") && !vo.action?starts_with("showPatient")>
</#if>
<#if vo.action?starts_with("showExon") || vo.action?starts_with("showMutation") || vo.action?starts_with("showProteinDomain")>
<#assign criteria = vo.mutationSearchCriteriaVO>
<form method="get" enctype="multipart/form-data">
${form.__target}
${form.__action}
<#if criteria.proteinDomainId??>${form.domain_id}</#if>
<#if criteria.exonId??>${form.exon_id}</#if>
<#if criteria.identifier??>${form.mid}</#if>
<p>
<table cellpadding="5">
<tr><th colspan="5" align="left">Display options</th></tr>
<tr>
<#if vo.action?starts_with("showMutation") || vo.action?starts_with("findMutations")>
	<td>SNPs (dbSNP)</td>
</#if>
<#if vo.action?starts_with("showProteinDomain") || vo.action?starts_with("showExon")>
	<td>Introns</td>
</#if>
<#if vo.action?starts_with("showProteinDomain")>
	<td>Exon/intron names</td>
	<td>Nucleotide numbering</td>
</#if>
<#if vo.action?starts_with("showExon")>
	<td>Mutations</td>
</#if>
</tr>
<tr>
<#--
<#if vo.action?starts_with("showMutation") || vo.action?starts_with("findMutations")>
	<td>${form.snpbool}</td>
</#if>
-->
<#if vo.action?starts_with("showProteinDomain") || vo.action?starts_with("showExon")>
	<td>${form.showIntrons}</td>
</#if>
<#if vo.action?starts_with("showProteinDomain")>
	<td>${form.showNames}</td>
	<td>${form.showNumbering}</td>
</#if>
<#if vo.action?starts_with("showExon")>
	<td>${form.showMutations}</td>
</#if>
	<td><input type="submit" value="Refresh"></td>
</tr>
</table>
</p>
</form>
</#if>