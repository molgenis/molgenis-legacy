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
