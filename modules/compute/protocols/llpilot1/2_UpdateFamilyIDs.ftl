#MOLGENIS walltime=00:45:00

inputs "${familyUpdateFile}"

${plink} --noweb --silent --bfile ${filehandleRemoveString} --update-ids ${familyUpdateFile} --make-bed --out ${filehandleUpdateIdsString}  
 