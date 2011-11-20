<#macro QtlFinder screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
	<!-- pass entity type on disambiguation-->
	<input type="hidden" name="__type">
	
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
		
<#if screen.myModel?exists>
	<#assign modelExists = true>
	<#assign model = screen.myModel>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>

<table cellpadding="10">
	<tr>
		<td>
			<h1>QTL Finder</h1>
			<ul>
			<li>Enter (part of a) name or description of your gene and press 'find'.</li>
			<li>(values of <b>gene</b> and <b>probe</b> annotations are matched)</li>
			</ul>
		</td>
	</tr>
	<tr>
		<td align="right">			
			<input type="text" name="findme" <#if model.selectedName??>value="${model.selectedName}"</#if>>
			<input type="submit" value="Find" onclick="document.forms.${screen.name}.__action.value = 'findQtl'; document.forms.${screen.name}.submit();">
		</td>
	</tr>
</table>
<br>

<#if model.noResultsFound??>
<table cellpadding="30">
	<tr>
		<td>
			<h2>No results found for "${model.selectedName}".
		</td>
	</tr>
</table>
</#if>

<#if model.disambiguate??>
<table cellpadding="30">
	<tr>
		<td>
			<#if model.disambiguate?size gt 1000>
				<h2>More than 1000 matches for "${model.selectedName}". Please be more specific.</h2>
			<#else>
			<h2>Multiple matches for "${model.selectedName}". Please choose one:</h2>
			<ul>
				<#list model.disambiguate as d>
					<li><a href="#" onclick="document.forms.${screen.name}.__action.value = 'disambig_${d.name}'; document.forms.${screen.name}.__type.value = '${d.__type}'; document.forms.${screen.name}.submit();">${d.name}</a></li>
				</#list>
			</ul>
			</#if>
		</td>
	</tr>
</table>
</#if>
	
<#if model.result??>
<#import "../reportbuilder/ReportBuilder.ftl" as rb>
<table cellpadding="30">
	<tr>
		<td>
			<h1>
			${model.result.__type} ${model.result.name}
			<#if model.selectedName != model.result.name>matches "${model.selectedName}"</#if>
			
			</h1>
			<h2>Record information</h2>
			<@rb.printEntity r=model.result/>
		</td>
	</tr>
</table>


<table cellpadding="30">
	<tr>
		<td>
			<h1>
			<#if model.qtlsFound?size gt 0>
				QTLs for ${model.result.name}:
			<#else>
				No QTL information could be retrieved for ${model.result.name}.
			</#if>
			</h1>
		</td>
	</tr>
</table>




<#list model.qtlsFound as qtl>

<table cellpadding="30">
	<tr>
		<td>
			<table cellpadding="3" border="1" style="width:700px;">
				<tr class="form_listrow0">
					<td colspan="2">
						<b>Highest peak<b>
					</td>
				</tr>
				<tr class="form_listrow1">
					<td>
						Value: 
					</td>
					<td>
						${qtl.peakValue}
					</td>
				</tr>
				<tr class="form_listrow1">
					<td>
						Marker: 
					</td>
					<td>
						${qtl.peakMarker}
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<table cellpadding="3" border="1" style="width:700px;">
				<tr class="form_listrow0">
					<td>
						<b>Plot<b>
					</td>
				</tr>
				<tr class="form_listrow1">
					<td align="center">
					<i>Click to enlarge</i><br>
						<#if qtl.plot??>
							<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + qtl.plot + "></body></html>">
							<a href="#" onclick="var generate = window.open('', '', 'width=850,height650,resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
								<img src="tmpfile/${qtl.plot}" width="160" height="120">
							</a>
						</#if>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
			<table cellpadding="3" border="1" style="width:700px;">
			<tr class="form_listrow0">
				<td colspan="2">
					<b>All information</b>
				</td>
			</tr>
			<tr class="form_listrow1">
				<td>
					<i>Marker</i>
				</td>
				<td>
					<i>LOD score</i>
				</td>
			</tr>
			<#list qtl.markers as m>
				<tr class="form_listrow1">
					<td>
						${m}
					</td>
					<td>
							${qtl.valuesForMarkers[m_index]}
					</td>
				</tr>
			</#list>
			</table>
		</td>
	</tr>
</table>

	

</#list>

</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
