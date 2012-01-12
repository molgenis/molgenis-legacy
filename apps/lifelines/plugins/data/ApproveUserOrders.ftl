<#macro plugins_data_ApproveUserOrders screen>

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
				<h2> Use order administration page </h2>
			    <h3> Please choose a user </h3>
			    <select name="user" id="user"> 
					<#list screen.arrayUsers as user>
						<option value="${user.name}">${user.name}</option>			
					</#list>
				</select>
				<input type="submit" name="chooseUser" value="show orders" onclick="__action.value='showOrders';"/>
			  
			    <div id="ShoppingCartLabel">Old User Orders</div>
					<div class="ShoppingCartContents">
						<ul>
							<#if screen.getUserOrders()??>
								<#list screen.getUserOrders() as eachOrder>
									The order is created at ${eachOrder.getDateOfOrder()}
									<#list eachOrder.getMeasurements_Name() as name>
										<li>${name}</li> 
									</#list>
									<br>
								</#list>
							</#if>
						</ul>
						<input type="submit" value="Delete old orders" onclick="if (confirm('You are about to delete ALL orders. This action is irreversible. Are you sure you want to proceed?')) { __action.value='DeleteOldOrders';return true; } else {return false;}"/><br /><br />
					</div>
					<input type="submit" value="Approve user orders" onclick="__action.value='approveOrder';return true;"}"/><br /><br />
			</div>
		</div>
	</div>
	
</form>
</#macro>

