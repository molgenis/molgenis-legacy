<#macro plugins_data_ApproveUserDownloads screen>
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
				<h2> Use Download administration page </h2>
			    <h3> Please choose a user </h3>
			    <select name="user" id="user"> 
					<#list screen.arrayUsers as user>
						<option value="${user.name}">${user.name}</option>			
					</#list>
				</select>
				<input type="submit" name="chooseUser" value="show Downloads" onclick="__action.value='showDownloads';"/>
				<br/><br/><br/>
					<#if screen.getUserDownloads()??>
						<table id="approveDownloadList">
							<tr>
	  							<th>Date & time created</th>
	  							<th>Measurement details</th>
	 							<th>Approve the Download</th>
							</tr>
							<tr>
							<#list screen.getUserDownloads() as eachDownload>
									<tr class="alt">
										<td>  ${eachDownload.getDateOfOrder()}</td>
										<td> <#list eachDownload.getMeasurements_Name() as name> ${name} - <br/></#list> </td>
										<td><input type="checkbox" name="approvedItems" value=${eachDownload.getId()}><br/> </td>
									</tr>
							</#list>
						</table>
				  	</#if>
				  	
				<br/><br/>  	
				<input type="submit" value="Approve selected Downloads" onclick="if (confirm('Approve the selected Downloads?')) { __action.value='ApproveSelectedDownloads';return true; } else {return false;}"/><br /><br />
				<input type="submit" value="Delete old Downloads" onclick="if (confirm('You are about to delete ALL Downloads. This action is irreversible. Are you sure you want to proceed?')) { __action.value='DeleteOldDownloads';return true; } else {return false;}"/><br /><br />
			</div>
		</div>
	</div>
	
</form>
</#macro>

