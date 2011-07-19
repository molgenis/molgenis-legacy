<#macro plugin_LuceneIndex_OntoCatIndexPlugin screen>
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
			<input type="text" name="InputToken" value="<#if screen.getInputToken()?exists>${screen.getInputToken()} </#if>"/><br /><br />
			<input type="submit" value="Search Ontocat Index" onclick="__action.value='SearchOntocatLuceneIndex';return true;"/><br /><br /><br />
			
			<label> 	    		Searching for :  <#if screen.getInputToken()?exists>${screen.getInputToken()} </#if>  </label><br /><br /><br />
  			<label> 	    		 <#if screen.getStatus()?exists>${screen.getStatus()} </#if>  </label>
	
			</div>
		</div>
	</div>
</form>
</#macro>


