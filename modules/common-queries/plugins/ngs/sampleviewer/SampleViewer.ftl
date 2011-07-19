<#macro plugins_ngs_samplerviewer_SampleViewer screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}"" />
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" />
	
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
		
		
<#if screen.model?exists>
	<#assign modelExists = true>
	<#assign model = screen.model>
<#else>
	No model. An error has occurred.
	<#assign modelExists = false>
</#if>
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->	

<h2> Sample Workflow </h2>

<div id="demo">

<div id="projecttypebox" class="row">
<select name="projecttype" id="projecttype" class="selectbox">
<option value="0">&nbsp;</option>
<#list model.projects as projects>
<option value="${projects.getName()}"> ${projects.getName()} </option>
</#list>
</select>
</div>
<div id='buttons_part' class='row'>
<input type="submit" value="Select Project" onclick="__action.value='selectProject';return true;"/-->
</div>

<div id="protocoltypebox" class="row">
<select name="protocoltype" id="protocoltype" class="selectbox">
<option value="0">&nbsp;</option>
<#list model.protocols as protocol>
<option value="${protocol.getId()}"> ${protocol.getName()} </option>
</#list>
</select>
</div>
<div id='buttons_part' class='row'>
<input type="submit" value="Select Protocol" onclick="__action.value='selectProtocol';return true;"/-->
</div>

<div id='buttons_part' class='row'>
<input type="submit" value="Show all Samples" onclick="__action.value='showAllSamples';return true;"/-->
</div>

<div id='buttons_part' class='row'>
<input type="submit" value="Show all Features" onclick="__action.value='showAllProtocols';return true;"/-->
</div>

<#assign projectName = model.projectName>
<#assign protocolName = model.protocolName>

<#if projectName != "">
<p> Current selected Project: ${projectName}</p>
<#else>
<p> No project filter selected  </p>
</#if>

<#if protocolName != "">
<p> Current selected Protocol: ${protocolName} </p>
<#else>
<p> No protocol filter selected </p>
</#if>

<br> <br> <br>

	<table cellpadding="1" cellspacing="1" border="1" class="display" id="listtable">
	
	
	<#--Creates a row with all Measurements-->
	<tr>
	<td style="color:darkblue;font-weight:bold">Samples </td> 
	<#list model.features as features>
	<td  style="color:darkblue;font-weight:bold"> ${features.getName()} </td>
	</#list>
	</tr>
	
	
	<#--Fill table by making a row (with sample name) followed by filling in the row with values -->
	
	
	<#list model.samples as samples>
	<tr>
	<#-- TODO: nowrap is deprecated. Replace with proper style="whatever" -->
	<td nowrap  style="color:darkblue;font-weight:bold"> ${samples.getName()} </td> 
	<#assign id = samples.getId()>
	<#assign features = model.features>
	<#assign column = 0>
	<#list model.getValuesBySample(id, features) as vbs> 
	 
		<td>
		 
		<input type="text" name="${id}_${column}" id="${id}_${column}" class="textbox" value="${vbs.getValue()}">
		 
		 </input>
		 </td>
		<#assign column = column+1>
		</#list>
		</tr>
	</#list>
	
	
	
	</table>
</div>

<div id='buttons_part' class='row'>
<input type="submit" value="Submit Changes" onclick="__action.value='submitchanges';return true;"/-->
</div>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>