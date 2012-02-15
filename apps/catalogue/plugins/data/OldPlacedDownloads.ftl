<#macro plugins_data_OldPlacedDownloads screen>
<style type="text/css">
#oldDownloads {
	font-family:"Trebuchet MS", Arial, Helvetica, sans-serif;
	width:100%;
	border-collapse:collapse;
}

#oldDownloads td, #approveShoppingCart th  {
	font-size:1em;
	border:1px solid #98bf21;
	padding:3px 7px 2px 7px;
}

#oldDownloads th {
	font-size:1.1em;
	text-align:left;
	padding-top:5px;
	padding-bottom:4px;
	background-color:#E01B6A;
	color:#ffffff;
}

#oldDownloads tr.alt td {
	color:#000000;
	background-color:#EAF2D3;
}
</style>
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
			 	<h3> Your Downloads  </h3>
				<h4> Please select if you want to see the list of ALL you placed Downloads, or ONLY the approved ones </h4>
					<select name="DownloadsChoice" id="DownloadsChoice"> 
						<option value="AllPlacedDownloads">All user placed Downloads</option>
	  					<option value="ApprovedDownloads">Only admin approved Downloads</option>
	  					<br/><br/>
						<#if screen.selectedDownloadsChoice??>
							<#if screen.selectedDownloadsChoice == "AllPlacedDownloads">selected="selected"</#if>
							<#if screen.selectedDownloadsChoice == "ApprovedDownloads">selected="selected"</#if>
						</#if> >
					</select>
					<input type="submit" name="submitDownloadsChoice" value="show Downloads" onclick="__action.value='showDownloads';"/>
				
					<br/><br/>	
					
				   <#if screen.getshoppingCart()??>
				   <div id="ShoppingCartLabel"> ${screen.getChoiceLabel()}</div>
							<table id="oldDownloads">
								<tr>
		  							<th>Date & time created</th>
		  							<th>Measurement details</th>
								</tr>
								<tr>
								<#list screen.getshoppingCart() as eachDownload>
										<tr class="alt">
											<td>  ${eachDownload.getDateOfOrder()}</td>
											<td> <#list eachDownload.getMeasurements_Name() as name> ${name} - <br/></#list> </td>
										</tr>
								</#list>
							</table>
					  	</#if>
				   
			</div>
		</div>
	</div>
</form>
</#macro>

