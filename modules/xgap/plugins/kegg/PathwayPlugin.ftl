<#macro plugins_kegg_PathwayPlugin screen>
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

TODO!<br>
<br>
Example of such a system:<br>
<br>
<iframe src="http://gbic.target.rug.nl:8080/kave/molgenis.do" width="100%" height="800">
  <p>Your browser does not support iframes.</p>
</iframe>


	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
