#MOLGENIS walltime=00:45:00

inputs "${filehandleFilterString}"

${plink} --noweb --silent --bfile ${filehandleUpdateSexString} --remove ${filehandleFilterString}  --out ${filehandleRemoveSampleString}  
 