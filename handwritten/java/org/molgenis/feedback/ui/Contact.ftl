<#macro org_molgenis_feedback_ui_Contact screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}">
	
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
<#assign form = screen.form>
<p>
If you have any comments, questions or suggestions to improve The COL7A1 mutation database, please do not hesitate to contact us. Please enter your name, a valid email address and your message and press "submit". We will reply shortly.
</p>
<table>
${form.__action}
${form.__target}
<tr><td>Name *</td><td>${form.name}</td></tr>
<tr><td>Email *</td><td>${form.email}</td></tr>
<tr><td>Comments *</td><td>${form.comments}</td></tr>
<tr><td colspan="2">${form.send}</td></tr>
</table>
			</div>
		</div>
	</div>
</form>
</#macro>
