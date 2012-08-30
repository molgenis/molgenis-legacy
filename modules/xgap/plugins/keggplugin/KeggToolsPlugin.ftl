<!--Date:        July 24, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenFTLTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro plugins_keggplugin_KeggToolsPlugin screen>
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

<#if screen.sourceOrganism?exists>
	<#assign sourceOrganism = screen.sourceOrganism>
<#else>
	<#assign sourceOrganism = "">
</#if>

<#if screen.targetOrganism?exists>
	<#assign targetOrganism = screen.targetOrganism>
<#else>
	<#assign targetOrganism = "">
</#if>

<#if screen.input?exists>
	<#assign input = screen.input>
<#else>
	<#assign input = "">
</#if>

<br>

<table>
	<tr>
		<td>
			Source organism <input type="text" name="sourceOrganism" value="${sourceOrganism}" /><br><br>

			Target organism <input type="text" name="targetOrganism" value="${targetOrganism}" /><br><br>
			
			<a href="http://www.genome.jp/kegg/catalog/org_list.html">KEGG Organism codes</a><br><br>
			
			Identifier list<br><textarea name="inputIdList" rows="15" cols="25">${input}</textarea><br><br>
		</td>
		<td>
			&nbsp;
		</td>
		<td>
			Output (simple)<br><textarea name="output" rows="20" cols="25">${screen.outputSimple}</textarea><br><br>
		</td>
		<td>
			Output (advanced)<br><textarea name="output" rows="20" cols="50">${screen.outputAdvanced}</textarea><br><br>
		</td>
	</tr>
</table>

		
			<table>
				<tr>
					<td>
						<input type="submit" value="Annotation" onclick="document.forms.${screen.name}.__action.value = 'doAnnotation'; document.forms.${screen.name}.submit();"/>
					</td>
					<td>
						Attempt to retrieve KEGG annotations for your list of gene identifiers. Ignores the <i>Target organism</i> field.
					</td>
				</tr>
				<tr>
					<td>
						<input type="submit" value="Orthology" onclick="document.forms.${screen.name}.__action.value = 'doOrthology'; document.forms.${screen.name}.submit();"/>
					</td>
					<td>
						Attempt to find the best possible orthologous gene for each gene in your list of gene identifiers.
					</td>
				</tr>
				<tr>
					<td>
						<input type="submit" value="Example data" onclick="document.forms.${screen.name}.__action.value = 'example'; document.forms.${screen.name}.submit();"/>
					</td>
					<td>
						Fill the fields with some example identifiers and organism codes. Running the orthology will take a couple of minutes.
					</td>
				</tr>
			</table>

<br>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
