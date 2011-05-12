<#include "ScreenCommand.ftl">
<#include "Layout.ftl"/>
<#-- start with the 'main' screen, which is called 'application'-->

<#assign title=screen.label/>

<#--rendering starts here -->
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
<div id="footer">
	<i>This database was generated using the open source <a href="http://www.molgenis.org">MOLGENIS database generator</a> version ${screen.getVersion()}.
	<br>Please cite <a href="http://www.ncbi.nlm.nih.gov/pubmed/15059831">Swertz et al (2004)</a> or <a href="http://www.ncbi.nlm.nih.gov/pubmed/17297480">Swertz & Jansen (2007)</a> on use.</i>
</div>
<@molgenis_footer />	