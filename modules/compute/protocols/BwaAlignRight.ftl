#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

<#if seqType == "PE">
#MOLGENIS walltime=15:00:00 nodes=1 cores=4 mem=6

#INPUTS indexfile,rightbarcodefqgz
#OUTPUTS rightbwaout
#LOGS log
#EXES bwaalignjar
#TARGETS

inputs "${indexfile}" 
inputs "${rightbarcodefqgz}"
alloutputsexist "${rightbwaout}"

mkdir -p "${intermediatedir}"

${bwaalignjar} aln \
${indexfile} \
${rightbarcodefqgz} \
-t ${bwaaligncores} \
-f ${rightbwaout}
</#if>