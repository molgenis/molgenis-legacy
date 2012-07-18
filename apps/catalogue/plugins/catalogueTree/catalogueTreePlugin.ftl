<#macro plugins_catalogueTree_catalogueTreePlugin screen>

<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" id="plugins_catalogueTree_catalogueTreePlugin" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" id="test" value="">
	<!-- hidden input for measurementId -->
	<input type="hidden" name="measurementId" id="measureId" value="">
	<style type="text/css">
	div#selection{
	   	z-index: 9000;
	   	display: none;
	}
	</style>
	<script type="text/javascript">
		
		function searchInTree(){
			
			var inputToken = $('#InputToken').val();
			
			$('#details').empty();
			
			$('#leftSideTree li').hide();
					
			if($('#selectedField').val() == "Measurements" || $('#selectedField').val() == "All fields"){
				
				$('#leftSideTree li').each(function(){
					
					if($(this).find('li').length == 0){
						
						var name = $(this).text().replace(/_/g,' ');
						
						var id = $(this).attr('id');
						
						inputToken = inputToken.replace(/_/g,' ');
						
						if(name.search(new RegExp(inputToken, "gi")) != -1){
							
							$(this).show();
							$('#leftSideTree li#' + id).parents().show();
						}
					}
				});
			}
			if($('#selectedField').val() == "Protocols" || $('#selectedField').val() == "All fields"){
				
				$('#leftSideTree li').each(function(){
					
					if($(this).find('li').length > 0){
						
						var name = $(this).children('span').text().replace(/_/g,' ');
						
						var id = $(this).attr('id');
						
						inputToken = inputToken.replace(/_/g,' ');
						
						if(name.search(new RegExp(inputToken, "gi")) != -1){
							$(this).show();
							$('#leftSideTree li#' + id).parents().show();
							$('#leftSideTree li#' + id).find('li').show();
						}
					}
				});
				
			}
			if($('#selectedField').val() == "Details" || $('#selectedField').val() == "All fields"){
				
				var json = eval(${screen.getInheritance()});
				
				$('#leftSideTree li').each(function(){
					
					if($(this).find('li').length == 0){
						
						var id = $(this).attr('id');
						
						var table = json[id];
						
						table = table.replace(/_/g,' ');
						
						inputToken = inputToken.replace(/_/g,' ');
						
						if(table.search(new RegExp(inputToken, "gi")) != -1){
							$(this).show();
							$('#leftSideTree li#' + id).parents().show();
						}
					}
				});
			}
			
		}		
		
		function removeVerticalLine(id){
			
			if($('#' + id).css('display') != 'none' && $('#' + id).children('ul').children('li').length > 0){
						
				if($('#' + id).nextAll().length > 0 && !$('#' + id).nextAll().is(':visible')){
					$('#' + id).css('background-image','none');
				}
				
				$('#' + id).children('ul').children('li').each(function(){
						
					removeVerticalLine($(this).attr('id'));
					
				});
			}
		}
		
		function checkSearchingStatus(){
			
			if($('#InputToken').val() === ""){
				$('#leftSideTree li').show();
			}
		}
		
	</script>

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
				<#if screen.isSelectedInv() == true>
					<table class="box" width="100%" cellpadding="0" cellspacing="0">
						<tr><td class="box-header" colspan="2">  
								        <label>Choose a cohort:
										<!--select name="investigation" id="investigation"--> 
											<#list screen.arrayInvestigations as inv>
												<#assign invName = inv.name>
												<!--option value="${invName}" <#if screen.selectedInvestigation??><#if screen.selectedInvestigation == invName>selected="selected"</#if></#if> >${invName}</option-->			
													<input class="cohortSelect" type="submit" id="cohortSelectSubmit" name="cohortSelectSubmit" value= ${invName}
														onclick="__action.value='cohortSelect';" 
														style="color: #000; background: #8EC7DE;
												   		border: 2px outset #d7b9c9;
												   		font-size:12px;
												   	"/>
											
											</#list>
										<!--/select-->
										<script>$('#investigation').chosen();</script>
										<!--input type="submit" name="chooseInvestigation" value="refresh tree" onclick="__action.value='chooseInvestigation';"></input-->
										<!--input type="image" src="res/img/refresh.png" alt="Submit" 
											name="chooseInvestigation" style="vertical-align: middle;" 
											value="refresh tree" onclick="__action.value='chooseInvestigation';DownloadMeasurementsSubmit.style.display='inline'; 
											DownloadMeasurementsSubmit.style.display='inline';" title="load another study"	/-->	
										</label>
										
										<div id="masstoggler"> 		
						 					<label>Browse protocols and their variables '${screen.selectedInvestigation}':click to expand, collapse or show details</label>
						 					<a id="collapse" title="Collapse entire tree" href="#"><img src="res/img/toggle_collapse_tiny.png"  style="vertical-align: bottom;"></a> 
						 					<a id="expand" title="Expand entire tree" href="#"><img src="res/img/toggle_expand_tiny.png"  style="vertical-align: bottom;"></a>
			 							</div>
			 							<div style="float:left">
				 							<input type="submit" id="downloadButton" name="downloadButton" value="Download as Excel" 
											onclick="__action.value='downloadButton';" "/>
			 							</div>
			 							<div style="float:left">
				 							<input type="submit" id="downloadButtonEMeasure" name="downloadButton" value="Download as E-Measure" 
											onclick="__action.value='downloadButtonEMeasure';" "/>
			 							</div>
			 							
					    			</td></tr>
					    			<tr><td class="box-body" style="width:50%;">
					    
			
				<select id="selectedField" name="selectedField" title="choose field" name="chooseField" style="display:none"> 
					<#list screen.arraySearchFields as field>
								<!--#assign FieldName = field.name-->
						<option value="${field}" <#if screen.selectedField??>
							<#if screen.selectedField == field>selected="selected"</#if></#if> >Search ${field}</option>			
					</#list>
					 <!--option value="All fields">All fields</option-->
				</select>
				
				<input title="fill in search term" type="textfield" name="InputToken" id="InputToken"
					onfocus="selectedField.style.display='inline'; selectedField.style.display='inline';" 
					onkeyup="checkSearchingStatus();">
				
				<input type="button" name="SearchCatalogueTree" value="search" onclick="searchInTree()"/>
					    <!--
					    <#list screen.getFilters() as filters>
							<div class="filterslabel">
								<b>Search results for ${filters}</b>
								<img id="remove_filter" height="16" class="navigation_button" src="generated-res/img/cancel.png" alt="Cancel" onclick="__action.value='removeFilters';" title="remove filter"
							</div>
						</#list>
						-->
						
						<#list screen.getFilters() as filter>			
							<!--<b>${filter}</b> <img id="remove_filter_${filter_index}" height="16" class="navigation_button" src="generated-res/img/cancel.png" alt="Cancel" onclick="setInput('${screen.name}_form','_self','','${screen.name}','removeFilters','iframe'); document.forms.${screen.name}_form.filter_id.value='${filter_index}'; document.forms.${screen.name}_form.submit();" title="remove filter"/>-->
							<!--<b>${filter}</b> <img id="remove_filter_${filter_index}" height="16" class="navigation_button" src="generated-res/img/cancel.png" alt="Cancel" onclick="setInput('${screen.name}_form','_self','','${screen.name}','removeFilters','iframe');  document.forms.${screen.name}_form.submit();" title="remove filter"/>-->
							<b>${filter}</b>
							<input type="image" src="generated-res/img/cancel.png" alt="Remove filter" 
											name="chooseInvestigation" style="vertical-align: middle;" 
											value="refresh tree" onclick="__action.value='chooseInvestigation';DownloadMeasurementsSubmit.style.display='inline'; 
											DownloadMeasurementsSubmit.style.display='inline';" title="load another study"	/>	
						<#if filter_has_next> and </#if>
						</#list>
					    
					    </td><td class="box-body" style="width: 50%;">Details:</td></tr>
					    <tr><td class="box-body">
								<div id="leftSideTree">  
									${screen.getTreeView()}<br/>
								</div>
								<div>
								</div>
						    </td>
						    <td class="box-body"> 
      							<div id="selection">
					    			<textarea rows="2" cols="20">
					    				Here is your selection: 
									</textarea>
					    		</div>
      							<div id="details">
      								<script>
										<#if screen.getListOfJSONs()??>
											<#list screen.getListOfJSONs() as eachJSON>
										   		var json = eval(${eachJSON});
										   		$.each(json, function(measurementID, htmlTable){
										   			$('#' + measurementID).click(function(){
										   				$('#details').empty();
										   				$('#details').append(htmlTable);
										   			});
										   		});
										    </#list>
										</#if>		
									</script>		
      							</div>
						   </td>
						</tr>
						<tr>
							<td class="box-body">
							
							</td>
							<td class="box-body">
							<label>Fill in selection name</label>
							<input title="fill in selection name" type="text" name="SelectionName" >
							<input class="saveSubmit" type="submit" id="SaveSelectionSubmit" name="SaveSelectionSubmit" value="Save selection" 
									onclick="__action.value='SaveSelectionSubmit';" 
									style="color: #000; background: #8EC7DE;
										   border: 2px outset #d7b9c9;
										   font-size:15px;
										   font-weight:bold;
										   "/>
							</td>
						</tr>
					</table>
			   	</#if>	
			    <label><#if screen.getStatus()?exists>${screen.getStatus()} </#if>  </label>
 				<script>
 					$('div#leftSideTree input:checkbox').each(function(index){
 						$(this).click(function() {
							 if($(this).attr('checked')){
								 $(this).parent()
								.siblings('ul')
								.find('input:checkbox')
						     	.attr('checked',true);
							 }else{
							 	 $(this).parent()
								.siblings('ul')
								.find('input:checkbox')
						     	.attr('checked',false);
							 }
						});
 					});
 					$('#GenericDCM').find('li').each(function(){
 						if($(this).find('li').length == 0){
 							$(this).find(':checkbox').attr('disabled','true');
 						}
 					});
 				</script>
			</div>
		</div>
	</div>
</form>

</#macro>
