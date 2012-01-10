#
# =====================================================
# $Id: coverage.ftl 10298 2011-12-27 16:02:05Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/coverage.ftl $
# $LastChangedDate: 2011-12-27 17:02:05 +0100 (Tue, 27 Dec 2011) $
# $LastChangedRevision: 10298 $
# $LastChangedBy: mdijkstra $
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