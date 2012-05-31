<#macro QtlFinder screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
	<!-- pass resultset key on disambiguation-->
	<input type="hidden" name="__key">
	
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

<#assign plotWidth = (1024+50)>
<#assign plotHeight = (768+50)>

<#import "../reportbuilder/ReportBuilder.ftl" as rb>

<table cellpadding="10">
	<tr>
		<td>
			<h1>QTL Finder</h1>
			<ul>
			<li>Enter (part of a) name or description of your gene/probe and press 'find'.</li>
			<li>You can enter one or more of such items. Each item must be on a new line.</li>
			<li>Values of <a href="molgenis.do?__target=BasicAnnotations&select=Genes">gene</a> and <a href="molgenis.do?__target=AdvancedAnnotations&select=Probes">probe</a> annotations are matched to your query.</li>
			<li>Datasets of <b>probe</b> or <b>gene</b> x <b>marker</b> are searched for QTL data.</li>
			</ul>
		</td>
	</tr>
	<tr>
		<td>			
			<textarea rows="10" cols="40" name="findme"><#if model.query??>${model.query}</#if></textarea> 
			
		</td>
	</tr>
	<tr>
		<td>			
			LOD score must at least be <input type="text" name="threshold" <#if model.threshold??>value="${model.threshold}"</#if> /> or higher (leave empty for any)
		</td>
	</tr>
	<tr>
		<td>Find in only in these datasets (leave empty for any):
		<div style="overflow: auto; max-height: 400px;">
		<table>
			<#list model.dataSets as d>
			<tr><td>
				<input type="checkbox" name="dataset_filter_${d.id}" value="true" <#if model.tickedDataSets?? && model.tickedDataSets?seq_contains(d.name)>CHECKED</#if> > ${d.name} (${d.investigation_name})<br>
			</td></tr>
			</#list>
			</table>
		</div>
		</td>
	</tr>
	<tr>
		<td>
			<input type="submit" id="findQtl" onclick="$(this).closest('form').find('input[name=__action]').val('findQtl');" value="Find (One QTL per plot)" /><script>$("#findQtl").button();</script>
			<input type="submit" id="findQtlMulti" onclick="$(this).closest('form').find('input[name=__action]').val('findQtlMulti');" value="Find (All-in-one plot)" /><script>$("#findQtlMulti").button();</script>
		</td>
	</tr>
</table>
<br>

<#if model.qmpr??>
<table cellpadding="30"><tr><td>
	<h2>Results for "${model.query}"</h2><br>
	
	<#if model.qmpr.plot??>
	<i>All-in-one plot of search matches, click to enlarge:</i><br>
		<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + model.qmpr.plot + "></body></html>">
		<a href="#" onclick="var generate = window.open('', '', 'width=${plotWidth?c},height=${plotHeight?c},resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
			<img src="tmpfile/${model.qmpr.plot}" width="320" height="320">
		</a><br>
	</#if>
	
	<#if model.qmpr.cisTransPlot??>
	<br><br><i>Cis-trans plot, click to enlarge:</i><br>
		<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + model.qmpr.cisTransPlot + "></body></html>">
		<a href="#" onclick="var generate = window.open('', '', 'width=${plotWidth?c},height=${plotHeight?c},resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
			<img src="tmpfile/${model.qmpr.cisTransPlot}" width="320" height="320">
		</a><br>
	</#if>
	
	<br><br>
	<i>Details of all matches that are in the plot (hover for details):</i>
	<div style="overflow: auto; width: 780px; max-height: 400px;">
	<#list model.qmpr.matches?values as d>
		<a href="molgenis.do?__target=${d.get(typefield)}s&__action=filter_set&__filter_attribute=${d.get(typefield)}_name&__filter_operator=EQUALS&__filter_value=${d.name}"><div style="display: inline; text-decoration: underline; color: blue;" onmouseover="return overlib('<@rb.printEntityText r=d/>', CAPTION, 'Description')" onmouseout="return nd();">${d.name}</div></a> <div style="display: inline;"><#list d.getFields() as f><#if d.get(f)?exists><#if d.get(f)?is_enumerable && f == 'AlternateId_name' && d.get(f)?size gt 0>Alternative ID's: <b><#list d.get(f) as i>${i} <#if i_index == 5>...<#break></#if></#list></b></#if></#if></#list><#if d.description??>Description: <b><#if d.description?length gt 40>${d.description?substring(0, 20)} ... ${d.description?substring(d.description?length-20, d.description?length)}<#else>${d.description}</#if></b></#if></div><br>
	</#list>
	</div>
</td></tr></table>
</#if>

<#if model.resultSet?size gt 0>
	<h2>&nbsp;Result overview</h2><br>
</#if>

<#list model.resultSet?keys as key>

	<#assign result = model.resultSet[key]>
	
	<div id="${result.selectedName}_closeme" style="float: left; padding: 5px; border: 1px solid #999; width: 400px; height: 400px; text-align:center; ">
	
	<#if result.noResultsFound??>
		No results found for "${result.selectedName}".
	</#if>
	
	<#if result.disambiguate??>
		<#if result.disambiguate?size gt 1000>
			More than 1000 matches for "${result.selectedName}".<br>
			Please be more specific.
		<#else>
			Multiple matches for "${result.selectedName}".<br><br>
			Please disambiguate below.<br><br>
			<a href="#${result.selectedName}"><img src="generated-res/img/filter.png" /></a>
		</#if>
	</#if>
	
	<#if result.result??>
		<#list result.qtlsFound as qtl>
			${result.result.get(typefield)} <a href="molgenis.do?__target=${result.result.get(typefield)}s&__action=filter_set&__filter_attribute=${result.result.get(typefield)}_name&__filter_operator=EQUALS&__filter_value=${result.result.name}">${result.result.name}</a>
			<#if result.selectedName != result.result.name>matches "${result.selectedName}"</#if><br><b>Max. LOD: ${qtl.peakValue}</b><br><br>
			<#if qtl.plot??>
				<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + qtl.plot + "></body></html>">
				<a href="#" onclick="var generate = window.open('', '', 'width=${plotWidth?c},height=${plotHeight?c},resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
					<img src="tmpfile/${qtl.plot}" width="320" height="240">
				</a><br>
			</#if>
			<a href="#${result.selectedName}">View QTL details <img src="generated-res/img/filter.png" /></a>
			
			<#if qtl_has_next>
			</div><div style="float: left; padding: 5px; border: 1px solid #999; width: 400px; height: 400px; text-align:center; ">
			</#if>
		</#list>
		
		<#if result.qtlsFound?size == 0>
		No QTL information for ${result.result.name}.<br><br>
		No data or below threshold.<br><br>
		<img src="generated-res/img/cancel.png" onclick="showhide('${result.selectedName}_closeme');"/>
		</#if>
	</#if>
	
	

	</div>
</#list>

<br style="clear: both;">

<#if model.resultSet?size gt 0>
	<br><br><br><h2>&nbsp;Detailed overview</h2>
</#if>

<#list model.resultSet?keys as key> <#-- NB: result.selectedName == key -->
	<#assign result = model.resultSet[key]>
	<#if result.noResultsFound??>
	<table cellpadding="30">
		<tr>
			<td>
				<h2>No results found for "${result.selectedName}".
			</td>
		</tr>
	</table>
	</#if>

	<#if result.disambiguate??>
	<table cellpadding="30">
		<tr>
			<td>
				<#if result.disambiguate?size gt 1000>
					<h2>More than 1000 matches for "${result.selectedName}". Please be more specific.</h2>
				<#else>
				<h2 id="${result.selectedName}">Multiple matches for "${result.selectedName}". Please choose:</h2>
				<div style="overflow: auto; width: 780px; max-height: 400px;">

				<#list result.disambiguate as d>
					<input type="checkbox" name="disambig_option_${d.name}@${d.get(typefield)}" value="true">${d.get(typefield)} <a href="molgenis.do?__target=${d.get(typefield)}s&__action=filter_set&__filter_attribute=${d.get(typefield)}_name&__filter_operator=EQUALS&__filter_value=${d.name}"><div style="display: inline; text-decoration: underline; color: blue;" onmouseover="return overlib('<@rb.printEntityText r=d/>', CAPTION, 'Description')" onmouseout="return nd();">${d.name}</div></a> <div style="display: inline;"><#list d.getFields() as f><#if d.get(f)?exists><#if d.get(f)?is_enumerable && f == 'AlternateId_name' && d.get(f)?size gt 0>Alternative ID's: <b><#list d.get(f) as i>${i} <#if i_index == 5>...<#break></#if></#list></b></#if></#if></#list><#if d.description??>Description: <b><#if d.description?length gt 40>${d.description?substring(0, 20)} ... ${d.description?substring(d.description?length-20, d.description?length)}<#else>${d.description}</#if></b></#if></div><br>
				</#list>
				
				</div>
				<br>
				<input type="submit" value="Select" onclick="document.forms.${screen.name}.__action.value = 'disambig'; document.forms.${screen.name}.__key.value = '${key}'; document.forms.${screen.name}.submit();">
				
				</#if>
			</td>
		</tr>
	</table>
	</#if>
	
	<#if result.result??>
		<table cellpadding="30">
			<tr>
				<td>
					<h1 id="${result.selectedName}">
					${result.result.get(typefield)} ${result.result.name}
					<#if result.selectedName != result.result.name>matches "${result.selectedName}"</#if>
					</h1>
					<h2>Record information</h2>
					<@rb.printEntity r=result.result/>
				</td>
			</tr>
		</table>
		
		<#if result.qtlsFound?size == 0>
		<table cellpadding="30">
			<tr>
				<td>
					<h2>
						No QTL information for ${result.result.name}. (no data or below threshold)
					</h2>
				</td>
			</tr>
		</table>
		</#if>



		<#list result.qtlsFound as qtl>
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
									<a href="#" onclick="var generate = window.open('', '', 'width=${plotWidth?c},height=${plotHeight?c},resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
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
								<#if qtl.markerAnnotations?keys?seq_contains(qtl.peakMarker)>(at bp ${qtl.markerAnnotations[qtl.peakMarker].bpstart?c}<#if qtl.markerAnnotations[qtl.peakMarker].cm??>, cM ${qtl.markerAnnotations[qtl.peakMarker].cm}</#if>)</#if>
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
							<td colspan="5">
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
								<i>Marker bp</i>
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
									<#if qtl.markerAnnotations?keys?seq_contains(m)><#if qtl.markerAnnotations[qtl.peakMarker].cm??>${qtl.markerAnnotations[qtl.peakMarker].cm}</#if></#if>
							</td>
							<td>
									<#if qtl.markerAnnotations?keys?seq_contains(m)>${qtl.markerAnnotations[m].bpstart?c}</#if>
							</td>
							<td>
									<#if qtl.markerAnnotations?keys?seq_contains(m) && qtl.markerAnnotations[m].chromosome_name??><a href="molgenis.do?__target=Chromosomes&__action=filter_set&__filter_attribute=Chromosome_name&__filter_operator=EQUALS&__filter_value=${qtl.markerAnnotations[m].chromosome_name}">${qtl.markerAnnotations[m].chromosome_name}</a></#if>
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
	
</#list>
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
