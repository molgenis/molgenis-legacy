#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=08:00:00 nodes=1 cores=1 mem=1 clusterQueue=cluster
#INPUTS 
#OUTPUTS
#EXEC
#FOREACH

inputs "${leftfilegz}"
inputs "${rightfilegz}"

outputs "${leftfastqczip}"
outputs "${leftfastqcsummarytxt}"
outputs "${leftfastqcsummarylog}"
outputs "${rightfastqczip}"
outputs "${rightfastqcsummarytxt}"
outputs "${rightfastqcsummarylog}"

# first make logdir...
mkdir -p "${intermediatedir}"

# pair1
${fastqcjar} ${leftfilegz} \
-Dfastqc.output_dir=${intermediatedir} \
-Dfastqc.unzip=false

# pair2
${fastqcjar} ${rightfilegz} \
-Dfastqc.output_dir=${intermediatedir} \
-Dfastqc.unzip=false