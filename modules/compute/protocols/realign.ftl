#
# =====================================================
# $Id: markduplicates.ftl 10198 2011-12-20 08:34:26Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/org/molgenis/compute/protocols/markduplicates.ftl $
# $LastChangedDate: 2011-12-20 09:34:26 +0100 (Tue, 20 Dec 2011) $
# $LastChangedRevision: 10198 $
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
outputs "${realignedbam}"

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