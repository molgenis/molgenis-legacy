<#macro plugins_cluster_scriptmanager_ScriptManager screen>
<#assign model = screen.model>
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
		
		<#-- Hack to immediatly clear the message so it doesn't "stick". -->
		${screen.clearMessage()}
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->	

<h2>Add new R script</h2>

<table>
	<tr>
		<td rowspan="2">
		<font size=2>
			<textarea class="listtable" name="inputTextArea" rows="23" value="" cols="100"><#if model.uploadTextAreaContent?exists>${model.uploadTextAreaContent}</#if></textarea>
		</font>
		</td>
		<td>
			<table class="listtable">
				<tr class="form_listrow0">
					<td>
						<b>Scripts in the database</b>
					</td>
				</tr>
				<#list model.customScripts as script>
				<tr class="form_listrow1">
					<td>
						${script.name} -define parameters- -edit- -delete-
					</td>
				</tr>
				</#list>
			</table>
		</td>
	</tr>
	<tr>

		<td>
			<input type="text" size="20" name="scriptName" value=""><b>.R</b><br/>
			<input type="submit" value="Check" onclick="document.forms.${screen.name}.__action.value = 'check'; document.forms.${screen.name}.submit();"/>
			<input type="submit" value="Upload" onclick="document.forms.${screen.name}.__action.value = 'uploadTextArea'; document.forms.${screen.name}.submit();"/>
		</td>
	</tr>
</table>



<font color="red">Due to demo constraints, this functionality is not active</font>


<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
