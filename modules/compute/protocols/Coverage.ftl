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
<#assign fileprefix = "externalSampleID " + externalSampleID>
<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=65:59:00 mem=12 cores=1
#FOREACH externalSampleID

inputs "${mergedbam}"
alloutputsexist "${sample}.coverage.csv" \
"${coverageplotpdf}" \
"${sample}.coverage.Rdata"

${coveragescript} \
--bam ${mergedbam} \
--chromosome 1 \
--interval_list ${targetintervals} \
--csv ${sample}.coverage.csv \
--pdf ${coverageplotpdf} \
--Rcovlist ${sample}.coverage.Rdata
<@end/>