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