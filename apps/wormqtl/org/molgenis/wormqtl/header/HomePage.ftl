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
					<h2>WormQTL Ð Public archive and analysis web portal for natural variation data in Caenorhabditis spp.</h2>
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
				<a href="?select=QtlFinderPublic2"><img height="100" width="100" src="clusterdemo/wormqtl/qtl_plot_button.png" /><h3>Find QTLs</h3></a>
			</td>
			<td align="center">
				<a href="?select=GenomeBrowser"><img height="100" width="100" src="clusterdemo/wormqtl/qtl_gb_button.png" /><h3>Genome browser</h3></a>
			</td>
			<td align="center">
				<a href="?select=Investigations"><img height="100" width="100" src="clusterdemo/wormqtl/qtl_data_button.png" /><h3>Browse data</h3></a>
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
		
		<tr>
			<td colspan="4" align="center">
				<div align="left">
				<h3>What can you do?</h3>
					<ul>
						<li>
							<h4>I want to search (e)QTLs for my trait or gene</h4>
							<ol>
								<li>Go to <a href="?select=QtlFinderPublic2">Find QTLs</a></li>
								<li>Type the name or identifier of your trait or gene and press <i>Search</i></li>
								<li>Put any relevant hits in the shopping cart</li>
								<li>Click <i>Plot cart</i> now and explore the results</li>
							</ol>
						</li>
						<li>
							<h4>I want to know which genes have a QTL on my favourite position</h4>
							<ol>
								<li>Go to <a href="?select=GenomeBrowser">Genome browser</a></li>
								<li>Add tracks from experiments of interest</li>
								<li>Navigate to your favourite location (tip: use <i>open in new window</i>)</li>
								<li>Collect significant probe identifiers from that region</li>
								<li>Use the identifiers to e.g. search with <a href="?select=QtlFinderPublic2">Find QTLs</a></li>
							</ol>
						</li>
						<li>
							<h4>I want to compare the QTLs of two and more traits or genes</h4>
							<ol>
								<li>Go to <a href="?select=QtlFinderPublic2">Find QTLs</a></li>
								<li>Type the name or identifier of your trait or gene and press <i>Search</i></li>
								<li>Put any relevant matching hits in the shopping cart</li>
								<li>Repeat from step 2 to add more hits, up to 500</li>
								<li>Press <i>Plot cart now</i> and explore the results</li>
							</ol>
						</li>
						<li>
							<h4>I want to know everything about my trait or gene</h4>
							<ol>
								<li>Go to <a href="?select=QtlFinderPublic2">Find QTLs</a></li>
								<li>Type the name or identifier of your trait or gene and press <i>Search</i></li>
								<li>Click the bold hyperlink of the hit (e.g. <b>AGIUSA9288 / gst-30</b>)</li>
								<li>After a while, you are presented with an aggregate of all WormQTL data for this hit</li>
							</ol>
						</li>
					</ul>
				</div>
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
