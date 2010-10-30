<#macro molgenis_header>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" >
		<title>${application.getLabel()}</title>
		<!--link rel="shortcut icon" href="generated-res/img/molgenis.ico"-->
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
		${application.getCustomHtmlHeaders()}
	</head>
	<body onload="RecoverScroll.init();${application.getCustomHtmlBodyOnLoad()}">
</#macro>

<#macro molgenis_footer>
	</body>
</html>
</#macro>

<#macro UserInterface screen>
<@molgenis_header/>
<div id="container">
	
<#list screen.children as subscreen>
	<@layout subscreen />
</#list>

	<div id="push">
	</div>
</div>
<div id="footer">
	<i>This database was generated using the open source <a href="http://www.molgenis.org">MOLGENIS database generator</a> version ${application.getVersion()}.
	<br>Please cite <a href="http://www.ncbi.nlm.nih.gov/pubmed/15059831">Swertz et al (2004)</a> or <a href="http://www.ncbi.nlm.nih.gov/pubmed/17297480">Swertz & Jansen (2007)</a> on use.</i>
</div>
<@molgenis_footer />	
</#macro>