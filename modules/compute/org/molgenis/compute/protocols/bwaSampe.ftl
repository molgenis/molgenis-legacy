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

inputs "${indexfile}"
inputs "${leftbwaout}"
inputs "${rightbwaout}"
inputs "${leftfilegz}"
inputs "${rightfilegz}"
outputs "${samfile}"

${bwasampejar} sampe -P \
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