<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" name="${model.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${model.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	<#global imgM="res/img/gids/Min_pic.png">
	<#global imgP="res/img/gids/Plus_pic.png">
<!-- this shows a title and border -->
	<div class="formscreen">
		<div class="form_header" id="${model.getName()}">
		${model.label}
		</div>
		
		<#--optional: mechanism to show messages-->
		<#list model.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		<script>
            $(document).ready(function() {
                $('.showHideLabel').click(function() {
				
        			$('#showHide' +$(this).attr('id')).toggle();
                   
                    if(  $(this).attr('src') == '${imgM}' ) {
						 $(this).attr('src', '${imgP}');
					} else {
						$(this).attr('src', '${imgM}');
					}
                }
            );
            });
    </script>	
    
    
		<div class="screenbody">
			<div class="screenpadding">	
	
<#--begin your plugin-->	
<table>
	<tr>
		<td><label for="investigation"> Choose project:</label></td>
		<td><select name="investigationDropdown" class="ui-widget-content ui-corner-all" id="investigationDropdown" style="width:150px;">
			<#list screen.listInvest as investigation>
				<option value="${investigation.getName()}"
					<#if screen.chosenInv??>
						<#if screen.chosenInv==investigation.name>
							selected="selected"
						</#if>
					</#if>
				>${investigation.getName()}
				</option>			
			</#list>		
			</select>
		</td>
		<script>$("#investigationDropdown").chosen();</script>
	<td><@action name="goToIndiv" label="update"/></td>
	</tr>
	<#if screen.stateStart ="chooseIndividual">		
		<tr>
		  <td><label for="individual"> Choose individual:</label></td>
		  <td>
			<select name="individual"class="ui-widget-content ui-corner-all" id="individual" style="width:150px;">
			  <option value="Select individual"></option>
			  <#list screen.listIndiv as individual>
		        <option value="${individual.getName()}"<#if screen.chosenInd??><#if screen.chosenInd==individual.name> selected="selected"</#if></#if>
				 >${individual.getName()}
				 </option>			
		      </#list>		
			</select>
		  </td>
		  <script>$("#individual").chosen();</script>
		  <td><@action name="goToTable" label="update"/></td>
		</tr>
</table>		

	<#if screen.state = "individual">
		<#list screen.hashProtocols?keys as key>
	
		
			<#if "${key}"=="Individual_info">
			<p style="font-weight:bold"><img id="${key}" class="showHideLabel" src="${imgM}"/>&nbsp;${key}</p>
				<div class="showHide" style="display:block" id="showHide${key}">
			
			<#else>
			<p style="font-weight:bold"><img id="${key}" class="showHideLabel" src="${imgP}"/>&nbsp;${key}</p>
				<div class="showHide" style="display:none" id="showHide${key}">
			</#if>
					<table>
					<td width:180px"></td>
						<tr>
			    		<#if screen.hashProtocols[key]??>
			    			<#list screen.hashProtocols[key] as la>
			    				<label><tr><td style="width:120px">${la}</td><td><input class="ui-widget-content ui-corner-all" name='createNewInvest' id="${la}${key}" type='text'></td></tr></label>
			    			</#list>
			    		</#if>
			    		</tr>
			    	</table>
			    	
	    		</div>
			
		</#list>
		<@action name="submitting" label="Submit"/>
	</#if>
	  
	</#if>

<#--end of your plugin-->	
			</div>
		</div>
	</div>
</form>
