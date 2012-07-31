

#MOLGENIS walltime=24:00:00 nodes=1 cores=1 mem=4

#INPUTS plinkdata
#OUTPUTS plinkdatatransposed
#EXES plink
#TARGETS project,chr

inputs "${plinkdata}.bed"
inputs "${plinkdata}.bim"
inputs "${plinkdata}.fam"
inputs "${plinkdata}.map"
inputs "${plinkdata}.ped"
alloutputsexist "${plinkdatatransposed}.tfam"
alloutputsexist "${plinkdatatransposed}.tped"

#Build in iteration over chromosome!

#Transpose plink data
${plink} \
--bfile ${plinkdata} \
--chr ${chr} \
--transpose \
--recode \
--out ${plinkdatatransposed} \
--noweb