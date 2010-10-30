<#macro molgenis_header>
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
	<div class="headerdiv" id="headerdiv" style="position:absolute; z-index: 1;">
		<iframe src="http://www.ebi.ac.uk/inc/head.html" name="head" id="head" frameborder="0" marginwidth="0px" marginheight="0px" scrolling="no"  width="100%" style="position:absolute; z-index: 1; height: 57px;">Your browser does not support inline frames or is currently configured not to display inline frames. Content can be viewed at actual source page: http://www.ebi.ac.uk/inc/head.html</iframe>
	</div>
	<div class="contents" id="contents">
			<table class="contentspane" id="contentspane" summary="The main content pane of the page"   style="width: 100%">
				<tr>
				  <td class="leftmargin"><img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer"  /></td>
				  <td class="leftmenucell" id="leftmenucell">
				  	<div class="leftmenu" id="leftmenu"><img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer"  /></div>
				  </td>
				  <td class="contentsarea" id="contentsarea">
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

<#macro UserInterface screen>
<@molgenis_header/>
<div id="container">
	<div id="header">	
		<div class="breadcrumbs"><a href="/" class="firstbreadcrumb">EBI</a><a href="/arrayexpress/">ArrayExpress</a><a href="">${application.getLabel()}</a></div>
		<h1>${application.getLabel()}</h1>
	</div>
<#list screen.getVisibleChildren() as subscreen>
	<@layout subscreen />
</#list>
	<div id="push">
	</div>
</div>
<div style="text-align: center">
	<i>This database was generated using the open source <a href="http://www.molgenis.org">MOLGENIS database generator</a> version ${application.getVersion()}.
	<br>Please cite <a href="http://www.ncbi.nlm.nih.gov/pubmed/15059831">Swertz et al (2004)</a> or <a href="http://www.ncbi.nlm.nih.gov/pubmed/17297480">Swertz & Jansen (2007)</a> on use.</i>
</div>
<@molgenis_footer />	
</#macro>

<#macro Navigation screen submenu>
	<#assign selectedItem = screen.getSelected()/>
    <#list screen.getVisibleChildren() as item>
		<#assign __target = screen.getName() />
		<#assign select = item.getName() />
		<#if submenu == "true" && item.getParent().getClass().getSuperclass().getSimpleName() == "FormScreen" && item.getClass().getSuperclass().getSimpleName() == "MenuScreen">
		<#elseif item == selectedItem>
<tr><td class="leftNavigationSelected" name="${item.getLabel()}" onClick="document.forms.navigationForm.__target.value='${__target}';document.forms.navigationForm.select.value='${select}';document.forms.navigationForm.submit();">${item.getLabel()}</td></tr>
		<#if item.getChildren()?size &gt; 1>
<tr><td class="navigation"><table class="navigation">
				<#if selectedItem.getClass().getSuperclass().getSimpleName() == "MenuScreen"><@Navigation screen=item submenu="true" /></#if>
</table></td></tr>
		</#if>
		<#else>
<tr><td class="leftNavigationNotSelected" name="${item.getLabel()}" onClick="document.forms.navigationForm.__target.value='${__target}';document.forms.navigationForm.select.value='${select}';document.forms.navigationForm.submit();">${item.getLabel()}</td></tr>
		</#if>
	</#list>
</#macro>