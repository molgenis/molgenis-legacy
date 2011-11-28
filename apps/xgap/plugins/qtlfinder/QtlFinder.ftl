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

<#if model.qtlsFound?size gt 0>
<#else>
<table cellpadding="30">
	<tr>
		<td>
			<h1>
				<!--QTLs for ${model.result.name}:-->
				No QTL information could be retrieved for ${model.result.name}.
			</h1>
		</td>
	</tr>
</table>
</#if>



<#list model.qtlsFound as qtl>
<table cellpadding="30">
	<tr>
		<td>
			<h2>QTL #${qtl_index+1} - In data matrix <a href="molgenis.do?__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=${qtl.matrix.id}">${qtl.matrix.name}</a>. Basic information:</h2>
			<table cellpadding="3" border="1" style="width:700px;">
				<tr class="form_listrow0">
					<td colspan="2">
						<b>Plot<b>
					</td>
				</tr>
				<tr class="form_listrow1">
					<td align="center"  colspan="2">
					<i>Click to enlarge</i><br>
						<#if qtl.plot??>
							<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + qtl.plot + "></body></html>">
							<a href="#" onclick="var generate = window.open('', '', 'width=850,height650,resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
								<img src="tmpfile/${qtl.plot}" width="160" height="120">
							</a>
						</#if>
					</td>
				</tr>
				<tr class="form_listrow0">
					<td colspan="2">
						<b>Highest LOD score<b>
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
						<a href="molgenis.do?__target=Markers&__action=filter_set&__filter_attribute=Marker_name&__filter_operator=EQUALS&__filter_value=${qtl.peakMarker}">${qtl.peakMarker}</a>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td><h2>QTL #${qtl_index+1} - Advanced information:</h2>
		<div style="overflow: auto; max-height: 400px; width: 720px;">
			<table cellpadding="3" border="1" style="width:700px;">
				<tr class="form_listrow0">
					<td colspan="4">
						<b>All LOD scores</b>
					</td>
				</tr>
				<tr class="form_listrow1">
					<td>
						<i>Marker name</i>
					</td>
					<td>
						<i>LOD score</i>
					</td>
					<td>
						<i>Marker cM</i>
					</td>
					<td>
						<i>Marker chromosome</i>
					</td>
				</tr>
			<#list qtl.markers as m>
				<tr class="form_listrow1">
					<td>
						<a href="molgenis.do?__target=Markers&__action=filter_set&__filter_attribute=Marker_name&__filter_operator=EQUALS&__filter_value=${m}">${m}</a>
					</td>
					<td>
							${qtl.valuesForMarkers[m_index]}
					</td>
					<td>
							<#if qtl.markerAnnotations?keys?seq_contains(m)>${qtl.markerAnnotations[m].cm}</#if>
					</td>
					<td>
							<#if qtl.markerAnnotations?keys?seq_contains(m)><a href="molgenis.do?__target=Chromosomes&__action=filter_set&__filter_attribute=Chromosome_name&__filter_operator=EQUALS&__filter_value=${qtl.markerAnnotations[m].chromosome_name}">${qtl.markerAnnotations[m].chromosome_name}</a></#if>
					</td>
				</tr>
			</#list>
			</table>
			<h3>Data matrix where this QTL was found:</h3>
			<@rb.printEntity r=qtl.matrix/>
		</div>
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
