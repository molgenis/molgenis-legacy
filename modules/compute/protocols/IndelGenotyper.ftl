#
# =====================================================
# $Id: bwaAlignLeft.ftl 10235 2011-12-20 16:59:51Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/bwaAlignLeft.ftl $
# $LastChangedDate: 2011-12-20 17:59:51 +0100 (Tue, 20 Dec 2011) $
# $LastChangedRevision: 10235 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=33:00:00 mem=8

inputs "${sortedrecalbam}"
inputs "${indexfile}"
outputs "${indelsvcf}"
outputs "${indelsbed}"
outputs "${indelsverboseoutput}"

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