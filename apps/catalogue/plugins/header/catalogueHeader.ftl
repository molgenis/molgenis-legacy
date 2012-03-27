	<#macro plugins_header_catalogueHeader screen>
	
	<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
	<#--optional: mechanism to show messages-->

<div id="header">
	
	<div align="right" style="color: maroon; font: 12px Arial; padding-bottom: 5px;">
	   	
	
  	</div>
		<img src="res/img/catalogue/lifelinesHeader2.png">
		
		<div id="feedbackDiv" style="float:right; padding-right:75px; font-size: 75%; font-family: arial, sans-serif; font-style: italic; display:none; ">
			<fieldset><legend style="font-size: 75%; font-family: arial, sans-serif; font-style: italic;">Provide here your feedback on the ${screen.getActivePlugin()} screen:</legend>
			<label for="feedback" style="vertical-align: middle">feedback text:</label>
			<br />
			<textarea name="feedback" id="feedback" rows="3" cols="70"></textarea>
			<br />
			<label for="name" style="vertical-align: middle">name:</label>
			<br />
			<input id="name" name="name" type="text" />
			<input type="hidden" name="plugin" id="plugin" value="${screen.getActivePlugin()}" />
			<br /><br />
			<input type="submit" value="Send" onclick="hideFeedback(); __action.value='sendFeedback'" />
			<input type="reset" value="Cancel" onclick="hideFeedback(); __action.value='sendFeedback'" />
			</fieldset>
		</div>
		
		<div style="clear:both"></div>
	
	<div class="form_header" id="headermenu" style="width:100%" >
		<div >
	   		<div style="float:right; font-size: 75%; font-family: arial, sans-serif; font-style: italic;">
				<#if screen.getFullUserName()??>
					<a href=# onclick="showFeedback();"><img src="res/img/feedback.jpeg" align="middle" height="20px" alt="Give feedback or comments on the active page." title="Give feedback or comments on the active page."> feedback</a>
					<span style="color:black">&nbsp;|&nbsp;</span>
					<a href='molgenis.do?__target=main&select=UserLogin'>Logged in as: ${screen.getFullUserName()}</a>
					<span style="color:black">&nbsp;|&nbsp;</span>
					<a href="molgenis.do?__target=catalogueHeader&select=catalogueHeader&__action=doLogout">Logout</a>
				<#else>
					<a href="molgenis.do?__target=main&select=UserLogin">Login</a>
				</#if>
					   	<a href="api/R"> | R-project API</a><br/>
			</div>
		</div>
	</div>
	
	<div style="clear:both"></div>
	
	</div>

</form>

<script>
function showFeedback() {
	document.getElementById("feedbackDiv").style.display="block";
	//document.getElementById("buttonDiv").style.display="none";
}

function hideFeedback() {
	document.getElementById("feedbackDiv").style.display="none";
	//document.getElementById("buttonDiv").style.display="block";
}
</script>
</#macro>
