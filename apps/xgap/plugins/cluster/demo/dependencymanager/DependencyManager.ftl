<#macro plugins_cluster_demo_dependencymanager_DependencyManager screen>
<#assign model = screen.myModel>
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
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->	


<h2>R-package dependency installer</h2>

<table class="listtable">
	<tr class="form_listrow0">
		<td>
			<b>Local dependency</b>
		</td>
		<td>
			<b>Installed?</b>
		</td>
		<td>
			<b>Attempt install</b>
		</td>
	</tr>
	<tr class="form_listrow1">
		<td>
			R/qtl (package 'qtl')
		</td>
		<td>
			<#if model.rqtl == true><p class="successmessage">Yes</p><#else><p class="errormessage">No</p></#if>
		</td>
		<td>
			<#if model.rqtl == false><input type="submit" value="Install" onclick="__action.value='installQtl';return true;"/><#else>N/A</#if>
		</td>
	</tr>
	<tr class="form_listrow1">
		<td>
			Curl + RCurl (package 'RCurl')
		</td>
		<td>
			<#if model.rcurl == true><p class="successmessage">Yes</p><#else><p class="errormessage">No</p></#if>
		</td>
		<td>
			<#if model.rcurl == false><input type="submit" value="Install" onclick="__action.value='installRcurl';return true;"/><#else>N/A</#if>
		</td>
	</tr>
	<tr class="form_listrow1">
		<td>
			Bitops (packages 'bitops')
		</td>
		<td>
			<#if model.bitops == true><p class="successmessage">Yes</p><#else><p class="errormessage">No</p></#if>
		</td>
		<td>
			<#if model.bitops == false><input type="submit" value="Install" onclick="__action.value='installBitops';return true;"/><#else>N/A</#if>
		</td>
	</tr>
	<tr class="form_listrow1">
		<td>
			QTLbim (packages 'qtlbim')
		</td>
		<td>
			<#if model.qtlbim == true><p class="successmessage">Yes</p><#else><p class="errormessage">No</p></#if>
		</td>
		<td>
			<#if model.qtlbim == false><input type="submit" value="Install" onclick="__action.value='installQtlbim';return true;"/><#else>N/A</#if>
		</td>
	</tr>
	<#-- no longer needed>
	<tr class="form_listrow1">
		<td>
			ClusterJobs (from source)
		</td>
		<td>
			<#if model.clusterjobs == true><p class="successmessage">Yes</p><#else><p class="errormessage">No</p></#if>
		</td>
		<td>
			<#if model.clusterjobs == false><input type="submit" value="Install" onclick="__action.value='installClusterJobs';return true;"/><#else>N/A</#if>
		</td>
	</tr>
	-->
</table>	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
