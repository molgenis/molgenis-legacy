<#-- Factory methods to rapidly create form inputs in MOLGENIS -->


<#macro molgenis_header screen>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<title>${screen.getLabel()}</title>
		<link rel="shortcut icon" type="image/x-icon" href="generated-res/img/molgenis.ico">
		<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/main.css">
		<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/data.css">
		<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/colors.css">
		<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/dateinput.css">
		<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/xrefinput.css">		
		<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/menu.css">
		<!--[if lt IE 8]>
			<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/data_ie.css">
		<![endif]-->
		<script src="generated-res/scripts/all.js" type="text/javascript" ></script>	
		<script src="generated-res/scripts/popup.js" type="text/javascript" language="javascript"></script>	
		<script src="generated-res/scripts/textinput.js" type="text/javascript" language="javascript"></script>		
		<script src="generated-res/scripts/datetimeinput.js" type="text/javascript" language="javascript"></script>
		<script src="generated-res/scripts/xrefinput.js" type="text/javascript" language="javascript"></script>
		<script src="generated-res/scripts/mrefinput.js" type="text/javascript" language="javascript"></script>					
		<script src="generated-res/scripts/menu.js" type="text/javascript" language="javascript"></script>			
		<script src="generated-res/scripts/recoverscroll.js" type="text/javascript" language="javascript"></script>	
		<script src="generated-res/scripts/overlib.js" type="text/javascript" language="javascript"></script>
  		
  		<script src="generated-res/lib/jquery/js/jquery-1.8.3.min.js"></script>
  		<script src="generated-res/lib/jquery/js/jquery-ui-1.9.2.custom.min.js"></script>
  		<link rel="stylesheet" href="generated-res/lib/jquery/css/smoothness/jquery-ui-1.9.2.custom.min.css">

  		<link rel="stylesheet" style="text/css" type="text/css" href="generated-res/css/molgenis_jquery_icons.css">
  		<script src="generated-res/lib/jquery-plugins/validate.js" type="text/javascript"></script>
  		<script src="generated-res/lib/jquery-plugins/autogrow.js" type="text/javascript"></script>
  		<script src="generated-res/lib/jquery-plugins/timepicker.js" type="text/javascript"></script>
  		<link rel="stylesheet" href="generated-res/lib/jquery-plugins/chosen.css">
  		<script src="generated-res/lib/jquery-plugins/chosen.js" type="text/javascript"></script>
  		<script src="generated-res/lib/jquery-plugins/ajax-chosen.js" type="text/javascript"></script>
  		<script src="generated-res/lib/jquery-plugins/autogrowinput.js" type="text/javascript"></script>
  		<script src="generated-res/lib/jquery-plugins/jquery.tooltip.js" type="text/javascript"></script>
  		<link rel="stylesheet" href="generated-res/lib/jquery-plugins/jquery.tooltip.css">
  		<script src="generated-res/lib/jquery-plugins/jquery.bt.js" type="text/javascript"></script>
  		<link rel="stylesheet" href="generated-res/lib/jquery-plugins/jquery.bt.css">
  		<script src="generated-res/lib/jquery-plugins/jquery.hoverintent.js" type="text/javascript"></script>
  		<script src="generated-res/lib/jquery-plugins/jquery.dataTables.js" type="text/javascript"></script>
  		<link rel="stylesheet" href="generated-res/lib/jquery-plugins/jquery.dataTables.css">
  		<link rel="stylesheet" href="generated-res/lib/jquery-plugins/jquery.dataTables_jui.css">
  		<link rel="stylesheet" href="generated-res/lib/jquery-plugins/jquery.dataTables_demo_page.css">
  		
  		<script src="generated-res/lib/jquery-plugins/grid.locale-en.js" type="text/javascript"></script>
  		<script src="generated-res/lib/jquery-plugins/jquery.jqGrid.min.js" type="text/javascript"></script>  		
  		<link rel="stylesheet" href="generated-res/lib/jquery-plugins/ui.jqgrid.css">
  		  		
  		
  		<!--script src="generated-res/lib/jquery-plugins/jquery-xqs-flyoutmenu.js"></script>
  		<script src="generated-res/lib/jquery-plugins/jquery-xqs-flyoutmenu.css"></script>
  		<script src="generated-res/lib/jquery-plugins/jquery-xqs-menubar.js"></script
  		<link rel="stylesheet" href="generated-res/lib/jquery-plugins/fg.menu.css"-->
 
		<!--<script src="http://ajax.googleapis.com/ajax/libs/dojo/1.6/dojo/dojo.xd.js" djConfig="parseOnLoad: true"></script>
        <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/dojo/1.6/dijit/themes/claro/claro.css"/>-->

		<#if screen.controller??>${screen.controller.getCustomHtmlHeaders()}</#if>
	</head>
<#--claro is for dojo-->
	<body class="claro" onload="RecoverScroll.init();<#if screen.controller??>${screen.controller.getCustomHtmlBodyOnLoad()}</#if>">
	<#if applicationHtmlError?exists>${applicationHtmlError}</#if>
</#macro>

<#macro molgenis_footer>
	</body>
</html>
</#macro>