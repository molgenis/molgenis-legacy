<#macro AnimalDbFileViewer screen>
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

<#if model.hasFile == true>
	<#--iframe width="750px" height="600px" src="downloadfile?name=${model.molgenisFile.name}"></iframe-->
	<h3>${model.molgenisFile.name}.${model.molgenisFile.extension}</h3>
	<a target="_blank" href="viewfile?name=${model.molgenisFile.name}">View this file</a><br><br>
	<a target="_blank" href="downloadfile?name=${model.molgenisFile.name}">Download this file</a><br><br>
	Size: ${model.fileSize} bytes<br>
<#else>
	No file found. Please upload it here.<br>
	<input type="file" name="upload"/>
	<input type="submit" value="Upload" id="upload_file" onclick="__action.value='upload';return true;"/><br><br>
	Alternatively, use this textarea to upload text data.<br>
	<textarea name="inputTextArea" rows="15" cols="50"><#if model.uploadTextAreaContent?exists>${model.uploadTextAreaContent}</#if></textarea>
	<input type="submit" id="uploadTextArea" value="Upload" onclick="__action.value='uploadTextArea';return true;"/><br>
</#if>

</center>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
