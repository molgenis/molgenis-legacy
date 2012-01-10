#
# =====================================================
# $Id: FilterIndels.ftl 10298 2011-12-27 16:02:05Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/FilterIndels.ftl $
# $LastChangedDate: 2011-12-27 17:02:05 +0100 (Tue, 27 Dec 2011) $
# $LastChangedRevision: 10298 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=40:00:00
inputs "${indelsbed}"
alloutputsexist "${indelsfilteredbed}"

perl ${filterSingleSampleCallsperl} \
--calls ${indelsbed} \
--max_cons_av_mm 3.0 \
--max_cons_nqs_av_mm 0.5 \
--mode ANNOTATE \
> ${indelsfilteredbed}
<@end />