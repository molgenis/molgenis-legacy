<#macro org_molgenis_mutation_ui_mycol7a1_MyCOL7A1 screen>
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

<#assign vo = screen.myCOL7A1VO>

<#assign pager = vo.pager>
<h4><a name="result">Submitted patients (${vo.pager.count})</a></h4>
<#include "../../../../../plugin/search/patients.ftl">
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
