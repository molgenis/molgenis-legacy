#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=16

#FOREACH project,chr

alloutputsexist ${imputationResult}/chr_${chr}


#Concate the bins with compute for each

cat ${ssvQuoted(impute2ResultChrBinInfoFile)} > ${imputationResult}/chr_${chr}.gen

cat ${ssvQuoted(impute2ResultChrBinGenFile)} > ${imputationResult}/chr_${chr}.info