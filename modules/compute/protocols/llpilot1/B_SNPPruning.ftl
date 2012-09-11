#MOLGENIS walltime=00:45:00

inputs "${filehandleRemoveSampleString}"

${plink} --noweb --silent --bfile ${filehandleRemoveSampleString} --indep-pairwise ${snpWindowN} ${snpIntervalM} ${rSquaredThreshold} --out ${filehandlePairwiseString}
 