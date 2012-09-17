#
# =====================================================
# $Id: Fastqc.ftl 11668 2012-04-18 13:08:24Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/Fastqc.ftl $
# $LastChangedDate: 2012-04-18 15:08:24 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11668 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=08:00:00 nodes=1 cores=1 mem=1

<#if seqType == "SR">
	inputs "${srbarcodefqgz}"
	alloutputsexist \
	 "${leftfastqczip}" \
	 "${leftfastqcsummarytxt}" \
	 "${leftfastqcsummarylog}" \
<#else>
	inputs "${leftbarcodefqgz}"
	inputs "${rightbarcodefqgz}"
	
	alloutputsexist \
	 "${leftfastqczip}" \
	 "${leftfastqcsummarytxt}" \
	 "${leftfastqcsummarylog}" \
	 "${rightfastqczip}" \
	 "${rightfastqcsummarytxt}" \
	 "${rightfastqcsummarylog}"
</#if>

# first make logdir...
mkdir -p "${intermediatedir}"

# pair1
${fastqcjar} ${leftbarcodefqgz} \
-Dfastqc.output_dir=${intermediatedir} \
-Dfastqc.unzip=false

<#if seqType == "PE">
# pair2
${fastqcjar} ${rightbarcodefqgz} \
-Dfastqc.output_dir=${intermediatedir} \
-Dfastqc.unzip=false
</#if>