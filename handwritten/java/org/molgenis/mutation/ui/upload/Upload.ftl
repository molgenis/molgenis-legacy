<#macro org_molgenis_mutation_ui_upload_Upload screen>
<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"/>
	<input type="hidden" name="__action"/>

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
<#if screen.login.isAuthenticated()>

<#assign stringUtils = screen.stringUtils>

<#if screen.action?ends_with("Batch")>

<#include "uploadBatch.ftl">

<#elseif screen.action?ends_with("Patient")>

<#include "uploadPatient.ftl">

<#elseif screen.action?ends_with("Mutation")>

<#include "uploadMutation.ftl">

</#if>
<#--
<p>
We aim to implement a fully automated submit wizard for single patients and novel mutations in the near future.
</p>
-->

<#else>

<p>
Use this page to submit any unpublished data. Before you can submit any data you need to <a href="molgenis.do?__target=View&select=UserLogin">login</a>. If you do not have an account yet, please <a href="molgenis.do?__target=UserLogin&select=UserLogin&__action=Register">register</a> to create an account.
</p>

</#if>
			</div>
		</div>
	</div>
</form>
</#macro>
