<#macro plugins_LLcatalogueTree_LLcatalogueTreePlugin screen>
<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
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
				<div id="leftSide" style="width:200px;">
					${screen.getTreeView()}
				</div>
				<!--div id="rightSide" style="position: absolute;  float:right; top: 10px; "-->
				<div id="CatalogueRightSide">
					right side	
				</div><br/>
				<div id="ShoppingCartLabel">Shopping cart</div>
				<div id="ShoppingCartContents">
					laaaaaaaaaaaaaaaaaaa
					<ul>
					<#list screen.getShoppingCart() as measurement>
					<li>${measurement.name}</li>
					</#list>
					</ul>
				</div>
				
				<!--<div id="bottom" style="clear:both;">
					bottom
				</div-->
				
			</div>
			<input type="hidden" id="test" value="">
		</div>
	</div>
</form>
</#macro>
