#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#


<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=23:59:00

inputs "${indexfile}"
inputs "${leftbwaout}"
inputs "${rightbwaout}"
inputs "${leftbarcodefqgz}"
inputs "${rightbarcodefqgz}"
alloutputsexist "${samfile}"

<#if seqType == "PE">${bwasampejar} sampe -P \<#else>${bwasampejar} samse \</#if>
-p illumina \
-i ${lane} \
-m ${externalSampleID} \
-l ${library} \
${indexfile} \
${leftbwaout} \
<#if seqType == "PE">${rightbwaout} \
</#if>${leftbarcodefqgz} \
<#if seqType == "PE">${rightbarcodefqgz} \
</#if>-f ${samfile}
<@end/>