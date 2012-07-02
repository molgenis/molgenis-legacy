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

<table>
	<tr>
		<td colspan="3" height="10" align="center">
			&nbsp;
		</td>
	</tr>
	<tr>
		<td width="290" align="right">
			<select class=" ui-widget-content ui-corner-all" id="Phenotype_select" name="dataTypeSelect"  style="width:220px;" name="dataTypeSelect">
				<option value=${allDataTypes} <#if model.selectedAnnotationTypeAndNr?? && model.selectedAnnotationTypeAndNr == allDataTypes>selected="selected"</#if>>All data (${model.annotationTypeAndNr[allDataTypes]})</option>
				<#list model.annotationTypeAndNr?keys as key>
					<#if key != allDataTypes>
						<option value="${key}" <#if model.selectedAnnotationTypeAndNr?? && model.selectedAnnotationTypeAndNr == key>selected="selected"</#if>>${key} (${model.annotationTypeAndNr[key]})</option>
					</#if>
				</#list>
			</select><script>$("#Phenotype_select").chosen();</script>
		</td>
		<td width="250" align="center">
			<input type="text" name="query" class="searchBox" value="${query}" >
		</td>
		<td width="290" align="left">
			<#--<input type="submit" id="search" onclick="$(this).closest('form').find('input[name=__action]').val('search');" value="Search" /><script>$("#search").addClass('grayButton').button();</script>-->
			 <div class="buttons"><button type="submit" id="search" onclick="document.forms.${screen.name}.__action.value = 'search'; document.forms.${screen.name}.submit();"><img src="generated-res/img/recordview.png" alt=""/>Search</button></div>
			 
			 <div class="buttons"><button type="submit" id="search" onclick="document.forms.${screen.name}.__action.value = 'reset'; document.forms.${screen.name}.submit();"><img src="generated-res/img/reset.png" alt=""/>Reset</button></div>
    </button>
		</td>
	</tr>
	<tr>
		<td colspan="3" height="70" align="center">
			<span style="font-size:12px;">(<i>for example:</i> ctl, daf, pgp-7, gst-27, Y65B4BR, K02B12, WBGene00021562, WBGene00006727, acetylcholine, luciferase ... )</span>
		</td>
	</tr>
</table>



<#if model.cartView>

<#if model.shoppingCart?? && model.shoppingCart?size gt 0>
	

	<div class="buttons"><button type="submit" onclick="document.forms.${screen.name}.__action.value = 'gotoSearch'; document.forms.${screen.name}.submit();"><img src="generated-res/img/listview.png" alt=""/> View hits (${model.hits?size})</button></div>

	<div class="buttons"><button style="background: #ccc" type="submit" onclick="document.forms.${screen.name}.__action.value = 'gotoCart'; document.forms.${screen.name}.submit();"><img src="generated-res/img/listview.png" alt=""/> View cart (${model.shoppingCart?keys?size})</button></div>
	
	<div class="buttons"><button type="submit" onclick="document.forms.${screen.name}.__action.value = 'emptyShoppingCart'; document.forms.${screen.name}.submit();"><img src="generated-res/img/cancel.png" alt=""/> Clear cart</button></div>
	
	<div class="buttons"><button class="positive" type="submit" onclick="document.forms.${screen.name}.__action.value = 'plotShoppingCart'; document.forms.${screen.name}.submit();"><img src="clusterdemo/icons/icon_plaintext_plot.png" alt=""/> Plot cart now</button></div>

	<br><br><br>

	<h3>Shopping cart</h3>

	<br>

<#list model.shoppingCart?keys as name>
	
	<div class="buttons"><button type="submit" onclick="document.forms.${screen.name}.__action.value = 'unshop'; document.forms.${screen.name}.__shopMeName.value = '${name}'; document.forms.${screen.name}.submit();"><img src="generated-res/img/cancel.png" alt=""/> Remove</button></div>
	
	<#--<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${name}'; document.forms.${screen.name}.submit();">${name}</a><br><br>-->
	
		${model.shoppingCart[name].get(typefield)} <a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${name}'; document.forms.${screen.name}.submit();"><b>${name}<#if model.shoppingCart[name].label?? && model.shoppingCart[name].label?length gt 0> / ${model.shoppingCart[name].label}</#if></b></a>
		<#if model.shoppingCart[name].get('ReportsFor_name')?? && model.shoppingCart[name].get('ReportsFor_name')?is_string && model.shoppingCart[name].get('ReportsFor_name')?length gt 0>reports for <a target="_blank" href="molgenis.do?select=Genes&__target=Genes&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=Gene_name&__filter_operator=EQUALS&__filter_value=${model.shoppingCart[name].reportsFor_name}">${model.shoppingCart[name].reportsFor_name}</a></#if> <#if model.shoppingCart[name].symbol?? && model.shoppingCart[name].symbol?length gt 0>(${model.shoppingCart[name].symbol})</#if>
		
		<div style="display: inline;font-size:100%"><#if model.shoppingCart[name].description??> <br> <#if model.shoppingCart[name].description?length gt 70>${model.shoppingCart[name].description?substring(0, 70)} <#else>${model.shoppingCart[name].description}</#if> <a target="_blank" href="molgenis.do?select=${model.shoppingCart[name].get(typefield)}s&__target=${model.shoppingCart[name].get(typefield)}s&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=${model.shoppingCart[name].get(typefield)}_name&__filter_operator=EQUALS&__filter_value=${name}">...more</a><#else> <br> </#if></div>
	
		<br><br>
	
	</#list>
<#else>

	<div class="buttons"><button type="submit" onclick="document.forms.${screen.name}.__action.value = 'gotoSearch'; document.forms.${screen.name}.submit();"><img src="generated-res/img/listview.png" alt=""/> View hits (${model.hits?size})</button></div>

	<div class="buttons"><button style="background: #ccc" type="submit" onclick="document.forms.${screen.name}.__action.value = 'gotoCart'; document.forms.${screen.name}.submit();"><img src="generated-res/img/listview.png" alt=""/> View cart (${model.shoppingCart?keys?size})</button></div>
	
	<div class="buttons"><button type="submit"><img src="generated-res/img/cancel.png" alt=""/> Clear cart</button></div>
	
	<div class="buttons"><button class="positive" type="submit" onclick="document.forms.${screen.name}.__action.value = 'plotShoppingCart'; document.forms.${screen.name}.submit();"><img src="clusterdemo/icons/icon_plaintext_plot.png" alt=""/> Plot cart now</button></div>
	
	<br><br><br>
	
	<h3>Your shopping cart is empty.</h3>
	
	<br>
	
</#if>




<#elseif model.report??>

	<#import "../reportbuilder/ReportBuilder.ftl" as rb>
	
	<#assign r = model.report.entity>
	
	<h1>${r.get(typefield)} <a target="_blank" href="molgenis.do?select=${r.get(typefield)}s&__target=${r.get(typefield)}s&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=${r.get(typefield)}_name&__filter_operator=EQUALS&__filter_value=${r.name}">${r.name}<#if r.label?? && r.label?length gt 0> / ${r.label}</#if></a></h1>
	
	<#if model.qtls?? && model.qtls?size gt 0><#-- should have them really -->
	<br>

	<div id="${r.name}_closeme" style="float: left; padding: 5px; border: 1px solid #999; width: 400px; height: 400px; text-align:center; ">
		<#list model.qtls as qtl>
		
			Hit #${qtl_index+1}
			<br>Max. <#if qtl.plot??><#if qtl.plot?starts_with('eff')>effect size<#else>LOD score</#if><#else>value</#if>: ${qtl.peakValue}<br>in <a target="_blank" href="molgenis.do?select=Datas&__target=Datas&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=Data_name&__filter_operator=EQUALS&__filter_value=${qtl.matrix.name}">${qtl.matrix.name}</a><br><br>
			<#if qtl.plot??>
				<#assign html = "<html><head><title>QTL plot</title></head><body><img src=tmpfile/" + qtl.plot + "></body></html>">
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
									<#assign html = "<html><head><title>QTL plot</title></head><body><img src=tmpfile/" + qtl.plot + "></body></html>">
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
								[<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${qtl.peakMarker}'; document.forms.${screen.name}.submit();">explore deeper</a>]
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
								[<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${m}'; document.forms.${screen.name}.submit();">explore deeper</a>]
							</td>
							<td>
									${qtl.valuesForMarkers[m_index]}
							</td>
							<td>
									<#if qtl.markerAnnotations?keys?seq_contains(m) && qtl.markerAnnotations[m].cm??>${qtl.markerAnnotations[m].cm?c}</#if>
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
					
					<#assign html = "<html><head><title>Row plot</title></head><body><img src=tmpfile/" + ml.rowImg + "></body></html>">
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
					
					<#assign html = "<html><head><title>Column plot</title></head><body><img src=tmpfile/" + ml.colImg + "></body></html>">
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
									${key} [<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${key}'; document.forms.${screen.name}.submit();">explore deeper</a>]
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
	
		<div class="buttons"><button type="submit" onclick="document.forms.${screen.name}.__action.value = 'gotoSearch'; document.forms.${screen.name}.submit();"><img src="generated-res/img/listview.png" alt=""/> View hits (${model.hits?size})</button></div>

		<div class="buttons"><button type="submit" onclick="document.forms.${screen.name}.__action.value = 'gotoCart'; document.forms.${screen.name}.submit();"><img src="generated-res/img/listview.png" alt=""/> View cart (${model.shoppingCart?keys?size})</button></div>
		
		<div class="buttons"><button type="submit" onclick="document.forms.${screen.name}.__action.value = 'emptyShoppingCart'; document.forms.${screen.name}.submit();"><img src="generated-res/img/cancel.png" alt=""/> Clear cart</button></div>
		
		<div class="buttons"><button class="positive" style="background: #ccc;" type="submit" onclick="document.forms.${screen.name}.__action.value = 'plotShoppingCart'; document.forms.${screen.name}.submit();"><img src="clusterdemo/icons/icon_plaintext_plot.png" alt=""/> Plot cart now</button></div>
		
		<br><br><br>
		

	<table cellpadding="30"><tr><td>
		<h2>Results for my selected hits:</h2><br>
		
		<table>
			<tr>
				<td>
					<#if model.multiplot.plot??>
					<i>Heatplot, click to enlarge:</i><br>
						<#assign html = "<html><head><title>QTL multiplot</title></head><body><img src=tmpfile/" + model.multiplot.plot + "></body></html>">
						<a href="#" onclick="var generate = window.open('', '', 'width=${plotWidth?c},height=${plotHeight?c},resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
							<img src="tmpfile/${model.multiplot.plot}" width="220" height="200">
						</a>
					</#if>
				</td>
				<td>
					<#if model.multiplot.cisTransPlot??>
					<i>Cis-trans plot, click to enlarge:</i><br>
						<#assign html = "<html><head><title>QTL cis-trans plot</title></head><body><img src=tmpfile/" + model.multiplot.cisTransPlot + "></body></html>">
						<a href="#" onclick="var generate = window.open('', '', 'width=${plotWidth?c},height=${plotHeight?c},resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
							<img src="tmpfile/${model.multiplot.cisTransPlot}" width="220" height="200">
						</a><br>
					</#if>
				</td>
				<td>
					<#if model.multiplot.regularPlot??>
					<i>Profile plot, click to enlarge:</i><br>
						<#assign html = "<html><head><title>QTL profile plot</title></head><body><img src=tmpfile/" + model.multiplot.regularPlot + "></body></html>">
						<a href="#" onclick="var generate = window.open('', '', 'width=${plotWidth?c},height=${plotHeight?c},resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
							<img src="tmpfile/${model.multiplot.regularPlot}" width="220" height="200">
						</a><br>
					</#if>
				</td>
			</tr>
			<tr>
				<td>
				<br><br>
				<#if model.multiplot.plot??>
					<span style="font-size:15px;font-weight:bold;">Legend, click to enlarge:</span><br/><br/>
						<#assign html = "<html><head><title>Legend</title></head><body><img src=clusterdemo/wormqtl/legend.png></body></html>">
						<a href="#" onclick="var generate = window.open('', '', 'width=1000,height=650,resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
							<img src="clusterdemo/wormqtl/legend.png" width="220" height="170">
						</a>
					</#if>
				</td>
				<td colspan="2">
				<br><br>
					<span style="font-size:15px;font-weight:bold;">More downloads:</span><br/>
					<br>Get the <a target="_blank" href="tmpfile/${model.multiplot.cytoNetwork}">Cytoscape network</a> for this plot. (<a target="_blank" href="http://wiki.cytoscape.org/Cytoscape_User_Manual/Creating_Networks#Import_Free-Format_Table_Files">how-to import</a>)
					<br>Get the <a target="_blank" href="tmpfile/${model.multiplot.cytoNodes}">Cytoscape nodes</a> for this plot. (<a target="_blank" href="http://cytoscape.org/manual/Cytoscape2_6Manual.html#Import Attribute Table Files">how-to import</a>)
					<br>Note: includes <b>significant results only</b>. (LOD > 3.5)
					<br><i>Save both files. Import network (has LOD scores), then node <br>attributes (chrom, bploc, dataset).</i> <a target="_blank" href="clusterdemo/wormqtl/cyto_example.png">Example visualization</a>
					<br>
					<br>Get the generated <a target="_blank" href="tmpfile/${model.multiplot.srcData}">source data</a> for these plots.
					<br>Get the generated <a target="_blank" href="tmpfile/${model.multiplot.plot?replace(".png",".R")}">multiplot plot R script</a>.
					<br>Get the generated <a target="_blank" href="tmpfile/${model.multiplot.cisTransPlot?replace(".png",".R")}">cistrans R plot script</a>.
					<br>Get the generated <a target="_blank" href="tmpfile/${model.multiplot.regularPlot?replace(".png",".R")}">profile R plot script</a>.
				</td>
			</tr>
			<tr>
				<td colspan="3">
				
					<br><br>
					<span style="font-size:15px;font-weight:bold;">Hits plotted:</span><br/>
					<div style="overflow: auto; width: 780px; max-height: 400px;">
					<table border="0">
					<#list model.multiplot.matches?values as d>
						<tr>
							<td>
								<a target="_blank" href="molgenis.do?select=${d.get(typefield)}s&__target=${d.get(typefield)}s&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=${d.get(typefield)}_name&__filter_operator=EQUALS&__filter_value=${d.name}">${d.name}<#if d.label?? && d.label?length gt 0> / ${d.label}</#if></a>
							</td>
							<td>
								[<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${d.name}'; document.forms.${screen.name}.submit();">explore deeper</a>]
							</td>
							<td>
								<#if d.description??>&nbsp;&nbsp;&nbsp;&nbsp;<#if d.description?length gt 50>${d.description?substring(0, 50)}...<#else>${d.description}</#if></#if><br>
							</td>
						</tr>
					</#list>
					</table>
					</div>
					<br>
					<span style="font-size:15px;font-weight:bold;">From datasets:</span><br/>
					<table border="0">
					<#list model.multiplot.datasets?values as d>
						<tr>
							<td>
								<nobr><b>${d.id}</b>: <a target="_blank" href="molgenis.do?select=Datas&__target=Datas&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=Data_name&__filter_operator=EQUALS&__filter_value=${d.name}">${d.name}</a></nobr>
							</td>
							<td>
								<#if d.description??>${d.description}</#if>
							</td>
						</tr>
					</#list>
					</table>
				</td>
			</tr>
		</table>
	</td></tr></table>
	
	<#else>
	
	
	
	
	
	
	
	<#if model.hits??>
	
	<#--find out how many items have been 'shopped'-->
	<#assign shopped = 0>
	<#list model.hits?keys as name>
		<#if model.shoppingCart?keys?seq_contains(name)>
			<#assign shopped = shopped+1>
		</#if>
	</#list>
	
	
	<div class="buttons"><button style="background: #ccc" type="submit" onclick="document.forms.${screen.name}.__action.value = 'gotoSearch'; document.forms.${screen.name}.submit();"><img src="generated-res/img/listview.png" alt=""/> View hits (${model.hits?size})</button></div>

	<div class="buttons"><button type="submit" onclick="document.forms.${screen.name}.__action.value = 'gotoCart'; document.forms.${screen.name}.submit();"><img src="generated-res/img/listview.png" alt=""/> View cart (${model.shoppingCart?keys?size})</button></div>
	
	<div class="buttons"><button type="submit" onclick="document.forms.${screen.name}.__action.value = 'emptyShoppingCart'; document.forms.${screen.name}.submit();"><img src="generated-res/img/cancel.png" alt=""/> Clear cart</button></div>
	
	<div class="buttons"><button class="positive" type="submit" onclick="document.forms.${screen.name}.__action.value = 'plotShoppingCart'; document.forms.${screen.name}.submit();"><img src="clusterdemo/icons/icon_plaintext_plot.png" alt=""/> Plot cart now</button></div>
	
	
	<br><br><br>
	<h3>Found ${model.hits?size} hits.</h3>
	
	<h4>
		<#if model.shortenedQuery??>
			<br>Your query was too specific for any hits, so it was shortened to <u>${model.shortenedQuery}</u>.
		</#if>
		
		<#if model.hits?? && model.hits?size == 100 && model.shortenedQuery??>
			<br>These results were limited to the first 100.
		<#elseif model.hits?? && model.hits?size == 100>
			<br>Your results were limited to the first 100. Please be more specific.
		<#else>
	
		</#if>
	</h4>

	
	<#if shopped gt 0>
		<#if shopped == model.hits?size>
			<h4>All ${shopped} hits are currently in your cart.</h4>
		<#else>
			<h4>Please note: ${shopped} hits are not shown because they are already in your cart.</h4>
		</#if>
	</#if>
	
	<#if shopped gt 0 && shopped == model.hits?size>
		<#-- do not show 'add all hits' button when there is nothing to be added -->
	<#else>
		<div class="buttons"><button type="submit" onclick="document.forms.${screen.name}.__action.value = 'shopAll'; document.forms.${screen.name}.submit();"><img src="generated-res/img/run.png" alt=""/><img src="clusterdemo/icons/shoppingcart.png" alt=""/> Add all hits to cart</button></div>
		<br><br>
	</#if>
	
	<br>
	
	
	<#--<input type="submit" id="shopAll" onclick="$(this).closest('form').find('input[name=__action]').val('shopAll');" value="Add all to cart" /><script>$("#shopAll").addClass('grayButton').button();</script><br><br>-->
	<#--<input type="submit" class="shop" value="" onclick="document.forms.${screen.name}.__action.value = 'shopAll'; document.forms.${screen.name}.submit();"><b><i>Add all to cart</b></i><br><br>-->
	
	<#list model.hits?keys as name>
	
	
	
	<#if model.shoppingCart?keys?seq_contains(name)>
		<#--<input type="submit" class="unshop" value="" onclick="document.forms.${screen.name}.__action.value = 'unshop'; document.forms.${screen.name}.__shopMeName.value = '${name}'; document.forms.${screen.name}.submit();">-->
		<#--<div class="buttons"><button type="submit" onclick="document.forms.${screen.name}.__action.value = 'unshop';document.forms.${screen.name}.__shopMeName.value = '${name}'; document.forms.${screen.name}.submit();"><img src="generated-res/img/select.png" alt=""/> (remove)</button></div>-->
	<#else>
		<div class="buttons"><button type="submit" onclick="document.forms.${screen.name}.__action.value = 'shop'; document.forms.${screen.name}.__shopMeId.value = '${model.hits[name].id?c}'; document.forms.${screen.name}.__shopMeName.value = '${name}'; document.forms.${screen.name}.submit();"><img src="clusterdemo/icons/shoppingcart.png" alt=""/> Add to cart</button></div>
		<#--<input type="submit" class="shop" value="" onclick="document.forms.${screen.name}.__action.value = 'shop'; document.forms.${screen.name}.__shopMeId.value = '${model.hits[name].id?c}'; document.forms.${screen.name}.__shopMeName.value = '${name}'; document.forms.${screen.name}.submit();">-->

		${model.hits[name].get(typefield)} <a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${name}'; document.forms.${screen.name}.submit();"><b>${name}<#if model.hits[name].label?? && model.hits[name].label?length gt 0> / ${model.hits[name].label}</#if></b></a>
		<#if model.hits[name].get('ReportsFor_name')?? && model.hits[name].get('ReportsFor_name')?is_string && model.hits[name].get('ReportsFor_name')?length gt 0>reports for <a target="_blank" href="molgenis.do?select=Genes&__target=Genes&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=Gene_name&__filter_operator=EQUALS&__filter_value=${model.hits[name].reportsFor_name}">${model.hits[name].reportsFor_name}</a></#if> <#if model.hits[name].symbol?? && model.hits[name].symbol?length gt 0>(${model.hits[name].symbol})</#if>
		
		<div style="display: inline;font-size:100%"><#if model.hits[name].description??> <br> <#if model.hits[name].description?length gt 70>${model.hits[name].description?substring(0, 70)} <#else>${model.hits[name].description}</#if> <a target="_blank" href="molgenis.do?select=${model.hits[name].get(typefield)}s&__target=${model.hits[name].get(typefield)}s&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=${model.hits[name].get(typefield)}_name&__filter_operator=EQUALS&__filter_value=${name}">...more</a><#else> <br> </#if></div>
		<br>
		<br>

	</#if>
		
		
	</#list>
	
	<#-->table cellpadding="10">
		<tr>
			<td>
				<input type="submit" class="shop" value="" onclick="document.forms.${screen.name}.__action.value = 'shopAll'; document.forms.${screen.name}.submit();"><b><i>Add all to cart</b></i>
			</td>
			<td>
				<input type="submit" class="unshop" value="" onclick="document.forms.${screen.name}.__action.value = 'emptyShoppingCart'; document.forms.${screen.name}.submit();"><b><i>Clear current cart</b></i>
			</td>
		</tr>
	</table-->
	</#if>
	
	
	</#if>
</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
