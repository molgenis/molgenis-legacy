<#-- table for search options to the left, news to the right -->
<table width="100%">
<tr>
<td valign="top">

<#if !queryParametersVO.expertSearch>
<#assign form      = vo.simpleSearchForm>
<#assign expert    = vo.toExpertSearchForm>
<#assign mutations = vo.listAllMutationsForm>
<#assign patients  = vo.listAllPatientsForm>
<h3>
Welcome to the <b>international, open-access database of dystrophic epidermolysis bullosa (DEB) patients and associated COL7A1 mutations</b>.
</h3>
<p>
The International COL7A1 Mutation Database contains anonymised data on both published and unpublished dystrophic epidermolysis bullosa patients, as well as their associated COL7A1 mutations and genotypes, and clinical and molecular phenotypes.
</p>
<p>
The database currently contains ${vo.numPatients} DEB patients, of which ${vo.numUnpublished} unpublished, and ${vo.numMutations} COL7A1 mutations.
</p>
<p>
Search or browse below.
</p>
<br/>
<h3>Search database</h3>
<p>
Search by typing any search term in the search field, like cDNA (e.g. "3G>T") or protein (e.g. "Arg525Ter") notations of mutations, mode of inheritance (e.g. "dominant") or specific phenotypes (e.g. "severe generalized"). Search results are shown at bottom of page.
</p>
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
Perform a more advanced search on the COL7A1 mutation database. If you enter or select two or more search terms simultaneously, the search engine will perform a combined search action (i.e. AND).
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
<h3>Browse the COL7A1 gene</h3>
<p>
Click anywhere on this schematic representation of the COL7A1 gene to graphically browse the gene. With every click you will zoom in deeper on the COL7A1 gene. Mutated nucleotides are depicted in red. If the cursor is placed over the mutated nucleotide(s), the corresponding mutation is shown.
</p>
<br/>
<p>
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
</p>
<br/>
<h4>General remarks</h4>
<ol>
<li>Mutations are numbered according to the current reference sequence (<a href="http://www.ncbi.nlm.nih.gov/nuccore/157389010" target="_new">GenBank Accession no. NM_000094.3</a>)</li>
<li>Mutation nomenclature is according to the <a href="http://www.hgvs.org/mutnomen/" target="_new">HGVS recommendations</a></li>
</ol>
<br/><br/>

</td>
<td class="news_box">
<@layout screen.getChild("NewsBar")/>
</td>
</tr>
</table>