#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
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
-Djava.io.tmpdir=${tempdir} \
-Dfastqc.output_dir=${intermediatedir} \
-Dfastqc.unzip=false

<#if seqType == "PE">
# pair2
${fastqcjar} ${rightbarcodefqgz} \
-Djava.io.tmpdir=${tempdir} \
-Dfastqc.output_dir=${intermediatedir} \
-Dfastqc.unzip=false
</#if>