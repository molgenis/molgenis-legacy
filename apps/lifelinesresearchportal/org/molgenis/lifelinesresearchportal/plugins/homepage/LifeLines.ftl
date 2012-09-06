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
		
<#--begin your plugin-->
<div align="center">
<table width="700px">
	<tr>
		<td align="center" colspan="7" >
			<div align="left">
			<#if screen.userIsAdminAndDatabaseIsEmpty == true>
				<table bgcolor="white" border="3" bordercolor="red">
					<tr>
						<td>
							<br><i><font color="red">You are logged in as admin, and the database does not contain any investigations or other users. Automated setup is now possible. Database additions will disable this notice.</font></i><br><br>
							<input type="file" name="llrptar"/>
							<input type="submit" value="Load" id="loadExamples" onclick="document.forms.${screen.name}.__action.value = 'setPathAndLoad'; document.forms.${screen.name}.submit();"/>
							<br><br>
						</td>
					</tr>
				</table>
			</#if>
			<!-- <font style='font-size:24px; font-weight:bold;'>xQTL workbench</font>-->
			<br><br>
			<h3>Welcome to the LifeLines Research Platform<#if screen.studyInfo??> for study ${screen.studyInfo}</#if></h3>
			<br /><br />
			This Research Platform is based on xQTL workbench, a platform for the storage and analysis of geno- and phenotypic data<br />
			For manuals and more information, see <a href="http://www.xgap.org/wiki/xQTL">xQTL workbench wiki</a>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="7" width="675">
			<div style="height: 20px">&nbsp;</div>
			<i>References:</i>
			<br><br>
			<a href="http://www.ncbi.nlm.nih.gov/pubmed/20214801" target="_blank">XGAP: a uniform and extensible data model and software platform for genotype and phenotype experiments.</a> Swertz MA, Velde KJ, Tesson BM, Scheltema RA, Arends D, Vera G, Alberts R, Dijkstra M, Schofield P, Schughart K, Hancock JM, Smedley D, Wolstencroft K, Goble C, de Brock EO, Jones AR, Parkinson HE; Coordination of Mouse Informatics Resources (CASIMIR); Genotype-To-Phenotype (GEN2PHEN) Consortiums, Jansen RC.
			<br><br>
			<a href="http://www.ncbi.nlm.nih.gov/pubmed/15059831" target="_blank">Molecular Genetics Information System (MOLGENIS): alternatives in developing local experimental genomics databases.</a> Swertz MA, De Brock EO, Van Hijum SA, De Jong A, Buist G, Baerends RJ, Kok J, Kuipers OP, Jansen RC.
			</div>
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
						<a target="_blank" href="http://www.rug.nl/target/index"><img src="clusterdemo/logos/targetlogo.jpg" width="100px" height="55px" alt="logo Target"></a>
						<a target="_blank" href="http://tcc.umcg.nl/"><img src="clusterdemo/logos/tcc_logo.gif" width="100px" height="92px" alt="logo TCC"></a>
					</td>
				</tr>
			</table>
			<font size=1>(c) 2009-2012 GCC - TCC - Target</font>
		</td>
	</tr>
</table>
</div>


<#--end of your plugin-->	
</div>
</form>
</#macro>
