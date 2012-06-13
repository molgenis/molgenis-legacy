#
# =====================================================
# $Id: Fastqc.ftl 10962 2012-02-21 09:59:42Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/Fastqc.ftl $
# $LastChangedDate: 2012-02-21 10:59:42 +0100 (Tue, 21 Feb 2012) $
# $LastChangedRevision: 10962 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

#MOLGENIS walltime=08:00:00 nodes=1 cores=1 mem=1

<#if seqType == "SR">

#INPUTS srbarcodefqgz,
#OUTPUTS leftfastqczip,leftfastqcsummarytxt,leftfastqcsummarylog
#LOGS log
#EXES fastqcjar
#TARGETS

	inputs "${srbarcodefqgz}"
	alloutputsexist \
	 "${leftfastqczip}" \
	 "${leftfastqcsummarytxt}" \
	 "${leftfastqcsummarylog}" \
<#else>

#INPUTS leftbarcodefqgz,rightbarcodefqgz
#OUTPUTS leftfastqczip,leftfastqcsummarytxt,leftfastqcsummarylog,rightfastqczip,rightfastqcsummarytxt,rightfastqcsummarylog
#LOGS log
#EXES fastqcjar
#TARGETS

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