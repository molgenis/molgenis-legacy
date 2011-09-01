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
		
		<div class="screenbody">
			<div class="screenpadding">		

<#assign vo = model>
<#assign queryParametersVO = vo.queryParametersVO>

<#include "searchForm.ftl">

<#if vo.action != "init">
<a name="results"><hr/></a>
<h3>Search results</h3>
<h4>${vo.header}</h4>
</#if>

<#if vo.action?starts_with("showProteinDomain")>

	<#include "mbrowse.ftl">

	${vo.rawOutput}

<#elseif vo.action?starts_with("showExon")>

	<#include "exon.ftl">

	<#include "mbrowse.ftl">

	${vo.rawOutput}

<#elseif vo.action?starts_with("showMutation")>

	<#include "mutation.ftl">

<#elseif vo.action?starts_with("showFirstMutation")>

	<#include "mutation.ftl">

<#elseif vo.action?starts_with("showPrevMutation")>

	<#include "mutation.ftl">

<#elseif vo.action?starts_with("showNextMutation")>

	<#include "mutation.ftl">

<#elseif vo.action?starts_with("showLastMutation")>

	<#include "mutation.ftl">

<#elseif vo.action?starts_with("showPatient")>

	<#if vo.geneName == "COL7A1">
		<#include "col7a1/patient.ftl">
	<#else>
		<#include "patient.ftl">
	</#if>

<#elseif vo.action?starts_with("showPhenotypeDetails")>

	<#include "phenotypedetails.ftl">

<#elseif vo.action?starts_with("findMutationsByTerm")>

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
	

<#elseif vo.action?starts_with("findPatients")>

	<#assign rawOutput = vo.rawOutput>
	${rawOutput}

<#elseif vo.action?starts_with("find")>

	${vo.rawOutput}

<#elseif vo.action?starts_with("listAllMutations") || vo.action?starts_with("mutations")>

	${vo.rawOutput}

<#elseif vo.action?starts_with("listAllPatient") || vo.action?starts_with("patients")>

	<#assign rawOutput = vo.rawOutput>
	${rawOutput}

</#if>

<#-- <#include "displayOptions.ftl"> -->

			</div>
		</div>
	</div>