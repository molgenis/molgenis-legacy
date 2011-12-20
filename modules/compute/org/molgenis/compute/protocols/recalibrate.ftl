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
#MOLGENIS walltime=45:59:00 mem=4 cores=1

inputs "${indexfile}" 
inputs "${matefixedbam}"
inputs "${matefixedcovariatecsv}"
outputs "${recalbam}"

java -jar -Xmx4g \
${tooldir}/GATK-1.0.5069/Sting/dist/GenomeAnalysisTK.jar -l INFO \
-T TableRecalibration \
-U ALLOW_UNINDEXED_BAM \
-R ${indexfile} \
-I ${matefixedbam} \
--recal_file ${matefixedcovariatecsv} \
--out ${recalbam}
<@end />