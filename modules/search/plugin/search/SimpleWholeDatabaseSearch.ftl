<#macro plugin_search_SimpleWholeDatabaseSearch screen>
<#if screen.myModel?exists>
	<#assign modelExists = true>
	<#assign model = screen.myModel>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>

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
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->

<h3><u>Find</u></h3>
<input type="text" name="searchThis" <#if model.searchThis?exists>value="${model.searchThis}"</#if>/>
<input type="submit" id="simple_search" value="Go" onclick="__action.value='doSearch';return true;"/><br>
<br>
<br>
<#if model.results?exists>
<h3><u>Results</u></h3>
Found ${model.results?size} result(s) in ${model.time} seconds.
<#assign currentType = "null">
<#list model.results as r>
	<#if currentType != r.get(typefield)>
	<#if currentType != "null">
		</table></div>
	</#if>
	<h4>Type: <i>${r.get(typefield)}</i></h4>
	<div style="width:inherit;overflow:auto;">
	<table border="1" bgcolor="#F5F5F5" bordercolor="silver" cellpadding="3">
		<tr>
		<td><i><b><nobr>Go there</nobr></b></i></td>
		<#list r.getFields() as f>
			<#if f != typefield>
			<td><b>${f}</b></td>
			</#if>
		</#list>
		</tr>
	</#if>
	<tr>
	<td>
		<a href="?__target=${r.get(typefield)}s&__action=filter_set&__filter_attribute=${r.get(typefield)}_id&__filter_operator=EQUALS&__filter_value=${r.get("id")}"> <b>Link<b> </a>
	</td>
		<#list r.getFields() as f>
			<#if f != typefield>
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
	<#assign currentType = r.get(typefield)>
</#list>
</table></div>
</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
