<#--Date:        November 11, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenFTLTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro org_molgenis_batch_ui_AnimalDBHeader screen>
<div id="header">

	<div style="float:left">
		<a href="http://www.animaldb.org"><img src="res/img/animaldb.png" width="361px" height="90px"/></a>
	</div>
	
	<div align="right">
		<a href="http://www.molgenis.org"><img src="generated-res/img/logo_molgenis.gif" height="70px" align="right"/></a>
	</div>
	
	<div align="right">
		<a href="http://www.ontocat.org"><img src="res/img/ontocat.png" width="70px" height="70px" align="right"/></a>
	</div>
	
	<div align="right" style="clear:left; color: maroon; font-size: 25%; font-family: arial, sans-serif; font-style: italic;">
		<a href="http://www.animaldb.org"><b>AnimalDB<b></a>:&nbsp;
		<a href="http://www.animaldb.org/milestone/AnimalDB%200.9.0">Version 0.9.0</a>&nbsp;|&nbsp;
		<a href="http://www.animaldb.org/timeline">build 5741</a>
<#if screen.getLogin().isAuthenticated()>
		|&nbsp;Logged in as ${screen.login.userName}
</#if>
	</div>

</div>
</#macro>
