# Load backend specific header
<#if backend = "PBS">
	<#include "PBSHeader.ftl">
<#elseif backend = "GRID">
	source maverick.sh
</#if>

# Configures the GCC bash environment
. ${root}/gcc.bashrc

<#include "Macros.ftl"/>
<@begin/>
<#include "NGSHeader.ftl"/>
<#if defaultInterpreter = "R"><@Rbegin/></#if>