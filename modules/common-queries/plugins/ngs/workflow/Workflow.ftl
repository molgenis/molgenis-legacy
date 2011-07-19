<#macro plugins_ngs_workflow_Workflow screen>
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

<#if model.action == "ShowSampleInfo">

<div id='buttons_part' class='row'>
<input type="submit" value="Show all Samples" onclick="__action.value='showAll';return true;"/-->
</div>

<h3> Select a sample </h3>
<div id="sample" class="row">
<br>
<select name="sample" id="sample" class="selectbox" onchange="callScreenWithSampleName(this.value);>
<option value="0">&nbsp;</option>
<#list model.samples as sample>
<option value="${sample.getName()}">${sample.getName()}</option>
</#list>
</select>
</div>

<div id="demo">

<div id="projecttypebox" class="row">
<select name="projecttype" id="projecttype" class="selectbox" onchange="callScreenWithProjectName(this.value);">
<option value="0">&nbsp;</option>
<#list model.projects as projects>
<option value="${projects.getName()}"> ${projects.getName()} </option>
</#list>
</select>
</div>

<hr>
<#assign sample = model.sample>
<#assign protocol = model.currentProtocol>

<h4> Information for Sample: ${sample.getName()} </h4>

<p> Current sample protocol: ${protocol.getName()} </p>

 <p> ${protocol.getName()} values: </p>
 
 <table cellpadding="1" cellspacing="1" border="1" class="display" id="listtable">
	
	
	<#--Creates a row with all Measurements for given protocol-->
	<tr>
	<td style="color:darkblue;font-weight:bold">Samples </td> 
	<#list model.features as features>
	<td  style="color:darkblue;font-weight:bold"> ${features.getName()} </td>
	</#list>
	</tr>
	
	<#--Fill table by making a row (with sample name) f=ollowed by filling in the row with values -->

	
	<tr>
	<#-- TODO: nowrap is deprecated. Replace with proper style="whatever" -->
	<td nowrap  style="color:darkblue;font-weight:bold"> ${sample.getName()} </td> 
	
	<#assign id = sample.getId()>
	<#assign column = 0>
	<#list model.getValuesBySample() as vbs> 

		<td>
		 
		<input type="text" name="${id}_${column}" id="${id}_${column}" class="textbox" value="${vbs.getValue()}">
		 
		 </input>
		 </td>
		<#assign column = column+1>
		</#list>
		</tr>
	
	</table>
	
	<div id='buttons_part' class='row'>
	<input type="submit" value="Submit Changes" onclick="__action.value='submitchanges';return true;"/-->
	</div>
	
	<p> Mark current Protocol as done and choose following protocol: </p>
	
<div id="protocolbox" class="row">
<select name="protocolbox" id="protocolbox" class="selectbox">
<option value="0">&nbsp;</option>
<#list model.getWorkflowElements() as workflowElements>
<option value="${workflowElements.getId()}"> ${workflowElements.getName()} </option>
</#list>
</select>
</div>

<div id='buttons_part1' class='row'>
	<input type="submit" value="Submit" onclick="__action.value='changeProtocol';return true;"/-->
	</div>


<#else>

<div id='buttons_part' class='row'>
<input type="submit" value="Show all Samples" onclick="__action.value='showAll';return true;"/-->
</div>

<h3> Select a sample </h3>
<div id="sample" class="row">
<br>
<select name="sample" id="sample" class="selectbox" onchange="callScreenWithSampleName(this.value);">
<option value="0">&nbsp;</option>
<#list model.getSamples() as samp>
<option value="${samp.getName()}">${samp.getName()}</option>
</#list>
</select>
</div>

<div id="projecttype" class="row">
<select name="projecttype" id="projecttype" class="selectbox" onchange="callScreenWithProjectName(this.value);">
<option value="0">&nbsp;</option>
<#list model.projects as projects>
<option value="${projects.getName()}"> ${projects.getName()} </option>
</#list>
</select>
</div>

</#if>


<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>

<script type="text/javascript">

function callScreenWithProjectName(value) {
	window.location='molgenis.do?__target=${screen.name}&__action=ShowSamplesForProject&id=' + value;
}

function callScreenWithSampleName(value) {
	window.location='molgenis.do?__target=${screen.name}&__action=ShowSampleInfo&id=' + value;
}

</script>
</#macro>