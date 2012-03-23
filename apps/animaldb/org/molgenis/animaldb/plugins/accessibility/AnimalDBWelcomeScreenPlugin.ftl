<!--Date:        October 28, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenFTLTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro org_molgenis_animaldb_plugins_accessibility_AnimalDBWelcomeScreenPlugin screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
	
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
	
<h1>Welcome to AnimalDB!</h1>

<!--[if IE]>
<div style="border: 2px solid red; padding: 4px">
<p>You are using <strong>Internet Explorer</strong>, which is at this moment <strong>NOT SUPPORTED</strong> by AnimalDB! Please use Mozilla Firefox, Google Chrome or another browser instead.</p>
<p>U gebruikt de <strong>Internet Explorer</strong>-browser, die op dit moment <strong>NIET ONDERSTEUND</strong> wordt door AnimalDB! Gebruik aub Mozilla Firefox, Google Chrome of een andere browser.</p>
</div>
<![endif]-->

<#list screen.news as newsItem>
	<h3>${newsItem.getTitle()}</h3>
	<p>${newsItem.getText()}</p>
	<p><em>Posted on <#if newsItem.getDate()??>${newsItem.getDate()}</#if> by ${newsItem.getAuthor()}</em></p>
	<br />
</#list>

<#--end of your plugin-->
			</div>
		</div>
	</div>
</form>
</#macro>
