<#macro plugins_system_appcustomizer_AppCustomizer screen>
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

<h2>AppCustomizer</h2>

	Upload an image (PNG preferred) to set as the new banner, overwriting the default one.<br>(<a href="clusterdemo/bg/xqtl_default_banner.png">Click here</a> to get the current banner, you might want to keep it.)<br>
	<input type="file" name="uploadBannerFile"/>
	<input type="submit" value="Upload" onclick="__action.value='uploadBanner';return true;"/><br>
	<br>
	
	Upload a CSS file to set as the new color theme, overwriting the default one.<br>(<a href="clusterdemo/colors.css">Click here</a> to get the current CSS, you might want to use it for customizing, and keeping it.)<br>
	<input type="file" name="uploadCssFile"/>
	<input type="submit" value="Upload" onclick="__action.value='uploadCss';return true;"/><br>
	
	<br>
	Toggle the display of the buttons on the home screen. (Login with the default passwords as biologist/bioinformatician and subsequently 'Browse data', 'Upload data', etc)
	<br>
	<#if screen.hideLoginButtons>
		<input type="submit" value="Show login home screen buttons" id="showHomeButtons" onclick="document.forms.${screen.name}.__action.value = 'showHomeButtons'; document.forms.${screen.name}.submit();"/>
	<#else>
		<input type="submit" value="Hide login home screen buttons" id="hideHomeButtons" onclick="document.forms.${screen.name}.__action.value = 'hideHomeButtons'; document.forms.${screen.name}.submit();"/>
	</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
