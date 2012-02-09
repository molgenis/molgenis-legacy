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
#MOLGENIS walltime=46:00:00 mem=8 cores=5
#FOREACH externalSampleID

inputs "${mergedbam}" 
inputs "${indexfile}"
inputs "${dbsnprod}"
alloutputsexist \
 "${snpsvcf}" \
 "${snpsvcf}.metrics"

java -Xmx8g -Djava.io.tmpdir=${tempdir} -XX:+UseParallelGC -XX:ParallelGCThreads=1 -jar \
${genomeAnalysisTKjar} \
-l INFO \
-T UnifiedGenotyper \
-I ${mergedbam} \
--out ${sample}.snps.vcf \
-R ${indexfile} \
-D ${dbsnprod} \
-stand_call_conf 30.0 \
-stand_emit_conf 10.0 \
-nt 4 \
--metrics_file ${sample}.snps.vcf.metrics
<@end />
