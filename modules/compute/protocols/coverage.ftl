#
# =====================================================
# $Id: picardQC.ftl 10235 2011-12-20 16:59:51Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/picardQC.ftl $
# $LastChangedDate: 2011-12-20 17:59:51 +0100 (Tue, 20 Dec 2011) $
# $LastChangedRevision: 10235 $
# $LastChangedBy: mdijkstra $
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