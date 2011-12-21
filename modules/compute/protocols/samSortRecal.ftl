
#
# =====================================================
# $Id:$
# $URL:$
# $LastChangedDate:$
# $LastChangedRevision:$
# $LastChangedBy:$
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=35:59:00 mem=4
#INPUTS 
#OUTPUTS
#EXEC
#FOREACH

inputs "${recalbam}"
outputs "${sortedrecalbam}"
outputs "${sortedrecalbamindex}"

${sortsamjar} \
INPUT=${recalbam} \
OUTPUT=${sortedrecalbam} \
SORT_ORDER=coordinate \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}

${buildbamindexjar} \
INPUT=${sortedrecalbam} \
OUTPUT=${sortedrecalbamindex} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}
<@end />