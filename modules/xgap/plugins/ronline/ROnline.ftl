<#macro ROnline screen>
<#assign model = screen.myModel>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
	<#assign bgColor = "#ffffff">
	<#assign borderStyle = "solid">
	<#assign borderWidthSpacing = "1px">
	<#assign picSize = "15">
	<#assign padding = "5px">
	
	<#assign style1 = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #800000; background-color: ${bgColor};\"">
	<#assign style2 = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #0000FF; background-color: ${bgColor};\"">
	<#assign styleWhite = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #FFFFFF; background-color: ${bgColor};\"">
	<#assign styleGreen = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #00FF00; background-color: ${bgColor};\"">
	<#assign styleBlack = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #000000; background-color: ${bgColor};\"">
	
	
	<#assign green = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #00FF00; background-color: #00FF00;\"">
	<#assign orange = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #FF8040; background-color: #FF8040;\"">
	<#assign red = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #FF0000; background-color: #FF0000;\"">
	
	<#assign center = "align=\"center\"">
	<#assign courier = "<font face=\"Courier New, Courier, mono, serif\">">	
	<#assign endFont = "</font>">
	
	<#assign greenBg = "<font style=\"background-color: #52D017\">"> <#--successmess: 52D017-->
	<#assign redBg = "<font style=\"background-color: red; color: white; font-weight:bold\">">
		
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

ROnline<br><br>
Your R session is kept for ${model.timeOut} seconds after the last entered command.<br>
Type single commands in the main view and press Enter to submit.<br>
Use the Multiline input with clicking Execute to submit many commands at once.<br>
(FIXME: only works if the number of input and output lines are equal)

<br><br>

<#-- no point? -->
<#--input type="submit" style="display: none;" value="Reset" onclick="document.forms.${screen.name}.__action.value = 'reset'; document.forms.${screen.name}.submit();"/-->


<div style="width: 800px; height: 300px; overflow-y: auto; vertical-align:bottom; background-color: #FFFFFF; border: 2px #C0C0C0 solid;">
	<font style="font-size:medium; font-family: Courier, 'Courier New', monospace">
	<#if model.results?exists><#list model.results as res>
		<#if res?starts_with("> ")><font color="blue">${res}</font><#else>${res}</#if><#if res_has_next><br></#if>
	</#list></#if></font>
	<br><font style="color:red;">&gt;</font><input type="text" size="80" style="border: 0px; color:red; display:inline; font-size:medium; font-family: Courier, 'Courier New', monospace" id="inputBoxROnline" name="executeThis" value="" onFocus="this.select();" onkeypress="if(window.event.keyCode==13){document.forms.${screen.name}.__action.value = 'execute';}">
</div>

<input type="submit" style="display: none;" value="Execute" onclick="document.forms.${screen.name}.__action.value = 'execute'; document.forms.${screen.name}.submit();"/>


<br>

Multiline commands:<br>

<textarea style="color:red; font-size:medium; font-family: Courier, 'Courier New', monospace" name="executeMulti"rows="2" cols="70"></textarea>
	<br>
<!--input type="image" src="res/img/delete.png" value="Execute" onclick="document.forms.${screen.name}.__action.value = 'execute';"/-->


<input type="submit" value="Execute" onclick="document.forms.${screen.name}.__action.value = 'executeMulti'; document.forms.${screen.name}.submit();"/>



<br><br>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
