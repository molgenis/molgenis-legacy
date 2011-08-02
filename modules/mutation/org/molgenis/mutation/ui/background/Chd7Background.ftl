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
This CHD7 mutation database is made to aid all clinicians and scientists working in the field of CHARGE syndrome and the CHD7 gene. The database contains all published variants and also unpublished variants can be submitted. The CHD7 mutation database can be searched for patients and his/her clinical phenotype or mutations. This database can be used as a central, quick reference database for anyone who encounters variations in the CHD7-gene. Mutations are numbered according to the current reference sequence (GenBank Accession no. NM017780.2). Mutation nomenclature is according to the HGVS recommendations.
</p>
<p>
You are free to use the database for your studies. This database is constructed based on a database on COL7A1 mutations (www.col7a1.org, vd Akker et all). Please cite http://www.chd7.org/ when using the database.
</p>
<h3>About CHARGE syndrome and the CHD7-gene</h3>
<p>
Heterozygous mutations and deletions of the CHD7 gene (OMIM 608892) result in CHARGE syndrome (OMIM 214800), a complex of multiple congenital malformations involving the central nervous system, eye, ear, nose and mediastinal organs (Vissers et al, 2004). CHARGE is an acronym (Pagon). Clinical features include coloboma, heart defect, choanal atresia, retarded growth and development, genital hypoplasia, ear anomalies, deafness and semi-circular canal hypoplasia (Jongmans, 2006). Clinical criteria (see table for CHARGE syndrome have been defined by Blake (Blake 1998) and Verloes (Verloes, 2005). CHD7-analysis is a major contributor to the diagnosis today, although not all clinically diagnosed patients with CHARGE syndrome carry a mutation in this gene (Jongmans 2006). Contrary in patients who do not fulfill the clinical criteria, mutations are found. CHD7 mutations have also been found in patients initially diagnosed with Kallmann syndrome and idiopathic hypogonadotropic hypogonadism and it is well-recognized that Kallmann syndrome can be part of the phenotypic spectrum of CHARGE syndrome (Jongmans, Kim, Bergman)
</p>
<h3>About the data</h3>
<p>
The CHD7 mutation database is maintained by the department of Genetics of the University Medical Center Groningen, the Netherlands. The CHD7 mutation database currently contains ... pathogenic CHD7 mutations in É patients, É unclassified variants of CHD7 in É. patients, and É polymorphism of CHD7. Of these, 0 are unpublished CHARGE patients and 0 are unpublished variants. This database is a work in progress. New publications will be added monthly. The institutes that are currently working together in this database are:
</p>
<ul>
<li>Departments of Genetics, University Medical Center Groningen, Groningen, the Netherlands</li>
</ul>
<p>
Please contact Dr. Nicole Janssen if you need any assistance with the data or database (<a href="mailto:n.janssen01@umcg.nl">n.janssen01@umcg.nl</a>)
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
Curation will be done by the curator (Dr. Nicole Janssen, MD, Department of Genetics, University Medical Center Groningen, Groningen, the Netherlands). All submitted data will be checked and completed where necessary. If questions remain, we will contact the user who submitted the data.
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
</tr>
</table>
			</div>
		</div>
	</div>