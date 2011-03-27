<!--Date:        June 2, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenFTLTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro plugins_cluster_ClusterPlugin screen>
<#assign model = screen.model>

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

<input type="hidden" name="jobToDelete">

<input type="hidden" name="resubmitJob">
<input type="hidden" name="resubmitSubjob">

<#assign padding = "5px">
<#assign borderStyle = "solid">
<#assign borderWidthSpacing = "1px">

<#assign green = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #808080; background-color: #00FF00; color: #000000;\"">
<#assign orange = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #808080; background-color: #FF8040; color: #FFFFFF;\"">
<#assign red = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #808080; background-color: #FF0000; color: #FFFFFF;\"">
<#assign yellow = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #808080; background-color: #FFFF00; color: #000000;\"">
<#assign blue = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #808080; background-color: #0000FF; color: #FFFFFF;\"">
<#assign grey = "style=\"padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #808080; background-color: #C0C0C0; color: #FFFFFF;\"">


<#assign bgColor = "#ffffff">
<#assign style1 = "style=\"text-align: center; padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #808080; background-color: ${bgColor};\"">
<#assign style2 = "style=\"text-align: center; padding: ${padding}; border-width: ${borderWidthSpacing}; border-spacing: ${borderWidthSpacing}; border-style: ${borderStyle}; border-color: #808080; background-color: ${bgColor};\"">


<#if model.state="main">
<br>
<h1>Run QTL analysis jobs</h1><br><br>
<input type="submit" value="Create new job" onclick="__action.value='newClusterJob';return true;">
<br><br>
<input type="submit" value="Job manager" onclick="__action.value='viewJobManager';return true;">
<br><br>

<table>
	<tr>
		<td>
			Compute resource:
		</td>
		<td>
			<input type="radio" name="computeResource" value="local" checked>Local<br>
			<input type="radio" name="computeResource" value="cluster">Cluster<br>
			<input type="radio" DISABLED name="computeResource" value="cloud">Cloud<br>
			<input type="radio" DISABLED name="computeResource" value="image">Image
		</td>
	</tr>
</table>

<#elseif model.state="newjob1">
<br>
<h3>Step 1</h3><br><br>
<table>
	<tr>
		<td>
			Enter name of output datamatrix:
		</td>
		<td>
			<input type="text" name="outputDataName" value="MyOutput"/>
		</td>
	</tr>
	<tr>
		<td>
			Select analysis type:
		</td>
		<td>
			<select name="selectedAnalysis">
				<#list model.analysis as a>
					<option value="${a.getId()?c}">${a.getName()}</option>
				</#list>
			</select>
		</td>
	</tr>
	<tr>
		<td>
			Number of subjobs (limited to 50):
		</td>
		<td>
			<select name="nJobs">
				<option value="1">1</option>
				<option value="2">2</option>
				<option value="5" SELECTED>5</option>
				<option value="10">10</option>
				<option value="15">15</option>
				<option value="20">20</option>
				<option value="25">25</option>
				<option value="50">50</option>
				<!--option value="100">100</option>
				<option value="150">150</option>
				<option value="200">200</option>
				<option value="250">250</option-->
			</select>
		</td>
	</tr>
	<#if model.selectedComputeResource == "cluster" || model.selectedComputeResource == "cloud">
	<tr>
		<td>
			&nbsp;
		</td>
		<td>
			&nbsp;
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<b>Login settings:</b><br>
			<font color="red"><i>Leave empty to use guest account - demo purpose only</i></font>
		</td>
	</tr>
	<tr>
		<td>
			Adress:
		</td>
		<td>
			<input type="text" name="serverAdress" value="millipede.service.rug.nl"/>
		</td>
	</tr>
	<tr>
		<td>
			Port:
		</td>
		<td>
			<input type="text" name="serverPort" value="22">
		</td>
	</tr>
	<tr>
		<td>
			User:
		</td>
		<td>
			<input type="text" name="serverUser" value="">
		</td>
	</tr>
	<tr>
		<td>
			Password:
		</td>
		<td>
			<input type="password" name="serverPassword" value="">
		</td>
	</tr>
	</#if>
</table>
	
<br>

<input type="submit" value="Previous" onclick="__action.value='goBack';return true;"/>
<input type="submit" value="Next" onclick="__action.value='toStep2';return true;"/>

	
<#elseif model.state="newjob2">
<br>
<b>Step 2</b><br>
<br>

Select input data:<br>
<table>
<#list model.datanames as d_n>
<tr>
<td>
${d_n.getName()}:
</td>
<td>
	<#--select name="dataNameID${d_n.getId()}"-->
	<select name="${d_n.getName()}">
		<#list model.datavalues as d_v>
			<#if d_v.dataname == d_n.getId()>
				<#--option value="dataValueID${d_v.getId()}">${d_v.getName()}</option-->
				<option value="${d_v.getValue()?c}">${d_v.getName()}</option>
			</#if>
		</#list>
	</select>
</td>
</tr>
</#list>
</table>
<br>
Select parameters:<br>
<table>
<#list model.parameternames as p_n>
<tr><td>
${p_n.getName()}:
</td><td>
	<#--select name="parameterNameID${p_n.getId()}"-->
	<select name="${p_n.getName()}">
		<#list model.parametervalues as p_v>
			<#if p_v.parametername == p_n.getId()>
				<#--option value="parameterValueID${p_v.getId()}">${p_v.getName()}</option-->
				<option value="${p_v.getValue()}">${p_v.getName()}</option>
			</#if>
		</#list>
	</select>
</td></tr>
</#list>
</table>
<br>
<input type="submit" value="Previous" onclick="__action.value='toStep1';return true;"/>
<input type="submit" value="Start" onclick="__action.value='startClusterJob';return true;"/>

<br>

<#elseif model.state="jobmanager">
<br>Refresh page every
<select name="chosenRefresh">
	<option value="5" <#if model.refreshRate == '5'>SELECTED</#if>>5</option>
	<option value="15" <#if model.refreshRate == '15'>SELECTED</#if>>15</option>
	<option value="30" <#if model.refreshRate == '30'>SELECTED</#if>>30</option>
	<option value="60" <#if model.refreshRate == '60'>SELECTED</#if>>60</option>
	<option value="off" <#if model.refreshRate == 'off'>SELECTED</#if>>Off</option>
</select>
seconds.
<input type="submit" value="Change" onclick="__action.value='changeRefresh';return true;"/>

<br>
<div style="text-align: center; overflow: scroll;">
<table class="listtable">
	<#if model.maxSubjobs == 0>
		<tr>
			<td>
				No jobs to display.
			</td>
		</tr>
	<#else>
	
	<tr class="form_listrow0">
		<td colspan="7">
			<font size="3"><b>Jobs</b></font>
		</td>
		<td colspan="${model.maxSubjobs}">
			<font size="3"><b>Subjobs</b></font><br>
			<font size="1"><nobr>Hover for status</nobr><br>
			<nobr>Click to resubmit</nobr></font>
		</td>
	</tr>
	
	<tr class="form_listrow0">
		<td>
			<nobr><b>ID</b></nobr>
		</td>
		<td>
			<nobr><b>Analysis</b></nobr>
		</td>
		<td>
			<nobr><b>Output</b></nobr>
		</td>
		<td>
			<nobr><b>Run location</b></nobr>
		</td>
		<td>
			<nobr><b>Start date/time</b></nobr>
		</td>
		<td>
			<nobr><b>All info</b></nobr>
		</td>
		<td>
			<nobr><b>Remove</b></nobr>
		</td>
		
		<#list 0..(model.maxSubjobs-1) as x>
		<td width="10">
			<nobr><div style="display: inline; font-size: x-small;"><b>${x}</b></div></nobr>
		</td>
		</#list>
	</tr>
	
	<#list model.jobs as t>
	<tr class="form_listrow1">
		<td style="vertical-align:middle;">
			<nobr>${t.getId()?c}</nobr>
		</td>
		<td style="vertical-align:middle;">
			<nobr><#if t.analysis_name?length gt 15>${t.analysis_name?substring(0, 15)}...<#else>${t.analysis_name}</#if></nobr>
		</td>
		<td style="vertical-align:middle;">
			<#--nobr><#if t.outputDataName?length gt 10>${t.outputDataName?substring(0, 10)}...<#else>${t.outputDataName}</#if></nobr-->
			<nobr><#if t.outputDataName?length gt 10><a href="/${model.deployName}/molgenis.do?__target=Datas&__action=filter_set&__filter_attribute=Data_name&__filter_operator=EQUALS&__filter_value=${t.outputDataName}">${t.outputDataName?substring(0, 10)}...</a><#else><a href="/${model.deployName}/molgenis.do?__target=Datas&__action=filter_set&__filter_attribute=Data_name&__filter_operator=EQUALS&__filter_value=${t.outputDataName}">${t.outputDataName}</a></#if></nobr>			
		</td>
		<td style="vertical-align:middle;">
			<nobr>${t.computeResource}</nobr>
		</td>
		<td style="vertical-align:middle;">
			<nobr>${t.timeStamp}</nobr>
		</td>
		<td style="vertical-align:middle;">
			<div style="display: inline; font-size: x-small; text-align: center;" onmouseover="return overlib('${model.getJobParamMap()[t.getId()?c]}', CAPTION, 'Settings:')" onmouseout="return nd();"><nobr><u>[hover]</u></nobr></div>
		</td>
		<td style="vertical-align:middle;">
			<input type="image" src="generated-res/img/exit.bmp" title="Delete job" onclick="if (confirm('You are about to delete this job. Are you sure?')) { document.forms.${screen.name}.jobToDelete.value = '${t.getId()?c}'; document.forms.${screen.name}.__action.value = 'deleteJob'; } else { return false; }"/>
		</td>
		<#assign countJobs = 0>
		<#list model.subjobs as j>
			<#if j.job == t.getId()>
				<#assign countJobs = countJobs+1>
					<#if j.statuscode == -1>
						<#if j.statustext == "undefined columns selected">
							<#assign color = grey>
						<#else>
							<#assign color = red>
						</#if>
					<#elseif j.statuscode == 0>
						<#assign color = orange>
					<#elseif j.statuscode == 1>
						<#assign color = yellow>
					<#elseif j.statuscode == 2>
						<#assign color = blue>
					<#elseif j.statuscode == 3>
						<#assign color = green>
					<#else>
						<#assign color = red>
					</#if>
					<td ${color}>
						<div align="center" style="display: inline; font-size: x-small;" <#if j.nr != 0 && j.statuscode != 3>onclick="document.forms.${screen.name}.resubmitSubjob.value = '${j.getId()?c}'; document.forms.${screen.name}.__action.value = 'resubmitSubjob'; document.forms.${screen.name}.submit();"</#if> onmouseover="return overlib('${j.statustext}', CAPTION, 'Status:')" onmouseout="return nd();"><b>${j.statuscode}</b></div>
					</td>
			</#if>
		</#list>
		<#if countJobs < model.maxSubjobs>
			<#list 1..(model.maxSubjobs-countJobs) as x>
			<td>
				<!--empty-->
			</td>
			</#list>
		</#if>
	</tr>
	</#list>
	
	</font>
	
	</#if>
</table>
</div>
<br><br>
<font color="red"><i>Resubmission of subjobs on cluster not possible due to demo constraints</i></font>
<br><br>
<input type="submit" value="Go back" onclick="__action.value='goBack';return true;"/>
<br>

</#if>





<br><br>
_____________________<br>
<i>Current login settings:</i><br>
<#if model.ls?exists && model.ls.host?exists>
	Adress: ${model.ls.host}<br>
	Port: ${model.ls.port}<br>
	User: <#if model.ls.user?exists>${model.ls.user}<#else>-</#if><br>
	Password: <#if model.ls.password?exists><#list 1..(model.ls.password?length) as x>*</#list><#else>-</#if><br>
<#else>
	No settings saved.<br>
</#if>


<br><br>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
