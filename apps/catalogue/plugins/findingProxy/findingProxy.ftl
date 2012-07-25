<#macro plugins_findingProxy_findingProxy screen>
<style>
	div.text{
		color:#123481;
	}
</style>

<script type="text/javascript">
	$(document).ready(function() {
 		$("input#stepTwo").button();
		$("input#stepTwo").css({
			'font-size':'0.7em',
			'color':'#123481'
		});
		$("input#stepTwo").show();
	});


	function insertTable(tableId){

		var table = document.getElementById(tableId);

		if(table.style.display == "none") {
     		table.style.display = "inline";
   		} else {
			table.style.display = "none";
		}
	}

	function getClickedTableById(key){
		$('#details > table').hide();
		$('#' + key + '_table').show();
	}
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
			
				<#if screen.getStage() == true>
				
					<div class='text'><h1>Find a proxy variable</h1></div>
					<div class='text' style='font-size:1.3em'>Search for proxies if certain variables are missing in validation study. Please 
					choose the prediction model and study that you want to validate with</div></br>
					<div class='text'>Choose a prediction model:</div>
					<select name="predictionModel" id="predictionModel"> 
						<#list screen.getlistOfPredictionModel() as predictionModel>
							<option value="${predictionModel}">${predictionModel}</option>			
						</#list>
					</select>
					<script>$('#predictionModel').chosen();</script></br>
					</br>
					<div class='text'>Choose a validation study:</div>
					<select name="validationStudy" id="validationStudy"> 
						<#list screen.getListOfValidationStudy() as validationStudy>
							<option value="${validationStudy}" <#if screen.getSelectedValidationStudyName()??><#if screen.getSelectedValidationStudyName() == validationStudy>selected="selected"</#if></#if> >${validationStudy}</option>			
						</#list>
					</select>
					<script>$('#validationStudy').chosen();</script>
					</br></br>
					<input id="stepTwo" type="submit" value="next step" style="display:none" onclick="__action.value='chooseModelAndStudy';"/>
								
				<#else>
					
					<table border=1 width="100%" height="600px">
						<tr height="50px">
							<td width="50%">
								<font size="3">Selected prediction model: <b>${screen.getSelectedPredictionModel()}</b></br>
								<font size="3">Selected validation study: <b>${screen.getSelectedValidationStudyName()}</b></font>
							</td>
						</tr>
						<tr height="550px">
							<td><font size="3">
								</br>Choose a parameter from <b>${screen.getSelectedPredictionModel()}</b>
								and search this term in <b>${screen.getSelectedValidationStudyName()}</b> study</br>
								<select name="selectParameter" id="selectParameter"> 
									<#list screen.getListOfParameters() as parameter>
										<option value="${parameter}" <#if screen.getSelectedManualParameter()??><#if screen.getSelectedManualParameter() == parameter> selected="selected"</#if></#if>>${parameter}</option>			
									</#list>
								</select>
								<script>$('#selectParameter').chosen();</script>
								</br></br>1.You can either search for the term itself and it will become the searching tokens
								</br></br>2.You can also give the definition for the term and this definition will become your searching tokens 
								</br></br>Example: For term "Age", you can directly search Age or search "How old are you?"
								</br></br>Define your term here: <input type="text" id="userDefinedQuery" name="userDefinedQuery" size="25" value="${screen.getUserDefinedQuery()}">
								</br></br>You can set your cut off value (%) here:  <input type="text" name="cutOffValue" size="5"> e.g. 50
								</br></br><input type="submit" style="font-size: larger" name="customizedSearch" value="customized search" onclick="__action.value='customizedSearch';"/></br></br>
								<script>
									if($('#userDefinedQuery').attr('value') === ""){
										$('#userDefinedQuery').attr('value', $('#selectParameter').val());
									}
								</script>
								<table>
									<tr>
										<td>
											</br><input type="submit" style="font-size: small" name="addToExistingMapping" value="add to existing mapping" onclick="__action.value='addToExistingMapping';"/>
										</td>
										<td>
											</br><input type="button" style="font-size: small" name="saveManualMapping" value="save manual mapping" onclick="refreshByHits();"/>
										</td>
									</tr>
								</table>
							</font></td>
							<td><font size="3"><i>
								</br>The matchings will be done using Levenshtein string matching algorithms.
								</br></br>The mapping result will be shown here!
								</br><div id="tableDisplay" style="overflow:auto;height:500px">
									<script>
										<#if screen.getManualMappingResultTable()??>
											<#list screen.getManualMappingResultTable() as eachMapping>
												var json = eval(${eachMapping});
												$('#tableDisplay').append(json.result);
												$('#tableDisplay').append(json.script);
											</#list>
										</#if>
									</script>
								</div>
							</i></font></td>
						</tr>
					</table>
					</br><input type="submit" value="back to mapping" 
						id="backToSelection" name="backToSelection" onclick="__action.value='backToSelection';" />
				</#if>
			</div>
		</div>
	</div>
</form>
</#macro>
