#
# =====================================================
# $Id: bwaAlignLeft.ftl 10235 2011-12-20 16:59:51Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/bwaAlignLeft.ftl $
# $LastChangedDate: 2011-12-20 17:59:51 +0100 (Tue, 20 Dec 2011) $
# $LastChangedRevision: 10235 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=40:00:00
inputs "${indelsbed}"
outputs "${indelsfilteredbed}"

perl ${filterSingleSampleCallsperl} \
--calls ${indelsbed} \
--max_cons_av_mm 3.0 \
--max_cons_nqs_av_mm 0.5 \
--mode ANNOTATE \
> ${indelsfilteredbed}
<@end />