#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=23:59:00

#INPUTS	indexfile,leftbwaout,rightbwaout,leftbarcodefqgz,rightbarcodefqgz
#OUTPUTS samfile
#LOGS log
#EXES bwasampejar
#TARGETS

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