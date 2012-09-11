#MOLGENIS walltime=00:45:00

inputs "${filehandleUpdateIdsString}"

${plink} --noweb --silent --bfile ${filehandleUpdateIdsString} --update-ids ${parentsUpdateFile} --make-bed --out ${filehandleUpdateParentsString}  
 