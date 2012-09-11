#MOLGENIS walltime=00:45:00

inputs "${filehandlePairwiseString}"

${plink} --noweb --silent --bfile ${filehandlePairwiseString} --extract ${filehandlePairwiseString}.prune.in --genome --out ${filehandleGenomeString}
 