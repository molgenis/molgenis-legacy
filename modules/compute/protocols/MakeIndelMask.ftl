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
#MOLGENIS walltime=40:00:00
inputs "${indelsfilteredbed}"
alloutputsexist "${indelsmaskbed}"

python ${makeIndelMaskpyton} \
${indelsfilteredbed} \
10 \
${indelsmaskbed}
<@end />