<#macro plugins_molgenisfile_plink_PlinkFileManager screen>
<#if screen.myModel?exists>
	<#assign modelExists = true>
	<#assign model = screen.myModel>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>

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


<table cellpadding="10"><tr>
<td><input name="selectUpload" type="radio" onclick="display('show', 'bin');display('hide', 'csv');display('hide', 'other');" <#if model.uploadMode == 'bin'>checked</#if>>Binary (BIM + FAM + BED)</td>
<td><input name="selectUpload" type="radio" onclick="display('show', 'csv');display('hide', 'bin');display('hide', 'other');" <#if model.uploadMode == 'csv'>checked</#if>>CSV (MAP + PED)</td>
<td><input name="selectUpload" type="radio" onclick="display('show', 'other');display('hide', 'bin');display('hide', 'csv');" <#if model.uploadMode == 'other'>checked</#if>>Other..</td>
</tr></table>

<br>
<i>Into investigation:</i>
<select name = "invSelect">
<#if model.investigations?exists>
	<#list model.investigations as i>
		<option value="${i.id?c}" <#if model.selectedInv?exists && model.selectedInv == i.id>SELECTED</#if>>${i.name}</option>
	</#list>
</#if>
</select>
<br>

<div id="bin" <#if model.uploadMode != 'bin'>style="display:none"</#if>>
	<table cellpadding="5">
		<tr>
			<td>
				BIM file:
			</td>
			<td>
				<input type="file" name="bim_file"/>
			</td>
		</tr>
		<tr>
			<td>
				FAM file:
			</td>
			<td>
				<input type="file" name="fam_file"/>
			</td>
		</tr>
		<tr>
			<td>
				BED file:
			</td>
			<td>
				<input type="file" name="bed_file"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				Please name this set: <input type="text" name="binFileSetName" />
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="submit" value="Upload" onclick="__action.value='uploadBinPlink';return true;"/>
			</td>
		</tr>
	</table>
</div>

<div id="csv" <#if model.uploadMode != 'csv'>style="display:none"</#if>>
	<table cellpadding="5">
		<tr>
			<td>
				MAP file:
			</td>
			<td>
				<input type="file" name="map_file"/>
			</td>
		</tr>
		<tr>
			<td>
				PED file:
			</td>
			<td>
				<input type="file" name="ped_file"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				Please name this set: <input type="text" name="csvFileSetName" />
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="submit" value="Upload" onclick="__action.value='uploadCsvPlink';return true;"/>
			</td>
		</tr>
	</table>
</div>

<div id="other" <#if model.uploadMode != 'other'>style="display:none"</#if>>
	<table cellpadding="5">
		<tr>
			<td>
				Other file:
			</td>
			<td>
				<input type="file" name="other_file"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<input type="submit" value="Upload" onclick="__action.value='uploadOtherPlink';return true;"/>
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
