




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