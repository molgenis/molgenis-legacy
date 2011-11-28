<#macro plugins_data_lifeLineImporter screen>

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
			    <h3> Import dataShaper data to pheno model  </h3>
		        <input type="submit" value="Import" onclick="__action.value='ImportLifelineToPheno';return true;"/><br /><br />
				<input type="submit" value="Empty Database" onclick="__action.value='fillinDatabase';return true;"/>
 				<label> 	<#if screen.getStatus()?exists>${screen.getStatus()} </#if>  </label>	

			</div>
		</div>
	</div>
	
</form>
</#macro>
