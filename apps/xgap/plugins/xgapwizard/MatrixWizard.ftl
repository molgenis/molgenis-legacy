<#macro MatrixWizard screen>
<#assign model = screen.myModel>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
	
	<input type="hidden" name="__dataId" />
		
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

<h2>Tag data</h2>
This screen provides an overview of all data matrices stored in your database.
The status of the storage is checked and displayed. You can import matrices here if there is no
associated storage for Data (matrix) definition. These tags are taken from the 'Add analysis' menu: Datasets and Datanames. In addition, you can tag matrices and view some
examples.
<br><br>
<a target="_blank" href="http://www.molgenis.org/wiki/xQTLBioinformaticianTag">Learn more about tagging..</a>
<br><br>
<#if model.getShowVerified() == true>
	<input type="submit" id="hide_verified" value="Hide verified in list" onclick="__action.value='hideVerified';return true;"/>
<#else>
	<input type="submit" id="show_verified" value="Show verified in list" onclick="__action.value='showVerified';return true;"/>
</#if>

<br><br>

<#-- Deduce how many matrices will be shown on screen-->
<#assign shownMatrices = 0>
<#list model.dataInfo as di>
	<#assign show = true>
	<#if model.getShowVerified() == false>
		<#if di.existingDataSource != 'null'>
			<#assign show = false>
		</#if>
	</#if>
	<#if show == true>
		<#assign shownMatrices = shownMatrices+1>
	</#if>
</#list>

<#if shownMatrices == 0>
	<table class="listtable">
		<tr class="form_listrow0">
			<td>
				<b>Nothing to display</b>
			</td>
		</tr>
	</table>
<#else>
	<table class="listtable">
		<tr class="form_listrow0">
			<td>
				<b>Investigation / Data</b>
			</td>
			<#-->td>
				<b>Properties</b>
			</td-->
			<td>
				<b>Tagged as..</b>
			</td>
			<td>
				<b>Verified as..</b>
			</td>
		</tr>
		
		<#list model.dataInfo as di>
		
			<#assign show = true>
			<#if model.getShowVerified() == false>
				<#if di.existingDataSource != 'null'>
					<#assign show = false>
				</#if>
			</#if>
			
			<#if show == true>
			<tr class="form_listrow1">
				<td>
					${di.data.investigation_name} / ${di.data.name}
				</td>
				<#--td>
					rows = ${di.data.rowtype}<br>columns = ${di.data.coltype}<br>
					values = ${di.data.valuetype}<br>source = ${di.data.source}<br><br>
				</td-->
				<td>
					<#if di.tags?size == 0>
						<b>Matrix is not tagged</b><br>
					<#else>
						Matrix currently tagged as:<br>
						<#list di.tags as tag>
						<i>${tag}</i><br>
						</#list>
					</#if>
					
					<br>
					Tag this matrix as:<br>
					<select name="tagging_${di.data.id?c}">
					<#list model.tagList as tag>
						<option value="${tag}">${tag}</option>
					</#list>
					</select>
					
					<input type="submit" value="Tag" id="tagdata_${di.data.id?c}" onclick="__dataId.value='${di.data.id?c}';__action.value='tag';return true;"/>
					<br><br>
				</td>
				<td>
					<#if di.existingDataSource == 'null'>
						<p class="errormessage">${di.existingDataSource}</p>
							Please select your data matrix file and proceed with upload into this source.<br>
							<input type="file" name="upload${di.data.id?c}"/>
							<input type="submit" value="Upload" onclick="__dataId.value='${di.data.id?c}';__action.value='upload';return true;"/><br>
							<br>
							Alternatively, use this textarea to input your data.<br>
							<textarea name="inputTextArea" rows="2" cols="30"><#if model.uploadTextAreaContent?exists>${model.uploadTextAreaContent}</#if></textarea>
							<input type="submit" value="Upload" onclick="__dataId.value='${di.data.id?c}';__action.value='uploadTextArea';return true;"/><br>
		
					<#else>
						<p class="successmessage">${di.existingDataSource}</p>
					</#if>
				</td>
			</tr>
			
			</#if>
		
		</#list>
	</table>
</#if>

<div style="height: 25px;">&nbsp;</div>

<table>
	<tr>
		<td>
			<a href="clusterdemo/genotypes.txt"><img src="clusterdemo/file_txt.png"/></a><label>Download example matrix file #1</label>
		</td>
	</tr>
	<tr>
		<td>
			<a href="clusterdemo/metaboliteexpression.txt"><img src="clusterdemo/file_txt.png"/></a><label>Download example matrix file #2</label>
		</td>
	</tr>
</table>
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
