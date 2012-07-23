<#macro plugins_catalogueTree_catalogueTreePlugin screen>

<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" id="plugins_catalogueTree_catalogueTreePlugin" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" id="test" value="">
	<!-- hidden input for measurementId -->
	<input type="hidden" name="measurementId" id="measureId" value="">
	
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
							$('#leftSideTree li#' + id).parents().children('div').removeClass('expandable-hitarea');
							$('#leftSideTree li#' + id).parents().children('div').addClass('collapsable-hitarea');
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
							$(this).children('div').removeClass('expandable-hitarea');
							$(this).children('div').addClass('collapsable-hitarea');
							$('#leftSideTree li#' + id).parents().show();
							$('#leftSideTree li#' + id).parents().children('div').removeClass('expandable-hitarea');
							$('#leftSideTree li#' + id).parents().children('div').addClass('collapsable-hitarea');
							$('#leftSideTree li#' + id).find('ul').show();
							$('#leftSideTree li#' + id).find('li').show();
							$('#leftSideTree li#' + id).find('div').removeClass('expandable-hitarea');
							$('#leftSideTree li#' + id).find('div').addClass('collapsable-hitarea');
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
							$('#leftSideTree li#' + id).parents().children('div').removeClass('expandable-hitarea');
							$('#leftSideTree li#' + id).parents().children('div').addClass('collapsable-hitarea');
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
		
		function whetherReload(e){
			
			if(e.keyCode === 13){
				if($('#InputToken').val() != ""){
					searchInTree();
				}
				return false;
			}
		}
		
		$(document).ready(function(){	
			
			$('#cohortSelectSubmit').button();
			$('#cohortSelectSubmit').css({
				'font-size':'1.2em',
				'color':'#123481'
			});
			$('#cohortSelectSubmit').show();
			
			$('#downloadButton').button();
			$('#downloadButton').css({
				'font-size':'0.8em'
			});
			$('#downloadButton').show(); 
			$('#downloadButtonEMeasure').button();
			$('#downloadButtonEMeasure').css({
				'font-size':'0.8em'
			});
			$('#downloadButtonEMeasure').show();
			
			$('#clearSearchingResult').click(function(){
				$('#InputToken').val('');
				checkSearchingStatus();
			});
		});
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
					<table class="box" width="100%" cellpadding="0" cellspacing="0" style="border-right:1px solid lightgray">
						<tr>
							<td class="box-header" colspan="2">  
						        <label style='font-size:14px'>Choose a cohort:
									<#list screen.arrayInvestigations as inv>
										<#assign invName = inv.name>
											<input class="cohortSelect" type="submit" id="cohortSelectSubmit" name="cohortSelectSubmit" value= ${invName}
												style="display:none" onclick="__action.value='cohortSelect';"/>
									
									</#list>
								<script>$('#investigation').chosen();</script>	
								</label>
								
							<!--	<div id="masstoggler"> 		
				 					<label style='font-size:14px'>Browse protocols and their variables '${screen.selectedInvestigation}':click to expand, collapse or show details</label>
				 					<a id="collapse" title="Collapse entire tree" href="#"><img src="res/img/toggle_collapse_tiny.png"  style="vertical-align: bottom;"></a> 
				 					<a id="expand" title="Expand entire tree" href="#"><img src="res/img/toggle_expand_tiny.png"  style="vertical-align: bottom;"></a>
	 							</div>-->
	 						</td>
			    		</tr>
			    		<tr>
			    			<td class="box-body" style="width:50%;">
								<select id="selectedField" name="selectedField" title="choose field" name="chooseField" style="display:none"> 
									<#list screen.arraySearchFields as field>
												<!--#assign FieldName = field.name-->
										<option value="${field}" <#if screen.selectedField??>
											<#if screen.selectedField == field>selected="selected"</#if></#if> >Search ${field}</option>			
									</#list>
								</select>
								<input title="fill in search term" type="text" name="InputToken" id="InputToken"
									onfocus="selectedField.style.display='inline'; selectedField.style.display='inline';" 
									onkeyup="checkSearchingStatus();" onkeypress="return whetherReload(event);">
									
								
								<input type="button" id="SearchCatalogueTree" class='addbutton ui-button ui-widget ui-state-default ui-corner-all' name="SearchCatalogueTree" 
								value="search" style="font-size:0.8em" onclick="searchInTree()"/>
								<input type="button" id="clearSearchingResult" class='addbutton ui-button ui-widget ui-state-default ui-corner-all' name="clearSearchingResult" 
								value="clear" style="font-size:0.8em"/>					
								<#list screen.getFilters() as filter>			
									<b>${filter}</b>
									<input type="image" src="generated-res/img/cancel.png" alt="Remove filter" 
													name="chooseInvestigation" style="vertical-align: middle;" 
													value="refresh tree" onclick="__action.value='chooseInvestigation';DownloadMeasurementsSubmit.style.display='inline'; 
													DownloadMeasurementsSubmit.style.display='inline';" title="load another study"	/>	
								<#if filter_has_next> and </#if>
								</#list>				    
					    	</td>
					    <td class="box-body" style="width: 50%"><div >Details:</div></td></tr>
					    <tr>
					    	<td class="box-body">
								<div id="leftSideTree">
									${screen.getTreeView()}<br/>
								</div>
							</td>
						    <td class="box-body" id="showInformation"> 
						    	<table  style="height:500px;width:100% ">
							    	<tr>
								    	<td style="height:250px; padding:0px" >
									    	<div id="details" style="height:250px;overflow:auto">
			      								<script>
													<#if screen.getListOfJSONs()??>
														<#list screen.getListOfJSONs() as eachJSON>
													   		var json = eval(${eachJSON});
													   		$.each(json, function(measurementID, htmlTable){
													   			$('#' + measurementID + '>span').click(function(){
													   				$('#details').empty();
													   				$('#details').append(htmlTable);
													   			});
													   			
													   			$('#' + measurementID + '>span').mouseenter(function(){
										 							$('#popUpDialogue').show();
										 						});
										 						$('#' + measurementID + '>span').mouseout(function(){
										 							$('#popUpDialogue').hide();
										 						});
													   		});
													    </#list>
													</#if>		
												</script>
			      							</div>
      									</td>
  									</tr>
									<tr>
								    	<td style="height:20px; border-top:1px solid lightgray;">
											<div id="selectionState" >Your selection:
												<div id="popUpDialogue" style="float:right;display:none">Click to see details</div>
											</div>
										</td>
									</tr>
									<tr>
							    		<td>
			  								<div id="selectionHeader" style="margin:0px"></div>
										</td>
									</tr>
									<tr>
							    		<td style="height:185px" >
			  								<div id="selection" style="height:185px; overflow:auto; width:100%"></div>
										</td>
									</tr>
									<tr>
										<td style="height:25px; border-top:1px solid lightgray; margin:0px;padding:0px">
				  							<div id="selection" style="height:25px; margin:0px;padding:0px">
												<div style="float:right">
						 							<input class='addbutton ui-button ui-widget ui-state-default ui-corner-all' type="submit" id="downloadButton" name="downloadButton" value="Download as Excel" 
													 onclick="__action.value='downloadButton';"/>
				 								</div>
				 								<div style="float:right">
						 							<input type="submit" class='addbutton ui-button ui-widget ui-state-default ui-corner-all' id="downloadButtonEMeasure" name="downloadButton" value="Download as E-Measure" 
													 onclick="__action.value='downloadButtonEMeasure';"/>
				 								</div>
											</div>
										</td>
									</tr>
								</table>
						   </td>
						</tr>
						<tr>
							<td class="box-body">
							
							</td>
							<td class="box-body">
							<!--<label>Fill in selection name</label>
							<input title="fill in selection name" type="text" name="SelectionName" >
							<input class="saveSubmit" type="submit" id="SaveSelectionSubmit" name="SaveSelectionSubmit" value="Save selection" 
									onclick="__action.value='SaveSelectionSubmit';"/>-->
							</td>
						</tr>
					</table>
			   	</#if>	
			    <label><#if screen.getStatus()?exists>${screen.getStatus()} </#if>  </label>
 				<!-- The detailed table bound to the branch is store in a click event. Therefore this table is not available
 							until the branch has been clicked. As checkbox is part of this branch therefore when the checkbox is ticked
 							the table shows up on the right as well. Another event is fired when the checkbox is checked which is
 							adding a new variable in the selection table and it happens before the detailed table pops up. But we want to
 							use the information (description) from the datailed table. Therefore we have to trigger the click event on branch
 							here first and create the detailed table!-->
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
 					
 					$('#browser').find('input:checkbox').each(function(){
 						
 						$(this).attr('checked', false);
 						
 						$(this).click(function(){
 							
 							$(this).parent().parent().find('span').trigger('click');
 							
 							if($(this).attr('checked') != 'checked'){
 								
 								if($('#selectedVariableTable').find('tr').length > 1){
 									$('#selectedVariableTable').find('tr:last-child').remove();
 								}else{
 									$('#selectedVariableTable').remove();
 									$('#selectedVariableHeader').remove()
 								}
 							}else{
 								
 								var label = $(this).parent().text();
 								var checkBoxID = $(this).attr('id');
		 						var uniqueID = $(this).parent().parent().attr('id');
		 						
		 						var protocolName = $(this).parents('li').eq(1).children('span').text();
		 						var variableDescription = $('#' + uniqueID + '_description').find('td').eq(1).text();
		 						var descriptionShows = variableDescription.substr(0, 10);
		 						var deleteButton = '<img src=\"generated-res/img/cancel.png\" id=\"'+uniqueID+'_delete\" style=\"cursor:pointer;length:16px;width:16px\">';
	 							var content = '<tr id=\"'+uniqueID +'_row\" ><td style=\"width:30%; text-align:left\">' + label + '</td><td id=\"'+uniqueID +'_hover\" style=\"cursor:pointer;width:30%; text-align:left\">' + 
	 										descriptionShows + '...</td><td style=\"width:30%; text-align:left\">' + 
	 										protocolName + '</td><td style=\"text-align:center; width:10%; text-align:left\">' + 
	 										deleteButton + '</td></tr></table>';
		 						
	 							<!--We are going to check whether this selectedVariableTable already existed-->
	 							if($('#selectedVariableTable').length == 0){
	 							
	 								var newTableHeader = '<table id=\"selectedVariableHeader\" style=\"width:100%\" class=\"listtable\">'+
	 								'<th style=\"width:30%; text-align:left\">Variables</th><th style=\"width:30%; text-align:left\">Description</th>'+
	 								'<th style=\"width:30%; text-align:left\">Sector/Protocol</th><th style=\"width:10%;text-align:center\">Delete</th></table>';
	 								var newTable = '<table id=\"selectedVariableTable\"  class=\"listtable\" style=\"width:100%; overflow:auto\">';
	 								newTable += content;
	 								$('#selection').append(newTable);
	 								$('#selectionHeader').append(newTableHeader);
	 								
	 								
	 								
	 								$('#'+uniqueID+'_delete').click(function(){
	 									if($('#selectedVariableTable').find('tr').length > 1){
		 									$('#'+uniqueID+'_row').remove();
		 								}else{
		 									$('#selectedVariableHeader').remove();
		 									$('#selectedVariableTable').remove();
		 									
		 								}
		 								$('#' + checkBoxID).attr('checked',false);
	 								});
	 								
	 							}else{
	 								
	 								$('#selectedVariableTable').find('tr:last-child').after(content);
	 								
	 								if($('#selectedVariableTable tr').length%2 == 1){
	 									$('#'+uniqueID +'_row').addClass('form_listrow0');
	 								}else{
	 									$('#'+uniqueID +'_row').addClass('form_listrow1');
	 								}
	 								
	 								$('#'+uniqueID+'_delete').click(function(){
	 									if($('#selectedVariableTable').find('tr').length > 1){
		 									$('#'+uniqueID+'_row').remove();
		 								}else{
		 									$('#selectedVariableTable').remove();
		 									$('#selectedVariableHeader').remove();
		 								}
		 								$('#' + checkBoxID).attr('checked',false);
	 								});
	 							}
	 							
	 							$('#' + uniqueID +'_hover').click(function(){
	 								$('#' + uniqueID + ' span').trigger('click');
	 							});
	 							$('#' + uniqueID +'_hover').mouseenter(function(){
	 								$('#popUpDialogue').show();
	 							});
	 							$('#' + uniqueID +'_hover').mouseout(function(){
	 								$('#popUpDialogue').hide();
	 							});
 							}
 							$('#variableCount').empty();
 							var count = $('#selectedVariableTable tr').length - 1;
 							$('#variableCount').append(count);
 						});	
 					});
 				</script>
			</div>
		</div>
	</div>
</form>

</#macro>
