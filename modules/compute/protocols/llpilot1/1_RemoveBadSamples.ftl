#MOLGENIS walltime=00:45:00

inputs "${bedFile}" 
inputs "${bimFile}"
inputs "${famFile}"
inputs "${badSampleFile}"

${plink} --noweb --silent --bed ${bedFile} --bim ${bimFile} --fam ${famFile} --remove ${badSampleFile} --make-bed --out ${filehandleRemoveString} 
 