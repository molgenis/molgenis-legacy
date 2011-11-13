<#--Date:        November 11, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenFTLTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro plugins_header_AnimalDBHeader screen>
<div id="header">

	<div id=logo container style="width:100%; height:54px">
		<div style="float:left; margin-top:3px; margin-bottom:2px">
			<a href="http://www.animaldb.org"><img src="res/img/rug_fmns_animaldb_header_logo.png" width="620px" height="49px"></a>
		</div>
	
		<div style="float:right">
			<a href="http://www.molgenis.org"><img src="generated-res/img/logo_molgenis.gif" height="49px"></a>
		</div>
	</div>
	<div style="clear:both"></div>
	<div class="form_header" id="headermenu" style="width:100%" >
		<div style="float:right; font-size: 75%; font-family: arial, sans-serif; font-style: italic;">
	   		${screen.getUserLogin()}
		</div>
	</div>
	
	<div style="clear:both"></div>
	
	<!-- if screen.loggedIn == true> APIs
		<div align="right" style="color: maroon; font: 12px Arial;margin-right: 10px;">| <a href="about.html">About</a>  | <a href="doc/objectmodel.html">Object model</a>  | <a href="api/R">R-project API</a> | <a href="api/find">HTTP API</a> | <a href="api/rest/?_wadl">REST API</a> | <a href="api/soap?wsdl">Web Services API</a></div>
	</if -->

</div>
</#macro>
