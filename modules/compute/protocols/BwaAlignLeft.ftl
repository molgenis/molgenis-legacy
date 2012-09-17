#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=15:00:00 nodes=1 cores=4 mem=6

#INPUTS indexfile,leftbarcodefqgz
#OUTPUTS leftbwaout
#LOGS log
#EXES bwaalignjar
#TARGETS

inputs "${indexfile}" 
inputs "${leftbarcodefqgz}"
alloutputsexist "${leftbwaout}"

mkdir -p "${intermediatedir}"

${bwaalignjar} aln \
${indexfile} \
${leftbarcodefqgz} \
-t ${bwaaligncores} \
-f ${leftbwaout}