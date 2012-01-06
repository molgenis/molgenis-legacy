<#function csvQuoted items>
	<#local result = "">
	<#list items as item>
		<#if item_index != 0>
			<#local result =  result + ",">
		</#if>
		<#local result = result + "\"" + item + "\"">
	</#list>
	<#return result>
</#function>

<#function csv items>
	<#local result = "">
	<#list items as item>
		<#if item_index != 0>
			<#local result =  result + ",">
		</#if>
		<#local result = result + "" + item + "">
	</#list>
	<#return result>
</#function>

<#function ssvQuoted items>
	<#local result = "">
	<#list items as item>
		<#if item_index != 0>
			<#local result =  result + " ">
		</#if>
		<#local result = result + "\"" + item + "\"">
	</#list>
	<#return result>
</#function>
