#MOLGENIS walltime=00:45:00

inputs "${filehandleGenomeString}"

${plink} --noweb --silent --bfile ${filehandleGenomeString} --me ${familyError} ${snpError} --make-bed --out ${filehandleMendelString}
 