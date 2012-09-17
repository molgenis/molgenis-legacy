<#--main screen having header and footer-->
<#macro screens_MainScreen screen>
<html><head>
	<link href="designgg.css" rel="stylesheet" type="text/css"/>
	${screen.changeState()}
	<#if screen.selectedScreen == 2>
	<META HTTP-EQUIV="Refresh" CONTENT="5;">
	</#if>
	<title>Genetical Genomics Experiment Designer</title>
<body>
	<div class="head">
	<h1>Optimize your Genetical Genomics Experiment</h1>
	<form style="margin:0px;" method="post" enctype="multipart/form-data"> 
	<input type="hidden" name="__target" value="${screen.name}"/>
	<input type="submit" name="__action" value="back"/>
	</form>
	</div>
	<!-- screen body -->
	<@layout screen.getSelected()/>

</body>
</html>
</#macro>