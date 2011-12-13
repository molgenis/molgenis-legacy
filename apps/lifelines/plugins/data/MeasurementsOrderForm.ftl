<#macro plugins_data_MeasurementsOrderForm screen>

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
			    <h3> Measurements Order Form  </h3>
		        <input type="submit" value="Confirm" onclick="if (confirm('You are about to complete your order. Are you sure you want to proceed?')) {__action.value='ConfirmMeasurementsOrderForm';return true;} else {return false;}"/><br /><br />
		        
				<label> 	<#if screen.getStatus()?exists>${screen.getStatus()} </#if>  </label>	

			</div>
		</div>
	</div>
	
</form>
</#macro>

