#
# =====================================================
# $Id: realignTargetCreator.ftl 10324 2012-01-03 16:02:16Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/realignTargetCreator.ftl $
# $LastChangedDate: 2012-01-03 17:02:16 +0100 (Tue, 03 Jan 2012) $
# $LastChangedRevision: 10324 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=35:59:00 mem=10

inputs "${dedupbam}" 
inputs "${indexfile}" 
inputs "${dbsnprod}"
inputs "${pilot1KgVcf}"
alloutputsexist "${realignTargets}"

java -Xmx10g -jar -Djava.io.tmpdir=${tempdir} \
${genomeAnalysisTKjar} \
-l INFO \
-T RealignerTargetCreator \
-U ALLOW_UNINDEXED_BAM \
-I ${dedupbam} \
-R ${indexfile} \
-D ${dbsnprod} \
-B:indels,VCF ${pilot1KgVcf} \
-o ${realignTargets}
<@end />