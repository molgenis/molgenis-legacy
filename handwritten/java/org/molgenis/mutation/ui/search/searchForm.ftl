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
	<td>${form.term}&nbsp;<a href="molgenis.do?__target=${screen.name}&__action=init&expertSearch=1">Advanced Search</a></td>
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
<#assign show = vo.showMutationForm>
<!--<form method="post">${back.__target}${back.__action}${back.expertSearch}${back.submit}</form>-->
<h3>Advanced search page</h3>
<p>
Perform a more advanced search on the COL7A1 mutation database. If you enter or select two or more search terms simultaneously, the search engine will perform a combined search action (i.e. AND).
</p>
<table cellpadding="4" cellspacing="4">
<form action="molgenis.do#results" method="post" name="AdvSearch">
${form.__target}
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
${show.__target}
${show.__action}
${show.expertSearch}
<tr><td colspan="2"></td><td align="right">or</td><td>${show.mid}</td><td colspan="2"></td></tr>
</form>
</table>
[<a href="molgenis.do?__target=${screen.name}&__action=init&expertSearch=0">Back to simple search</a>]
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
<div style="overflow-x: auto; margin-right: 10p;">
<table>
<tr>
	<td colspan="4">
		<table cellpadding="0" cellspacing="0">
		<tr>
<#list vo.proteinDomainList as proteinDomainSummaryVO>
	<#assign exons = proteinDomainSummaryVO.exons>
	<#if exons?size &gt; 0>
		<#assign firstExon = exons?first>
		<#assign lastExon  = exons?last>
			<td align="center" valign="bottom">${proteinDomainSummaryVO.proteinDomain.getName()} (exon ${firstExon.getNumber()}-${lastExon.getNumber()})</td>
	</#if>
</#list>
		</tr>
		<tr>
<#list vo.proteinDomainList as proteinDomainSummaryVO>
	<#if proteinDomainSummaryVO.proteinDomain.getName() == 'Triple helix domain'>
		<#assign imgsrc = "protdom0.png">
	<#else>
		<#assign imgsrc = "protdom1.png">
	</#if>
			<td>
				<table border="0" cellpadding="0" cellspacing="0"><tr>
	<#assign exons = proteinDomainSummaryVO.exons>
	<#assign i = 1>
	<#list exons as exon>
					<td class="exonbox"><a href="molgenis.do?__target=${screen.name}&__action=showProteinDomain&domain_id=${proteinDomainSummaryVO.proteinDomain.getId()?c}&snpbool=1#exon${exon.getId()?c}"><img src="res/img/col7a1/${imgsrc}" width="${exon.getLength() / 10}" height="26px" alt="${exon.getName()}" title="${exon.getName()}"></a></td>
		<#assign i = i + 1>
	</#list>
				</tr></table>
			</td>
</#list>
		</tr>
		</table>
	</td>
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
<iframe src="molgenis.do?__target=NewsPlugin&select=NewsPlugin&__action=top&__show=popup" width="350" height="800" name="News" scrolling="no" marginheight="0" marginwidth="0" frameborder="0">
</iframe>
<#--
<div class="formscreen">
<div class="form_header" id="SearchPlugin">News</div>
<div class="screenpadding">
<#list vo.news as newsItem>
<div class="news_title">${newsItem.getTitle()}</div>
<div class="news_subtitle">${newsItem.getSubtitle()}</div>
<div>${newsItem.getDate()}</div>
<div><a href="molgenis.do?__target=NewsPlugin&select=NewsPlugin&__action=entry&id=${newsItem.getId()}">More</a></div>
<br/><br/>
</#list>
<div align="center"><a href="molgenis.do?__target=NewsPlugin&select=NewsPlugin&__action=all">All News</a></div>
</div>
</div>
-->
</td>
</table>