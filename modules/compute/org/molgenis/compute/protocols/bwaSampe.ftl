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
#MOLGENIS walltime=23:59:00
#INPUTS 
#OUTPUTS
#EXEC
#FOREACH

inputs "${indexfile}"
inputs "${leftbwaout}"
inputs "${rightbwaout}"
inputs "${leftfilegz}"
inputs "${rightfilegz}"
outputs "${samfile}"

${bwasampejar} \
-p illumina \
-i ${lane} \
-m ${externalSampleID} \
-l ${library} \
${indexfile} \
${leftbwaout} \
${rightbwaout} \
${leftfilegz} \
${rightfilegz} \
-f ${samfile}
<@end/>