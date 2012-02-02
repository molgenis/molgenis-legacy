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

QTL finder v.2<br><br>

<div style="position:absolute;top:250px;left:100px;">
		<select class=" ui-widget-content ui-corner-all" id="Phenotype_select" name="dataTypeSelect"  style="width:220px;" name="dataTypeSelect">
			<option value=${allDataTypes} <#if model.selectedAnnotationTypeAndNr?? && model.selectedAnnotationTypeAndNr == allDataTypes>selected="selected"</#if>>All phenotypes (${model.annotationTypeAndNr[allDataTypes]})</option>
			<#list model.annotationTypeAndNr?keys as key>
				<#if key != allDataTypes>
					<option value="${key}" <#if model.selectedAnnotationTypeAndNr?? && model.selectedAnnotationTypeAndNr == key>selected="selected"</#if>>${key} (${model.annotationTypeAndNr[key]})</option>
				</#if>
			</#list>
		</select><script>$("#Phenotype_select").chosen();</script>
</div>
<div style="position:absolute;top:245px;left:300px;">

	<input type="text" name="query" class="searchBox" value="${query}" >
</div>

<div style="position:absolute;top:245px;left:550px;">
	<@action name="search" label="Search"/>
</div>

<br><br>





<#if model.shoppingCart?? && model.shoppingCart?size gt 0>


<div style="
position:absolute;
 top:200px;
 right:-200px;
 width:200px;
 z-index: 5;
">
SHOPPINGCART:<br><br>
<#list model.shoppingCart?keys as name>
<input type="submit" class="unshop" value="" onclick="document.forms.${screen.name}.__action.value = 'unshop'; document.forms.${screen.name}.__shopMeName.value = '${name}'; document.forms.${screen.name}.submit();">
<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${name}'; document.forms.${screen.name}.submit();">${name}</a><br>
</#list>

<br><br>
<input type="submit" value="PLOT" onclick="document.forms.${screen.name}.__action.value = 'plotShoppingCart'; document.forms.${screen.name}.submit();">
<input type="submit" value="EMPTY" onclick="document.forms.${screen.name}.__action.value = 'emptyShoppingCart'; document.forms.${screen.name}.submit();">



</div>


</#if>



<#if model.report??>
	<#import "../reportbuilder/ReportBuilder.ftl" as rb>
	
	<#assign r = model.report.entity>
	
	<table cellpadding="30">
		<tr>
			<td>
				<h1>${r.get(typefield)} "${r.name}"</h1>
				<h2>Record information</h2>
				<@rb.printEntity r=r/>
			</td>
		</tr>
		<tr>
			<td>
				<h2>Present in these matrices:</h2>
				<#list model.report.matrices as ml>
					<h2>"${ml.data.name}"</h2>
					<@rb.printEntity r=ml.data/>
					
					<br>
					<table cellpadding="3" border="1" style="width:700px;">
						<tr class="form_listrow1">
							<td>
								Total number of rows
							</td>
							<td>
								${ml.totalRows}
							</td>
						</tr>
						<tr class="form_listrow1">
							<td>
								Total number of columns
							</td>
							<td>
								${ml.totalCols}
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
						
						
					<h2>Plot of the values of ${r.name} in "${ml.data.name}"</h2>
					<h3>Row plot</h3>
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
						
					<h3>Column plot</h3>
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
					
					<h2>Spearman correlation results</h2>
					
					<h3>Row correlations</h3>
					
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
									<a href="#" onclick="document.forms.${screen.name}.__action.value = 'disambig_${key}'; document.forms.${screen.name}.submit();">${key}</a>
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
					
					<h3>Column correlations</h3>
						
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
									<a href="#" onclick="document.forms.${screen.name}.__action.value = 'disambig_${key}'; document.forms.${screen.name}.submit();">${key}</a>
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
	</table>











<#else>

	<#if model.multiplot??>

	<table cellpadding="30"><tr><td>
		<h2>Results for "${model.query}"</h2><br>
		
		<table><tr>
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
	
		<i>Details of all matches that are in the plot (hover for details):</i>
		<div style="overflow: auto; width: 780px; max-height: 400px;">
		<#list model.multiplot.matches?values as d>
			<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${d.name}'; document.forms.${screen.name}.submit();">${d.name}</a>
			<div style="display: inline;"><#if d.description??> - <#if d.description?length gt 70>${d.description?substring(0, 70)} <a href="molgenis.do?__target=${d.get(typefield)}s&__action=filter_set&__filter_attribute=${d.get(typefield)}_name&__filter_operator=EQUALS&__filter_value=${d.name}">...</a> <#else>${d.description}</#if></#if></div><br>
		</#list>
		</div>
		</td>
		</tr>
		</table>
	</td></tr></table>
	
	<#else>
	
	
	<br><br>
	
	
	
	<#if model.shortenedQuery??>SHORETEND QUERY: ${model.shortenedQuery}</#if>   <br><br>
	
	<#if model.hits??>
	HITS:   <br><br>
	
	<#if model.hits?size == 100>
	results limited to 100  <br><br>
	</#if>
	
	<#list model.hits?keys as name>
	
	
	
	<#if model.shoppingCart?keys?seq_contains(name)>
		<input type="submit" class="unshop" value="" onclick="document.forms.${screen.name}.__action.value = 'unshop'; document.forms.${screen.name}.__shopMeName.value = '${name}'; document.forms.${screen.name}.submit();">
	<#else>
		<input type="submit" class="shop" value="" onclick="document.forms.${screen.name}.__action.value = 'shop'; document.forms.${screen.name}.__shopMeId.value = '${model.hits[name].id?c}'; document.forms.${screen.name}.__shopMeName.value = '${name}'; document.forms.${screen.name}.submit();">
	</#if>
		
	<a href="#" onclick="document.forms.${screen.name}.__action.value = '__entity__report__for__${name}'; document.forms.${screen.name}.submit();">${name}</a>
	<div style="display: inline;"><#if model.hits[name].description??> - <#if model.hits[name].description?length gt 70>${model.hits[name].description?substring(0, 70)} <a href="molgenis.do?__target=${model.hits[name].get(typefield)}s&__action=filter_set&__filter_attribute=${model.hits[name].get(typefield)}_name&__filter_operator=EQUALS&__filter_value=${name}">...</a> <#else>${model.hits[name].description}</#if></#if></div><br>
	
	
	</#list>
	
	</#if>
	
	
	</#if>
</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
