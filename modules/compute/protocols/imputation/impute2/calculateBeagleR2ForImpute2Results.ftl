#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#INPUTS imputationResultDir/chr_${chr}
#OUTPUTS imputationResultDir/chr_${chr}.beagleR2
#EXES calculateBeagleR2ForIMpute2ResultsPythonScript
#LOGS log

#FOREACH project,chr

inputs "${imputationResultDir}/chr_${chr}"
alloutputsexist "${imputationResultDir}/chr_${chr}.beagleR2"


python ${calculateBeagleR2ForIMpute2ResultsPythonScript} ${imputationResultDir}/chr_${chr} ${imputationResultDir}/chr_${chr}.beagleR2