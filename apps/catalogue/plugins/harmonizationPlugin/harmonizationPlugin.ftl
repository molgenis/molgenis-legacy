<#macro plugins_harmonizationPlugin_harmonizationPlugin screen>
<style type="text/css">
div#popup{
	position: fixed;
   	top: 0;
   	right: 0;
   	bottom: 0;
   	left: 0;
   	height: 100%;
   	width: 100%;
   	margin: 0;
   	padding: 0;
   	background: #000000;
   	opacity: .15;
   	filter: alpha(opacity=15);
   	-moz-opacity: .15;
   	z-index: 101;
   	display: none;

}
.clickRow:hover{
	color: #d0e4fe;
}

.insertTable{
	display: none;
   	top: 50%;
   	left: 50%;
   	margin-left: -190px;
   	margin-top: -100px;
   	background-color: #ffffff;
   	border: 2px solid #336699;
   	padding: 0px;
   	z-index: 102;
   	font-family: Verdana;
   	font-size: 10pt;
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

<script type="text/javascript">
function refreshByHits () {
  
	$('#details >table').each(function(i) {
		
		$('>tbody >tr',this).each(function(j){
			if(j <= $('#changeHits').val()){
				$(this).show();
			}else{
				$(this).hide();
			}	
		});
	});
}


function setValidationStudy(validationStudyName){
	
	var dropBox = document.getElementById("validationStudy");
	
	var options = dropBox.getElementsByTagName("option");
	
	for(var i = 0; i < options.length; i++){
		
		if(options[i].value == validationStudyName){
			
			dropBox.selectedIndex = i;
		}
	}
}

function checkFileExisting(){

	if(!document.getElementById("ontologyFileForAlgorithm").value){
    	alert("No file selected");
    	 return false;
    }else{
    	 return true;
    }
}

function getClickedTableById(key){
	$('#details > table').hide();
	$('#' + key + '_table').show();
}

$(document).ready(function(){
	$('#startMatching').button();
	$('#saveMapping').button();
	$('#startMatching').show();
	$('#saveMapping').show();
	$('#changeHits').css({
		'font-family':'comic sans ms',
		'font-size':'14px',
		'height':'25px'
	});
});

</script>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" id="plugins_catalogueTree_catalogueTreePlugin" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" id="test" value="">
	<!-- hidden input for measurementId -->
	<input type="hidden" name="measurementId" id="measureId" value="">
	<input type="hidden" name="validationStudyName" id="validationStudyName" value="${screen.getValidationStudyName()}">
		
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
				<#if screen.getDevelopingAlgorithm() == true>
						
				<#elseif screen.getManualMatch() == true>
					
				<#else>
					<#if screen.isSelectedInv() == true>
						
						<table class="box" width="100%" cellpadding="0" cellspacing="0">
							<tr><td class="box-header" colspan="1">  
									<label>Choose a prediction model:
									<select name="investigation" id="investigation"> 
										<#list screen.getPredictionModel() as inv>
											<option value="${inv}">${inv}</option>			
										</#list>
									</select>
									<script>$('#investigation').chosen();</script>
									<!--input type="submit" name="chooseInvestigation" value="refresh tree" onclick="__action.value='chooseInvestigation';"></input-->
									<input type="image" src="res/img/refresh.png" alt="Submit" 
										name="chooseInvestigation" style="vertical-align: middle;" 
										value="refresh tree" onclick="__action.value='chooseInvestigation';DownloadMeasurementsSubmit.style.display='inline'; 
										DownloadMeasurementsSubmit.style.display='inline';" title="load another study"	/>	
									</label>
									<div id="masstoggler"> 	
										<label>Browse protocols and their variables '${screen.getSelectedPredictionModel()}':click to expand, collapse or show details</label>
								 			<a title="Collapse entire tree" href="#"><img src="res/img/toggle_collapse_tiny.png"  style="vertical-align: bottom;"></a> 
								 			<a title="Expand entire tree" href="#"><img src="res/img/toggle_expand_tiny.png"  style="vertical-align: bottom;"></a> 
					 				</div>
								</td>
						    	<td class="box-header" colspan="2">
						    		<label>Choose a validation study:
									<select name="validationStudy" id="validationStudy"> 
										<#list screen.arrayInvestigations as inv>
											<#assign invName = inv.name>
											<option value="${invName}" <#if screen.selectedInvestigation??><#if screen.selectedInvestigation == invName>selected="selected"</#if></#if> >${invName}</option>			
										</#list>
									</select></br></br>
									<script>
										setValidationStudy('${screen.getValidationStudyName()}');
										$('#validationStudy').chosen();
									</script>
									</br>
									Tick the box if this is baseline data<input type="checkbox" name="baseline" id="baseline"/>
						    	</td>
						    </tr>
						    <tr>
						    	<td class="box-body" style="width:50%;">
							
									Please upload your ontology file to extend your query (optional)<br/><br/>
									<input type="file" name = "ontologyFile"/>	    
						    
						    	</td>
						    	<td class="box-body" style="width: 50%;">
								</td>
							</tr>
						    <tr>
						    	<td class="box-body">
									<div id="leftSideTree" style="height:400px">  
										
										${screen.getTreeView()}
										
									</div><br/>
							    </td>
							    
							    <td class="box-body">
							    	<!--div id="scrollingDiv"--> 
	      								<div id="details" style="height:400px;overflow:auto">
	      									${screen.getHitSizeOption()}
	      									<script>
												<#if screen.getListOfJSON()??>
													<#list screen.getListOfJSON() as eachMapping>
														var json = eval(${eachMapping});
														$('#details').append(json.result);
														$('#details').append(json.script);
													</#list>
												</#if>
												$('#details > table').hide();
											</script>
											<script>
												$('#details > table.dataResult').each(function(index) {
													var length = $(this).attr('id').length;												   
												    var measurementId = $(this).attr('id').substring(0, length - 6);
												    $('#' + measurementId).click(function(){
												    	getClickedTableById(measurementId);
												    });
												});
											</script>
											<script>
												$('#details > table td.clickRow').click(function() {
													var id = $(this).parent().attr('id');
													var position = $(this).offset();
													var top = position.top + 140;
													var left = position.left + 10;
													$("#popup").show();
													$('#' + id + '_table').css('top', top + 'px');
													$('#' + id + '_table').css('left', left + 'px');
													$('#' + id + '_table').fadeIn(300);
													$("#popup").click(function(){
														$("#popup").hide();
														$('#' + id + '_table').fadeOut(300);
													});
													$('#' + id + '_table').click(function(){
														$("#popup").hide();
														$('#' + id + '_table').fadeOut(300);
													});
												});
	      									</script>
	      									<script>
	      										$('#browser li').each(function(){
	      											if($(this).find('li').length == 0){
	      												$(this).click(function(){
	      													$('#browser li').css('color','black');
	      													$('#browser li').css('font-size', 13);
	      													$('#browser li').css('font-style','normal');
	      													$('#browser li').css('font-weight', 400);
	      													$(this).css('color','#778899');
	      													$(this).css('font-size', 15);
	      													$(this).css('font-style','italic');
	      													$(this).css('font-weight', 'bold');
	      												});
	      											}
	      										});
	      									</script>
	      								</div><br/><br/>
	      							<!--/div-->
										<div id="popup">
										</div>
							   </td>
							</tr>
							<tr>
								<td class="box-body">
									<input type="submit" id="startMatching" name="startMatching" value="Matching" 
										style="display:none" onclick="__action.value='startMatching';" />
									<input type="submit" id="saveMapping" name="saveMapping" value="save Mapping" 
										style="display:none" onclick="__action.value='saveMapping';"/>	
								</td>
								<td class="box-body"></td>
							</tr>
						</table>
						<script>
							refreshByHits();
						</script>
				   </#if>
			   </#if>	
			</div>
		</div>
	</div>
</form>
</#macro>
