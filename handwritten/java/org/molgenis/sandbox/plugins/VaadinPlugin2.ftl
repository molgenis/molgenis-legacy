<#macro org_molgenis_sandbox_plugins_VaadinPlugin2 screen>
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
				<#--<input name="myinput" value="${screen.getMyValue()}">
				<input type="submit" value="Change name" onclick="__action.value='do_myaction';return true;"/-->
				
				<iframe tabIndex="-1" id="__gwt_historyFrame" style="position:absolute;width:0;height:0;border:0;overflow:hidden;" src="javascript:false"></iframe>
				<div id="vaadin_test" style="border: solid thin red"/>
				<label> <#if screen.getStatus()?exists>${screen.getStatus()} </#if>  </label>	
			</div>
		</div>
	</div>
</form>
</#macro>
