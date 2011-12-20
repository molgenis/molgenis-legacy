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
#MOLGENIS walltime=35:59:00 mem=6

inputs "${realignedbam}"
outputs "${matefixedbam}"
outputs "${matefixedbamindex}"

java -jar -Xmx6g \
${fixmateinformationjar} \
INPUT=${realignedbam} \
OUTPUT=${matefixedbam} \
SORT_ORDER=coordinate \
VALIDATION_STRINGENCY=SILENT \
TMP_DIR=${tempdir}

${buildbamindexjar} \
INPUT=${matefixedbam} \
OUTPUT=${matefixedbamindex} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}
<@end />