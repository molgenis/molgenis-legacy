#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

<#assign runtimelog = runtimelog[0] />
<#assign fileprefix = "externalSampleID " + externalSampleID>
<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=40:00:00
#FOREACH externalSampleID

inputs "${indelsfilteredbed}"
alloutputsexist "${indelsmaskbed}"

python ${makeIndelMaskpyton} \
${indelsfilteredbed} \
10 \
${indelsmaskbed}
<@end />