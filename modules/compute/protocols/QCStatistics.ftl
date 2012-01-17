#
# =====================================================
# $Id: analyzeCovariates.ftl 10298 2011-12-27 16:02:05Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/analyzeCovariates.ftl $
# $LastChangedDate: 2011-12-27 17:02:05 +0100 (Tue, 27 Dec 2011) $
# $LastChangedRevision: 10298 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#assign runtimelog = runtimelog[0] />
<#assign fileprefix = "project " + project>
<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=00:45:00
#FOREACH project
<#-->We cannot put ${runtimelog} in the #FOREACH, because this var holds 'paths' that are then put in script name...<-->
<#-->Moreover, runtimelog is Lane specific<-->

<#include "helpers.ftl"/>
inputs ${runtimelog}
inputs ${ssvQuoted(hsmetrics)}
inputs ${ssvQuoted(alignmentmetrics)}
inputs ${ssvQuoted(recalinsertsizemetrics)}
inputs ${ssvQuoted(dedupmetrics)}
alloutputsexist ${qcstatisticscsv}


${getStatisticsScript} \
--runtimelog ${runtimelog} \
--hsmetrics ${csvQuoted(hsmetrics)} \
--alignment ${csvQuoted(alignmentmetrics)} \
--insertmetrics ${csvQuoted(recalinsertsizemetrics)} \
--dedupmetrics ${csvQuoted(dedupmetrics)} \
--sample ${csvQuoted(externalSampleID)} \
--out ${qcstatisticscsv}

<@end/>