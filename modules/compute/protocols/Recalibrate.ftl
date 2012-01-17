#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
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