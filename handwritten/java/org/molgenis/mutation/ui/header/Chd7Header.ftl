<#macro org_molgenis_mutation_ui_header_Chd7Header screen>
<#if show == "popup">
		<@molgenis_header />
</#if>
<form name="Home" method="get" action="">
<input type="hidden" name="__target" value="View">
<input type="hidden" name="select" value="SearchPlugin">
<input type="hidden" name="__action" value="init">
<input type="hidden" name="expertSearch" value="0">
<a href="javascript:document.forms.Home.submit();">CHD7 Database</a>
</form>
<div class="printbox">
	<a href="javascript:window.print();"><img src="res/img/print.png"></a>
</div>
<br/>
<#if show == "popup">
		<@molgenis_footer />
</#if>
</#macro>
