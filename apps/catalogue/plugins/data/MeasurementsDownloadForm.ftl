<#macro plugins_data_MeasurementsDownloadForm screen>

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
			<table class="box_withoutBorder" width="100%" cellpadding="0" cellspacing="0">
			   <!--tr><td class="box-header"> <h4> Measurements Download Form  </h4> </td></tr-->
			
			    <h3> Measurements Download Form  </h3>
			       <h5>Review you Download list:</h5>
				   <tr><td class="box_withoutBorder-body">
					
						<div id="ShoppingCartLabel">Download list</div>
						<div class="ShoppingCartContents">
							<ul>
								<#if screen.getshoppingCart()??>
									<#assign measurements = screen.getshoppingCart()>
									<#list measurements.getMeasurements_Name() as name>
										<li>${name}</li> 
									</#list>
								</#if>
							</ul>
							<input type="submit" value="Empty download list" onclick="if (confirm('You are about to delete all you Downloads. Are you sure you want to proceed?')) { __action.value='EmptyShoppingCart';return true; } else {return false;}"/><br /><br />
					    </div>
					    </td>
				   </tr>	
				   <tr><td class="box_withoutBorder-body">
				   
						<h5>Before continuing to checkout, please complete your profile <a href="molgenis.do?__target=main&select=UserLogin"> here </a></h5> 
				
						<h5>If you want to complete your download press below:</h5>
				   </td></tr>		
				   <tr><td class="box_withoutBorderx-body"></td>	<td class="box_withoutBorder-body">
						<input type="image" src="res/img/Download-icon.png" value="Submit" alt="Download" onclick="if (confirm('You are about to complete your Download. Are you sure?')) {__action.value='checkoutDownload';return true;} else {return false;}"/><br /><br />
						<input type="submit" value="Review old placed Downloads" onclick="__action.value='seeOldPlacedDownloads';return true; "/><br /><br />
				   </td></tr>		
			</table>
				
			</div>
		</div>
	</div>
</form>
</#macro>

