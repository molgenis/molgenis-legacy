#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=16

#INPUTS studyTriTyperDir,referenceTriTyperDir
#OUTPUTS preparedStudyTempDir
#EXES imputationToolJar
#LOGS log
#TARGETS plinkdata

inputs ${studyTriTyperDir}/GenotypeMatrix.dat
inputs ${studyTriTyperDir}/Individuals.txt
inputs ${studyTriTyperDir}/PhenotypeInformation.txt
inputs ${studyTriTyperDir}/SNPMappings.txt
inputs ${studyTriTyperDir}/SNPsHash.txt
inputs ${studyTriTyperDir}/SNPs.txt
inputs ${referenceTriTyperDir}/GenotypeMatrix.dat
inputs ${referenceTriTyperDir}/Individuals.txt
inputs ${referenceTriTyperDir}/PhenotypeInformation.txt
inputs ${referenceTriTyperDir}/SNPMappings.txt
inputs ${referenceTriTyperDir}/SNPsHash.txt
inputs ${referenceTriTyperDir}/SNPs.txt
alloutputsexist preparedStudyTempDir/*



mkdir ${preparedStudyTempDir}


java -Xmx16g -jar ${imputationToolJar} --mode ttpmh --in ${studyTriTyperDir} --hap ${referenceTriTyperDir} --out ${preparedStudyTempDir}


#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	mv ${preparedStudyTempDir} ${preparedTempDir}

	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi