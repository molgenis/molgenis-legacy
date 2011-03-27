<#macro org_molgenis_mutation_ui_header_CvdbHeader screen>
<#if show == "popup">
		<@molgenis_header />
</#if>
<h3>Dutch Variant Database (DVD)</h3>
<div style="position: absolute;	top: 10px; right: 10px;">
	<a href="javascript:window.print();"><img src="res/img/print.png"></a>
</div>
<div align="right" style="color: maroon; font: 12px Arial;margin-right: 10px;">| <a href="about.html">About</a>  | <a href="generated-doc/objectmodel.html">Object model</a>  |
     <a href="generated-doc/fileformat.html">Exchange format</a> | <a href="api/R">R-project API</a> | <a href="api/find">HTTP API</a> | <a href="api/soap?wsdl">Web Services API</a></div>
<br/>
<#if show == "popup">
		<@molgenis_footer />
</#if>
</#macro>
