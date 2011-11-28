<#--Date:        November 11, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenFTLTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
-->
<#macro org_molgenis_xgap_xqtlworkbench_header_MolgenisHeader screen>

<div style="height:10px;">&nbsp;</div>

<div id="sample_div" style="background-image:url(clusterdemo/bg/bc_fog4.png); width: 900px; height: 125px; line-height:7.5em;">
<font style="color: white; font-family: verdana,arial,sans-serif; font-weight: bold; letter-spacing: 0.25em; font-size:50px; font-variant: small-caps">
&nbsp;<a href="#" style="text-decoration:none; color: #FFFFFF;" onClick="document.forms.main.__target.value='main';document.forms.main.select.value='ClusterDemo';document.forms.main.submit();">xQTL workbench</a></font>
</div>

<table style="width: 100%;">
	<tr>
		<td align="left">
			<font style="font-size:14px; font-weight:bold;">
			${screen.userLogin}
			</font>
		</td>
		<td align="right">
			<font style="font-size:14px; font-weight:bold;">
				<#-->| <a href="api/REST/">JSON api</a> | <a href="api/SOAP/">SOAP api</a> | <a href="api/REST/">REST api</a> | -->
				<a target="_blank" href="http://www.molgenis.org/wiki/xQTL">Help</a> | <a href="generated-doc/fileformat.html">Exchange format</a> | <a href="api/R/">R api</a> | <a href="api/find/">Find api</a>
			</font>
		</td>
	</tr>
</table>


<div style="height:10px;">&nbsp;</div>

	
</#macro>
