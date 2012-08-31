



mkdir ${preparedStudyTempDir}


java -Xmx16g -jar ${imputationToolJar} --mode ttpmh --in ${studyTriTyperTempDir} --hap ${referenceTriTyperFolder} --out ${preparedStudyTempDir}