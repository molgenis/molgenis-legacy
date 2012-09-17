<#include "../../../../org/molgenis/framework/ui/Layout.ftl"/>
<#assign title=screen.label/>
<#macro molgenis_header screen>
<html>
	<head>
		<title>${application.getLabel()}</title>
		<meta http-equiv="Content-Language" content="en-GB" />
		<meta http-equiv="Window-target" content="_top" />
		<meta name="no-email-collection" content="http://www.unspam.com/noemailcollection/" />		
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-11526343-1");
pageTracker._trackPageview();
} catch(err) {}
</script>		
		<!--link rel="shortcut icon" href="generated-res/img/molgenis.ico"-->
		<link rel="stylesheet" style="text/css" href="generated-res/css/colors.css" />
		<link rel="stylesheet" style="text/css" href="generated-res/css/main.css" />
		<link rel="stylesheet" style="text/css" href="generated-res/css/data.css" />
		<link rel="stylesheet" style="text/css" href="generated-res/css/dateinput.css" />
		<link rel="stylesheet" style="text/css" href="generated-res/css/xrefinput.css" />		
		<link rel="stylesheet" style="text/css" href="generated-res/css/menu.css" />
		<!-- ebi -->		
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />  
		<meta name="author" content="EBI Web Team" />
		<meta http-equiv="Content-Language" content="en-GB" />
		<meta http-equiv="Window-target" content="_top" />
		<meta name="no-email-collection" content="http://www.unspam.com/noemailcollection/" />
		<meta name="generator" content="Dreamweaver 8" />
		<link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/contents.css"     type="text/css" />
		<link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/userstyles.css"   type="text/css" />
		<script  src="http://www.ebi.ac.uk/inc/js/contents.js" type="text/javascript"></script>
		<link rel="SHORTCUT ICON" href="http://www.ebi.ac.uk/bookmark.ico" />
		<style type="text/css">
		@media print { 
			body, .contents, .header, .contentsarea, .head { 
				position: relative;
			}  
		} 
		</style>	
		<!-- end ebi -->
		<script src="generated-res/scripts/all.js" language="JavaScript"></script>	
		<script src="generated-res/scripts/textinput.js" language="javascript"></script>		
		<script src="generated-res/scripts/popup.js" language="javascript"></script>	
		<script src="generated-res/scripts/datetimeinput.js" language="javascript"></script>
		<script src="generated-res/scripts/xrefinput.js" language="javascript"></script>
		<script src="generated-res/scripts/mrefinput.js" language="javascript"></script>					
		<script src="generated-res/scripts/menu.js" language="javascript"></script>	
		
		<link href="generated-res/lib/jquery/css/smoothness/jquery-ui-1.8.7.custom.css" rel="stylesheet" type="text/css">
  		<script src="generated-res/lib/jquery/js/jquery-1.6.1.min.js" type="text/javascript"></script>
  		<script src="generated-res/lib/jquery/js/jquery-ui-1.8.7.custom.min.js" type="text/javascript"></script>

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
		
		
		${application.getCustomHtmlHeaders()}
	</head>
<#if show != "popup">	
	<body onload="if(navigator.userAgent.indexOf('MSIE') != -1) {document.getElementById('head').allowTransparency = true;}">

<div class="headerdiv" id="headerdiv" style="position:absolute; z-index: 100000;"> 
		<iframe src="http://www.ebi.ac.uk/inc/head.html" name="head" id="head" frameborder="0" marginwidth="0px" marginheight="0px" scrolling="no"  width="100%" style="position:absolute; z-index: 100; height: 57px;">Your browser does not support inline frames or is currently configured not to display inline frames. Content can be viewed at actual source page: http://www.ebi.ac.uk/inc/head.html</iframe> 
</div> 
	<div class="contents" id="contents"> 
			<table class="contentspane" id="contentspane" summary="The main content pane of the page"   style="width: 100%"> 
				<tr> 
				  <td class="leftmargin"><img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer"  /></td> 
				  <td class="leftmenucell" id="leftmenucell"> 
				  	<div class="leftmenu" id="leftmenu"><img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer"  /></div> 
				  </td> 
				  <td class="contentsarea" id="contentsarea"> 
	
<div class="breadcrumbs">
<a href="/" class="firstbreadcrumb">EBI</a>
<a href="/arrayexpress/">ArrayExpress</a>
<a href="">Phenoflow</a>
</div>


		<table><tr><td width="100%"><h1>GEN2PHEN Phenoflow reference implementation</h1></td><td>
		<a href="http://www.molgenis.org">
			<img src="generated-res/img/logo_molgenis.gif" height="70px" align="right"/>
		</a>
		</td></tr></table>	
		
		<div align="right" style="color: maroon; font: 12px Arial;margin-right: 10px;"><a href="generated-doc/objectmodel.html">Model Docs</a>  | <a href="generated-doc/fileformat.html">File Format Docs</a> | <a href="api/R">R-project API</a> | <a href="api/find">HTTP API</a> | <a href="api/rest/?_wadl">REST API</a> | <a href="api/soap?wsdl">Web Services API</a></div>
		

<#else>
<body>
</#if>	
</#macro>


<#macro molgenis_footer>

<#if show != "popup">	

</body> 
<!-- InstanceEnd --></html> 
</#if>

</body>
<!-- InstanceEnd --></html>
</#macro>

<@molgenis_header screen/>
<div id="container">
	
<#if screen.target?exists && screen.show=="popup">
	<@layout screen.target/>
<#else>	

<#list screen.children as subscreen>
	<@layout subscreen />
</#list>

</#if>

<div id="footer">
	<i>This database was generated using the open source <a href="http://www.molgenis.org">MOLGENIS database generator</a> version ${screen.getVersion()}.
	<br>Please cite <a href="http://www.ncbi.nlm.nih.gov/pubmed/21210979">Swertz et al (2010)</a> or <a href="http://www.ncbi.nlm.nih.gov/pubmed/17297480">Swertz & Jansen (2007)</a> on use.</i>
</div>
</div>
<@molgenis_footer />	
