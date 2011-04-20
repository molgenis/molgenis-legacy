<#macro org_molgenis_mutation_ui_search_Chd7Search screen>
<#if show == "popup">
		<@molgenis_header />
</#if>
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

<#assign vo = screen.searchPluginVO>
<#assign queryParametersVO = vo.queryParametersVO>

<#include "searchForm.ftl">

<#if vo.action != "init">
<a name="results"><hr/></a>
<h3>Search results</h3>
<h4>${vo.header}</h4>
</#if>

<#if vo.action?starts_with("showProteinDomain")>

	<#include "proteinDomain.ftl">
	<#include "mutations.ftl">

<#elseif vo.action?starts_with("showExon")>

	<#include "exon.ftl">

<#elseif vo.action?starts_with("showMutation")>

	<#include "mutation.ftl">

<#elseif vo.action?starts_with("showPatient")>

	<#include "patient.ftl">

<#elseif vo.action?starts_with("showPhenotypeDetails")>

	<#include "chd7phenotypedetails.ftl">

<#elseif vo.action?starts_with("findMutationsByTerm")>

	<#assign mutationSummaryVOHash = vo.mutationSummaryVOHash>

	<#list mutationSummaryVOHash?keys as key>
		<#assign pager = mutationSummaryVOHash[key]>
		<p>
		<#if pager.entities?size &gt; 0><img id="catimg${key}" src="res/img/open.png" onclick="toggleDiv('cat${key}', 'catimg${key}');"></#if>
		${pager.entities?size} mutations found in "${key}" (total ${screen.getNumPatients(pager.entities)} patients).
		</p>
		<div id="cat${key}" style="display:none">
		${vo.setPager(pager)}
		<#include "mutations.ftl">
		</div>
	</#list>

<#elseif vo.action?starts_with("findPatients")>

	<#include "patients.ftl">

<#elseif vo.action?starts_with("find")>

	<#include "mutations.ftl">

<#elseif vo.action?starts_with("listAllMutations") || vo.action?starts_with("mutations")>

	<#include "mutations.ftl">

<#elseif vo.action?starts_with("listAllPatient") || vo.action?starts_with("patients")>

	<#include "patients.ftl">

</#if>

<#-- <#include "displayOptions.ftl"> -->

			</div>
		</div>
	</div>
<#if show == "popup">
		<@molgenis_footer />
</#if>
</#macro>
