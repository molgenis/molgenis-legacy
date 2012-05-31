<#macro GenericWizard screen>
<#assign model = screen.myModel>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
			
	<#assign center = "align=\"center\"">
	<#assign courier = "<font face=\"Courier New, Courier, mono, serif\">">	
	<#assign endFont = "</font>">
	
	<#assign greenBg = "<font style=\"background-color: #52D017\">"> <#--successmess: 52D017-->
	<#assign redBg = "<font style=\"background-color: red; color: white; font-weight:bold\">">
		
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

<#if !model.getWhichScreen()?exists || model.getWhichScreen() == "one">
<h2>Data import wizard</h2>
<div style="height: 10px;">&nbsp;</div>

<i>Upload Excel file with your data</i>
<br>
<input type="file" name="upload"/>
<input type="submit" value="Upload" id="upload_excel" onclick="__action.value='upload';return true;"/><br>

<div style="height: 25px;">&nbsp;</div>

<a href="clusterdemo/ExampleExcel.xls"><img src="clusterdemo/excel.gif"/></a><label>Download example Excel file</label>




<#elseif model.getWhichScreen() == "two">

<h1>Import prognosis</h1>

<h3>Sheets</h3>
<table class="listtable">
	<tr class="form_listrow0">
		<td>Sheet name</td>
		<td>Importable?</td>
	</tr>
	
	<#list model.iwep.sheetsImportable?keys as sheet>
	<tr class="form_listrow1">
		<td>${sheet}</td>
		<td><#if model.iwep.sheetsImportable[sheet] == true><p class="successmessage">Yes</p><#else><p class="errormessage">No</p></#if></td>
	</tr>
	</#list>

</table>

<h3>Fields of importable sheets</h3>
<table class="listtable">
	<tr class="form_listrow0">
		<td>Import order</td>
		<td>Sheet name</td>
		<td>Importable fields</td>
		<td>Unknown fields</td>
	</tr>
<#assign count = 1>
<#list model.iwep.importOrder as sheet>
	<tr class="form_listrow1">
		<td>${count}</td>
		<td>${sheet}</td>
		
		<td><#if model.iwep.fieldsImportable[sheet]?size gt 0><#list model.iwep.fieldsImportable[sheet] as field>${greenBg}${field}${endFont}<#if field_has_next>, </#if></#list><#else><p class="errormessage">No importable fields</p></#if></td>
		
		<td><#if model.iwep.fieldsUnknown[sheet]?size gt 0><#list model.iwep.fieldsUnknown[sheet] as field>${redBg}${field}${endFont}<#if field_has_next>, </#if></#list><#else><p class="successmessage">No unknown fields</p></#if></td>
	</tr>
<#assign count = count+1>
</#list>
</table>

<br>
Unknown sheets and fields will be ignored during the import. If the current prognosis is not to your liking, please update your Excel file and upload it again.
<br><br>

<table>
	<tr>
		<td align="left">
			<i>Select new file</i> <input type="submit" value="Previous" onclick="document.forms.${screen.name}.__action.value = 'toScreenOne'; document.forms.${screen.name}.submit();"/>
		</td>
		<td align="right">
			<i>Done?</i> <input type="submit" value="Import" onclick="document.forms.${screen.name}.__action.value = 'import'; document.forms.${screen.name}.submit();"/>
		</td>
	</tr>
</table>


<#elseif model.getWhichScreen() == "three">

<br>
<#if model.importSuccess>
<p class="successmessage">Your import was successful.</p>
<#else>
Your import failed. See the above message for details. Please go back to the first screen and upload a new file.
</#if>
<br><br>
<table>
	<tr>
		<td align="left">
			<i>Select new file</i> <input type="submit" value="Reset" onclick="document.forms.${screen.name}.__action.value = 'toScreenOne'; document.forms.${screen.name}.submit();"/>
		</td>
	</tr>
</table>

<#else>
	<table>
		<tr>
			<td>
				<font style="color: #FFFFFF;"><b>Error: No screen specified.</b></font>
			</td>
		</tr>
		<tr>
			<td align="left">
				<i>Return to first screen</i> <input type="submit" value="Previous" onclick="document.forms.${screen.name}.__action.value = 'toScreenOne'; document.forms.${screen.name}.submit();"/>
			</td>
		</tr>
	</table>
</#if>


<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
