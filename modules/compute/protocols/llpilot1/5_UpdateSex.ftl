#MOLGENIS walltime=00:45:00

inputs "${filehandleSexCheckString}"

${plink} --noweb --silent --bfile ${filehandleUpdateParentsString} --update-sex ${filehandleSexCheckString} --make-bed --out ${filehandleUpdateSexString}  
 