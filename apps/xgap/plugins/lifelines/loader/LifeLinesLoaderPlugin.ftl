<#macro plugins_lifelines_loader_LifeLinesLoaderPlugin screen>
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

<h1>Load Publish data from Oracle CSV files</h1>
<em>Caution: this might interfere with existing database items!</em>

<div class="row">
	<label for="zip">Archive containing the CSV files:</label>
	<input type="file" name="zip" id="zip" class="textbox" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input type='submit' class='addbutton' value='Load' onclick="__action.value='load'" />
</div>
	
<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
</#macro>
