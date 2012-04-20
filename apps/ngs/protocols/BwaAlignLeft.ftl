#
# =====================================================
# $Id: BwaAlignLeft.ftl 11668 2012-04-18 13:08:24Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/BwaAlignLeft.ftl $
# $LastChangedDate: 2012-04-18 15:08:24 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11668 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=15:00:00 nodes=1 cores=4 mem=6

inputs "${indexfile}" 
inputs "${leftbarcodefqgz}"
alloutputsexist "${leftbwaout}"

mkdir -p "${intermediatedir}"

${bwaalignjar} aln \
${indexfile} \
${leftbarcodefqgz} \
-t ${bwaaligncores} \
-f ${leftbwaout}