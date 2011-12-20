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
#MOLGENIS walltime=35:59:00 mem=4 cores=1

inputs "${matefixedbam}"
inputs "${indexfile}" 
inputs "${dbsnprod}"
outputs "${matefixedcovariatecsv}"

java -jar -Xmx4g \
${genomeAnalysisTKjar} -l INFO \
-T CountCovariates \
-U ALLOW_UNINDEXED_BAM \
-R ${indexfile} \
--DBSNP ${dbsnprod} \
-I ${matefixedbam} \
-cov ReadGroupcovariate \
-cov QualityScoreCovariate \
-cov CycleCovariate \
-cov DinucCovariate \
-recalFile ${matefixedcovariatecsv}
<@end />