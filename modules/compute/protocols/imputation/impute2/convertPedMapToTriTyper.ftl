#MOLGENIS walltime=05:00:00 nodes=1 cores=1 mem=4


#INPUTS studyPedMap.map,studyPedMap.ped
#OUTPUTS studyTriTyperChrDir
#EXES imputationToolJar
#LOGS log

#FOREACH project,chr

inputs "${studyPedMapChr}.map"
inputs "${studyPedMapChr}.ped"
alloutputsexist "${studyTriTyperChrDir}/GenotypeMatrix.dat"
alloutputsexist "${studyTriTyperChrDir}/Individuals.txt"
alloutputsexist "${studyTriTyperChrDir}/PhenotypeInformation.txt"
alloutputsexist "${studyTriTyperChrDir}/SNPMappings.txt"
alloutputsexist "${studyTriTyperChrDir}/SNPsHash.txt"
alloutputsexist "${studyTriTyperChrDir}/SNPs.txt"


mkdir -p ${studyTriTyperChrTempDir}

java -jar ${imputationToolJar} --mode pmtt --in ${studyPedMapChrDir} --out ${studyTriTyperChrTempDir}


#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	mv ${studyTriTyperChrTempDir} ${studyTriTyperChrDir}

	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi