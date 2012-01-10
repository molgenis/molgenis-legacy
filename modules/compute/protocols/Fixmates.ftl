#
# =====================================================
# $Id: fixmates.ftl 10334 2012-01-06 15:11:25Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/fixmates.ftl $
# $LastChangedDate: 2012-01-06 16:11:25 +0100 (Fri, 06 Jan 2012) $
# $LastChangedRevision: 10334 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=35:59:00 mem=6

inputs "${realignedbam}"
alloutputsexist \
 "${matefixedbam}" \
 "${matefixedbamindex}"

java -jar -Xmx6g \
${fixmateinformationjar} \
INPUT=${realignedbam} \
OUTPUT=${matefixedbam} \
SORT_ORDER=coordinate \
VALIDATION_STRINGENCY=SILENT \
TMP_DIR=${tempdir}

java -jar -Xmx3g ${buildbamindexjar} \
INPUT=${matefixedbam} \
OUTPUT=${matefixedbamindex} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}
<@end />