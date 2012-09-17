<#macro plugins_cluster_demo_homepage_Tifn screen>
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
		
<#--begin your plugin-->
<div align="center">
<table width="700px">
	<tr>
		<td align="center" colspan="7" >
			<div align="left">
			<!-- <font style='font-size:24px; font-weight:bold;'>xQTL workbench</font>-->
			<#if screen.userIsAdminAndDatabaseIsEmpty == true>
				<br><br><table bgcolor="white"><tr><td>
				<br><i><font color="red">You are logged in as admin, and the database does not contain any investigations or other users.</font></i><br><br>
				Enter your preferred file storage location, and press 'Load' to validate this path and load the example dataset here. Unvalidated paths are overwritten. In addition, the demo users and permissions are loaded.<br><br>
				The default shown is ./data - consider changing this before continuing. Be aware of permissions your OS grants you on this directory, depending on which user started up the application.<br><br>
				
				<#if screen.validpath?exists>
					<b>A valid path is present and cannot be overwritten here. To do so, use Settings -> File storage.</b><br><br>
					Path: <font style="font-size:medium; font-family: Courier, 'Courier New', monospace">${screen.validpath}</font>
				<#else>
					Path: <input type="text" size="30" style="border:2px solid black; color:blue; display:inline; font-size:medium; font-family: Courier, 'Courier New', monospace" id="inputBox" name="fileDirPath" value="./data" onkeypress="if(window.event.keyCode==13){document.forms.${screen.name}.__action.value = 'setPathAndLoad';}">
				</#if>
				
				<input type="submit" value="Load" id="loadExamples" onclick="document.forms.${screen.name}.__action.value = 'setPathAndLoad'; document.forms.${screen.name}.submit();"/>
				<br><br></td></tr></table>
			</#if>
			<br><br>
			Welcome to the <b>TI Food Nutrition research portal</b>, based on xQTL workbench, a platform for the storage and analysis of geno- and phenotypic data. For manuals and	more information, see <a href="http://www.xgap.org/wiki/xQTL">xQTL workbench wiki</a>.<br><br> xQTL workbench provides the following features: 
			<ul>
				<li>Available as an <b>'in-house tool'</b> or used in a <b>collaborative setting</b> via web interfaces</li>
				<li><b>Customizable</b> database for genetics data using software generators</li>
			 	<li>High <b>performance</b> xQTL analysis</li>
				<li>Three levels of users:
					<ul>
						<li><b>Biologists</b> - Import, browse and analyze data</li>
			        	<li><b>Bio-informaticians</b> - Add new analysis and tools</li>
					 	<li><b>Administrators</b> - User and database management</li>
					</ul>
				</li>
			 	<li>Extensible computation interface</li>
			 	<ul>
			 		<li>Biologists can run <b>pre-defined analyses</b></li>
			     	<li>Bio-informaticians can <b>add their own (R) scripts</b></li>
			     	<li><b>Recombine</b> datasets and scripts into new analyses</li>
			 	</ul>
			 </li>
			 <li><b>Automatic annotation</b> of identifiers
			 ${screen.linkouter.render("<ul><li>A Thaliana: AT3G1000 At1g10000 At5G20000</li>
			         <li>C. Elegans: WBGene00000083</li>
					 <li>NCBI: NG_006560.2 LOC729998 AC008038.1</li>
					 <li>KEGG: K06560 K00134 K29897</li>
				</ul>")}
			</li>
			<li>Fully <b>open</b> user management and permission system</li>
			<li>Created using <b>software generation</b> allowing for quick tailoring to your needs</li>
			</ul>
			</div>
			<div style="height: 10px">&nbsp;</div>
		</td>
	</tr>
	<tr>
	
	<td align="center"
		<#if screen.loggedIn>
			onClick="document.forms.main.__target.value='main';document.forms.main.select.value='Investigations';document.forms.main.submit();">
		<#else>
			onClick="document.forms.main.__target.value='main';document.forms.main.select.value='UserLogin';document.forms.main.submit();">
		</#if>
			<table  bgcolor="#BBBBBB" cellpadding="10" cellspacing="10" border="2" width="160px">
				<tr>

					<td align="center">
						<font style='font-size:15px; font-weight:bold;'>Browse</font> <font style='font-size:15px;'>data</font>
					</td>
				</tr>
			</table>
			<table>
				<tr>
					<td align="center">
						<font style='font-size:10px;'>(login as user <b>bio-user</b>, password <b>bio</b>)</font>
					</td>
				</tr>
			</table>
		</td>
	
		<td width="20px">&nbsp;</td>
		
		<td align="center"
		<#if screen.loggedIn>
			onClick="document.forms.main.__target.value='main';document.forms.main.select.value='QTLWizard';document.forms.main.submit();">
		<#else>
			onClick="document.forms.main.__target.value='main';document.forms.main.select.value='UserLogin';document.forms.main.submit();">
		</#if>
			<table  bgcolor="#BBBBBB" cellpadding="10" cellspacing="10" border="2" width="160px">

				<tr>
					<td align="center">
						<font style='font-size:15px; font-weight:bold;'>Import</font> <font style='font-size:15px;'>data</font>
					</td>
				</tr>
			</table>
			<table>
				<tr>
					<td align="center">
						<font style='font-size:10px;'>(login as user <b>bio-user</b>, password <b>bio</b>)</font>
					</td>
				</tr>
			</table>
		</td>
		
		<td width="20px">&nbsp;</td>

		<td valign="top" align="center"
		<#if screen.loggedIn>
			onClick="document.forms.main.__target.value='main';document.forms.main.select.value='Cluster';document.forms.main.submit();">
		<#else>
			onClick="document.forms.main.__target.value='main';document.forms.main.select.value='UserLogin';document.forms.main.submit();">
		</#if>
			<table bgcolor="#BBBBBB" cellpadding="10" cellspacing="10" border="2" width="160px">
				<tr>
					<td align="center">
						<font style='font-size:15px; font-weight:bold;'>Run</font> <font style='font-size:15px;'>QTL mapping</font>
					</td>
				</tr>
			</table>
			<table>
				<tr>
					<td align="center">
						<font style='font-size:10px;'>(login as user <b>bio-user</b>, password <b>bio</b>)</font>
					</td>
				</tr>
			</table>
		</td>
		
		<td width="20px">&nbsp;</td>
		
		<td align="center"
		<#if screen.loggedIn>
			onClick="document.forms.main.__target.value='main';document.forms.main.select.value='AnalysisSettings';document.forms.main.submit();">
		<#else>
			onClick="document.forms.main.__target.value='main';document.forms.main.select.value='UserLogin';document.forms.main.submit();">
		</#if>
			<table  bgcolor="#BBBBBB" cellpadding="10" cellspacing="10" border="2" width="160px">
				<tr>
					<td align="center">
						<font style='font-size:15px; font-weight:bold;'>Add</font> <font style='font-size:15px;'>analysis</font>
					</td>
				</tr>
			</table>
			<table>
				<tr>
					<td align="center">
						<font style='font-size:10px;'>(login as user <b>bioinfo-user</b>, password <b>bioinfo</b>)</font>
					</td>
				</tr>
			</table>
			&nbsp;
		</td>
		
	</tr>
	
	<tr>
		<td colspan="7" width="675">
			<div style="height: 20px">&nbsp;</div>
		
			<i>References:</i>
			<br><br>
			<a href="http://www.ncbi.nlm.nih.gov/pubmed/20214801">XGAP: a uniform and extensible data model and software platform for genotype and phenotype experiments.</a> Swertz MA, Velde KJ, Tesson BM, Scheltema RA, Arends D, Vera G, Alberts R, Dijkstra M, Schofield P, Schughart K, Hancock JM, Smedley D, Wolstencroft K, Goble C, de Brock EO, Jones AR, Parkinson HE; Coordination of Mouse Informatics Resources (CASIMIR); Genotype-To-Phenotype (GEN2PHEN) Consortiums, Jansen RC.
			<br><br>
			<a href="http://www.ncbi.nlm.nih.gov/pubmed/12724300">R/qtl: QTL mapping in experimental crosses.</a> Broman KW, Wu H, Sen S, Churchill GA.
			<br><br>
			<a href="http://bioinformatics.oxfordjournals.org/content/early/2010/10/21/bioinformatics.btq565.abstract">R/qtl: high throughput Multiple QTL mapping.</a> Danny Arends, Pjotr Prins, Ritsert C. Jansen and Karl W. Broman
			<br><br>
			<a href="http://www.ncbi.nlm.nih.gov/pubmed/15059831">Molecular Genetics Information System (MOLGENIS): alternatives in developing local experimental genomics databases.</a> Swertz MA, De Brock EO, Van Hijum SA, De Jong A, Buist G, Baerends RJ, Kok J, Kuipers OP, Jansen RC.
		</td>
	</tr>	
	<tr>
		<td align="center" colspan="7" >
			<div style="height: 25px">&nbsp;</div>
		</td>
	</tr>
	<tr>
		<td align="center" colspan="7" >
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
			<font size=1>(c) 2009-2011 GBIC Groningen</font>
		</td>
	</tr>
</table>
</div>


<#--end of your plugin-->	
</div>
</form>
</#macro>
