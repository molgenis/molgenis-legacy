#
# =====================================================
# $Id: samSort.ftl 10273 2011-12-22 16:30:53Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/samSort.ftl $
# $LastChangedDate: 2011-12-22 17:30:53 +0100 (Thu, 22 Dec 2011) $
# $LastChangedRevision: 10273 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=35:59:00 mem=4
#INPUTS 
#OUTPUTS
#EXEC
#FOREACH

inputs "${bamfile}"
alloutputsexist \
 "${sortedbam}" \
 "${sortedbamindex}"

java -jar -Xmx3g ${sortsamjar} \
INPUT=${bamfile} \
OUTPUT=${sortedbam} \
SORT_ORDER=coordinate \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}

java -jar -Xmx3g ${buildbamindexjar} \
INPUT=${sortedbam} \
OUTPUT=${sortedbamindex} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}
<@end />

