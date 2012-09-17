<#macro org_molgenis_lifelinesresearchportal_plugins_loader_LifeLinesLoaderPlugin screen>
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

<h3>Load CIT Publish data from Oracle CSV files</h3>
<p><em>Caution: this might interfere with existing database items!</em></p>
<label for="zip">Archive containing the CSV files:</label>
<input type="file" name="zip" id="zip" />
<br />
<label for="study">Study:</label>
<br />
<input type="radio" name="study" value="1" />OV004+OV013 Steverink (The mediating role of postive and negative affects in the relation between social support and health. Social relationship factors, SWB, self-regulation, health outcomes)
<br />
<input type="radio" name="study" value="2" />OV039 Boezen (Identifying novel genes for lung function and lung function decline in LifeLines)
<br />
<input type="radio" name="study" value="3" />OV077 Van der Harst (Heritability, Genetics and Prognosis of PR conduction)
<br />
<input type='submit' value='Load' onclick="__action.value='load'" />
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
