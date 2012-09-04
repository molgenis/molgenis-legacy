



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