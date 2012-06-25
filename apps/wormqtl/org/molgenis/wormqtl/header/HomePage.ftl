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
			<td colspan="4">
				<div style="height: 10px">&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td colspan="4" align="center">
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
			<td colspan="4">
				<div style="height: 10px">&nbsp;</div>
			</td>
		</tr>
		<tr>
			<td colspan="4" align="center">
				<div align="left">
					<h2>WormQTL - Public archive and analysis web portal for natural variation data in Caenorhabditis elegans</h2>
					Over the past decade increased efforts have been made to explore the model metazoan Caenorhabditis elegans as a platform for molecular quantitative genetics and the systems biology of natural variation. These efforts have resulted in an accumulation of a huge amount of phenotypic and genotypic data across different developmental worm stages and environments in recombinant inbred populations. Next to C. elegans, C. briggsae is an emerging model organism that allows evolutionary comparisons with C. elegans and quantitative genetic exploration of its own unique biological attributes. In addition, a wealth of similar high-throughput data has been produced on hundreds of different C. elegans wild isolates.
					<br><br>
					This rapid increase in data calls for an easily accessible database allowing for comparative analysis and meta-analysis within and across species. Here we present WormQTL, a public portal for the management of all data on natural variation in Caenorhabditis spp. and integrated development of suitable analysis tools. The web server provides a rich set of analysis tools for genotype-phenotype mapping based on R/qtl. Users can upload and share new R scripts as 'plugin' for the colleagues in the community to use directly. New data can be uploaded and downloaded using XGAP, an extensible text format for genotype and phenotypes. All data  and tools can be accessed via a public web user interface as well as programming interfaces to R, REST, and SOAP web services. All software is free for download as MOLGENIS 'app'. WormQTL is freely accessible without registration and is hosted on a large computational cluster enabling high throughput analyses to all at http://www.wormqtl.org. 
					<br><br>
					WormQTL is an online scalable system for QTL exploration to service the worm community. WormQTL provides many publicly available datasets and welcomes submissions from other worm researchers.
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="4">
				<div style="height: 20px">&nbsp;</div>
			</td>
		</tr>
		
		<tr>
			<td align="center">
				<a href="?select=QtlFinderPublic2"><img height="100" width="100" src="clusterdemo/wormqtl/qtl_plot_button.png" /><h3>Find and plot QTLs</h3></a>
			</td>
			<td align="center">
				<a href="?select=GenomeBrowser"><img height="100" width="100" src="clusterdemo/wormqtl/qtl_gb_button.png" /><h3>Explore QTLs genomewide</h3></a>
			</td>
			<td align="center">
				<a href="?select=Investigations"><img height="100" width="100" src="clusterdemo/wormqtl/qtl_data_button.png" /><h3>Browse the raw data</h3></a>
			</td>
			<td align="center">
				<a href="?select=Help"><img height="100" width="100" src="res/img/designgg/helpicon.gif" /><h3>Help</h3></a>
			</td>
		</tr>
		
		<tr>
			<td colspan="4">
				<div style="height: 20px">&nbsp;</div>
			</td>
		</tr>
	
	</table>
</div>


</div>
</form>
</#macro>
