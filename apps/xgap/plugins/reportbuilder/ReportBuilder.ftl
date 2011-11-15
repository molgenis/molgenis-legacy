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

<br><br><br>

Search box will find substrings (e.g. 'drox' will match 'Hydroxypropyl' and 'Hydroxybutyl') but is case-sensitive, so 'hydro' won't match but 'Hydro' will.<br><br>

Select
<select name="dataTypeSelect">
	<#list model.annotationTypeAndNr?keys as key>
	<option value="${key}" <#if model.selectedAnnotationTypeAndNr?? && model.selectedAnnotationTypeAndNr == key>selected="selected"</#if>>${key} (${model.annotationTypeAndNr[key]})</option>
	</#list>
</select>

<input type="text" name="entityName" <#if model.selectedName??>value="${model.selectedName}"</#if> />

<input type="submit" value="Go" onclick="document.forms.${screen.name}.__action.value = 'buildReport'; document.forms.${screen.name}.submit();"/>

<br><br><br>

<#if model.disambiguate??>

Which ${model.selectedAnnotationTypeAndNr} did you mean?
<ul>
	<#list model.disambiguate as d>
		<li><a href="#" onclick="document.forms.${screen.name}.__action.value = 'disambig_${d.name}'; document.forms.${screen.name}.submit();">${d.name}</a></li>
	</#list>
</ul>

<br><br><br>
</#if>


<#if model.report??>

${model.report.entity.toString()}
<#assign r = model.report.entity>

<table cellpadding="5">
	<tr>

		<#list r.getFields() as f>
			<#if f != "__Type">
			<td><b>${f}</b></td>
			</#if>
		</#list>
	</tr>
	<tr>
	
	<#list r.getFields() as f>
			<#if f != "__Type">
			<td>
				<#if r.get(f)?exists>
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
				</#if>
			</td>
			</#if>
		</#list>
	</tr>
</table>

<br>

Present in these matrices:<br><br>
<#list model.report.matrices as ml>
	${ml.data}<br>
	${ml.colIndex}<br>
	${ml.rowIndex}<br>
	${ml.totalRows}<br>
	${ml.totalCols}<br><br><br>
	
	<#if ml.rowImg??>
		<br><table><tr><td><i>ROW IMG Click to enlarge</i></td></tr></table>
		<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + ml.rowImg + "></body></html>">
		<a href="#" onclick="var generate = window.open('', '', 'width=850,height650,resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
			<img src="tmpfile/${ml.rowImg}" width="160" height="120">
		</a>
	</#if>
	
	<#if ml.colImg??>
		<br><table><tr><td><i>COL IMG Click to enlarge</i></td></tr></table>
		<#assign html = "<html><head><title>Legend</title></head><body><img src=tmpfile/" + ml.colImg + "></body></html>">
		<a href="#" onclick="var generate = window.open('', '', 'width=850,height650,resizable=yes,toolbar=no,location=no,scrollbars=yes');  generate.document.write('${html}'); generate.document.close(); return false;">
			<img src="tmpfile/${ml.colImg}" width="160" height="120">
		</a>
	</#if>
	
	<br>
	
	<#if ml.rowCorr??>
		<#list ml.rowCorr?keys as key>
			${key} has row corr <#if ml.rowCorr[key]??>${ml.rowCorr[key]}<#else>N/A</#if> <br>
		</#list>
	</#if>
	
	<#if ml.colCorr??>
		<#list ml.colCorr?keys as key>
			${key} has col corr <#if ml.colCorr[key]??>${ml.colCorr[key]}<#else>N/A</#if> <br>
		</#list>
	</#if>
	
</#list>



</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
