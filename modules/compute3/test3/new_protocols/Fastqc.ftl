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

#INPUTS leftbarcodefqgz,rightbarcodefqgz
#OUTPUTS leftfastqczip,leftfastqcsummarytxt,leftfastqcsummarylog,rightfastqczip,rightfastqcsummarytxt,rightfastqcsummarylog
#LOGS log
#EXES fastqcjar
#TARGETS

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