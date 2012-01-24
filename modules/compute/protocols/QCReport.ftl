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

<#include "helpers.ftl"/>
inputs ${ssvQuoted(hsmetrics)}
inputs ${ssvQuoted(alignmentmetrics)}
inputs ${ssvQuoted(recalinsertsizemetrics)}
inputs ${ssvQuoted(dedupmetrics)}
inputs ${ssvQuoted(concordancefile)}
inputs ${qcstatisticscolnames}

mkdir -p ${qcdir}

${getStatisticsScript} \
--hsmetrics ${csvQuoted(hsmetrics)} \                                                                                                                    
--alignment ${csvQuoted(alignmentmetrics)} \                                                                                                      
--insertmetrics ${csvQuoted(recalinsertsizemetrics)} \                                                                                                     
--dedupmetrics ${csvQuoted(dedupmetrics)} \                                                                                                               
--concordance ${csvQuoted(concordancefile)} \                                                                                                              
--sample ${csvQuoted(externalSampleID)} \                                                                                                                  
--csvout ${qcstatisticscsv} \                                                                                                                              
--tableout ${qcstatisticstex} \                                                                                                                             
--descriptionout ${qcstatisticsdescription}

echo "<#include "QCReportTemplate.ftl"/>" > ${qcstatisticstexreport}

pdflatex -output-directory=${qcdir} ${qcstatisticstexreport}

<@end/>