<!--Date:        May 15, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenFTLTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro plugins_investigationoverview_InvestigationOverviewPlugin screen>
<#assign model = screen.myModel>
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
		
			<#-- Hack to immediatly clear the message so it doesn't "stick". -->
		${screen.clearMessage()}
		
		<#assign square = "<div style=\"display: table-cell; display: inline-block; vertical-align: top; width: 25px; height: 25px; background: ">
		<#assign blueSquare = square + "#0033FF;\"></div>">
		<#assign redSquare = square + "#CC0000;\"></div>">
		<#assign greenSquare = square + "#339900;\"></div>">
		
		<#assign colWidth = 350>
			
		<div class="screenbody">
			<div class="screenpadding">	
			
<h2>Overview of investigation '${model.selectedInv.name}'</h2>

<h3>Annotations</h3>

<table width=500 style='table-layout:fixed'>
	<col width=${colWidth}>
	<col width=${colWidth}>
	
	<tr>
	<#if model.annotationList?keys?size == 0>
		<td colspan="2">There are no annotations.</td>
	<#else>
		<#list model.annotationList?keys as a>
		DEBUG: A = ${a}
			<td>
			<#--if a?starts_with("Data")>
				<a href="?select=Data">${blueSquare} ${a}</a>
			<#else-->
				<a href=${model.annotationList[a]}>${blueSquare} ${a}</a>
			<#--if-->
			</td>
			
			<#if !model.showAllAnnotations && a_index == 3>
				<#break>
			</#if>
			
			<#if a_index%2!=0 && model.annotationList?keys?size-1 != a_index>
				 </tr>
				<tr>
					<td colspan="2">
						&nbsp;<#-- LINEBREAK -->
					</td>
				</tr>
				<tr>
			</#if>
		</#list>
		
		<#if model.showAllAnnotations && model.annotationList?keys?size%2!=0>
			<td>
				&nbsp;<#-- FILLER -->
			</td>
		</#if>
	</#if>
	</tr>
	
	<tr>
		<td colspan="2">
		&nbsp;<#-- DEFAULTLINEBREAK -->
		</td>
	</tr>
	
	<tr>
		<td>
			<#if !model.showAllAnnotations && model.annotationList?keys?size gt 4>
				${model.annotationList?keys?size - 4} more...
			<#else>
				&nbsp;
			</#if>
		</td>
		<td>
			<#if model.annotationList?keys?size lt 5>
				<#-- no need to show a button if 4 (or less) elements-->
			<#else>
				<#if model.showAllAnnotations>
					<input type="submit" value="Show four" onclick="document.forms.${screen.name}.__action.value = 'showFourAnnotations'; document.forms.${screen.name}.submit();"/>
				<#else>
					<input type="submit" value="Show all" onclick="document.forms.${screen.name}.__action.value = 'showAllAnnotations'; document.forms.${screen.name}.submit();"/>
				</#if>
			</#if>
			<input type="submit" value="Add" onclick="document.forms.${screen.name}.__action.value = 'addAnnotations'; document.forms.${screen.name}.submit();"/>
		</td>
	</tr>
</table>

<h3>Data</h3>

<table width=500 style='table-layout:fixed'>
	<col width=${colWidth}>
	<col width=${colWidth}>
	
	<tr>
	<#if model.expList?size == 0>
		<td colspan="2">There is no experimental data.</td>
	<#else>
		<#list model.expList?keys as a>
			<td>
				<a href="${model.expList[a]}">${redSquare} ${a}</a>
			</td>
			
			<#if !model.showAllExperiments && a_index == 3>
				<#break>
			</#if>
			
			<#if a_index%2!=0 && model.expList?size-1 != a_index>
				 </tr>
				<tr>
					<td colspan="2">
						&nbsp;<#-- LINEBREAK -->
					</td>
				</tr>
				<tr>
			</#if>
		</#list>
	
		<#if model.showAllExperiments && model.expList?size%2!=0>
			<td>
				&nbsp;<#-- FILLER -->
			</td>
		</#if>
	</#if>
	</tr>

	<tr>
		<td colspan="2">
		&nbsp;<#-- DEFAULTLINEBREAK -->
		</td>
	</tr>
	
	<tr>
		<td>
			<#if !model.showAllExperiments && model.expList?size gt 4>
				${model.expList?size - 4} more...
			<#else>
				&nbsp;
			</#if>
		</td>
		<td>
			<#if model.expList?size lt 5>
				<#-- no need to show a button if 4 (or less) elements-->
			<#else>
				<#if model.showAllExperiments>
					<input type="submit" value="Show four" onclick="document.forms.${screen.name}.__action.value = 'showFourExperiments'; document.forms.${screen.name}.submit();"/>
				<#else>
					<input type="submit" value="Show all" onclick="document.forms.${screen.name}.__action.value = 'showAllExperiments'; document.forms.${screen.name}.submit();"/>
				</#if>
			</#if>
			<input type="submit" value="Add" onclick="document.forms.${screen.name}.__action.value = 'addExperiments'; document.forms.${screen.name}.submit();"/>
		</td>
	</tr>
</table>


<h3>Files</h3>

<table width=500 style='table-layout:fixed'>
	<col width=${colWidth}>
	<col width=${colWidth}>
	
	<tr>
	<#if model.otherList?size == 0>
		<td colspan="2">There are no files.</td>
	<#else>
		<#list model.otherList?keys as a>
			<td>
				<a href="${model.otherList[a]}">${greenSquare} ${a}</a>
			</td>
			
			<#if !model.showAllOther && a_index == 3>
				<#break>
			</#if>
			
			<#if a_index%2!=0 && model.otherList?size-1 != a_index>
				 </tr>
				<tr>
					<td colspan="2">
						&nbsp;<#-- LINEBREAK -->
					</td>
				</tr>
				<tr>
			</#if>
		</#list>
	
		<#if model.showAllOther && model.otherList?size%2!=0>
			<td>
				&nbsp;<#-- FILLER -->
			</td>
		</#if>
	</#if>
	</tr>
	
	<tr>
		<td colspan="2">
		&nbsp;<#-- DEFAULTLINEBREAK -->
		</td>
	</tr>
	
	<tr>
		<td>
			<#if !model.showAllOther && model.otherList?size gt 4>
				${model.otherList?size - 4} more...
			<#else>
				&nbsp;
			</#if>
		</td>
		<td>
			<#if model.otherList?size lt 5>
				<#-- no need to show a button if 4 (or less) elements-->
			<#else>
				<#if model.showAllOther>
					<input type="submit" value="Show four" onclick="document.forms.${screen.name}.__action.value = 'showFourOther'; document.forms.${screen.name}.submit();"/>
				<#else>
					<input type="submit" value="Show all" onclick="document.forms.${screen.name}.__action.value = 'showAllOther'; document.forms.${screen.name}.submit();"/>
				</#if>
			</#if>
			<input type="submit" value="Add" onclick="document.forms.${screen.name}.__action.value = 'addOther'; document.forms.${screen.name}.submit();"/>
		</td>
	</tr>
</table>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
