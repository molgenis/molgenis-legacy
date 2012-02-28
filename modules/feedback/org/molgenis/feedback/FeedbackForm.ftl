<#--Date:        November 11, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenFTLTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro org_molgenis_feedback_FeedbackForm screen>

<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
	<#--optional: mechanism to show messages-->
	<#list screen.getMessages() as message>
		<#if message.success>
	<p class="successmessage">${message.text}</p>
		<#else>
	<p class="errormessage">${message.text}</p>
		</#if>
	</#list>

	<div id="buttonDiv" style="float:right; display:inline;">
		<span style="font-size: 60%; font-family: arial, sans-serif; font-style: italic;" >provide feedback:</span> 
		<a href=# onclick="toggle('feedbackDiv');"><img src="res/img/feedback_cropped.png" align="middle" height="25px" alt="Give feedback or comments on the active page." title="Give feedback or comments on the active page."></a>
		<#--input type="button" value="Give feedback" onclick="toggle('feedbackDiv');" /-->
	</div>

	<div id="feedbackDiv" style="background:#FFFFFF;z-index:1000; padding-right:75px; font-size: 100%; font-family: arial, sans-serif; font-style: italic; display:none; ">
		<fieldset><legend style="font-size: 75%; z-index:1000; font-family: arial, sans-serif; font-style: italic;">Share your feedback on the ${screen.getActivePlugin()} screen with us:</legend>
		<label for="feedback" style="vertical-align: middle">Feedback:</label>
		<br />
		<textarea name="feedback" id="feedback" rows="3" cols="70"></textarea>
		<br />
		<label for="name" style="vertical-align: middle">Name:</label>
		<br />
		<input id="name" name="name" type="text" />
		<input type="hidden" name="plugin" id="plugin" value="${screen.getActivePlugin()}" />
		<br /><br />
		<input type="submit" value="Send" onclick="display('hide', 'feedbackDiv'); __action.value='sendFeedback'" />
		<input type="reset" value="Cancel" onclick="display('hide', 'feedbackDiv'); __action.value='resetFeedbackForm'" />
		</fieldset>
	</div>
</form>

</#macro>

