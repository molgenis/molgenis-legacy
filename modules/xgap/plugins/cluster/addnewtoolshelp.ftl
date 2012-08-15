<#macro plugins_cluster_addnewtoolshelp screen>
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

<i>Overview:</i><br>
Here you can input custom R scripts. These are R scripts which output R scripts in a template-like fashion by using logic around input parameters. This system is subject to be overhauled due to excessive complexity.<br>
<br>
<i>Manual:</i><br>
<a href="http://www.xgap.org/wiki/xQTLDemoOwnAnalysis">http://www.xgap.org/wiki/xQTLDemoOwnAnalysis</a><br>
<br>
<i>Example scripts:</i><br>
<a href="api/R/plugins/cluster/R/ClusterJobs/R/MINjob.R">Minimal job</a><br>
<a href="api/R/plugins/cluster/R/ClusterJobs/R/MINjob.R">QTL job</a><br>
<br>
<i>Important notes:</i><br>
The script is added to the list of sourced R scripts under the <a href="api/R">R API</a><br>
This results in a big heap of functions (format: <b>run_MIN</b> where MIN is variable) in an R instance with unique names.<br>
The function names are mapped to TargetFunctionName in Analysis objects in the database. So a target function name could be 'MIN'.<br>
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
