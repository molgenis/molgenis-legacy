#MOLGENIS walltime=00:45:00

inputs "${filehandleUpdateParentsString}"

${plink} --noweb --silent --bfile ${filehandleUpdateParentsString} --check-sex --out ${filehandleSexCheckString} 
 