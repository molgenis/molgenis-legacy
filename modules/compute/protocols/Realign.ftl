#
# =====================================================
# $Id: realign.ftl 10273 2011-12-22 16:30:53Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/realign.ftl $
# $LastChangedDate: 2011-12-22 17:30:53 +0100 (Thu, 22 Dec 2011) $
# $LastChangedRevision: 10273 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=35:59:00 mem=10 cores=1


inputs "${dedupbam}" 
inputs "${indexfile}" 
inputs "${dbsnprod}"
inputs "${pilot1KgVcf}"
inputs "${realignTargets}"
alloutputsexist "${realignedbam}"

java -Djava.io.tmpdir=${tempdir} -Xmx10g -jar \
${genomeAnalysisTKjar} \
-l INFO \
-T IndelRealigner \
-U ALLOW_UNINDEXED_BAM \
-I ${dedupbam} \
--out ${realignedbam} \
-targetIntervals ${realignTargets} \
-R ${indexfile} \
-D ${dbsnprod} \
-B:indels,VCF ${pilot1KgVcf} \
-knownsOnly \
-LOD 0.4 \
-maxReads 2000000
<@end />