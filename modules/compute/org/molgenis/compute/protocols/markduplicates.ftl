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
#MOLGENIS walltime=35:59:00 mem=4
#INPUTS 
#OUTPUTS
#EXEC
#FOREACH

inputs "${sortedbam}"
inputs "${sortedbamindex}"
outputs "${dedupbam}"
outputs "${dedupmetrics}"

${markduplicatesjar} \
INPUT=${sortedbam} \
OUTPUT=${dedupbam} \
METRICS_FILE=${dedupmetrics} \
REMOVE_DUPLICATES=false \
ASSUME_SORTED=true \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir}

java -jar -Xmx3g ${tooldir}/picard-tools-1.32/BuildBamIndex.jar \
INPUT=${dedupbam} \
OUTPUT=${dedupbamindex} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}
<@end />