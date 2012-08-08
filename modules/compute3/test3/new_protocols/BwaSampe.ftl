#
# =====================================================
# $Id: BwaSampe.ftl 10962 2012-02-21 09:59:42Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/BwaSampe.ftl $
# $LastChangedDate: 2012-02-21 10:59:42 +0100 (Tue, 21 Feb 2012) $
# $LastChangedRevision: 10962 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

#MOLGENIS walltime=23:59:00

#INPUTS indexfile.*,leftbwaout,rightbwaout,leftbarcodefqgz,rightbarcodefqgz
#OUTPUTS samfile
#LOGS log
#EXES bwasampejar
#TARGETS

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
