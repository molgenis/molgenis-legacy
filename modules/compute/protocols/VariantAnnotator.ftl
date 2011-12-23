#
# =====================================================
# $Id: bwaAlignLeft.ftl 10235 2011-12-20 16:59:51Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/bwaAlignLeft.ftl $
# $LastChangedDate: 2011-12-20 17:59:51 +0100 (Tue, 20 Dec 2011) $
# $LastChangedRevision: 10235 $
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
outputs "${snpsvariantannotatedvcf}"

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