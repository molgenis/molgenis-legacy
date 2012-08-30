
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

</div>
<#--div id="footer">
	<i>This database was generated using the open source <a href="http://www.molgenis.org">MOLGENIS database generator</a> version ${screen.getVersion()}.
	<br>Please cite <a href="http://www.ncbi.nlm.nih.gov/pubmed/21210979">Swertz et al (2010)</a> and <a href="http://dx.doi.org/10.1093/bioinformatics/bts049" target="_blank">Arends & van der Velde et al (2012)</a> on use.</i>
</div-->
<@molgenis_footer />	