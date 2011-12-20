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
#MOLGENIS walltime=15:00:00 nodes=1 cores=4 mem=6
#INPUTS 
#OUTPUTS
#EXEC
#FOREACH

inputs "${indexfile}" 
inputs "${rightfilegz}"
outputs "${rightbwaout}"

mkdir -p "${intermediatedir}"

${bwaalignjar} \
${indexfile} \
${rightfilegz} \
-t ${bwaaligncores} \
-f ${rightbwaout}
<@end/>