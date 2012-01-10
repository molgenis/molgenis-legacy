#
# =====================================================
# $Id: MakeIndelMask.ftl 10298 2011-12-27 16:02:05Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/MakeIndelMask.ftl $
# $LastChangedDate: 2011-12-27 17:02:05 +0100 (Tue, 27 Dec 2011) $
# $LastChangedRevision: 10298 $
# $LastChangedBy: mdijkstra $
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