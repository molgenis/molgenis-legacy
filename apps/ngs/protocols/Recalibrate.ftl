#
# =====================================================
# $Id: Recalibrate.ftl 11668 2012-04-18 13:08:24Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/Recalibrate.ftl $
# $LastChangedDate: 2012-04-18 15:08:24 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11668 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=45:59:00 mem=4 cores=1

inputs "${indexfile}" 
inputs "${matefixedbam}"
inputs "${matefixedcovariatecsv}"
alloutputsexist "${recalbam}"

java -jar -Xmx4g \
${genomeAnalysisTKjar} \
-l INFO \
-T TableRecalibration \
-U ALLOW_UNINDEXED_BAM \
-R ${indexfile} \
-I ${matefixedbam} \
--recal_file ${matefixedcovariatecsv} \
--out ${recalbam}