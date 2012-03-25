<#include "header.ftl">

	<#if vo.result == "mutations">
		<#assign resultHash = vo.mutationSummaryVOHash>
		<#list resultHash?keys as field>
			<#if field?starts_with(" ")>
				<#assign rawOutput = resultHash[field]>
<p>
<img id="catimg${field}" src="res/img/open.png" onclick="toggleDiv('cat${field}', 'catimg${field}');">
${vo.result} found in "${field}"<#-- (total ${screen.getNumPatients(pager.entities)} patients)-->.
</p>
<div id="cat${field}" style="display:none">
				${rawOutput}
</div>
			</#if>
		</#list>
	<#else>
		<#assign resultHash = vo.patientSummaryVOHash>
		<#list resultHash?keys as field>
			<#if field?starts_with(" ")>
				<#assign rawOutput = resultHash[field]>
<p>
<img id="catimg${field}" src="res/img/open.png" onclick="toggleDiv('cat${field}', 'catimg${field}');">
${vo.result} found in "${field}".
</p>
<div id="cat${field}" style="display:none">
				${rawOutput}
</div>
			</#if>
		</#list>
	</#if>

<#-- <#include "displayOptions.ftl"> -->

<#include "footer.ftl">