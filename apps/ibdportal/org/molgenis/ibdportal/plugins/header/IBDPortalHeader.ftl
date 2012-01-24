<#--Date:        November 11, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenFTLTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro org_molgenis_ibdportal_plugins_header_IBDPortalHeader screen>

<form method="post" enctype="multipart/form-data" name="${screen.name}" action="">
	<!--needed in every form: to redirect the request to the right screen-->
	<input type="hidden" name="__target" value="${screen.name}">
	<!--needed in every form: to define the action. This can be set by the submit button-->
	<input type="hidden" name="__action">
	
	<#--optional: mechanism to show messages-->
	<#list screen.getMessages() as message>
		<#if message.success>
			<script>$.ctNotify("${message.text}", {type: 'confirmation', delay: 5000});</script>
	<!-- <p class="successmessage">${message.text}</p> -->
		<#else>
			<script>$.ctNotify("${message.text}", {type: 'error', delay: 7000});</script>	        	
	<!-- <p class="errormessage">${message.text}</p> -->
		</#if>
	</#list>

<div id="header">

	<div style="width:100%">
		<div style="float:left; margin-top:3px; margin-bottom:2px">
			<img src="res/img/ibdportal/logo.jpg" width="150px" height="49px">
		</div>

		<div style="float:right">
			<a href="http://www.molgenis.org" target="_blank"><img src="generated-res/img/logo_molgenis.gif" height="49px"></a>
		</div>
	</div>
	<div style="clear:both"></div>
	<div class="form_header" id="headermenu" style="width:100%" >
		<div style="float:right; font-size: 75%; font-family: arial, sans-serif; font-style: italic;">
	   		<div style="float:right; font-size: 75%; font-family: arial, sans-serif; font-style: italic;">
				<#if screen.getFullUserName()??>
					<a href='molgenis.do?__target=main&select=UserLogin'>Logged in as: ${screen.getFullUserName()}</a>
					<span style="color:black">&nbsp;|&nbsp;</span>
					<a href="molgenis.do?__target=IBDPortalHeader&select=IBDPortalHeader&__action=doLogout">Logout</a>
				<#else>
					<a href="molgenis.do?__target=main&select=UserLogin">Login</a>
				</#if>
			</div>
		</div>
	</div>
	<div style="clear:both"></div>
	
	<!-- if screen.loggedIn == true> APIs
		<div align="right" style="color: maroon; font: 12px Arial;margin-right: 10px;">| <a href="about.html">About</a>  | <a href="doc/objectmodel.html">Object model</a>  | <a href="api/R">R-project API</a> | <a href="api/find">HTTP API</a> | <a href="api/rest/?_wadl">REST API</a> | <a href="api/soap?wsdl">Web Services API</a></div>
	</if -->
	
	

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

