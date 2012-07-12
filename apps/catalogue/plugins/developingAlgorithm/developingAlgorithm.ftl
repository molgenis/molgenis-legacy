<#macro plugins_developingAlgorithm_developingAlgorithm screen>
<style type="text/css">
.insertTable{
	background-color:#d0e4fe;
}

.HighLightTR.highlight {
	margin: 0px;
	border: 0px;
	padding: 0px;
	background-image: none;
	background-color: transparent;
	padding-left: 3px;
}

</style>

</script>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" id="plugins_catalogueTree_catalogueTreePlugin" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" id="test" value="">
	<!-- hidden input for measurementId -->
	<input type="hidden" name="measurementId" id="measureId" value="">
		
<!-- this shows a title and border -->
	<link type="text/css" href="WebContent/jquery/css/smoothness/jquery-ui-1.8.7.custom.css" rel="Stylesheet"/>
	<script type="text/javascript" src="WebContent/jquery/development-bundle/ui/jquery-ui-1.8.7.custom.js"></script>

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
			Please choose an ontology file and algorithm will be automatically generated </br></br>
					<table width="100%">
						<tr>
							<td class="box-body" style="width:50%;">
								1. Choose a validation study
								<select name="validationStudy" id="validationStudy"> 
									
								</select></br></br>
								<script>
								
								</script>
								2. Please upload your ontology (compulsory)</br></br>
								<input type="file" id = "ontologyFileForAlgorithm" name = "ontologyFileForAlgorithm"/></br></br>
								
								<input type="submit" value="generate algorithm" id="continue" name="continue" onclick="__action.value='generateAlgorithm';return checkFileExisting();" />
								
								<input type="submit" value="back to mapping" id="backToMapping" name="backToMapping" onclick="__action.value='backToMapping';" />
							</td>
							<td class="box-body" style="width:50%;">
								<p align="justify" style="font-family:arial;margin-left:20px;font-size:12px;">
								
								</p>
							</td>
						</tr>
					</table>	
			</div>
		</div>
	</div>
</form>
</#macro>
