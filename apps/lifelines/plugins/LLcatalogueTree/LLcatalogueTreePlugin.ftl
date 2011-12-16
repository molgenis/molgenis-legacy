<#macro plugins_LLcatalogueTree_LLcatalogueTreePlugin screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" id="plugins_LLcatalogueTree_LLcatalogueTreePlugin" name="${screen.name}" action="">
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
			${screen.getName()}
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
				<div id="leftSide">
					${screen.getTreeView()}
				</div><br/>
				<!--div id="CatalogueRightSide"-->
					<div id="ShoppingCartLabel">Shopping cart</div>
					<div class="ShoppingCartContents">
						<ul>
							<#list screen.getShoppingCart() as measurement>
								<li>${measurement.name}
									<input type="image" src="res/img/delete.png" value="Submit" alt="Delete" onclick="if (confirm('You are about to delete an item. Are you sure you want to proceed?')) { __action.value='DeleteMeasurement&measurementName=${measurement.name}';return true; } else {return false;}"/>
								
									<!--input type="submit" value="Delete" onclick="if (confirm('You are about to delete an item. Are you sure you want to proceed?')) { __action.value='DeleteMeasurement&measurementName=${measurement.name}';return true; } else {return false;}"/><br /><br /-->
								</li> 
							</#list>
						</ul>
						<div id="ShoopingCartButton">
							<input type="submit" name="orderMeasurementsSubmit" value="Next" onclick="__action.value='OrderMeasurements';return true;"/><br /><br />
						</div> 
					</div>
				<!--/div-->
			</div>
			
			<input type="hidden" id="testInput" value="">
			
			<label> 	<#if screen.getStatus()?exists>${screen.getStatus()} </#if>  </label>	
			
		</div>
	</div>
</form>

</#macro>
