#
# =====================================================
# $Id$
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/Coverage.ftl $
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=23:59:00 mem=6 cores=2
#FOREACH externalSampleID

java -jar -Xmx6g ${mergesamfilesjar} \
<#list sortedrecalbam as srb>INPUT=${srb} </#list> \
ASSUME_SORTED=true USE_THREADING=true \
TMP_DIR=${tempdir} MAX_RECORDS_IN_RAM=6000000 \
OUTPUT=${mergedbam} \
SORT_ORDER=coordinate \
VALIDATION_STRINGENCY=SILENT

java -jar -Xmx3g ${buildbamindexjar} \
INPUT=${mergedbam} \
OUTPUT=${mergedbamindex} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}
<@end/>