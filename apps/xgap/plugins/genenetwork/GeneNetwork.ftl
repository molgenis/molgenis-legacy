<#macro GeneNetwork screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
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

<#if screen.myModel?exists>
	<#assign modelExists = true>
	<#assign model = screen.myModel>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>

<table><tr><td>

<label>Request from GeneNetwork:</label><br>
<br>
probeset: <input name="probeset" value="${model.probeset}"><br>
db: <input name="db" value="${model.db}"><br>
probe: <input name="probe" value="${model.probe}"><br>
format: <input name="format" value="${model.format}"><br>
<br><br>
<input type="submit" value="Download" onclick="__action.value='postInput';return true;"/><br>
<br>

Result:

<#list model.result as res>
${res}<br>
</#list>

</td>
<td>

<label>Push to GeneNetwork:</label><br>
<br>
<input type="file" name="theFile" /><input type="submit" value="Submit" onclick="__action.value='submitFile';return true;"><br>

<br>
Response:<br>

${model.uploadResponse}


</td>
</tr>
</table>


<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
