<#macro plugins_catalogueTree_catalogueTreePlugin screen>
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
		
		<div align="right">
				<label>Search:</label>
				<select id="selectedField" name="selectedField" title="choose field" name="chooseField" style="display:none"> 
					<#list screen.arraySearchFields as field>
								<!--#assign FieldName = field.name-->
						<option value="${field}" <#if screen.selectedField??>
							<#if screen.selectedField == field>selected="selected"</#if></#if> >${field}</option>			
					</#list>
					 <!--option value="All fields">All fields</option-->
				</select>
				<select name="searchingInvestigation" id="searchingInvestigation" style="display:none"> 
					<#list screen.arrayInvestigations as inv>
						<#assign searchingInvName = inv.name>
						<option value="${searchingInvName}" <#if screen.searchingInvestigation??><#if screen.searchingInvestigation == searchingInvName>selected="selected"</#if></#if> >${searchingInvName}</option>			
					</#list>
				</select>		
				<input title="fill in search term" type="text" name="InputToken" 
					onfocus="selectedField.style.display='inline'; selectedField.style.display='inline';searchingInvestigation.style.display='inline'; searchingInvestigation.style.display='inline'; " 
					onkeydown="if (event.keyCode==13)__action.value='SearchCatalogueTree';return true;">	
				
				<input type="submit" name="SearchCatalogueTree" value="search" onclick="__action.value='SearchCatalogueTree';"/>
				<img class="navigation_button" src="generated-res/img/filter.png" alt="Add filter"	onclick="__action.value='SearchCatalogueTree';return true;""/>		
		
		</div>

		
		<div class="screenbody">
			<div class="screenpadding">
				<h4> Choose an investigation</h4> 
				<select name="investigation" id="investigation"> 
					<#list screen.arrayInvestigations as inv>
						<#assign invName = inv.name>
						<option value="${invName}" <#if screen.selectedInvestigation??><#if screen.selectedInvestigation == invName>selected="selected"</#if></#if> >${invName}</option>			
					</#list>
				</select>
				<!--input type="submit" name="chooseInvestigation" value="refresh tree" onclick="__action.value='chooseInvestigation';"></input-->
				<input type="image" src="res/img/refresh.png" alt="Submit" 
					name="chooseInvestigation" style="vertical-align: middle;" 
					value="refresh tree" onclick="__action.value='chooseInvestigation';DownloadMeasurementsSubmit.style.display='inline'; 
					DownloadMeasurementsSubmit.style.display='inline';"	/>	
					
			   <#if screen.isSelectedInv() == true>
					<table class="box" width="100%" cellpadding="0" cellspacing="0">
					    <tr><td class="box-header">  
					        <div id="masstoggler"> 
					        	Catalogue
			 					<a title="Collapse entire tree" href="#"><img src="res/img/toggle_collapse_tiny.png"  style="vertical-align: bottom;"></a> 
			 					<a title="Expand entire tree" href="#"><img src="res/img/toggle_expand_tiny.png"  style="vertical-align: bottom;"></a> 
			 				</div>
					    </td></tr>
					    <tr><td class="box-body">
								<div id="leftSideTree">  
									${screen.getTreeView()}
								</div><br/>
						    </td>
						    <td class="box-body">
						    	<!--div id="scrollingDiv"--> 
      								<div id="details"></div> <br/><br/>
      							<!--/div-->
								<div id="ShoopingCartButton" style="float: right;">
									<!--input type="submit" style="display:none" id="DownloadMeasurementsSubmit" name="DownloadMeasurementsSubmit" value="Download" 
									onclick="__action.value='DownloadMeasurements';"/-->
									<input type="submit" style="display:none" id="DownloadMeasurementsSubmit" name="DownloadMeasurementsSubmit" value="Download" 
									onclick="__action.value='DownloadMeasurements';"/>
									
								</div>
								
						   </td>
						</tr>
						<tr><td class="box-body"></td>
							<td class="box-body"></td>
							<td class="box-body">
								<!-- download button here?? or up-->
							</td>
						</tr>
					</table>
			   </#if>	
			</div>
		</div>
	</div>
</form>

</#macro>
