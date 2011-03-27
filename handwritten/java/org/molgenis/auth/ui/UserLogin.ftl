<!--Date:        December 3, 2008
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generate.screen.PluginScreenFTLTemplateGen 3.0.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->

<#macro org_molgenis_auth_ui_UserLogin screen>
<form action="molgenis.do" method="post" enctype="multipart/form-data" name="${screen.name}">
	<input type="hidden" name="__target" value="${screen.name}"" />
	<input type="hidden" name="__action" value="Login"/>
	<input type="hidden" name="op" value=""/>
	<input type="hidden" name="__show" value=""/>
	
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
			${screen.label}
		</div>
		<#--messages-->
		<#list screen.getMessages() as message>
			<#if message.text??>
				<#if message.success>
					<p class="successmessage">${message.text}</p>
				<#else>
					<p class="errormessage">${message.text}</p>
				</#if>
			</#if>
		</#list>
		<div class="screenbody">
			<div class="screenpadding">	
			

				<#assign login = screen.login/>
				<#assign vo    = screen.userLoginVO/>
				
				<#if screen.action == "Register">
				
					<#include "register.ftl">
				
				<#elseif screen.action == "Forgot">
				
					<#include "forgot.ftl">
				
				<#elseif login.authenticated>
				
					<#include "userarea.ftl">
				
				<#else>
				
					<#include "authenticate.ftl">
				
				</#if>

			</div>

			<#--end of your plugin-->	
		</div>
	</div>
</form>
</#macro>
