<#macro plugins_protocol_ApplyProtocolPluginOld screen>
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
		
		<div class="screenbody">
			<div class="screenpadding">	
<#--begin your plugin-->	

<div id="addeventform">

	<div id="animalselect">
	
		<div style="float:left">
			<label for="animal">Target(s):</label>
			<select name="animal" id="animallist" size="20" multiple="multiple">
			<#if screen.selectedTargetIdList?exists>
				<#list screen.selectedTargetIdList as selectedTargetId>
					<#assign name = screen.getTargetName(selectedTargetId)>
					<option value="${selectedTargetId?string.computer}">${name}</option>
				</#list>
			</#if>
			</select>
		</div>
		
		<div>
			Add individual(s)...<br />
			<div style="float:left">
				<input type="submit" value="<<" onclick="__action.value='addIndividual'" />
			</div>
			<select name="ind_animal" size="10" multiple="multiple">
			<#if screen.targetIdList?exists>
				<#list screen.targetIdList as targetId>
					<#assign name = screen.getTargetName(targetId)>
					<option value="${targetId?string.computer}">${name}</option>
				</#list>
			</#if>
			</select>
		</div>
		<div>
			Add group...<br />
			<input type="submit" value="&lt;&lt;" onclick="__action.value='addGroup'" />
			<select name="group_animal">
			<#if screen.groupList??>
				<#list screen.groupList as g>
					<option value="${g.id?string.computer}">${g.name}</option>
				</#list>
			</#if>
			</select>
		</div>
		<div>
			Remove individual(s)... <br />
			<input type="submit" value="&gt;&gt;" onclick="__action.value='remIndividual'" />
		</div>
		<div>
			Remove all... <br />
			<input type="submit" value="X" onclick="__action.value='remAll'" />
		</div>
	
	</div>
	
	<div id="sepvaldiv" class="row">
		<label for="sepvaltoggle">Give separate values for each target:</label>
		<input type="checkbox" id="sepvaltoggle" name="sepvaltoggle" value="sepval" />
	</div>
	
	<div id="eventtypeselect" class="row">
		<label for="eventtype">Type:</label>
		<select name="eventtype" id="eventtype" class="selectbox" onchange="getRestOfEventMenu(this);">
			<option value="0">&nbsp;</option>
			<#if screen.protocolList??>
				<#list screen.protocolList as protocol>
					<option value="${protocol.id?string.computer}">${protocol.name}</option>
				</#list>
			</#if>
		</select>
	</div>
	
	<div id="featurevalues">
	<!-- This box is filled dynamically by the AddEventMenuServlet (Ajax-style) -->
	</div>

</div>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
