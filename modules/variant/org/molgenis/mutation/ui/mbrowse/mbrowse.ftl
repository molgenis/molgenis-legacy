<#if model.action?starts_with("showProteinDomain")>

<#assign proteinDomainSummaryVO = model.proteinDomainSummaryVO>
<#assign exons                  = proteinDomainSummaryVO.exons>
<#if exons?size &gt; 0>
<h4>Browse the ${exons?first.getGene_Name()} gene: ${proteinDomainSummaryVO.proteinDomain.getName()}</h4>
${model.getProteinDomainPanel()}

<br/>

${model.getExonIntronPanel()}

<br/>
</#if>

<#elseif model.action?starts_with("showExon")>

${mbrowse.getSequencePanel()}

</#if>