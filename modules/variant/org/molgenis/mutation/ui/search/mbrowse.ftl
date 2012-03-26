<#assign mbrowse = model.mBrowseVO>

<#if vo.action?starts_with("showProteinDomain")>

<#assign proteinDomainSummaryVO = vo.proteinDomainSummaryVO>
<#assign exonDTOList            = proteinDomainSummaryVO.exonDTOList>
<#if exonDTOList?size &gt; 0>
<h4>Browse the ${geneDTO.name} gene: ${proteinDomainSummaryVO.domainName}</h4>
${mbrowse.getProteinDomainPanel()}
<br/>
${mbrowse.getExonIntronPanel()}
<br/>
</#if>

<#elseif vo.action == "showExon" || vo.action == "showPrevExon" || vo.action == "showNextExon">

${mbrowse.getSequencePanel()}

</#if>