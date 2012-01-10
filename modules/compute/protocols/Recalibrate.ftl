#
# =====================================================
# $Id: recalibrate.ftl 10273 2011-12-22 16:30:53Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/recalibrate.ftl $
# $LastChangedDate: 2011-12-22 17:30:53 +0100 (Thu, 22 Dec 2011) $
# $LastChangedRevision: 10273 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=45:59:00 mem=4 cores=1

inputs "${indexfile}" 
inputs "${matefixedbam}"
inputs "${matefixedcovariatecsv}"
alloutputsexist "${recalbam}"

java -jar -Xmx4g \
${genomeAnalysisTKjar} \
-l INFO \
-T TableRecalibration \
-U ALLOW_UNINDEXED_BAM \
-R ${indexfile} \
-I ${matefixedbam} \
--recal_file ${matefixedcovariatecsv} \
--out ${recalbam}
<@end />