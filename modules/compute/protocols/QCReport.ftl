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
#MOLGENIS walltime=00:01:00
#FOREACH project
#DOCUMENTATION Documentation of QCReport.ftl, ${getStatisticsScript}

<#include "Helpers.ftl"/>

<#assign folded = foldParameters(parameters,"project,externalSampleID") />
<#assign samplehsmetrics 			= stringList(folded, "samplehsmetrics") />
<#assign samplealignmentmetrics 	= stringList(folded, "samplealignmentmetrics") />
<#assign sampleinsertsizemetrics 	= stringList(folded, "sampleinsertsizemetrics") />
<#assign sampleconcordancefile 		= stringList(folded, "sampleconcordancefile") />
<#assign externalSampleIDfolded		= stringList(folded, "externalSampleID") />

# parameters in *.tex template:
<#assign samplecoverageplotpdf 			= stringList(folded, "samplecoverageplotpdf") />
<#assign sampleinsertsizemetricspdf		= stringList(folded, "sampleinsertsizemetricspdf") />
<#assign samplegcbiasmetricspdf			= stringList(folded, "samplegcbiasmetricspdf") />

inputs ${ssvQuoted(samplehsmetrics)}
inputs ${ssvQuoted(samplealignmentmetrics)}
inputs ${ssvQuoted(sampleinsertsizemetrics)}
inputs ${ssvQuoted(dedupmetrics)}
inputs ${ssvQuoted(sampleconcordancefile)}
inputs ${qcstatisticscolnames}

mkdir -p ${qcdir}

# get general sample statistics
${getStatisticsScript} \
--hsmetrics ${csvQuoted(samplehsmetrics)} \
--alignment ${csvQuoted(samplealignmentmetrics)} \
--insertmetrics ${csvQuoted(sampleinsertsizemetrics)} \
--dedupmetrics ${csvQuoted(dedupmetrics)} \
--concordance ${csvQuoted(sampleconcordancefile)} \
--sample ${csvQuoted(externalSampleIDfolded)} \
--colnames ${qcstatisticscolnames} \
--csvout ${qcstatisticscsv} \
--tableout ${qcstatisticstex} \
--descriptionout ${qcstatisticsdescription} \
--baitsetout ${qcbaitset} \
--qcdedupmetricsout ${qcdedupmetricsout}

# create workflow figure
echo "${graph(workflowElements)}" | ${dot} -Tpng > ${workflowpng}

# get snp stats per sample
#<#-->${snpsfinalvcftabletype}-->

# save latex template in file
echo "<#include "QCReportTemplate.tex"/>" > ${qcstatisticstexreport}

pdflatex -output-directory=${qcdir} ${qcstatisticstexreport}
pdflatex -output-directory=${qcdir} ${qcstatisticstexreport} <#--do twice to fill all cross references-->

<#--
list foldParameters(parameters,"externalSampleID") as row>
${row.getString("samplehsmetrics")}
</#list>
<#list foldedStringList(parameters, "project,externalSampleID", "samplehsmetrics") as val> ${val}, </#list
-->

<@end/>