#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=16

#INPUTS ${csvQuoted(impute2ResultChrBinGenFile)},${csvQuoted(impute2ResultChrBinInfoFile)}
#OUTPUTS imputationResult/chr_${chr}
#EXES
#LOGS log
#TARGETS plinkdata,chr

#FOREACH plinkdata,chr

inputs ${csvQuoted(impute2ResultChrBinGenFile)},${csvQuoted(impute2ResultChrBinInfoFile)}
alloutputsexist ${imputationResult}/chr_${chr}


#Concate the bins with compute for each

cat ${ssvQuoted(impute2ResultChrBinInfoFile)} > ${imputationResult}/chr_${chr}.gen

cat ${ssvQuoted(impute2ResultChrBinGenFile)} > ${imputationResult}/chr_${chr}.info


