<!--Date:        June 2, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenFTLTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro plugins_cluster_ClusterPlugin screen>
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
<h1>Run QTL mapping</h1><br>
This is the main menu for starting a new analysis or viewing the progress of running analysis. Analyses use preconfigured sets of data and parameters, and calculate results using R scripts. When successfully completed, you can continue to browse the results. For more information, take a look at <a target="_blank" href="http://www.molgenis.org/wiki/xQTLBiologistRun">the xQTL wiki</a>.<br><br><br>
<input type="submit" value="Start new analysis" id="start_new_analysis" onclick="__action.value='newClusterJob';return true;">
<br><br>
<input type="submit" value="View running analysis" id="view_running_analysis" onclick="__action.value='viewJobManager';return true;">
<br><br>

<table>
	<tr>
		<td>
			Select compute resource:
		</td>
		<td>
			<input type="radio" name="computeResource" value="local" checked>Local<br>
			<input type="radio" name="computeResource" value="cluster">Cluster<br>
			<!--input type="radio" DISABLED name="computeResource" value="bot">IRC BOT network<br>
			<input type="radio" DISABLED name="computeResource" value="cloud">Cloud<br>
			<input type="radio" DISABLED name="computeResource" value="image">Image-->
		</td>
	</tr>
</table>

<#elseif model.state="newjob1">
<br>
<h3>Step 1</h3>Define result, choose analysis and in how many parts the computations should be chopped up. <a target="_blank" href="http://www.molgenis.org/wiki/xQTLBiologistRun">More detail</a>.<br><br>
<table>
	<tr>
		<td>
			Provide a name for the result:
		</td>
		<td>
			<input type="text" name="outputDataName" value="MyOutput"/>
		</td>
	</tr>
	<tr>
		<td>
			Select the analysis to be run:
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
			Select amount of divided parts:
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
			</select>
			<#--if model.selectedComputeResource == "cluster">
			or choose a queue
			<select name="clusterQueue">
				<option value="jobs" SELECTED># of sub jobs</option>
				<option value="short">short queue</option>
				<option value="nodes">normal queue</option>
				<option value="nodeslong">long queue</option>
				<option value="quadlong">long queue (Quad Cores)</option>
			</select>
			</#if-->
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

<input type="submit" id="toStep0" value="Previous" onclick="__action.value='goBack';return true;"/>
<input type="submit" id="toStep2" value="Next" onclick="__action.value='toStep2';return true;"/>

	
<#elseif model.state="newjob2">
<br>
<b>Step 2</b><br>
<br>

You have selected: <b>${model.selectedAnalysis.name}</b><br><br>
<#if model.selectedAnalysis.description?exists><i>${model.selectedAnalysis.description}</i><br><br></#if>

<#if model.datanames?size gt 0>
	Select input data:<br>
	<table cellpadding="2">
		<tr>
			<!--td><b>Split by</b></td-->
		</tr>
		<#list model.datanames as d_n>
		<tr>
			<td>
				${d_n.getName()}:
			</td>
			<td>
				<#--select name="dataNameID${d_n.getId()}"-->
				<select name="${d_n.getName()}">
					<#list model.datavalues as d_v>
						<#if d_v.getValue_Id()??>
						
						<#-- HACK FOR JPA!?!?
						<#if d_v.dataname_id?is_string>
							<#assign dv_dnid = d_v.dataname>
						<#else>
							<#assign dv_dnid = d_v.dataname_id>
						</#if>
						-->
						<#if d_v.dataName_Id == d_n.getId()>
							<#--option value="dataValueID${d_v.getId()}">${d_v.getName()}</option-->
							<option value="${d_v.getValue_Id()?c}">${d_v.getName()}</option>
						</#if>
						</#if>
					</#list>
				</select>
			</td>
		<!--td>
			<#if d_n.getName() == "phenotypes">
			<INPUT id="iterator" type="radio" value="${d_n.getName()}_r" name="itteratorGroup">rows
			<INPUT id="iterator" type="radio" value="${d_n.getName()}_c" name="itteratorGroup" CHECKED>cols<BR>
			<#else>
			<INPUT id="iterator" type="radio" value="${d_n.getName()}_r" name="itteratorGroup">rows
			<INPUT id="iterator" type="radio" value="${d_n.getName()}_c" name="itteratorGroup">cols<BR>
			</#if>
		</td-->
		</tr>
		</#list>
	</table>
	<br>
</#if>

<#if model.parameternames?size gt 0>
	Select parameters:<br>
	<table cellpadding="2">
		<#list model.parameternames as p_n>
		<tr>
			<td>
				${p_n.getName()}: <#if p_n.description?exists><div style="display: inline; font-size: x-small; text-align: center;" onmouseover="return overlib('${p_n.description}', CAPTION, 'Description')" onmouseout="return nd();"><nobr><u>[info]</u></nobr></div></#if>
			</td>
			<td>
			<#--select name="parameterNameID${p_n.getId()}"-->
			<select name="${p_n.getName()}">
				<#list model.parametervalues as p_v>
				
					<#-- HACK FOR JPA!?!?
					<#if p_v.parametername_id?is_string>
						<#assign pv_pn = p_v.parametername>
					<#else>
						<#assign pv_pn = p_v.parametername_id>
					</#if>
					-->
				
					<#if p_v.parameterName_Id == p_n.getId()>
						<#--option value="parameterValueID${p_v.getId()}">${p_v.getName()}</option-->
						<option value="${p_v.getValue()}">${p_v.getName()}</option>
					</#if>
				</#list>
			</select>
			</td>
		</tr>
		</#list>
	</table>
	<br>
</#if>

<input type="submit" id="toStep1" value="Previous" onclick="__action.value='toStep1';return true;"/>
<input id="startAnalysis" type="submit" value="Start" onclick="__action.value='startClusterJob';return true;"/>



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

<br><br>
<div style="text-align: center; overflow: scroll;">
<table class="listtable">
	<#if model.maxSubjobs == 0>
		<tr>
			<td>
				No running analysis to display.
			</td>
		</tr>
	<#else>
	
	<tr class="form_listrow0">
		<td colspan="7">
			<font size="3"><b>Running analysis</b></font>
		</td>
		<td colspan="${model.maxSubjobs}">
			<font size="3"><b>Parts</b></font><br>
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
			<#assign found = false>
			<#list model.getJobToOutputLink()?keys as key>
				<#if key == t.id?c>
					<#if model.getJobToOutputLink()[key] == 'DATA'>
						<#assign outputLink = "?select=Datas&__target=Datas&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=Data_name&__filter_operator=EQUALS&__filter_value=${t.outputDataName}">
					<#else>
						<#assign outputLink = "?select=Files&__target=Files&__comebacktoscreen=${screen.name}&__action=filter_set&__filter_attribute=InvestigationFile_name&__filter_operator=EQUALS&__filter_value=${t.outputDataName}">
					</#if>
					<nobr><a target="_blank" href="${outputLink}"><#if t.outputDataName?length gt 10>${t.outputDataName?substring(0, 10)}...<#else>${t.outputDataName}</#if></a></nobr>			
					<#assign found = true>
					<#break>
				</#if>
			</#list>		
			<#if found == false>
				<nobr><#if t.outputDataName?length gt 10>${t.outputDataName?substring(0, 10)}...<#else>${t.outputDataName}</#if></nobr>
			</#if>
		</td>
		<td style="vertical-align:middle;">
			<nobr>${t.computeResource} <#if t.computeResource == 'local'><a target="_blank" href="getlogs?job=${t.getId()?c}">[view logs]</a></#if></nobr>
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
			<#if j.getJob_Id() == t.getId()>
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

<!--font color="red"><i>Resubmission of subjobs on cluster not possible due to demo constraints</i></font-->
<br>
<a target="_blank" href="getlogs">[view all local logs]</a>
<br>
<br>
<input type="submit" value="Go back" id="back_to_start" onclick="__action.value='goBack';return true;"/>
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
