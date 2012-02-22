<#macro plugins_predictionSelection_predictionSelectionPlugin screen>
<style type="text/css">

table.predictionTable
{
	margin:10px;
	font-size:20px;
	text-align:center;
	width:1000px;
	border: 1px solid black;
}
table.predictionTable th
{
	background-color: #4674FD;
	border-style:inset;
	
}
table.predictionTable td{
	font-size:15px;
	border-style:inset;
	background-color: rgb(255, 255, 240);
}
</style>

<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" id="plugins_catalogueTree_catalogueTreePlugin" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" id="test" value="">
	<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
			${screen.label}
		</div>
	</div>
	<br>
	
	<div id="selectModelAndStudy">
		<select id="selectPredictionModel" name="selectPredictionModel">
			<#if screen.getPreidctionModel()??>
				<#list screen.getPreidctionModel() as eachModel>
					<option id=${eachModel}>${eachModel}</option>
				</#list>
			<#else>
				<option>No Model available</option>
			</#if>  
		</select>
		<select id="selectValidationStudy" name="selectValidationStudy">
			<#if screen.getValidationStudy()??>
				<#list screen.getValidationStudy() as eachStudy>
					<option id=${eachStudy}>${eachStudy}</option>
				</#list>
			<#else>
				<option>No Model available</option>
			</#if>  
		</select>
		
		<input type="submit" id="refreshSelection" value="refresh" onclick="__action.value='refreshSelection';"/>
	
	</div>
	
	<div class="table">
		${screen.getHtmlTable()}
	</div>
	
</form>
</#macro>
