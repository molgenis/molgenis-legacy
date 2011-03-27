<#macro plugins_cluster_demo_xqtlpanaceaheader_HomePage screen>
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
<div style="height: 10px;">&nbsp;</div>
<table width="700px">
	<tr>
		<td align="center" colspan="7" >
			<div style="height: 20px">&nbsp;</div>
			<div align="left">
			<h2>Panacea xQTL workbench</h2>
			Features: 
			<ul>
				 <li>Extensible database for genetics data</li>
				 <li>High performance xQTL analysis</li>
				 <li>Multifunctional computation interface</li>
				 <li>Run predefined analysis</li>
				 <li>Create and manage your own analysis</li>
				 <li>Automatic annotation of identifiers
				     <ul>
				     	<li>A Thaliana: AT3G1000 At1g10000 At5G20000</li>
				        <li>C. Elegans: WBGene00000083</li>
						<li>NCBI: NG_006560.2 LOC729998 AC008038.1</li>
						<li>KEGG: K06560 K00134 K29897</li>
					</ul>
				</li>
			</ul>
			</div>
			<div style="height: 50px">&nbsp;</div>
		</td>
	</tr>
	<tr>
		<td align="center" onClick="document.forms.main.__target.value='main';document.forms.main.select.value='Cluster';document.forms.main.submit();">
			<table bgcolor="#BBBBBB" cellpadding="10" cellspacing="10" border="2" width="160px">
				<tr>
					<td align="center">
						<font style='font-size:15px; font-weight:bold;'>Run</font> mapping
					</td>
				</tr>
			</table>
		</td>
		
		<td width="20px">&nbsp;</td>
		
		<td align="center" onClick="document.forms.main.__target.value='main';document.forms.main.select.value='Investigations';document.forms.main.submit();">
			<table  bgcolor="#BBBBBB" cellpadding="10" cellspacing="10" border="2" width="160px">
				<tr>

					<td align="center">
						<font style='font-size:15px; font-weight:bold;'>Browse</font> data
					</td>
				</tr>
			</table>
		</td>
		
		<td width="20px">&nbsp;</td>

		<td align="center" onClick="document.forms.main.__target.value='main';document.forms.main.select.value='QTLWizard';document.forms.main.submit();">
			<table  bgcolor="#BBBBBB" cellpadding="10" cellspacing="10" border="2" width="160px">

				<tr>
					<td align="center">
						<font style='font-size:15px; font-weight:bold;'>Import</font> data
					</td>
				</tr>
			</table>
		</td>
		
		<td width="20px">&nbsp;</td>
		
		<td align="center" onClick="document.forms.main.__target.value='main';document.forms.main.select.value='RScripts';document.forms.main.submit();">
			<table  bgcolor="#BBBBBB" cellpadding="10" cellspacing="10" border="2" width="160px">
				<tr>
					<td align="center">
						<font style='font-size:15px; font-weight:bold;'>Add</font> tools
					</td>
				</tr>
			</table>
			&nbsp;
		</td>
	</tr>
	
	<tr>
		<td colspan="7">
			<h4>Visit the <a href="http://www.panaceaproject.eu/UK/">project website</a>!<br><br>
			For manuals and more information, see <a href="http://www.xgap.org/wiki/xQTLDemo">xQTL demo wiki</a></h4>
		</td>
	</tr>
	
	<tr>
		<td colspan="7" width="675">
			<div style="height: 50px">&nbsp;</div>
		
			References:
			<br><br>
			<a href="http://www.ncbi.nlm.nih.gov/pubmed/20214801">XGAP: a uniform and extensible data model and software platform for genotype and phenotype experiments.</a> Swertz MA, Velde KJ, Tesson BM, Scheltema RA, Arends D, Vera G, Alberts R, Dijkstra M, Schofield P, Schughart K, Hancock JM, Smedley D, Wolstencroft K, Goble C, de Brock EO, Jones AR, Parkinson HE; Coordination of Mouse Informatics Resources (CASIMIR); Genotype-To-Phenotype (GEN2PHEN) Consortiums, Jansen RC.
			<br><br>
			<a href="http://www.ncbi.nlm.nih.gov/pubmed/12724300">R/qtl: QTL mapping in experimental crosses.</a> Broman KW, Wu H, Sen S, Churchill GA.
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
						<img src="clusterdemo/logos/wur_logo.png" width="450px" height="100px" alt="logo WUR"><br>
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
</form>
</#macro>
