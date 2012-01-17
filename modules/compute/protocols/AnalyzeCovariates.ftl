#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=00:45:00

<#include "macros.ftl"/>
<@begin/>
inputs "${matefixedcovariatecsv}"
inputs "${sortedrecalcovariatecsv}"
alloutputsexist \
"${cyclecovariatebefore}" \
"${cyclecovariateafter}"

java -jar -Xmx4g ${analyzecovariatesjar} -l INFO \
-resources ${indexfile} \
--recal_file ${matefixedcovariatecsv} \
-outputDir ${recalstatsbeforedir} \
-Rscript ${rscript} \
-ignoreQ 5

java -jar -Xmx4g ${analyzecovariatesjar} -l INFO \
-resources ${indexfile} \
--recal_file ${sortedrecalcovariatecsv} \
-outputDir ${recalstatsafterdir} \
-Rscript ${rscript} \
-ignoreQ 5
<@end />