#
# =====================================================
# $Id: RealignTargetCreator.ftl 11668 2012-04-18 13:08:24Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/RealignTargetCreator.ftl $
# $LastChangedDate: 2012-04-18 15:08:24 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11668 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=35:59:00 mem=10

inputs "${dedupbam}" 
inputs "${indexfile}" 
inputs "${dbsnprod}"
inputs "${pilot1KgVcf}"
alloutputsexist \
 "${realignTargets}"

java -Xmx10g -jar -Djava.io.tmpdir=${tempdir} \
${genomeAnalysisTKjar} \
-l INFO \
-T RealignerTargetCreator \
-U ALLOW_UNINDEXED_BAM \
-I ${dedupbam} \
-R ${indexfile} \
-D ${dbsnprod} \
-B:indels,VCF ${pilot1KgVcf} \
-o ${realignTargets}