<#macro ScreenCommand command>	
<#--<@molgenis_header />-->
<#if command.getInputs()?exists >
<#--<body>-->
<div class="formscreen">
	<form action="" method="post" enctype="multipart/form-data" name="molgenis_popup">
	<p class="form_header">${command.label}</p>
	<#if command.messages?exists><#list command.getMessages() as message>
		<#if message.success>
	<p class="successmessage">${message.text}</p>
		<#else>
	<p class="errormessage">${message.text}</p>
		</#if>
	</#list></#if>	
	<table>
			<input type="hidden" name="__target" value="${command.screen.name}"/>
			<input type="hidden" name="__action" value="${command.name}"/> 
			<input type="hidden" name="__show"/> 
	<#assign requiredcount = 0 />
	<#assign required = "" />
	<#list command.getInputs() as input>
		<#if !input.isHidden()>
			<tr>
				<td title="${input.description}"><label>${input.label}<#if !input.isNillable()  && !input.isReadonly()> *</#if></label></td>
				<td>${input.toHtml()}</td>
			</tr>
		<#else>
			${input.toHtml()}
		</#if>		
		<#if !input.isNillable() && !input.isHidden() && !input.isReadonly()>
			<#if requiredcount &gt; 0><#assign required = required + "," /></#if>
			<#assign required = required + "document.forms.molgenis_popup."+ input.id />
			<#assign requiredcount = requiredcount + 1 />
		</#if>
	</#list>
	
<script language="JavaScript" type="text/javascript">
var molgenis_required = new Array(${required});
</script>
	</table>
	<p align="right">
<#list command.getActions() as input>
	${input.toHtml()}
</#list>
	</p>
	</form>
<#--</body>-->
<#else>
	ERROR no command dialog to bee seen
</#if>
</div>
<#--</body>-->
</#macro>