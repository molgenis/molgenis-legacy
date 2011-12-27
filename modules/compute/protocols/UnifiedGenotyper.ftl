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
#MOLGENIS walltime=46:00:00 mem=8 cores=5
inputs "${sortedrecalbam}" 
inputs "${indexfile}"
inputs "${dbsnprod}"
outputs "${snpsvcf}"
outputs "${snpsvcf}.metrics"

java -Xmx8g -Djava.io.tmpdir=${tempdir} -XX:+UseParallelGC -XX:ParallelGCThreads=1 -jar \
${genomeAnalysisTKjar} \
-l INFO \
-T UnifiedGenotyper \
-I ${sortedrecalbam} \
--out ${fileWithIndexID}.snps.vcf \
-R ${indexfile} \
-D ${dbsnprod} \
-stand_call_conf 30.0 \
-stand_emit_conf 10.0 \
-nt 4 \
--metrics_file ${fileWithIndexID}.snps.vcf.metrics
<@end />
