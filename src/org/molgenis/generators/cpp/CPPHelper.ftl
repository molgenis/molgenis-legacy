<#function CPPName value>
	<#if value?is_hash>
		<#return helper.getJavaName(value.getName())>
	<#else>
		<#return helper.getJavaName(value)>
	</#if>
</#function>
<#function BLOCKName value>
	<#if value?is_hash> 
		<#return helper.toUpper(value.getName())>
	<#else>
		<#return helper.toUpper(value)>
	</#if>
</#function>
<#function CPPType field>
	<#return helper.getCppType(field)>
</#function>
<#function JavaName value>
	<#if value?is_hash>
		<#return helper.getJavaName(value.getName())>
	<#else>
		<#return helper.getJavaName(value)>
	</#if>
</#function>