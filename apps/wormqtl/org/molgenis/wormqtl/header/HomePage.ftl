<#macro org_molgenis_wormqtl_header_HomePage screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
<!-- this shows a title and border -->
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
		
<div align="center">
	<table width="700px">
		<tr>
			<td>
				<div style="height: 10px">&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td align="center">
				<div align="left">
				<!-- <font style='font-size:24px; font-weight:bold;'>xQTL workbench</font>-->
				<#if screen.userIsAdminAndDatabaseIsEmpty == true>
					<table bgcolor="white" border="3" bordercolor="red">
						<tr>
							<td>
								<br><i><font color="red">You are logged in as admin, and the database does not contain any investigations or other users. Automated setup is now possible. Database additions will disable this notice.</font></i><br><br>
								Enter your preferred file storage location, and press 'Load' to validate this path and load the example dataset here. Unvalidated paths are overwritten. In addition, the demo users and permissions are loaded.<br><br>
								The default shown is ./data - consider changing this before continuing. Be aware of permissions your OS grants you on this directory, depending on which user started up the application.<br><br>
								<#if screen.validpath?exists>
									<b>A valid path is present and cannot be overwritten here. To do so, use Settings -> File storage.</b><br><br>
									Path: <font style="font-size:medium; font-family: Courier, 'Courier New', monospace">${screen.validpath}</font>
								<#else>
									Path: <input type="text" size="30" style="border:2px solid black; color:blue; display:inline; font-size:medium; font-family: Courier, 'Courier New', monospace" id="inputBox" name="fileDirPath" value="./data" onkeypress="if(window.event.keyCode==13){document.forms.${screen.name}.__action.value = 'setPathAndLoad';}">
								</#if>
								<input type="submit" value="Load" id="loadExamples" onclick="document.forms.${screen.name}.__action.value = 'setPathAndLoad'; document.forms.${screen.name}.submit();"/>
								<br><br>
							</td>
						</tr>
					</table>
				</#if>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div style="height: 10px">&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td align="center">
				<div align="left">
					<h2>WormQTL – Public archive and analysis web portal for natural variation data in Caenorhabditis elegans</h2>
					Over the past decade increased efforts have been made to explore the model metazoan Caenorhabditis elegans as a platform for molecular quantitative genetics and the systems biology of natural variation. These efforts have resulted in an accumulation of a huge amount of phenotypic and genotypic data across different developmental worm stages and environments in recombinant inbred populations. Next to C. elegans, C. briggsae is an emerging model organism that allows evolutionary comparisons with C. elegans and quantitative genetic exploration of its own unique biological attributes. In addition, a wealth of similar high-throughput data has been produced on hundreds of different C. elegans wild isolates.
					<br><br>
					This rapid increase in data calls for an easily accessible database allowing for comparative analysis and meta-analysis within and across species. Here we present WormQTL, a public portal for the management of all data on natural variation in Caenorhabditis spp. and integrated development of suitable analysis tools. The web server provides a rich set of analysis tools for genotype-phenotype mapping based on R/qtl. Users can upload and share new R scripts as ‘plugin’ for the colleagues in the community to use directly. New data can be uploaded and downloaded using XGAP, an extensible text format for genotype and phenotypes. All data  and tools can be accessed via a public web user interface as well as programming interfaces to R, REST, and SOAP web services. All software is free for download as MOLGENIS ‘app’. WormQTL is freely accessible without registration and is hosted on a large computational cluster enabling high throughput analyses to all at http://www.wormqtl.org. 
					<br><br>
					WormQTL is an online scalable system for QTL exploration to service the worm community. WormQTL provides many publicly available datasets and welcomes submissions from other worm researchers.
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div style="height: 20px">&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td width="675">
				<h3>Citation:</h3>
				<i>WormQTL – Public archive and analysis web portal for natural variation data in Caenorhabditis elegans</i><br>
				L. Basten Snoek, Yang Li, K. Joeri Van der Velde, Danny Arends, Antje Beyer, Mark Elvin, Jasmin Fisher, Alex Hajnal, Michael Hengartner, Gino Poulin, Miriam Rodriguez, Tobias Schmid, Sabine Schrimpf, Feng Xue, Xue Zheng, Ritsert C. Jansen, Jan E. Kammenga, Morris A. Swertz - <b>in writing</b> 2012; doi: <a href="http://todo" target="_blank">todo</a>
				<br><br>
				<h3>References:</h3>
				<a href="http://dx.doi.org/10.1093/bioinformatics/bts049">xQTL workbench: a scalable web environment for multi-level QTL analysis</a> - Danny Arends; K. Joeri van der Velde; Pjotr Prins; Karl W. Broman; Steffen Moller; Ritsert C. Jansen; Morris A. Swertz
				<br><br>
				<a href="http://www.ncbi.nlm.nih.gov/pubmed/20214801">XGAP: a uniform and extensible data model and software platform for genotype and phenotype experiments</a> - Swertz MA, Velde KJ, Tesson BM, Scheltema RA, Arends D, Vera G, Alberts R, Dijkstra M, Schofield P, Schughart K, Hancock JM, Smedley D, Wolstencroft K, Goble C, de Brock EO, Jones AR, Parkinson HE; Coordination of Mouse Informatics Resources (CASIMIR); Genotype-To-Phenotype (GEN2PHEN) Consortiums, Jansen RC.
				<br><br>
				<a href="http://www.ncbi.nlm.nih.gov/pubmed/12724300">R/qtl: QTL mapping in experimental crosses</a> - Broman KW, Wu H, Sen S, Churchill GA.
				<br><br>
				<a href="http://bioinformatics.oxfordjournals.org/content/early/2010/10/21/bioinformatics.btq565.abstract">R/qtl: high throughput Multiple QTL mapping</a> - Danny Arends, Pjotr Prins, Ritsert C. Jansen and Karl W. Broman
				<br><br>
				<a href="http://www.ncbi.nlm.nih.gov/pubmed/15059831">Molecular Genetics Information System (MOLGENIS): alternatives in developing local experimental genomics databases</a> - Swertz MA, De Brock EO, Van Hijum SA, De Jong A, Buist G, Baerends RJ, Kok J, Kuipers OP, Jansen RC.
				<br><br>
				<a href="http://www.biomedcentral.com/1471-2105/11/S12/S12">The MOLGENIS toolkit: rapid prototyping of biosoftware at the push of a button</a> - Morris A Swertz, Martijn Dijkstra, Tomasz Adamusiak, Joeri K van der Velde, Alexandros Kanterakis, Erik T Roos, Joris Lops, Gudmundur A Thorisson, Danny Arends, George Byelas, Juha Muilu, Anthony J Brookes, Engbert O de Brock, Ritsert C Jansen and Helen Parkinson
			</td>
		</tr>	
		<tr>
			<td align="center">
				<div style="height: 25px">&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td align="center">
				<table style="background: #FFFFFF;" cellpadding="10" cellspacing="10" border="2" width="700px">
					<tr>
						<td align="center">
							<a target="_blank" href="http://www.molgenis.org/"><img src="clusterdemo/logos/molgenis_logo.png" width="75px" height="50px" alt="logo Molgenis"></a>
							<a target="_blank" href="http://wiki.gcc.rug.nl/"><img src="clusterdemo/logos/gcc_logo.png" width="300px" height="50px" alt="logo GCC"></a>
							<a target="_blank" href="http://www.rug.nl/"><img src="clusterdemo/logos/rug_logo.png" width="150px" height="50px" alt="logo RUG"></a><br>
							<a target="_blank" href="http://www.xgap.org/"><img src="clusterdemo/logos/xgap_logo.png" width="75px" height="50px" alt="logo XGAP"></a>
							<a target="_blank" href="http://www.umcg.nl/"><img src="clusterdemo/logos/umcg_logo.png" width="150px" height="50px" alt="logo UMCG"></a>
							<a target="_blank" href="http://www.cbsg.nl/"><img src="clusterdemo/logos/cbsg_logo.png" width="150px" height="50px" alt="logo CBSG"></a>
							<a target="_blank" href="http://www.panaceaproject.eu/"><img src="clusterdemo/logos/panacea_logo.gif" width="150px" height="50px" alt="logo Panacea"></a>
						</td>
					</tr>
				</table>
				<font size=1>(c) 2012 GBIC Groningen</font>
			</td>
		</tr>
	</table>
</div>


</div>
</form>
</#macro>
