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
"${fileWithIndexID}.coverageplot.pdf" \
"${fileWithIndexID}.coverage.Rdata"

${coveragescript} \
--bam ${sortedrecalbam} \
--chromosome 1 \
--interval_list ${targetintervals} \
--csv ${fileWithIndexID}.coverage.csv \
--pdf ${fileWithIndexID}.coverageplot.pdf \
--Rcovlist ${fileWithIndexID}.coverage.Rdata
<@end/>