<#macro QtlFinder2 screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
	<input type="hidden" name="__shopMeName">
	<input type="hidden" name="__shopMeId">
	
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

<#if model.query??>
	<#assign query = model.query>
<#else>
	<#assign query = "">
</#if>

<#assign allDataTypes = "__ALL__DATATYPES__SEARCH__KEY">

<br><br>

<div style="position:relative;left:80px;top:20px;">
		<select class=" ui-widget-content ui-corner-all" id="Phenotype_select" name="dataTypeSelect"  style="width:220px;" name="dataTypeSelect">
			<option value=${allDataTypes} <#if model.selectedAnnotationTypeAndNr?? && model.selectedAnnotationTypeAndNr == allDataTypes>selected="selected"</#if>>All phenotypes (${model.annotationTypeAndNr[allDataTypes]})</option>
			<#list model.annotationTypeAndNr?keys as key>
				<#if key != allDataTypes>
					<option value="${key}" <#if model.selectedAnnotationTypeAndNr?? && model.selectedAnnotationTypeAndNr == key>selected="selected"</#if>>${key} (${model.annotationTypeAndNr[key]})</option>
				</#if>
			</#list>
		</select><script>$("#Phenotype_select").chosen();</script>
</div>
<div style="position:relative;left:300px;top:-15px;">

	<input type="text" name="query" class="searchBox" value="${query}" >
</div>

<div style="position:relative;left:580px;top:-65px;">
	<input type="submit" id="search" onclick="$(this).closest('form').find('input[name=__action]').val('search');" value="Search" /><script>$("#search").button();</script>
</div>






<#if model.shoppingCart?? && model.shoppingCart?size gt 0>

	<div style="
	position:absolute;
	 top:275px;
	 right:-200px;
	 width:200px;
	 z-index: 5;
	">
	Multiplot cart:<br><br>
	<#list model.shoppingCart?keys as name>
	<input type="submit" class="unshop" value="" onclick="document.forms.${screen.name}.__action.value = 'unshop'; document.forms.${screen.name}.__shopMeName.value = '${name}'; document.forms.${screen.name}.submit();">
	<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${name}'; document.forms.${screen.name}.submit();">${name}</a><br>
	</#list>
	
	<br><br>
	<input type="submit" id="plotShoppingCart" onclick="$(this).closest('form').find('input[name=__action]').val('plotShoppingCart');" value="Create plot" /><script>$("#plotShoppingCart").button();</script>
	<#--@action name="emptyShoppingCart" label="Clear"/-->
	
	</div>
	
</#if>



<#if model.report??>

	<#import "../reportbuilder/ReportBuilder.ftl" as rb>
	
	<#assign r = model.report.entity>
	
	<h1>${r.get(typefield)} <a target="_blank" href="molgenis.do?select=${r.get(typefield)}s&__target=${r.get(typefield)}s&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=${r.get(typefield)}_name&__filter_operator=EQUALS&__filter_value=${r.name}">${r.name}</a></h1>
	
	<#if model.qtls?? && model.qtls?size gt 0><#-- should have them really -->
	<br>

	<div id="${r.name}_closeme" style="float: left; padding: 5px; border: 1px solid #999; width: 400px; height: 400px; text-align:center; ">
		<#list model.qtls as qtl>
		
			Hit #${qtl_index+1}
			<br>Max. <#if qtl.plot??><#if qtl.plot?starts_with('eff')>effect size<#else>LOD score</#if><#else>value</#if>: ${qtl.peakValue}<br>in <a target="_blank" href="molgenis.do?select=Datas&__target=Datas&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=Data_name&__filter_operator=EQUALS&__filter_value=${qtl.matrix.name}">${qtl.matrix.name}</a><br><br>
			<#if qtl.plot??>
				<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + qtl.plot + "></body></html>">
				<a href="#" onclick="var generate = window.open('', '', 'width=${plotWidth?c},height=${plotHeight?c},resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
					<img src="tmpfile/${qtl.plot}" width="320" height="240">
				</a><br>
			<#else>
				<br><br><br><i>No plot available.</i><br><br><br><br>
			</#if>
			<a href="#QTL${qtl_index+1}">View <#if qtl.plot??><#if qtl.plot?starts_with('eff')>effect<#else>QTL</#if><#else>value</#if> details <img src="generated-res/img/filter.png" /></a>
			
			<#if qtl_has_next>
			</div><div style="float: left; padding: 5px; border: 1px solid #999; width: 400px; height: 400px; text-align:center; ">
			</#if>
		</#list>
	</div>
		
</#if>


	<div style="clear: both; padding: 0px; border: 0px solid #999; width: 850px; height: 60px; text-align:left; "></div>
	<h1><#if model.qtls?? && model.qtls?size gt 0>Details<#else><i>No plots or details available</i></#if></h1>
	
	
	<#list model.qtls as qtl>
		<table cellpadding="30">
			<tr>
				<td>
					<h3 id="QTL${qtl_index+1}">#${qtl_index+1} - <#if qtl.plot??><#if qtl.plot?starts_with('eff')>Effect<#else>QTL</#if><#else>Values</#if> in data matrix <a target="_blank" href="molgenis.do?select=Datas&__target=Datas&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value=${qtl.matrix.id}">${qtl.matrix.name}</a>. Basic information:</h3>
					<table cellpadding="3" border="1" style="width:700px;">
						<tr class="form_listrow0">
							<td colspan="2">
								<b>Plot<b>
							</td>
						</tr>
						<tr class="form_listrow1">
							<td align="center"  colspan="2">
							
								<#if qtl.plot??>
									<i>Click to enlarge</i><br>
									<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + qtl.plot + "></body></html>">
									<a href="#" onclick="var generate = window.open('', '', 'width=${plotWidth?c},height=${plotHeight?c},resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
										<img src="tmpfile/${qtl.plot}" width="160" height="120">
									</a>
								<#else>
									<i>No plot available</i><br>
								</#if>
							</td>
						</tr>
						<tr class="form_listrow0">
							<td colspan="2">
								<b>Highest <#if qtl.plot??><#if qtl.plot?starts_with('eff')>effect size<#else>LOD score</#if><#else>value</#if><b>
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
								<#if qtl.plot??>Marker:<#else>Trait:</#if>
							</td>
							<td>
								<#if qtl.plot??><a target="_blank" href="molgenis.do?select=Markers&__target=Markers&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=Marker_name&__filter_operator=EQUALS&__filter_value=${qtl.peakMarker}">${qtl.peakMarker}</a><#else>${qtl.peakMarker}</#if>
								<#if qtl.markerAnnotations?keys?seq_contains(qtl.peakMarker)>at bp ${qtl.markerAnnotations[qtl.peakMarker].bpstart?c}<#if qtl.markerAnnotations[qtl.peakMarker].cm??>, cM ${qtl.markerAnnotations[qtl.peakMarker].cm}</#if></#if>
								[<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${qtl.peakMarker}'; document.forms.${screen.name}.submit();">explore further</a>]
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td><h3>#${qtl_index+1} - <#if qtl.plot??><#if qtl.plot?starts_with('eff')>Effect<#else>QTL</#if><#else>Value</#if> advanced information:</h3>
				<div style="overflow: auto; max-height: 400px; width: 720px;">
					<table cellpadding="3" border="1" style="width:700px;">
						<tr class="form_listrow0">
							<td colspan="5">
								<b>All <#if qtl.plot??><#if qtl.plot?starts_with('eff')>effect sizes<#else>LOD scores</#if><#else>Values</#if></b>
							</td>
						</tr>
						<tr class="form_listrow1">
							<td>
								<i><#if qtl.plot??>Marker<#else>Trait</#if> name</i>
							</td>
							<td>
								<i><#if qtl.plot??><#if qtl.plot?starts_with('eff')>Effect size<#else>LOD score</#if><#else>Value</#if></i>
							</td>
							<td>
								<i><#if qtl.plot??>Marker<#else>Trait</#if> cM pos.</i>
							</td>
							<td>
								<i><#if qtl.plot??>Marker<#else>Trait</#if> basepair pos.</i>
							</td>
							<td>
								<i><#if qtl.plot??>Marker<#else>Trait</#if> chromosome</i>
							</td>
						</tr>
					<#list qtl.markers as m>
						<tr class="form_listrow1">
							<td>
								<#if qtl.plot??><a target="_blank" href="molgenis.do?select=Markers&__target=Markers&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=Marker_name&__filter_operator=EQUALS&__filter_value=${m}">${m}</a><#else>${m}</#if>
								[<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${m}'; document.forms.${screen.name}.submit();">explore further</a>]
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
									<#if qtl.markerAnnotations?keys?seq_contains(m) && qtl.markerAnnotations[m].chromosome_name??><a target="_blank" href="molgenis.do?select=Chromosomes&__target=Chromosomes&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=Chromosome_name&__filter_operator=EQUALS&__filter_value=${qtl.markerAnnotations[m].chromosome_name}">${qtl.markerAnnotations[m].chromosome_name}</a></#if>
							</td>
						</tr>
						<#if m_index = 1000>
						<tr class="form_listrow1">
							<td colspan="5">
								<b>Limited at 1000, total size is ${qtl.markers?size?c}</b>
							</td>
						</tr>
						<#break>
						</#if>
					</#list>
					</table>
					<h3>Data matrix where this <#if qtl.plot??><#if qtl.plot?starts_with('eff')>effect<#else>QTL</#if><#else>value</#if> was found:</h3>
					<@rb.printEntity r=qtl.matrix/>
				</div>
				</td>
			</tr>
		</table>
		</#list>
	
	<div style="height: 40px; width: 800px; float: left;"></div><br>
	
	<h1>Additional</h1>
	
	<table cellpadding="30">
		<tr>
			<td>
				<h2>Record information</h2>
				<@rb.printEntity r=r/>
			</td>
		</tr>
		<#if model.report.matrices?size gt 0>
		<tr>
			<td>
				<#list model.report.matrices as ml>
					<br><br><br><h2>Present in data matrix <i>"<a target="_blank" href="molgenis.do?select=Datas&__target=Datas&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=Data_name&__filter_operator=EQUALS&__filter_value=${ml.data.name}">${ml.data.name}</a>"</i></h2>
					<h3>Matrix record information:</h3>
					<@rb.printEntity r=ml.data/>
					
					<br>
					<table cellpadding="3" border="1" style="width:700px;">
						<tr class="form_listrow1">
							<td>
								Total number of rows
							</td>
							<td>
								${ml.totalRows?c}
							</td>
						</tr>
						<tr class="form_listrow1">
							<td>
								Total number of columns
							</td>
							<td>
								${ml.totalCols?c}
							</td>
						</tr>
						<tr class="form_listrow1">
							<td>
								"${r.name}" present at row index
							</td>
							<td>
								<#if ml.rowIndex == -1>Not present in rows<#else>${ml.rowIndex}</#if>
							</td>
						</tr>
						<tr class="form_listrow1">
							<td>
								"${r.name}" present at column index
							</td>
							<td>
								<#if ml.colIndex == -1>Not present in columns<#else>${ml.colIndex}</#if>
							</td>
							
						</tr>
					</table>
						
						
					<h3>Values of ${r.name} in "${ml.data.name}":</h3>
					<h4>Row plot</h4>
					<#if ml.rowImg??>
					<table>
						<tr>
							<td>
								<i>Click to enlarge</i>
							</td>
						</tr>
					</table>
					
					<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + ml.rowImg + "></body></html>">
					<a href="#" onclick="var generate = window.open('', '', 'width=850,height650,resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
						<img src="tmpfile/${ml.rowImg}" width="160" height="120">
					</a>
					
					<#else>
						<#if ml.rowIndex == -1>
							${r.name} not in row values, no row plot was made.
						<#else>
							<#if ml.totalRows gt 1000>
								More than 1000 rows in the matrix, no plot was made.
							<#else>
								Image creation failed. Maybe you have missing values, or a wrong value type.
							</#if>
						</#if>
					</#if>
						
					<h4>Column plot</h4>
					<#if ml.colImg??>
					<table>
						<tr>
							<td>
								<i>Click to enlarge</i>
							</td>
						</tr>
					</table>
					
					<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + ml.colImg + "></body></html>">
					<a href="#" onclick="var generate = window.open('', '', 'width=850,height650,resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
						<img src="tmpfile/${ml.colImg}" width="160" height="120">
					</a>
					
					<#else>
						<#if ml.colIndex == -1>
							${r.name} not in column values, no column plot was made.
						<#else>
							<#if ml.totalCols gt 1000>
								More than 1000 columns in the matrix, no plot was made.
							<#else>
								Image creation failed. Maybe you have missing values, or a wrong value type.
							</#if>
						</#if>
					</#if>
						
						
					<br>
					
					<h3>Spearman correlation results</h3>
					
					<h4>Row correlations</h4>
					
					<#if ml.rowCorr??>
					<div style="overflow: auto; max-height: 400px; width: 720px;">
						<table border="1" cellpadding="3" style="width: 700px;">
							<tr class="form_listrow0">
								<td>
									<b>Other rows in "${ml.data.name}"</b>
								</td>
								<td>
									<b>Spearman's rho</b>
								</td>
							</tr>
						<#list ml.rowCorr?keys as key>
							<tr class="form_listrow1">
								<td>
									${key} [<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${key}'; document.forms.${screen.name}.submit();">explore further</a>]
								</td>
								<td>
									<#if ml.rowCorr[key]??>${ml.rowCorr[key]}<#else>N/A</#if>
								</td>
							</tr>
						</#list>
						</table>
					</div>
					<#else>
						<#if ml.rowIndex == -1>
							${r.name} not in row values, no row correlations were calculated.
						<#else>
							<#if (ml.totalCols * ml.totalRows) gt 1000000>
								Less than 2 elements to compare, or more than 1 million values in the matrix, no correlations were calculated.
							<#else>
								Correlation failed.
							</#if>
						</#if>
					</#if>
					
					<h4>Column correlations</h4>
						
					<#if ml.colCorr??>
					<div style="overflow: auto; max-height: 400px; width: 720px;">
						<table border="1" cellpadding="3" style="width: 700px;">
							<tr class="form_listrow0">
								<td>
									<b>Other columns in "${ml.data.name}"</b>
								</td>
								<td>
									<b>Spearman's rho</b>
								</td>
							</tr>
						<#list ml.colCorr?keys as key>
							<tr class="form_listrow1">
								<td>
									<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${key}'; document.forms.${screen.name}.submit();">${key}</a>
								</td>
								<td>
									<#if ml.colCorr[key]??>${ml.colCorr[key]}<#else>N/A</#if>
								</td>
							</tr>
						</#list>
						</table>
					</div>
					<#else>
						<#if ml.colIndex == -1>
							${r.name} not in column values, no column correlations were calculated.
						<#else>
							<#if (ml.totalCols * ml.totalRows) gt 1000000>
								Less than 2 elements to compare, or more than 1 million values in the matrix, no correlations were calculated.
							<#else>
								Correlation failed.
							</#if>
						</#if>
					</#if>
				</#list>
			</td>
		</tr>
		</#if>
	</table>











<#else>

	<#if model.multiplot??>

	<table cellpadding="30"><tr><td>
		<h2>Results for "<#if model.query??>${model.query}</#if>"</h2><br>
		
		<table>
			<tr>
				<td>
					<#if model.multiplot.plot??>
					<i>All-in-one plot of search matches, click to enlarge:</i><br>
						<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + model.multiplot.plot + "></body></html>">
						<a href="#" onclick="var generate = window.open('', '', 'width=${plotWidth?c},height=${plotHeight?c},resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
							<img src="tmpfile/${model.multiplot.plot}" width="320" height="320">
						</a>
					</#if>
				</td>
				<td>
		
					<#if model.multiplot.cisTransPlot??>
					<i>Cis-trans plot, click to enlarge:</i><br>
						<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + model.multiplot.cisTransPlot + "></body></html>">
						<a href="#" onclick="var generate = window.open('', '', 'width=${plotWidth?c},height=${plotHeight?c},resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
							<img src="tmpfile/${model.multiplot.cisTransPlot}" width="320" height="320">
						</a><br>
					</#if>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<br><br>
				<#if model.multiplot.plot??>
					<i>Legend, click to enlarge:</i><br>
						<#assign html = "<html><head><title>Legend</title></head><body><img src=clusterdemo/wormqtl/legend.png></body></html>">
						<a href="#" onclick="var generate = window.open('', '', 'width=1000,height=650,resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
							<img src="clusterdemo/wormqtl/legend.png" width="150" height="100">
						</a>
					</#if>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<br><br>
					<i>All items that are in the plot (click for details):</i>
					<div style="overflow: auto; width: 780px; max-height: 400px;">
					<#list model.multiplot.matches?values as d>
					<a target="_blank" href="molgenis.do?select=${d.get(typefield)}s&__target=${d.get(typefield)}s&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=${d.get(typefield)}_name&__filter_operator=EQUALS&__filter_value=${d.name}">${d.name}</a>
					[<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${d.name}'; document.forms.${screen.name}.submit();">explore further</a>]
					<#if d.description??> - <#if d.description?length gt 70>${d.description?substring(0, 70)}...<#else>${d.description}</#if></#if><br>
					</#list>
					<br>
					<i>All datasets used in the plot (click for details):</i>
					<table>
					<#list model.multiplot.datasets?values as d>
						<tr>
							<td>
								ID <b>${d.id}</b>
							</td>
							<td>
								: <a target="_blank" href="molgenis.do?select=Datas&__target=Datas&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=Data_name&__filter_operator=EQUALS&__filter_value=${d.name}">${d.name}</a>
								<#if d.description??> - <#if d.description?length gt 70>${d.description?substring(0, 70)}...<#else>${d.description}</#if></#if>
							</td>
						</tr>
					</#list>
					</table>
					</div>
				</td>
			</tr>
		</table>
	</td></tr></table>
	
	<#else>
	
	
	
	
	
	<#if model.shortenedQuery??><i>Your query was too specific for any hits, so it was shortened to:</i> <b>${model.shortenedQuery}</b>. </#if>
	
	<#if model.hits?? && model.hits?size == 100 && model.shortenedQuery??>
		<i>These results were limited to the first 100.</i><br><br>
	<#elseif model.hits?? && model.hits?size == 100>
		<i>Your results were limited to the first 100. Please be more specific.</i><br><br>
	<#else>

	</#if>
	
	<#if model.hits??>
	Found these hits:<br><br>
	
	
	
	<#list model.hits?keys as name>
	
	
	
	<#if model.shoppingCart?keys?seq_contains(name)>
		<input type="submit" class="unshop" value="" onclick="document.forms.${screen.name}.__action.value = 'unshop'; document.forms.${screen.name}.__shopMeName.value = '${name}'; document.forms.${screen.name}.submit();">
	<#else>
		<input type="submit" class="shop" value="" onclick="document.forms.${screen.name}.__action.value = 'shop'; document.forms.${screen.name}.__shopMeId.value = '${model.hits[name].id?c}'; document.forms.${screen.name}.__shopMeName.value = '${name}'; document.forms.${screen.name}.submit();">
	</#if>
		
	${model.hits[name].get(typefield)} <a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${name}'; document.forms.${screen.name}.submit();"><b>${name}</b></a>
	<#if model.hits[name].get('ReportsFor_name')?? && model.hits[name].get('ReportsFor_name')?is_string && model.hits[name].get('ReportsFor_name')?length gt 0>reports for <a target="_blank" href="molgenis.do?select=Genes&__target=Genes&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=Gene_name&__filter_operator=EQUALS&__filter_value=${model.hits[name].reportsFor_name}">${model.hits[name].reportsFor_name}</a></#if> <#if model.hits[name].symbol?? && model.hits[name].symbol?length gt 0>(${model.hits[name].symbol})</#if>
	
	<div style="display: inline;font-size:100%"><#if model.hits[name].description??> <br> <#if model.hits[name].description?length gt 70>${model.hits[name].description?substring(0, 70)} <#else>${model.hits[name].description}</#if> <a target="_blank" href="molgenis.do?select=${model.hits[name].get(typefield)}s&__target=${model.hits[name].get(typefield)}s&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=${model.hits[name].get(typefield)}_name&__filter_operator=EQUALS&__filter_value=${name}">...more</a> </#if></div>
	<br>
	<br>
	
	</#list>
	
	<table cellpadding="10">
		<tr>
			<td>
				<input type="submit" class="shop" value="" onclick="document.forms.${screen.name}.__action.value = 'shopAll'; document.forms.${screen.name}.submit();"><b><i>Add all to cart</b></i>
			</td>
			<td>
				<input type="submit" class="unshop" value="" onclick="document.forms.${screen.name}.__action.value = 'emptyShoppingCart'; document.forms.${screen.name}.submit();"><b><i>Clear current cart</b></i>
			</td>
		</tr>
	</table>
	</#if>
	
	
	</#if>
</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
