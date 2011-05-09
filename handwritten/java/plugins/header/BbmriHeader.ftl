<#macro plugins_header_BbmriHeader screen>
<#--Date:        November 11, 2009
 * Template:	PluginScreenFTLTemplateGen.ftl.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenFTLTemplateGen 3.3.2-testing

-->
<div id="header">	
	<p><a href="http://www.bbmri.nl/">
		<img src="res/img/bbmri.png" height="70px" align="left" style="vertical-align: bottom;"/> </a>
		&nbsp;${application.getLabel()}
		<br/><font size="2" face="Verdana">&nbsp;&nbsp;&nbsp;Biobanking and Biomolecular Research Infrastructure
	</p>
</div>
<div align="right" style="color: maroon; font: 12px Arial;margin-right: 10px;">
   	${screen.setUserLogin()}
   	${screen.getUserLogin()}
   	<!--<a href="about.html">About</a>  | <a href="generated-doc/objectmodel.html">Object model</a>  |--> 
    | <a href="molgenis.do?__target=main&select=BbmriHelp"> Help </a>
    | <a href="molgenis.do?__target=main&select=BbmriContact"> Contact </a>
          </div>
</#macro>

