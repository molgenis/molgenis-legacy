<#-- Factory methods to rapidly create form inputs in MOLGENIS -->


<#macro molgenis_header screen>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=ISO-8859-1" >
		<title>${screen.getLabel()}</title>
		<link rel="shortcut icon" type="image/x-icon" href="generated-res/img/molgenis.ico" />
		<script src="generated-res/scripts/jquery.js" type="text/javascript" language="javascript"></script>
		<script src="generated-res/scripts/jquery.validate.js" type="text/javascript" language="javascript"></script>
		<script type="text/javascript" language="javascript">
		$(document).ready(function(){
	    	$("#Varchars_form").validate();
	  	});
		</script>
		<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/main.css">
		<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/data.css">
		<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/colors.css">
		<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/dateinput.css">
		<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/xrefinput.css">		
		<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/menu.css">			
		<script language="JavaScript" type="text/javascript" src="generated-res/scripts/all.js"></script>	
		<script src="generated-res/scripts/popup.js" type="text/javascript" language="javascript"></script>	
		<script src="generated-res/scripts/textinput.js" type="text/javascript" language="javascript"></script>		
		<script src="generated-res/scripts/datetimeinput.js" type="text/javascript" language="javascript"></script>
		<script src="generated-res/scripts/xrefinput.js" type="text/javascript" language="javascript"></script>
		<script src="generated-res/scripts/mrefinput.js" type="text/javascript" language="javascript"></script>					
		<script src="generated-res/scripts/menu.js" type="text/javascript" language="javascript"></script>			
		<script src="generated-res/scripts/recoverscroll.js" type="text/javascript" language="javascript"></script>	
  <#--
  <link rel="stylesheet" style="text/css" type="text/css" href="jquery/css/smoothness/jquery-ui-1.8.7.custom.css">			
  <script src="jquery/js/jquery-1.4.4.min.js"></script>
  <script src="jquery/js/jquery-ui-1.8.7.custom.min.js"></script>
  -->
		<#if screen.controller??>${screen.controller.getCustomHtmlHeaders()}</#if>
	</head>
	<body onload="RecoverScroll.init();<#if screen.customHtmlBodyOnLoad?exists>${screen.getCustomHtmlBodyOnLoad()}</#if>">
	<#if applicationHtmlError?exists>${applicationHtmlError}</#if>
</#macro>

<#macro molgenis_footer>
	</body>
</html>
</#macro>