<#macro plugins_data_MySelectionsEditPlugin screen>

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
				
				  <#if screen.getUsersSelections()??>
				   <div id="ShoppingCartLabel"> ${screen.getChoiceLabel()}</div>
							<table id="oldDownloads">
								<tr>
									<th></th>
									<th>Name</th>
		  							<th>Measurement details</th>
		  							<th>Date of selection</th>
		  							<th></th>
		  						</tr>
								<tr>
								<#list screen.getUsersSelections() as eachDownload>
										<tr class="alt">
											<td>  <input class="editSelection" type="submit" id="editSelection" name="editSelection" value="edit selection" onclick="__action.value='editSelection';" 					style="color: #000; background: #8EC7DE;
										   border: 2px outset #d7b9c9;
										   font-size:15px;
										   font-weight:bold;
										   "/>
										   
										   <a href="molgenis.do?__target=main&select=CatalogueTreePlugin?measurementId=${eachDownload.getId()}"> Load </a>
											 
											</td>
											<td>  ${eachDownload.getName()}</td>
											<td> <#list eachDownload.getMeasurements_Name() as name> ${name} - <br/></#list> </td>
											<td>  ${eachDownload.getDateOfSelection()}</td>
										</tr>
								</#list>
							</table>
					  	</#if> 	
			</div>
		</div>
	</div>
	
</form>
</#macro>

