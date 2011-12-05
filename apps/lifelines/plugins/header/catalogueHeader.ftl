<#macro plugins_header_catalogueHeader screen>
<div id="header">	
	<p><a href="http://www.lifelines.nl/">
		<img src="res/img/lifelines.png" height="70px" align="left" style="vertical-align: bottom;"/> </a>
		<font size="6" face="Verdana">&nbsp;${application.getLabel()}
		<br/><font size="3" face="Verdana">&nbsp;&nbsp;&nbsp;Lifelines catalogue
	</p>
</div>
<div align="right" style="color: maroon; font: 12px Arial;">
   	${screen.setUserLogin()}
   	${screen.getUserLogin()}
<!--<a href="about.html">About</a>   	  | <a href="generated-doc/objectmodel.html">Object model</a>  |--> 
    <!--| <a href="molgenis.do?__target=main&select=BbmriHelp"> Help </a> 
    | <a href="molgenis.do?__target=main&select=BbmriContact"> Contact </a>-->
 </div>
</#macro>
