<#macro plugins_data_MeasurementsOrderForm screen>

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
			    <h3> Measurements Order Form  </h3>
			       <h5>Review you shopping cart:</h5>
				
					<div id="ShoppingCartLabel">Shopping cart</div>
					<div class="ShoppingCartContents">
						<ul>
							<#if screen.getshoppingCart()??>
								<#assign measurements = screen.getshoppingCart()>
								<#list measurements.getMeasurements_Name() as name>
									<li>${name}</li> 
								</#list>
							</#if>
						</ul>
						<input type="submit" value="Empty shopping cart" onclick="if (confirm('You are about to delete all you orders. Are you sure you want to proceed?')) { __action.value='EmptyShoppingCart';return true; } else {return false;}"/><br /><br />
						
					</div>
				<h5>Before continuing to checkout, please complete your profile <a href="molgenis.do?__target=main&select=UserLogin"> here </a></h5> 
				
				<h5>If you want to continue with your order to checkout:</h5>
				
				<input type="image" src="res/img/iconCheckout.png" value="Submit" alt="Checkout" onclick="if (confirm('You are about to complete your order. Are you sure you want to checkout?')) {__action.value='checkoutOrder';return true;} else {return false;}"/><br /><br />
				<input type="submit" value="Review old placed orders" onclick="__action.value='seeOldPlacedOrders';return true; "/><br /><br />
				
			</div>
		</div>
	</div>
</form>
</#macro>

