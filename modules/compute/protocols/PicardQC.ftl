#
# =====================================================
# $Id: picardQC.ftl 10334 2012-01-06 15:11:25Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/picardQC.ftl $
# $LastChangedDate: 2012-01-06 16:11:25 +0100 (Fri, 06 Jan 2012) $
# $LastChangedRevision: 10334 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=35:59:00 mem=4

inputs "${sortedbam}"
inputs "${indexfile}"
alloutputsexist \
 "${alignmentmetrics}" \
 "${gcbiasmetrics}" \
 "${gcbiasmetricspdf}" \
 "${insertsizemetrics}" \
 "${insertsizemetricspdf}" \
 "${meanqualitybycycle}" \
 "${meanqualitybycyclepdf}" \
 "${qualityscoredistribution}" \
 "${qualityscoredistributionpdf}" \
 "${hsmetrics}" \
 "${bamindexstats}"

java -jar -Xmx4g ${alignmentmetricsjar} \
I=${sortedbam} \
O=${alignmentmetrics} \
R=${indexfile} \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir}

java -jar ${gcbiasmetricsjar} \
R=${indexfile} \
I=${sortedbam} \
O=${gcbiasmetrics} \
CHART=${gcbiasmetricspdf} \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir}

java -jar ${insertsizemetricsjar} \
I=${sortedbam} \
O=${insertsizemetrics} \
H=${insertsizemetricspdf} \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir}

java -jar ${meanqualitybycyclejar} \
I=${sortedbam} \
O=${meanqualitybycycle} \
CHART=${meanqualitybycyclepdf} \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir}

java -jar ${qualityscoredistributionjar} \
I=${sortedbam} \
O=${qualityscoredistribution} \
CHART=${qualityscoredistributionpdf} \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir}

java -jar -Xmx4g ${hsmetricsjar} \
INPUT=${sortedbam} \
OUTPUT=${hsmetrics} \
BAIT_INTERVALS=${baitintervals} \
TARGET_INTERVALS=${targetintervals} \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir}

java -jar ${bamindexstatsjar} \
INPUT=${sortedbam} \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir} \
> ${bamindexstats}
<@end />