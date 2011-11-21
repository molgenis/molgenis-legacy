<#macro org_molgenis_scrum_plugins_ScrumHeader screen>
<div id="header">	
	<p>
		&nbsp;${application.getLabel()}
	</p>
</div>
<div align="right" style="color: maroon; font: 12px Arial;margin-right: 10px;">
   		${screen.setUserLogin()}
   		${screen.getUserLogin()}
     <!-- <form>  <input type="submit" value="Logout" onclick="__action.value='doLogout';return true;"/><br /><br /><br /> </form> --> 		
   | <!--<a href="about.html">About</a>  | <a href="generated-doc/objectmodel.html">Object model</a>  |--> 
     <a href="generated-doc/fileformat.html">Exchange format</a> | 
     <a href="api/R">R-project API</a> | 
     <a href="api/find">HTTP API</a> | 
     <a href="api/soap?wsdl">Web Services API</a></div>
</#macro>
