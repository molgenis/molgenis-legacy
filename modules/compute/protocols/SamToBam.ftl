#
# =====================================================
# $Id: samToBam.ftl 10337 2012-01-06 16:55:50Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/samToBam.ftl $
# $LastChangedDate: 2012-01-06 17:55:50 +0100 (Fri, 06 Jan 2012) $
# $LastChangedRevision: 10337 $
# $LastChangedBy: mdijkstra $
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