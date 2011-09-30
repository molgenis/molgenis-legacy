<#--Date:        November 11, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenFTLTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro plugins_header_AnimalDBHeader screen>
<div id="header">

	<div style="float:left">
		<a href="http://www.animaldb.org"><img src="res/img/animaldb.png" width="361px" height="90px"></a>
	</div>
	
	<div style="float:right; margin-top:20px">
		<a href="http://www.molgenis.org"><img src="generated-res/img/logo_molgenis.gif" height="70px"></a>
	</div>
	
	<div style="clear:both"></div>
	
	<div style="float:right; color: maroon; font-size: 35%; font-family: arial, sans-serif; font-style: italic;">
   		${screen.getUserLogin()}
	</div>
	
	<div style="clear:both"></div>
	
	<!-- if screen.loggedIn == true> APIs
		<div align="right" style="color: maroon; font: 12px Arial;margin-right: 10px;">| <a href="about.html">About</a>  | <a href="doc/objectmodel.html">Object model</a>  | <a href="api/R">R-project API</a> | <a href="api/find">HTTP API</a> | <a href="api/rest/?_wadl">REST API</a> | <a href="api/soap?wsdl">Web Services API</a></div>
	</if -->

</div>
</#macro>
