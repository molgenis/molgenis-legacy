#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=16

#INPUTS ${ssvQuoted(logisticRegressionLikelihoodRatioTestFile)}
#OUTPUTS imputationResult/chr_${chr}
#EXES
#LOGS log
#TARGETS plinkdata,chr

inputs "${ssvQuoted(logisticRegressionLikelihoodRatioTestFile)}"
alloutputsexist ${imputationResult}/chr_${chr}


#Concate the bins with compute for each

cat ${ssvQuoted(logisticRegressionLikelihoodRatioTestFile)} > ${imputationResult}/chr_${chr}



