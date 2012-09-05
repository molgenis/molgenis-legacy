#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=16

#INPUTS ${csvQuoted(impute2ResultChrBinGenFile)},${csvQuoted(impute2ResultChrBinInfoFile)}
#OUTPUTS imputationResult/chr_${chr}
#EXES
#LOGS log
#TARGETS project,chr,fromChrPos,toChrPos

#FOREACH project,chr,fromChrPos,toChrPos

inputs ${csvQuoted(impute2ResultChrBinGenFile)},${csvQuoted(impute2ResultChrBinInfoFile)}
alloutputsexist ${imputationResult}/chr_${chr}

####### MAKE SURE THE OUTPUT IS SORTED #######
#### IN OTHER WORDS, RETURN FROM CSVQUOTED MUST BE SORTED BY CHR,POS ###


#Concate the bins with compute for each

cat ${ssvQuoted(impute2ResultChrBinInfoFile)} > ${imputationResult}/chr_${chr}.gen

cat ${ssvQuoted(impute2ResultChrBinGenFile)} > ${imputationResult}/chr_${chr}.info


