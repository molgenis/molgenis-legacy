#
# =====================================================
# $Id: VariantAnnotator.ftl 10298 2011-12-27 16:02:05Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/VariantAnnotator.ftl $
# $LastChangedDate: 2011-12-27 17:02:05 +0100 (Tue, 27 Dec 2011) $
# $LastChangedRevision: 10298 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=45:00:00 mem=10
inputs "${snpsvcf}"
inputs "${sortedrecalbam}" 
inputs "${indexfile}"
inputs "${targetintervals}"
alloutputsexist "${snpsvariantannotatedvcf}"

java -Xmx10g -jar ${genomeAnalysisTKjar} \
-T VariantAnnotator \
-l INFO \
-R ${indexfile} \
-I ${sortedrecalbam} \
-B:variant,vcf ${snpsvcf} \
--useAllAnnotations \
-o ${snpsvariantannotatedvcf}

#-L ${targetintervals} \
<@end />