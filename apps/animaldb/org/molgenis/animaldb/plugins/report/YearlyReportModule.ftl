<#macro plugins_report_YearlyReportModule screen>
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

<div id="yearselect" class="row">
<label for="year">Year:</label>
<select name="year" id="year" class="selectbox">
	<#list screen.getLastYearsList() as y>
		<option value="${y?string.computer}" <#if screen.year??><#if screen.year==y>selected="selected"</#if></#if> >${y?string.computer}</option>
	</#list>
</select>
</div>

<div id="formselect" class="row">
<label for="form">Form:</label>
<select name="form" id="form" class="selectbox">
	<option value="4A" <#if screen.form??><#if screen.form=="4A">selected="selected"</#if></#if> >4A</option>
	<option value="4B" <#if screen.form??><#if screen.form=="4B">selected="selected"</#if></#if> >4B</option>
	<option value="4C" <#if screen.form??><#if screen.form=="4C">selected="selected"</#if></#if> >4C</option>
	<option value="5" <#if screen.form??><#if screen.form=="5">selected="selected"</#if></#if> >5</option>
</select>
</div>

<div class='row'>
	<input type='submit' id='generate' class='addbutton' value='Generate' onclick="__action.value='generateYearlyReport'" />
</div>

<#if screen.report?exists>
${screen.report}
</#if>

<#--<input name="myinput" value="${screen.getMyValue()}">
<input type="submit" value="Change name" onclick="__action.value='do_myaction';return true;"/-->
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
