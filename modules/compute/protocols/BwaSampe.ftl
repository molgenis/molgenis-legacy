#
# =====================================================
# $Id: bwaSampe.ftl 10235 2011-12-20 16:59:51Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/bwaSampe.ftl $
# $LastChangedDate: 2011-12-20 17:59:51 +0100 (Tue, 20 Dec 2011) $
# $LastChangedRevision: 10235 $
# $LastChangedBy: mdijkstra $
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
alloutputsexist "${samfile}"

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