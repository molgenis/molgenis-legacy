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
inputs ${qcstatisticscolnames}

#always do step, never skip
#alloutputsexist ${qcstatisticscsv} ${qcstatisticstex} ${qcstatisticsdescription}

mkdir -p ${qcdir}

${getStatisticsScript} \
--runtimelog ${runtimelog} \                                                                                                                               
--hsmetrics ${csvQuoted(hsmetrics)} \                                                                                                                    
--alignment ${csvQuoted(alignmentmetrics)} \                                                                                                      
--insertmetrics ${csvQuoted(recalinsertsizemetrics)} \                                                                                                     
--dedupmetrics ${csvQuoted(dedupmetrics)} \                                                                                                               
--concordance ${csvQuoted(concordancefile)} \                                                                                                              
--sample ${csvQuoted(externalSampleID)} \                                                                                                                  
--csvout ${qcstatisticscsv} \                                                                                                                              
--tableout ${qcstatisticstex} \                                                                                                                             
--descriptionout ${qcstatisticsdescription}

<#-- check out pipeline to build latex file -->
<#-->svn co http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/<#-->
<#-- ALSO: svn co QCworkflow -->

<#-- generate QC scripts -->
<#-->
${scriptgenerator} \
--worksheet ${qcstatisticscsv} \
--protocolsdir ${intermediate}/protocols \
--workflow  ${intermediate}/workflow_QCStatistics.csv \
--outputdir ${qcdir}
<#-->

<#-- submit QC scripts -->
<#-->sh ${qcdir}/submit.sh <#-->

<@end/>