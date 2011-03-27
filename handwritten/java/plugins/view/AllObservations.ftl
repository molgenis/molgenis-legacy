<#macro plugin_view_AllObservations screen>
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

<!--show a table with all values-->
<!--each row is a map-->
<table style="background: white; border-collapse: collapse;">
<!--header-->
<tr><td>&nbsp;</td>
<#list screen.features as feature>
		<td  style="border: solid thin black"><b>${feature}</b></td>
	</#list>
</tr>
<!--body-->
<#list screen.targets as target>
<tr><td style="border: solid thin black"><b>${target}</b></td>
	<#list screen.features as feature>
		<td  style="border: solid thin black">${screen.getValue(feature,target)}</td>
	</#list>
</tr>
</#list>
</table>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
