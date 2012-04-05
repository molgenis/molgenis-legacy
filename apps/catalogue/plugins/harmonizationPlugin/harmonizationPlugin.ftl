<#macro plugins_harmonizationPlugin_harmonizationPlugin screen>


<!-- normally you make one big form for the whole plugin-->
<form method="post" enctype="multipart/form-data" id="plugins_catalogueTree_catalogueTreePlugin" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action" id="test" value="">
	
	<#global imgM="res/img/gids/Min_pic.png">
	<#global imgP="res/img/gids/Plus_pic.png">
	
	
<!-- this shows a title and border -->


	<div class="formscreen">
		<div class="form_header" id="${screen.getName()}">
			${screen.label}
		</div>
		
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
		<#--optional: mechanism to show messages-->
		<#list screen.getMessages() as message>
			<#if message.success>
		<p class="successmessage">${message.text}</p>
			<#else>
		<p class="errormessage">${message.text}</p>
			</#if>
		</#list>
		
		<div class="screenbody">
			
			<input type="submit" name="mapping" value="Do Mapping" onclick="__action.value='mapping';"/>
			
			<input type="textfield" name="showItems" value="10"/> Please decide how many matched items u want to see
			
			</br></br></br>
			
			${screen.getMatchingResult()}
			
			<!--
			<p style="font-weight:bold"><img id="1" class="showHideLabel" src="${imgM}"/>&nbsp;1</p>
				<div class="showHide" style="display:block" id="showHide1">
				
			<p style="font-weight:bold"><img id="1" class="showHideLabel" src="${imgP}"/>&nbsp;2</p>
				<div class="showHide" style="display:none" id="showHide2">
			-->
			
			
		
			
		</div>
		
	</div>
</form>

</#macro>
