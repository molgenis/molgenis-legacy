<#--Date:        November 11, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenFTLTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro plugins_header_AnimalDBHeader screen>
<div id="header">

	<div style="float:left">
		<a href="http://www.animaldb.org"><img src="res/img/animaldb.png" width="361px" height="90px"/></a>
	</div>
	
	<div align="right">
		<a href="http://www.molgenis.org"><img src="generated-res/img/logo_molgenis.gif" height="70px" align="right"/></a>
	</div>
	
	<div align="right" style="margin-right:10px">
		<a href="http://www.ontocat.org"><img src="res/img/ontocat.png" width="70px" height="70px" align="right"/></a>
	</div>
	
	<#if screen.loggedIn == true>
		<div align="right" style="margin-right:10px">
			<a href="molgenis.do?__target=batches&select=BatchView"><img src="res/img/shoppingcart.jpg" height="70px" align="right"/></a>
		</div>
	</#if>
	
	<div align="right" style="clear:left; color: maroon; font-size: 25%; font-family: arial, sans-serif; font-style: italic;">
		<a href="http://www.animaldb.org"><b>AnimalDB<b></a>:&nbsp;
		<a href="http://www.animaldb.org/milestone/AnimalDB%200.9.0">Version 0.9.3</a>&nbsp;|&nbsp;
		<a href="http://www.animaldb.org/timeline">build 7102</a>
	</div>
	
	
	<#if screen.loggedIn == true>
		<!-- 
			<div align="right" style="color: maroon; font: 12px Arial;margin-right: 10px;">| <a href="about.html">About</a>  | <a href="doc/objectmodel.html">Object model</a>  | <a href="api/R">R-project API</a> | <a href="api/find">HTTP API</a> | <a href="api/rest/?_wadl">REST API</a> | <a href="api/soap?wsdl">Web Services API</a></div>
		-->
	</#if>

</div>
</#macro>
