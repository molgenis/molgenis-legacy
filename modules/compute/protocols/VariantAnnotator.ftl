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