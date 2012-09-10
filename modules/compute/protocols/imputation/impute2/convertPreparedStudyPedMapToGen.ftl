#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#INPUTS preparedStudyDir/chr${chr}.ped,preparedStudyDir/chr${chr}.map
#OUTPUTS preparedStudyDir/chr${chr}.gen,preparedStudyDir/chr${chr}.sample
#EXES gtoolBin
#LOGS log
#TARGETS project,chr

#FOREACH project,chr

inputs "${preparedStudyDir}/chr${chr}.ped"
inputs "${preparedStudyDir}/chr${chr}.map"
alloutputsexist "${preparedStudyDir}/chr${chr}.gen"
alloutputsexist "${preparedStudyDir}/chr${chr}.sample"


${gtoolBin} -P --ped ${preparedStudyDir}/chr${chr}.ped --map ${preparedStudyDir}/chr${chr}.map --og ${preparedStudyDir}/~chr${chr}.gen --os ${preparedStudyDir}/~chr${chr}.sample

#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	
	echo -e "\nMoving temp files to final files\n\n"

	mv ${preparedStudyDir}/~chr${chr}.gen ${preparedStudyDir}/chr${chr}.gen
	mv ${preparedStudyDir}/~chr${chr}.sample ${preparedStudyDir}/chr${chr}.sample

	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"
	#Return non zero return code
	exit 1

fi