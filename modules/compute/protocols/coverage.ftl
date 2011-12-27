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
outputs "${fileWithIndexID}.coverage.csv"
outputs "${fileWithIndexID}.coverageplot.pdf"
outputs "${fileWithIndexID}.coverage.Rdata"

${coveragescript} \
--bam ${sortedrecalbam} \
--interval_list ${targetintervals} \
--csv ${fileWithIndexID}.coverage.csv \
--pdf ${fileWithIndexID}.coverageplot.pdf \
--Rcovlist ${fileWithIndexID}.coverage.Rdata
<@end/>