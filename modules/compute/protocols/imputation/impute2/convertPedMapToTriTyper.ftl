#MOLGENIS walltime=05:00:00 nodes=1 cores=1 mem=4


#INPUTS studyPedMap.map,studyPedMap.ped
#OUTPUTS studyTriTyperDir
#EXES imputationToolJar
#LOGS log
#TARGETS plinkdata

inputs ${studyPedMap}.map
inputs ${studyPedMap}.ped

alloutputsexist ${studyTriTyperDir}/GenotypeMatrix.dat
alloutputsexist ${studyTriTyperDir}/Individuals.txt
alloutputsexist ${studyTriTyperDir}/PhenotypeInformation.txt
alloutputsexist ${studyTriTyperDir}/SNPMappings.txt
alloutputsexist ${studyTriTyperDir}/SNPsHash.txt
alloutputsexist ${studyTriTyperDir}/SNPs.txt


mkdir ${studyTriTyperTempDir}

java -jar ${imputationToolJar} --mode pmtt --in ${studyPedMapDir} --out ${studyTriTyperTempDir}


#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	mv ${studyTriTyperTempDir} ${studyTriTyperDir}

	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi