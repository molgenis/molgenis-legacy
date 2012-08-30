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
This International Dystrophic Epidermolysis Bullosa Patient Registry is constructed to aid all clinicians and scientists working in the field of dystrophic epidermolysis bullosa (DEB) and the COL7A1 gene. The registry contains all DEB patients who have been published in the medical literature together with their COL7A1 genotypes and molecular phenotypes (i.e. results from immunofluorescence and electron microscopy investigations). The DEB Registry can be searched for patients or mutations, and each by several categories. The registry also contains a number of unpublished patients and COL7A1 mutations. Therefore, this registry can be used as a central, quick reference for all who work in the DEB field. Mutations are numbered according to the current reference sequence (<a href="http://www.ncbi.nlm.nih.gov/nuccore/157389010" target="_new">GenBank Accession no. NM000094.3</a>). Mutation nomenclature is according to the <a href="http://www.hgvs.org/mutnomen/" target="_new">HGVS recommendations</a>. 
</p>
<p>
You are free to use the registry for your studies. A paper describing the development, construction and data of the DEB registry is currently under review. Please cite <a href="http://www.col7a1.org/" target="_new">http://www.col7a1.org/</a> when using the registry. 
</p>
<h3>About dystrophic epidermolysis bullosa and the COL7A1 gene</h3>
<p>
Dystrophic epidermolysis bullosa is a heritable blistering disorder that is caused by mutations in the COL7A1 gene [OMIM *120120] encoding type VII collagen. DEB can be inherited either dominantly (DDEB, [OMIM #131750, #131800]) or recessively (RDEB, [OMIM #226600]). See Bruckner-Tuderman (2010) for a good review on DEB. Although a few recurrent mutations have been identified, most families carry unique mutations. Many studies have shown that phenotypic outcomes cannot always be predicted on the basis of COL7A1 genotypes. Therefore, a central registry listing all patients, mutations, genotypes and phenotypes is necessary.
</p>
<p>
Fine et al. (2008) describe the clinical subtypes of DEB and the clinical and molecular features that are part of DEB. In this registry, the phenotypic consensus classification as published by Fine et al. is used (table 1).
</p>
Table 1. Dystrophic EB subtypes (modified from Fine et al., 2008)
<table class="listtable">
<caption align="bottom">* Rare variants in italic type.<br/>** Previously called RDEB, Hallopeau-Siemens.</caption>
<tr class="tableheader"><th>Type</th><th>Subtype*</th><th>Number of patients</th></tr>
<tr class="form_listrow1"><td rowspan="7">DDEB</td><td>DDEB, generalized (DDEB-gen)</td><td>${model.getPhenotypeCount("DDEB, generalized")}</td></tr>
<tr class="form_listrow0"><td><i>DDEB, acral (DDEB-ac)</i></td><td>${model.getPhenotypeCount("DDEB, acral")}</td></tr>
<tr class="form_listrow1"><td><i>DDEB, pretibial (DDEB-Pt)</i></td><td>${model.getPhenotypeCount("DDEB, pretibial")}</td></tr>
<tr class="form_listrow0"><td><i>DDEB, pruriginosa (DDEB-Pr)</i></td><td>${model.getPhenotypeCount("DDEB, pruriginosa")}</td></tr>
<tr class="form_listrow1"><td><i>DDEB, nails only (DDEB-na)</i></td><td>${model.getPhenotypeCount("DDEB, nails only")}</td></tr>
<tr class="form_listrow0"><td><i>DDEB, bullous dermolysis of the newborn (DDEB-BDN)</i></td><td>${model.getPhenotypeCount("DDEB, bullous dermolysis of the newborn")}</td></tr>
<tr class="form_listrow1"><td><i>DDEB, unknown (DDEB-u)</i></td><td>${model.getPhenotypeCount("DDEB, unknown")}</td></tr>
<tr class="form_listrow0"><td>DEB</td><td><i>DEB, unknown (DEB-u)</i></td><td>${model.getPhenotypeCount("DEB-u")}</td></tr>
<tr class="form_listrow1"><td rowspan="9">RDEB</td><td>RDEB, severe generalized (RDEB-sev gen)**</td><td>${model.getPhenotypeCount("RDEB, severe generalized")}</td></tr>
<tr class="form_listrow0"><td>RDEB, generalized other (RDEB-O)</td><td>${model.getPhenotypeCount("RDEB, generalized other")}</td></tr>
<tr class="form_listrow1"><td><i>RDEB, acral (RDEB-ac)</i></td><td>${model.getPhenotypeCount("RDEB, acral")}</td></tr>
<tr class="form_listrow0"><td><i>RDEB, inversa (RDEB-i)</i></td><td>${model.getPhenotypeCount("RDEB, inversa")}</td></tr>
<tr class="form_listrow1"><td><i>RDEB, pretibial (RDEB-Pt)</i></td><td>${model.getPhenotypeCount("RDEB, pretibial")}</td></tr>
<tr class="form_listrow0"><td><i>RDEB, pruriginosa (RDEB-Pr)</i></td><td>${model.getPhenotypeCount("RDEB, pruriginosa")}</td></tr>
<tr class="form_listrow1"><td><i>RDEB, centripetalis (RDEB-Ce)</i></td><td>${model.getPhenotypeCount("RDEB-Ce")}</td></tr>
<tr class="form_listrow0"><td><i>RDEB, bullous dermolysis of the newborn (RDEB-BDN)</i></td><td>${model.getPhenotypeCount("RDEB, bullous dermolysis of the newborn")}</td></tr>
<tr class="form_listrow1"><td><i>RDEB, unknown (RDEB-u)</i></td><td>${model.getPhenotypeCount("RDEB, unknown")}</td></tr>
</table>
<h3>About the data</h3>
<p>
The DEB registry is maintained by the departments of Genetics and Dermatology of the University Medical Center Groningen, the Netherlands on behalf of an international initiative of departments involved in the clinical care for patients with DEB and research into DEB. The International DEB Patient Registry currently contains ${model.numPatients} DEB patients and ${model.numMutations} COL7A1 mutations. Of these, ${model.numPatientsUnpub} are unpublished DEB patients. This registry is a work in progress. New publications will be added regularly. The institutes that are currently working together on this registry are:
</p>
<ul>
<li>Istituto Dermopatico dell'Immacolata, Rome, Italy</li>
<li>Department of Dermatology, University Medical Center Freiburg, Freiburg, Germany</li>
<li>St John's Institute of Dermatology, Guy's and St Thomas' NHS Foundation Trust, London, United Kingdom</li>
<li>EB House Austria, Department of Dermatology, Paracelsus Medical University, Salzburg, Austria</li>
<li>Departments of Genetics and Dermatology, University Medical Center Groningen, Groningen, the Netherlands</li>
</ul>
<p>
All departments that are willing to collaborate in this project are welcome and can <a href="molgenis.do?__target=View&select=Contact">contact</a> us. We will contact other departments to set up broader collaborations in the future. Please contact <a href="molgenis.do?__target=View&select=Contact">Dr. Peter van den Akker</a> if you need any assistance with the data or registry. 
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
Curation will be done by the curator (Dr. Peter C. van den Akker, MD, Department of Genetics, University Medical Center Groningen, Groningen, the Netherlands). All submitted data will be checked and completed where necessary. If questions remain, we will contact the user who submitted the data.
</p>
<h3>About the software</h3>
<p>
The database software has been constructed by the <a href="http://wiki.gcc.rug.nl">Genomics Coordination Center</a>, a joined venture of the Dept. of Genetics, UMCG and the Groningen Bioinformatics Center, University of Groningen, the Netherlands. 
All software is build using the open source <a href="http://www.molgenis.org">MOLGENIS</a> framework (<a href="http://www.ncbi.nlm.nih.gov/pubmed/15059831">Swertz et al., Bioinformatics, 2004</a> and <a href="http://www.ncbi.nlm.nih.gov/pubmed/17297480">Swertz & Jansen, Nature Reviews Genetics, 2007</a>). 
and is freely available to others working on locus specific databases at <a href="http://www.molgenis.org/svn/col7a1">http://www.molgenis.org/svn/col7a1</a>.
Please contact Dr. Morris Swertz, <a href="mailto:m.a.swertz@rug.nl">m.a.swertz@rug.nl</a> if you need assistence.
</p>
<h3>References</h3>
<ol>
<li>Bruckner-Tuderman L. Dystrophic epidermolysis bullosa: pathogenesis and clinical features. Dermatol Clin. 2010 Jan;28(1):107-14. Review. PubMed PMID: <a href="http://www.ncbi.nlm.nih.gov/pubmed/19945622" target="_new">19945622</a>.</li>
<li>Fine JD, Eady RA, Bauer EA, Bauer JW, Bruckner-Tuderman L, Heagerty A, Hintner H, Hovnanian A, Jonkman MF, Leigh I, McGrath JA, Mellerio JE, Murrell DF, Shimizu H, Uitto J, Vahlquist A, Woodley D, Zambruno G. The classification of inherited epidermolysis bullosa (EB): Report of the Third International Consensus Meeting on Diagnosis and Classification of EB. J Am Acad Dermatol. 2008 Jun;58(6):931-50. Epub 2008 Apr 18. PubMed PMID: <a href="http://www.ncbi.nlm.nih.gov/pubmed/18374450" target="_new">18374450</a>.</li>
<li>Swertz MA, De Brock EO, Van Hijum SA, De Jong A, Buist G, Baerends RJ, Kok J, Kuipers OP, Jansen RC. Molecular Genetics Information System (MOLGENIS): alternatives in developing local experimental genomics databases. Bioinformatics. 2004 Sep 1;20(13):2075-83. Epub 2004 Apr 1. PubMed PMID: <a href="http://www.ncbi.nlm.nih.gov/pubmed/15059831" target="_new">15059831</a>.</li>
<li>Swertz MA, Jansen RC. Beyond standardization: dynamic software infrastructures for systems biology. Nat Rev Genet. 2007 Mar;8(3):235-43. Epub 2007 Feb 13. Review. PubMed PMID: <a href="http://www.ncbi.nlm.nih.gov/pubmed/17297480" target="_new">17297480</a>.</li>
</ol>
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
			</div>
		</div>
	</div>