#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=65:59:00 mem=12 cores=1

inputs "${sortedrecalbam}"
alloutputsexist "${fileWithIndexID}.coverage.csv" \
"${coverageplotpdf}" \
"${fileWithIndexID}.coverage.Rdata"

${coveragescript} \
--bam ${sortedrecalbam} \
--chromosome 1 \
--interval_list ${targetintervals} \
--csv ${fileWithIndexID}.coverage.csv \
--pdf ${coverageplotpdf} \
--Rcovlist ${fileWithIndexID}.coverage.Rdata
<@end/>