#
# =====================================================
# $Id: IndelGenotyper.ftl 10298 2011-12-27 16:02:05Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/IndelGenotyper.ftl $
# $LastChangedDate: 2011-12-27 17:02:05 +0100 (Tue, 27 Dec 2011) $
# $LastChangedRevision: 10298 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=33:00:00 mem=8

inputs "${sortedrecalbam}"
inputs "${indexfile}"
alloutputsexist \
 "${indelsvcf}" \
 "${indelsbed}" \
 "${indelsverboseoutput}"

java -Xmx8g -jar ${genomeAnalysisTKjar} \
-l INFO \
-T IndelGenotyperV2 \
-I ${sortedrecalbam} \
-o ${indelsvcf} \
--bedOutput ${indelsbed} \
-R ${indexfile} \
-verbose ${indelsverboseoutput} \
--window_size 300
<@end />