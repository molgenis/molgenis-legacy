<#macro org_molgenis_core_ui_AllPublications screen>
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

<table class="listtable" cellpadding="4">
<tr class="tableheader"><th>No</th><th>Title</th><th>External link</th></tr>
<#list screen.publicationVOs as publicationVO>
<#if screen.publicationVOs?seq_index_of(publicationVO) % 2 == 0>
	<#assign clazz = "form_listrow1">
<#else>
	<#assign clazz = "form_listrow0">
</#if>
<tr class="${clazz}"><td>${screen.publicationVOs?seq_index_of(publicationVO) + 1}</td><td>${publicationVO.getTitle()}</td><td><a href="${publicationVO.getPubmed()}" target="_new">Pubmed</a></td></tr>
</#list>
</table>

			</div>
		</div>
	</div>
</#macro>
