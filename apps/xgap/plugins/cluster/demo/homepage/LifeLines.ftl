<#macro plugins_cluster_demo_homepage_LifeLines screen>
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
		
		<div class="screenbody">
			<div class="screenpadding">	
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
			Welcome to the LifeLines xQTL workbench, a platform for the storage and analysis of genetic and phenotype data.<br> 
			
			For manuals and	more information, see <a href="http://www.xgap.org/wiki/xQTL">xQTL workbench wiki</a>.<br><br> xQTL workbench aims to provide the following features: 
			<ul>
				<li>Available as an <b>'In-House tool'</b> or usable in a <b>collaborative setting</b> via web interfaces</li>
				<li><b>Customizable database</b> for genetics/phenotype data using software generators</li>
			 	<li>High performance xQTL analysis</li>
				<li>Three levels of users:
					<ul>
						<li><b>Biologists</b> - Running analysis</li>
			        	<li><b>Bio-informaticians</b> - Adding new analysis and tools</li>
					 	<li><b>Administrators</b> - Checks for the infrastructure guys</li>
					</ul>
					And user management to data and analysis tools
				</li>
			 	<li>Extensible computation interface</li>
			 	<ul>
			 		<li>Biologists can run <b>pre-defined analysis</b></li>
			     	<li>Bio-informaticians can <b>add their own (R) analysis</b></li>
			     	<li>Manage previous analysis and their settings</li>
			 	</ul>
			 </li>
			 <li>Automatic annotation of identifiers
			 ${screen.linkouter.render("<ul><li>A Thaliana: AT3G1000 At1g10000 At5G20000</li>
			         <li>C. Elegans: WBGene00000083</li>
					 <li>NCBI: NG_006560.2 LOC729998 AC008038.1</li>
					 <li>KEGG: K06560 K00134 K29897</li>
				</ul>")}
			</li>
			<li>Build using the <b>software generation</b> allowing for easy tailoring to your needs</li>
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
		
			References:
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
			<div style="height: 50px">&nbsp;</div>
		</td>
	</tr>
	<tr>
		<td align="center" colspan="7" >
			<table style="background: #FFFFFF;" cellpadding="10" cellspacing="10" border="2" width="700px">
				<tr>
					<td align="center">
						<img src="clusterdemo/logos/molgenis_logo.png" width="75px" height="50px" alt="logo Molgenis">
						<img src="clusterdemo/logos/xgap_logo.png" width="75px" height="50px" alt="logo XGAP">
						<img src="clusterdemo/logos/gcc_logo.png" width="300px" height="50px" alt="logo GCC"><br>
						
						<img src="clusterdemo/logos/rug_logo.png" width="150px" height="50px" alt="logo RUG">
						<img src="clusterdemo/logos/umcg_logo.png" width="150px" height="50px" alt="logo UMCG">
						<img src="clusterdemo/logos/cbsg_logo.png" width="150px" height="50px" alt="logo CBSG">
					</td>
				</tr>
			</table>
			<font size=1>(c) 2009-2010 GBIC Groningen</font>
		</td>
	</tr>
</table>
</div>


<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
