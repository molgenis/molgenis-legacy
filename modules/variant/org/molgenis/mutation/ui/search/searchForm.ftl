<#-- table for search options to the left, news to the right -->
<table width="100%">
<tr>
<td valign="top">

<#if !queryParametersVO.expertSearch>
<#assign form      = model.simpleSearchForm>
<#assign expert    = model.toExpertSearchForm>
<#assign mutations = model.listAllMutationsForm>
<#assign patients  = model.listAllPatientsForm>

${model.textWelcome}

<#if model.geneDTO.symbol == "COL7A1">
<p>
The database currently contains ${model.numPatients} DEB patients, of which ${model.numUnpublished} unpublished, and ${model.numMutations} COL7A1 mutations. Search or browse below.
</p>
<#elseif model.geneDTO.symbol == "MYO5B">
<p>
The database currently contains ${model.numPatients} MVID patients, of which ${model.numUnpublished} unpublished, and ${model.numMutations} MYO5B mutations. Search or browse below.
</p>
<#elseif model.geneDTO.symbol == "CHD7">
<p>
The database currently contains ${model.getNumMutationsByPathogenicity("pathogenic")} pathogenic mutations in ${model.getNumPatientsByPathogenicity("pathogenic")} patients, ${model.getNumMutationsByPathogenicity("unclassified variant")} unclassified variants in ${model.getNumPatientsByPathogenicity("unclassified variant")} patients, and ${model.getNumMutationsByPathogenicity("benign")} benign variants.
</p>
<p>
You can search or browse below.
</p>
</#if>
<br/>

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
<#assign back = model.toSimpleSearchForm>
<#assign muta = model.showMutationForm>
<!--<form method="post">${back.__target}${back.__action}${back.expertSearch}${back.submit}</form>-->
<h3>Advanced search page</h3>
<p>
Perform a more advanced search on the ${model.geneDTO.symbol} mutation database. If you enter or select two or more search terms simultaneously, the search engine will perform a combined search action (i.e. AND).
</p>
<table cellpadding="4" cellspacing="4">
<form action="molgenis.do#results" method="post" name="AdvSearch">
${model.expertSearchFormWrapper.render()}
<tr>
<td colspan="4" align="center"><input type="submit" value="Search">&nbsp;<input type="reset" name="reset" value="Reset">&nbsp;<input type="button" name="clear" value="Clear" onclick="clearForm(document.forms.AdvSearch);"></td>
</tr>
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
<#if model.mbrowse.isVisible>
<br/><br/>
<h3>Browse the ${model.geneDTO.symbol} gene</h3>
<p>
Click anywhere on this schematic representation of the ${model.geneDTO.symbol} gene to graphically browse the gene. With every click you will zoom in deeper on the ${model.geneDTO.symbol} gene. Mutated nucleotides are depicted in red. If the cursor is placed over the mutated nucleotide(s), the corresponding mutation is shown.
</p>
<br/>
<p>
<#--
${model.controller.getChild("MBrowse").render()}
-->
${model.mbrowse.createGenePanel()}
</p>
</#if>

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