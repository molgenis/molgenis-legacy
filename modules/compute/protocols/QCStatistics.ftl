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
inputs ${ssvQuoted(concordancefile)}
alloutputsexist ${qcstatisticscsv}


${getStatisticsScript} \
<#-->runtimelog ${runtimelog} \<-->
--hsmetrics ${csvQuoted(hsmetrics)} \
--alignment ${csvQuoted(alignmentmetrics)} \
--insertmetrics ${csvQuoted(recalinsertsizemetrics)} \
--dedupmetrics ${csvQuoted(dedupmetrics)} \
--concordance ${csvQuoted(concordancefile)} \
--sample ${csvQuoted(externalSampleID)} \
--out ${qcstatisticscsv}

<@end/>