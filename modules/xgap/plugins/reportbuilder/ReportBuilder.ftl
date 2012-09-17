<#macro ReportBuilder screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<!--need to be set to "true" in order to force a download-->
	<input type="hidden" name="__show">
	
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

<table cellpadding="30">
	<tr>
		<td>
			<h1>Report builder</h1>
			<ul>
			<li>Enter the name of your concept of interest and click 'Go'.</li>
			<li>The search will find part of names (e.g. 'drox' will match 'Hydroxypropyl' and 'Hydroxybutyl'), though is case-sensitive, so 'hydro' does not match, but 'Hydro' will.</li>
			<li>You will be prompted to disambiguate your search term if multiple matches occur.</li>
			</ul>
		</td>
	</tr>
	<tr>
		<td>
			Select
			<select name="dataTypeSelect">
				<#list model.annotationTypeAndNr?keys as key>
				<option value="${key}" <#if model.selectedAnnotationTypeAndNr?? && model.selectedAnnotationTypeAndNr == key>selected="selected"</#if>>${key} (${model.annotationTypeAndNr[key]})</option>
				</#list>
			</select>
			
			<input type="text" name="entityName" <#if model.selectedName??>value="${model.selectedName}"</#if> />
			
			<input type="submit" value="Go" onclick="document.forms.${screen.name}.__action.value = 'buildReport'; document.forms.${screen.name}.submit();"/>
		</td>
	</tr>
	<#if model.disambiguate??>
	<tr>
		<td>
			<h2>Which ${model.selectedAnnotationTypeAndNr} did you mean?</h2>
			<ul>
				<#list model.disambiguate as d>
					<li><a href="#" onclick="document.forms.${screen.name}.__action.value = 'disambig_${d.name}'; document.forms.${screen.name}.submit();">${d.name}</a></li>
				</#list>
			</ul>
		</td>
	</tr>
	</#if>
</table>

<#if model.report??>
<#assign r = model.report.entity>

<table cellpadding="30">
	<tr>
		<td>
			<h1>${model.selectedAnnotationTypeAndNr} "${model.selectedName}"</h1>
			<h2>Record information</h2>
			<@printEntity r=r/>
		</td>
	</tr>
	<tr>
		<td>
			<h2>Present in these matrices:</h2>
			<#list model.report.matrices as ml>
				<h2>"${ml.data.name}"</h2>
				<@printEntity r=ml.data/>
				
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
							"${model.selectedName}" present at row index
						</td>
						<td>
							<#if ml.rowIndex == -1>Not present in rows<#else>${ml.rowIndex}</#if>
						</td>
					</tr>
					<tr class="form_listrow1">
						<td>
							"${model.selectedName}" present at column index
						</td>
						<td>
							<#if ml.colIndex == -1>Not present in columns<#else>${ml.colIndex}</#if>
						</td>
						
					</tr>
				</table>
					
					
				<h2>Plot of the values of ${model.selectedName} in "${ml.data.name}"</h2>
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
						${model.selectedName} not in row values, no row plot was made.
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
						${model.selectedName} not in column values, no column plot was made.
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
						${model.selectedName} not in row values, no row correlations were calculated.
					<#else>
						<#if (ml.totalCols * ml.totalRows) gt 1000000>
							More than 1 million values in the matrix, no correlations were calculated.
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
						${model.selectedName} not in column values, no column correlations were calculated.
					<#else>
						<#if (ml.totalCols * ml.totalRows) gt 1000000>
							More than 1 million values in the matrix, no correlations were calculated.
						<#else>
							Correlation failed.
						</#if>
					</#if>
				</#if>
				
				
			</#list>
		</td>
	</tr>
</table>





</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>


<#macro printEntity r>
<table cellpadding="3" border="1" style="width:700px;">
	<tr class="form_listrow0">
		<td>
			<b>Field</b>
		</td>
		<td>
			<b>Value</b>
		</td>
	</tr>
	<#list r.getFields() as f>
	<#if r.get(f)?exists>
	<tr class="form_listrow1">
		<td>
			${f}
		</td>
		<td>
		<#if r.get(f)?is_string || r.get(f)?is_date || r.get(f)?is_number>
			${r.get(f)}
		<#elseif r.get(f)?is_boolean>
			<#if r.get(f) == true>true<#else>false</#if>
		<#elseif r.get(f)?is_enumerable>
			<#list r.get(f) as i>
				${i} 
				<#if i_index == 3>
				...<#break>
				</#if>
			</#list>
		<#else>
			TYPE NOT SUPPORTED, CONTACT JOERI
		</#if>
		</td>
	</tr>
	</#if>
	</#list>
</table>
</#macro>

<#macro printEntityText r>
<@compress single_line=true>
<u><b>Field</b>: Value</u><br>
<#list r.getFields() as f>
	<#if r.get(f)?exists>
		<b>${f}</b>: 
		<#if r.get(f)?is_string || r.get(f)?is_date || r.get(f)?is_number>
			${r.get(f)}
		<#elseif r.get(f)?is_boolean>
			<#if r.get(f) == true>true<#else>false</#if>
		<#elseif r.get(f)?is_enumerable>
			<#list r.get(f) as i>
				${i} 
				<#if i_index == 3>
				...<#break>
				</#if>
			</#list>
		<#else>
			TYPE NOT SUPPORTED, CONTACT JOERI
		</#if>
		<br>
	</#if>
</#list>
</@compress>
</#macro>

<#macro printEntityTextClean r>
<@compress single_line=true>
<#list r.getFields() as f>
	<#-- dont print '__Type', 'name', 'Investigation_name', booleans, dates or numbers-->
	<#if r.get(f)?exists && f != '__Type' && f != 'name' && f != 'Investigation_name'>
		<#if r.get(f)?is_string && r.get(f)?length gt 0>
			<#list r.get(f)?split(' ') as sp>
				<#if sp?length gt 1>${sp?substring(0,1)?upper_case + sp?substring(1,sp?length)} </#if>
			</#list>
		<#elseif r.get(f)?is_enumerable>
			<#list r.get(f) as i>
				<#if i?is_string && i?length gt 0>
					<#list i?split(' ') as sp>
						<#if sp?length gt 1>${sp?substring(0,1)?upper_case + sp?substring(1,sp?length)} </#if>
					</#list>
				<#else>
					<#-- ignore non string stuff-->
				</#if>
			</#list>
		</#if>
	</#if>
</#list>
</@compress>
</#macro>