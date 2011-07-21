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
		<link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/contents.css"     type="text/css" />
		<link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/userstyles.css"   type="text/css" />		
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
		<!-- InstanceBeginEditable name="doctitle" -->
		<title>A 100% width page with no left menu</title>
		<!-- InstanceEndEditable -->
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
		<link rel="SHORTCUT ICON" href="http://www.ebi.ac.uk/bookmark.ico" />
		${application.getCustomHtmlHeaders()}
	</head>
<#if show != "popup">	
	<body onload="if(navigator.userAgent.indexOf('MSIE') != -1) {document.getElementById('head').allowTransparency = true;}">
	<!--div class="headerdiv" id="headerdiv" style="position:absolute; z-index: 1;">
		<iframe src="http://www.ebi.ac.uk/inc/head.html" name="head" id="head" frameborder="0" marginwidth="0px" marginheight="0px" scrolling="no"  width="100%" style="position:absolute; z-index: 1; height: 57px;">Your browser does not support inline frames or is currently configured not to display inline frames. Content can be viewed at actual source page: http://www.ebi.ac.uk/inc/head.html</iframe>
	</div>
	<div class="contents" id="contents">
			<table class="contentspane" id="contentspane" summary="The main content pane of the page"   style="width: 100%">
				<tr>
				  <td class="leftmargin"><img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer"  /></td>
				  <td class="leftmenucell" id="leftmenucell">
				  	<div class="leftmenu" id="leftmenu"><img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer"  /></div>
				  </td>
				  <td class="contentsarea" id="contentsarea">-->
<#else>
<body>
</#if>	
</#macro>


<#macro molgenis_footer>
		<!-- end contents here -->					
<!-- InstanceEndEditable -->
<#if show != "popup">	
					<img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer"  alt="spacer" /></td>
					<td class="rightmenucell" id="rightmenucell">
					  <div class="rightmenu" id="rightmenu"><img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer" /></div>
				  	</td>
				</tr>
				</table>
				<table class="footerpane" id="footerpane" summary="The main footer pane of the page">
				<tr>
				  <td colspan ="4" class="footerrow">
					<div class="footerdiv" id="footerdiv"  style="z-index:2;">
						<iframe src="http://www.ebi.ac.uk/inc/foot.html" name="foot" frameborder="0" marginwidth="0px" marginheight="0px" scrolling="no"  height="22px" width="100%"  style="z-index:2;">Your browser does not support inline frames or is currently configured not to display inline frames. Content can be viewed at actual source page: http://www.ebi.ac.uk/inc/foot.html</iframe>
					</div>
				  </td>
				</tr>
	  </table>
	  <script  src="http://www.ebi.ac.uk/inc/js/footer.js" type="text/javascript"></script>
	</div>
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

	<div id="push">
	</div>
</div>
<@molgenis_footer />	
