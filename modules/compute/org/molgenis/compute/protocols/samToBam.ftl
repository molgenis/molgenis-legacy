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

inputs "${samfile}"
outputs "${bamfile}"

${samtobamjar} \
INPUT=${samfile} \
OUTPUT=${bamfile} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=2000000 \
TMP_DIR=${tempdir}
<@end />