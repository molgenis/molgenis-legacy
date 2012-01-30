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

<#function figures figs>
	<#local result = "">
	<#assign maxnfigs=6 />
	<#assign nplots=figs?size />
	<#list 0..(nplots - 1) as i>
	<#if i % maxnfigs = 0><#local result =  result + "\\begin{figure}[ht]"></#if>
			<#local result =  result + "\\begin{minipage}{0.4\\linewidth}">
				<#local result =  result + "\\centering">
				<#local result =  result + "\\includegraphics[width=\\textwidth]{${figs[i]}}">
				<#local result =  result + "\\caption{${externalSampleID[i]}}">
				<#local result =  result + "\\end{minipage}">
	<#local result =  result + "\\hspace{2cm}"><#if (i+1) = nplots || (i+1) % maxnfigs = 0><#local result =  result + "\\end{figure}"></#if></#list>
	<#return result>
</#function>













