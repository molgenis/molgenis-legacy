#
# =====================================================
# $Id: BwaSampe.ftl 11668 2012-04-18 13:08:24Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/BwaSampe.ftl $
# $LastChangedDate: 2012-04-18 15:08:24 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11668 $
# $LastChangedBy: pneerincx $
# =====================================================
#

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