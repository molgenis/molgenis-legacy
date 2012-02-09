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
#DOCUMENTATION Documentation of QCReport.ftl, ${getStatisticsScript}

<#include "Helpers.ftl"/>
inputs ${ssvQuoted(hsmetrics)}
inputs ${ssvQuoted(alignmentmetrics)}
inputs ${ssvQuoted(sampleinsertsizemetrics)}
inputs ${ssvQuoted(dedupmetrics)}
inputs ${ssvQuoted(concordancefile)}
inputs ${qcstatisticscolnames}

mkdir -p ${qcdir}

${getStatisticsScript} \
--hsmetrics ${csvQuoted(hsmetrics)} \
--alignment ${csvQuoted(alignmentmetrics)} \
--insertmetrics ${csvQuoted(sampleinsertsizemetrics)} \
--dedupmetrics ${csvQuoted(dedupmetrics)} \
--concordance ${csvQuoted(concordancefile)} \
--sample ${csvQuoted(externalSampleID)} \
--colnames ${qcstatisticscolnames} \
--csvout ${qcstatisticscsv} \
--tableout ${qcstatisticstex} \
--descriptionout ${qcstatisticsdescription} \
--baitsetout ${qcbaitset}

# create workflow figure
echo "${graph(workflowElements)}" | ${dot} -Tpng > ${workflowpng}

# save latex template in file
echo "<#include "QCReportTemplate.tex"/>" > ${qcstatisticstexreport}

pdflatex -output-directory=${qcdir} ${qcstatisticstexreport}
pdflatex -output-directory=${qcdir} ${qcstatisticstexreport} <#--do twice to fill all cross references-->

<@end/>