	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
		${screen.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list screen.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			<div class="screenpadding">
<h3>Background</h3>
<p>
This international Microvillus Inclusion Disease (MVID) Patient Registry is constructed to aid all clinicians and scientists working in the field of Microvillus Inclusion Disease and the MYO5B gene. The registry contains all MVID patients who have been published in the medical literature together with their MYO5B genotypes, clinical phenotypes and molecular phenotypes. The MVID Registry can be searched for patients or mutations, and each by several categories. The registry also contains a number of unpublished patients and MYO5B mutations. Therefore, this registry can be used as a central, quick reference for all who work in the MVID field. You are  free to use the registry for your studies.
</p>
<p>
Mutations are numbered according to the current reference sequence (Genbank Accession no. xxxxxxxxxx). Mutations nomenclature is according to the HGVS recommendations
</p>
<h3>About Microvillus Inclusion Disease and the MYO5B gene</h3>
<p>
Microvillus inclusion disease (MVID; OMIM 251850) is a rare autosomal recessive disease presenting with severe intractable diarrhea and malabsorption in neonates. An early onset and late onset form of MVID are distinguished. At the cellular level, variable brush border atrophy with intracellular accumulation of lysosomal granules and microvillus inclusions in the apical cytoplasm is observed in MVID enterocytes. Apical brush border proteins involved in the processing and absorption of nutrients are typically absent from the cell surface and accumulate in compartments in the apical cytoplasm. The MVID diagnosis is made by light and electron microscopy. 
</p>
<p>
MVID is believed to be caused by mutations in the MYO5B gene on chromosome 18. To date, different nonsense, missense, splice site, or in-frame insertion mutations in the MYO5B gene (OMIM# 606540) have been identified in MVID patients. 
</p>
<p>
The MYO5B gene encodes myosin Vb, which is an actin filament-based motor protein that interacts with and regulates among others the subcellular spatial distribution of recycling endosomes that express small GTPase proteins such as Rab11a or Rab8 on their cytoplasmic surface. 
</p>
<h3>About the data</h3>
<p>
The MVID Registry is maintained by the departments of Genetics, Pediatrics, and Cell Biology of the University Medical Center Groningen, the Netherlands on behalf of an international initiative of departments involved in the clinical care for patients with MVID and research into MVID. 
</p>
<p>
</p>
<h3>Adding your unpublished data</h3>
<p>
The inclusion of unpublished data improves the quality of the data and the use of the registry. Therefore, we aim to increase the number of unpublished patients and mutations in the coming years by collaborating with an increasing number of departments. In the "<a href="molgenis.do?__target=View&select=UploadPlugin">Submit data</a>" field you can upload the unpublished data from your department. We highly recommend the use of the provided excel sheet in order to standardize the data. Submitted data will be inserted after curation. Before you can submit data, you need to login. By doing so, a unique user ID is generated that is attached to each patient you submit. This will enable you to come back at a later stage and edit the data you submitted.
</p>
<p>
All users who upload unpublished data will be mentioned in the news field on the homepage and in the news archive. Additionally, we will contact these users to cooperate on future update papers on the registry.
</p>
<h3>About curation of unpublished data</h3>
<p>
Curation will be done by the curator (Dr. Sven van IJzendoorn, MD, Department of Cellbiology, University Medical Center Groningen, Groningen, the Netherlands). All submitted data will be checked and completed where necessary. If questions remain, we will contact the user who submitted the data.
</p>
<h3>About the software</h3>
<p>
The database software has been constructed by the <a href="http://wiki.gcc.rug.nl/">Genomics Coordination Center</a>, a joined venture of the Dept. of Genetics, UMCG and the Groningen Bioinformatics Center, University of Groningen, the Netherlands. 
All software is build using the open source <a href="http://www.molgenis.org/">MOLGENIS</a> framework (<a href="http://www.ncbi.nlm.nih.gov/pubmed/15059831">Swertz et al., Bioinformatics, 2004</a> and <a href="http://www.ncbi.nlm.nih.gov/pubmed/17297480">Swertz & Jansen, Nature Reviews Genetics, 2007</a>). 
and is freely available to others working on locus specific databases at <a href="http://www.molgenis.org/svn/molgenis_apps">http://www.molgenis.org/svn/col7a1</a>.
Please contact Dr. Morris Swertz, <a href="mailto:m.a.swertz@rug.nl">m.a.swertz@rug.nl</a> if you need assistence.
</p>
<h3>References</h3>
<ol>
<#--
<li>Bruckner-Tuderman L. Dystrophic epidermolysis bullosa: pathogenesis and clinical features. Dermatol Clin. 2010 Jan;28(1):107-14. Review. PubMed PMID: <a href="http://www.ncbi.nlm.nih.gov/pubmed/19945622" target="_new">19945622</a>.</li>
<li>Fine JD, Eady RA, Bauer EA, Bauer JW, Bruckner-Tuderman L, Heagerty A, Hintner H, Hovnanian A, Jonkman MF, Leigh I, McGrath JA, Mellerio JE, Murrell DF, Shimizu H, Uitto J, Vahlquist A, Woodley D, Zambruno G. The classification of inherited epidermolysis bullosa (EB): Report of the Third International Consensus Meeting on Diagnosis and Classification of EB. J Am Acad Dermatol. 2008 Jun;58(6):931-50. Epub 2008 Apr 18. PubMed PMID: <a href="http://www.ncbi.nlm.nih.gov/pubmed/18374450" target="_new">18374450</a>.</li>
-->
<li>Swertz MA, De Brock EO, Van Hijum SA, De Jong A, Buist G, Baerends RJ, Kok J, Kuipers OP, Jansen RC. Molecular Genetics Information System (MOLGENIS): alternatives in developing local experimental genomics databases. Bioinformatics. 2004 Sep 1;20(13):2075-83. Epub 2004 Apr 1. PubMed PMID: <a href="http://www.ncbi.nlm.nih.gov/pubmed/15059831" target="_new">15059831</a>.</li>
<li>Swertz MA, Jansen RC. Beyond standardization: dynamic software infrastructures for systems biology. Nat Rev Genet. 2007 Mar;8(3):235-43. Epub 2007 Feb 13. Review. PubMed PMID: <a href="http://www.ncbi.nlm.nih.gov/pubmed/17297480" target="_new">17297480</a>.</li>
</ol>
<#--
<h3>Collaborators and supporters</h3>
<table width="100%">
<tr>
<td><a href="http://www.umcg.nl/NL/UMCG/overhetumcg/organisatie/Specialismen/dermatologie/Pages/default.aspx" target="_new"><img src="res/img/col7a1/umcg.jpg" width="250"/></a></td>
<td><a href="http://www.idi.it/web/idi/home" target="_new"><img src="res/img/col7a1/idi.jpg" height="100"/></a></td>
<td><a href="http://www.eb-haus.eu/index.php?id=21&L=1" target="_new"><img src="res/img/col7a1/ebhaus.png" height="100"/></a></td>
<td><a href="http://www.guysandstthomas.nhs.uk/services/dash/dermatology/dermatology.aspx" target="_new"><img src="res/img/col7a1/stjohns.jpg" height="100"/></a></td>
<td><a href="http://www.uniklinik-freiburg.de/ims/live/hospital/dermatology_en.html" target="_new"><img src="res/img/col7a1/ukl-logo.jpg" width="250"/></a></td>
<td><a href="http://www.debra-international.org/" target="_new"><img src="res/img/col7a1/debra_international.png" height="100"/></a></td>
</tr>
</table>
-->
			</div>
		</div>
	</div>