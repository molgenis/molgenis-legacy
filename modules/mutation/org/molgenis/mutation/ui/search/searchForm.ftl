<#-- table for search options to the left, news to the right -->
<table width="100%">
<tr>
<td valign="top">

<#if !queryParametersVO.expertSearch>
<#assign form      = vo.simpleSearchForm>
<#assign expert    = vo.toExpertSearchForm>
<#assign mutations = vo.listAllMutationsForm>
<#assign patients  = vo.listAllPatientsForm>

${model.textWelcome}

${model.textSearch}
<br/>
<table cellpadding="4" cellspacing="4">
<tr>
	<form action="molgenis.do#results" method="post">
	${form.__target}
	${form.__action}
	<td>Enter search term:</td>
	<td>${form.term}&nbsp;<a href="molgenis.do?__target=${screen.name}&select=${screen.name}&__action=init&expertSearch=1">Advanced Search</a></td>
</tr>
<tr>
	<td></td>
	<td>${form.result}</td>
</tr>
<tr>
	<td></td>
	<td>
		${form.findMutationsByTerm}
		</form>
		<form action="molgenis.do#results" method="post">${mutations.__target}${mutations.__action}${mutations.listAllMutations}</form>
		<form action="molgenis.do#results" method="post">${patients.__target}${patients.__action}${patients.listAllPatients}</form>
	</td>
</tr>
</table>
<#else>
<#assign form = vo.expertSearchForm>
<#assign back = vo.toSimpleSearchForm>
<#assign muta = vo.showMutationForm>
<!--<form method="post">${back.__target}${back.__action}${back.expertSearch}${back.submit}</form>-->
<h3>Advanced search page</h3>
<p>
Perform a more advanced search on the ${vo.geneName} mutation database. If you enter or select two or more search terms simultaneously, the search engine will perform a combined search action (i.e. AND).
</p>
<table cellpadding="4" cellspacing="4">
<form action="molgenis.do#results" method="post" name="AdvSearch">
${form.__target}
${form.select}
${form.__action}
${form.expertSearch}
<tr><td>Variation: </td><td>${form.variation}</td><td>Nucleotide No: </td><td>${form.nuclno}</td></tr>
<tr><td>Amino Acid No: </td><td>${form.aano}</td><td>Exon/Intron: </td><td>${form.exon_id}</td></tr>
<tr><td>Mutation type:</td><td>${form.type}</td><td>Consequence:</td><td>${form.consequence}</td></tr>
<tr><td>Protein domain:</td><td>${form.domain_id}</td><td>Phenotype:</td><td>${form.phenotype_id}</td></tr>
<tr><td>Inheritance:</td><td>${form.inheritance}</td></tr>
<td colspan="4" align="center">${form.findMutations}&nbsp;<input type="reset" name="reset" value="Reset">&nbsp;<input type="button" name="clear" value="Clear" onclick="clearForm(document.forms.AdvSearch);"></td></tr>
</form>
<form action="molgenis.do#results" method="post">
${muta.__target}
${muta.select}
${muta.__action}
${muta.expertSearch}
<tr><td colspan="2"></td><td align="right">or</td><td>${muta.mid}</td><td colspan="2"></td></tr>
</form>
</table>
[<a href="molgenis.do?__target=${screen.name}&select=${screen.name}&__action=init&expertSearch=0">Back to simple search</a>]
</#if>
<#--
	
	
-->
<br/><br/>
<h3>Browse the ${vo.geneName} gene</h3>
<p>
Click anywhere on this schematic representation of the ${vo.geneName} gene to graphically browse the gene. With every click you will zoom in deeper on the ${vo.geneName} gene. Mutated nucleotides are depicted in red. If the cursor is placed over the mutated nucleotide(s), the corresponding mutation is shown.
</p>
<br/>
<p>
${model.mBrowseVO.getGenePanel()}
<#--
<div class="scrollable">
<table cellpadding="0" cellspacing="0" width="1%">
<tr>
<#assign gb = screen.MBrowseVO>
<#list gb.proteinDomainList as proteinDomainSummaryVO>
	<#assign exons = proteinDomainSummaryVO.exons>
	<#if exons?size &gt; 0>
	<td align="center" valign="bottom" colspan="${exons?size}" width="1%">${proteinDomainSummaryVO.proteinDomain.getName()} (exon ${exons?first.getNumber()} - ${exons?last.getNumber()})</td>
	</#if>
</#list>
</tr>
<tr>
<#assign numDomains = 0>
<#list gb.proteinDomainList as proteinDomainSummaryVO>
	<#if numDomains % 2 == 0>
		<#assign colour = "#d95a14">
	<#else>
		<#assign colour = "#6dcbfe">
	</#if>		
	<#assign exons = proteinDomainSummaryVO.exons>
	<#list exons as exon>
	<td align="left"><div style="background-color: ${colour}; display: block; width: ${exon.getLength() / 10}px; height: 26px;"><a style="display: block; height: 100%; width: 100%;" href="molgenis.do?__target=${screen.name}&select=${screen.name}&__action=showProteinDomain&domain_id=${proteinDomainSummaryVO.proteinDomain.getId()?c}&snpbool=1#exon${exon.getId()?c}" alt="${exon.getName()}" title="${exon.getName()}"></a></div></td>
	</#list>
<#assign numDomains = numDomains + 1>
</#list>
</tr>
</table>
</div>
-->
</p>
<br/>
${model.textRemarks}
<br/>
${model.textCollaborations}
<br/><br/>

</td>
<td class="news_box">
${model.controller.getChild("NewsBar").render()}
<#--<@layout screen.getChild("NewsBar")/>-->
</td>
</tr>
</table>