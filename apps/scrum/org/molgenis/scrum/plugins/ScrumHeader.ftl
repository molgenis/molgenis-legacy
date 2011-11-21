<#macro org_molgenis_scrum_plugins_ScrumHeader screen>
<div id="header">	
	<p>
		&nbsp;${application.getLabel()}
	</p>
</div>
<div align="right" style="color: maroon; font: 12px Arial;margin-right: 10px;">
   	${screen.getUserLogin()}
   	&nbsp;|&nbsp;
    <a href="generated-doc/fileformat.html">Exchange format</a>
</div>
</#macro>
