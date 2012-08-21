

#MOLGENIS walltime=72:00:00 nodes=1 cores=1 mem=35

#INPUTS beaglefile,beaglefilephased,markers
#OUTPUTS imputedoutputbeagle.*
#EXES beagle
#TARGETS project,chr

inputs "${beaglefile}"
inputs "${beaglefilephased}"
inputs "${markers}"
alloutputsexist "${imputedoutputbeagle}"


#Impute using Beagle
java -jar -Xmx35g -Djava.io.tmpdir=${tempdir} \
${beaglejar} \
unphased=${beaglefile} \
phased=${beaglefilephased} \
markers=${markers} \
missing=0 \
out=${imputedoutputbeagle}