

#MOLGENIS walltime=24:00:00 nodes=1 cores=1 mem=4

#INPUTS plinkdatatransposed
#OUTPUTS beaglefile
#EXES
#TARGETS project,chr

inputs "${plinkdatatransposed}.tped"
alloutputsexist "${beaglefile}"

#Create beagle format file to be used for reference (GoNL) beagle phased data
gawk '{$2=$1":"$4; $1="M"; $3=""; $4=""; print $0}' \
${plinkdatatransposed}.tped \
> ${beaglefile}