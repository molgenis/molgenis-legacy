<#macro plugin_report_InvestigationOverview screen>
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
<#if screen.state = "Select_Investigation">
	<#assign i = screen.selectedInvestigation>
	<h1>Investigation Report: ${i.name}</h1>
	<div style="background: white; min-height: 100px;"><#if i.description?exists>${i.description}<#else>no description provided.</#if></div>
	
	<!--show a table with all values-->
	<!--each row is a map-->
	<input type="submit" value="Back" onclick="__action.value='Show_All_Investigations'; return true;"/>
	<h2>Observed values</h2>
	<table style="background: white; border-collapse: collapse;">
	<!--header-->
	<tr><td>&nbsp;</td>
	<#list screen.features as feature>
			<td  ${style}><b>${feature}</b></td>
		</#list>
	</tr>
	<!--body-->
	<#list screen.targets as target>
	<tr><td ${style}><b>${target}</b></td>
		<#list screen.features as feature>
			<td  ${style}>${screen.getValue(feature,target)}</td>
		</#list>
	</tr>
	</#list>
	</table>	
	
	<input type="submit" value="Back" onclick="__action.value='Show_All_Investigations'; return true;"/>
<#else>
	<#--an input to send the selected investigation with-->
	<input type="hidden" name="investigationId" />
	<h1>Investigation Index:</h1>
	<input type="submit" value="Refresh" onclick="__action.value='Refresh'; return true;"/>	
	<table style="background: white">
	<tr><td ${style}><b>Investigation</b></td><td ${style}><b>Description</b></td><td ${style}><b>No. features</b></td><td ${style}><b>No. targets</b></td></tr>
	<#list screen.investigations as i>
		<tr>
			<td ${style}>
				<img class="edit_button" src="res/img/recordview.png" title="show details" alt="show details" 
				onclick="document.forms.${screen.name}.investigationId.value = ${i.getInt("id")};document.forms.${screen.name}.__action.value= 'Select_Investigation'; document.forms.${screen.name}.submit(); "/>
				${i.getString("investigation")}
			</td>
			<td ${style}>${i.getString("description")}</td>
			<td ${style}>${i.getInt("features")}</td>
			<td ${style}>${i.getInt("targets")}</td></td>
	</#list>
	</tr>
	</table>
	
	<input type="submit" value="Refresh" onclick="__action.value='Refresh'; return true;"/>
</#if>

	<#--end of your plugin-->	
				</div>
			</div>
		</div>
	</form>
</#macro>
