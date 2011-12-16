#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=15:00:00 nodes=1 cores=4 mem=6 clusterQueue=cluster
#INPUTS 
#OUTPUTS
#EXEC
#FOREACH

inputs "${indexfile}" 
inputs "${rightfilegz}"
outputs "${bwaoutput}"

mkdir -p "${intermediatedir}"

${bwaalignjar} \
${indexfile} \
${rightfilegz} \
-t ${bwaaligncores} \
-f ${bwaoutput}
