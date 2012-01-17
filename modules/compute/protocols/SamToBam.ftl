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
#MOLGENIS walltime=35:59:00 mem=3
#INPUTS 
#OUTPUTS
#EXEC
#FOREACH

inputs "${samfile}"
alloutputsexist "${bamfile}"

java -jar -Xmx3g ${samtobamjar} \
INPUT=${samfile} \
OUTPUT=${bamfile} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=2000000 \
TMP_DIR=${tempdir}
<@end />