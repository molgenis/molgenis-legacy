<#macro plugin_report_InvestigationReport screen>
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
<#assign style = ' style="border: solid 1px black; padding: 5px; background: white"'>

	<#assign i = screen.selectedInvestigation>
	<h1>Investigation Report: ${i.name}</h1>
	<div style="background: white; min-height: 100px;"><#if i.description?exists>${i.description}<#else>no description provided.</#if></div>
	
	<!--show a table with all values-->
	<!--each row is a map-->
	<h2>Observed values</h2>
	<div style="width: 800px; overflow: scroll">
	<table style="background: white; border-collapse: collapse;">
	<!--header-->
	<tr><td>&nbsp;</td>
	<#list screen.features as feature>
			<td  ${style} <#if feature.description?exists>title="${feature.description}"</#if>><b>${feature.name}</b></td>
		</#list>
	</tr>
	<!--body-->
	<#list screen.targets as target>
	<tr><td ${style} <#if target.description?exists>title="${target.description}"</#if>><b>${target.name}</b></td>
		<#list screen.features as feature>
			<td  ${style}>${screen.getValue(feature.name,target.name)}</td>
		</#list>
	</tr>
	</#list>
	</table>	
	</div>
	<#--end of your plugin-->	
				</div>
			</div>
		</div>
	</form>
</#macro>
