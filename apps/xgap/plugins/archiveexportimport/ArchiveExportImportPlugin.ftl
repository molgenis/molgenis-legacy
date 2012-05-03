<!--Date:        May 13, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenFTLTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro plugins_archiveexportimport_ArchiveExportImportPlugin screen>
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

	<#assign bgColor = "#ffffff">
	<#assign borderStyle = "solid">
	<#assign borderWidthSpacing = "1px">
	<#assign picSize = "15">
	<#assign padding = "5px">
	
	<#assign styleRed = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #800000; background-color: ${bgColor};\"">
	<#assign styleBlue = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #0000FF; background-color: ${bgColor};\"">
	<#assign styleWhite = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #FFFFFF; background-color: ${bgColor};\"">
	<#assign styleGreen = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #00FF00; background-color: ${bgColor};\"">
	<#assign center = "align=\"center\"">


	
	<#--table>
		<tr>
			<td ${center} ${styleRed} colspan=2>
				<b>Status:</b>
				<#if screen.getMessages()?size gt 0>
					<#list screen.getMessages() as message>
						<#if message.success>
							<p class="successmessage">${message.text}</p>
						<#else>
							<p class="errormessage">${message.text}</p>
						</#if>
					</#list>
				<#else>
					Nothing to report.
				</#if>
			</td>
		</tr>
	</table-->

	<br>

	<table>
		<tr>
			<td ${center} ${styleRed} colspan=2>
				<label><font size="4">XGAP Archive Export</font></label>
			</td>
		</tr>
		<tr>
			<td ${center} ${styleBlue}>
				Here you can export your XGAP data into a compressed archive file.<br>
				A link will appear when your file is ready for download.
				<br><br>
				<table>
					<tr>
						<td ${styleWhite}>
							<select name="selectInvestigation">
								<option <#if screen.selectedInvestigation?exists && screen.selectedInvestigation == "__download_every_investigation_1256037232589246000">SELECTED</#if> value="__download_every_investigation_1256037232589246000">Every investigation</option>
								<#list screen.investigationList as inv>
									<option <#if screen.selectedInvestigation?exists && screen.selectedInvestigation == inv.name>SELECTED</#if> value="${inv.name}">${inv.name}</option>
								</#list>
							</select>
						</td>
						<td ${styleWhite}>
							<input type="submit" value="Export" onclick="__action.value='exportAll';return true;"/>
						</td>
					</tr>
					<tr>
						<td ${styleWhite}>
							<b>Internal format</b><br>
							<input type="radio" name="format" value="csv" <#if screen.selectedFormat?exists><#if screen.selectedFormat=="csv">checked</#if><#else>checked</#if>> CSV
							<input type="radio" name="format" value="excel" <#if screen.selectedFormat?exists && screen.selectedFormat=="excel">checked</#if>> Excel
						</td>
						<td ${styleWhite}>
							<font size="1">
							<b>Csv</b> scatters annotations across many files,<br>but is suitable for large data.<br><br>
							<b>Excel</b> organizes all annotations in a single<br>file, but is unsuitable for more than a few thousand records.
							</font>
						</td>
					</tr>
				</table>
				<br>
			</td>
			<td ${center} ${styleBlue}>
				<b>Download:</b><br><br> 
					<#if screen.tmpFileName?exists>
						<a href="file/${screen.tmpFileName}">XGAP data export</a> (*.tar.gz format)<br><br>
					<#else>
						Not available.
					</#if>
			</td>
		</tr>
	</table>
		
	<br>
	
	<table>
		<tr>
			<td ${center} ${styleRed} colspan=2>
				<label><font size="4">XGAP Archive Import</font></label>
			</td>
		</tr>
		<tr>
			<td ${center} ${styleBlue}>
				Here you can use an archive file to import data into an empty XGAP database.<br>
				A status message will tell you if the import was successful or not.
				<br><br>
				<table>
					<tr>
						<td ${styleWhite}>
								Archive (*.tar.gz format):
								<input type="file" name="importArchive">
						</td>
						<td ${styleWhite}>
							<input type="submit" value="Import" onclick="__action.value='importAll';return true;"/>
						</td>
					</tr>
				</table>
				<br>
			</td>
			<#--td ${center} ${styleBlue}></td-->
		</tr>
	</table>
	
			</div>
		</div>
	</div>
</form>
</#macro>
