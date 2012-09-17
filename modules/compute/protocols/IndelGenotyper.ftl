#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=33:00:00 mem=8
#FOREACH externalSampleID

inputs "${mergedbam}"
inputs "${indexfile}"
alloutputsexist \
 "${indelsvcf}" \
 "${indelsbed}" \
 "${indelsverboseoutput}"

java -Xmx8g -jar ${genomeAnalysisTKjar} \
-l INFO \
-T IndelGenotyperV2 \
-I ${mergedbam} \
-o ${indelsvcf} \
--bedOutput ${indelsbed} \
-R ${indexfile} \
-verbose ${indelsverboseoutput} \
--window_size 300