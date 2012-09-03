

#MOLGENIS walltime=24:00:00 nodes=1 cores=1 mem=4

#INPUTS plinkdata
#OUTPUTS plinkdatatransposed
#EXES plink
#TARGETS project,chr

inputs "${plinkdata}.bed"
inputs "${plinkdata}.bim"
inputs "${plinkdata}.fam"
alloutputsexist "${plinkdatatransposed}.tfam"
alloutputsexist "${plinkdatatransposed}.tped"





#Transpose plink data
${plink} \
--bfile ${plinkdata} \
--chr ${chr} \
--transpose \
--recode \
--out ~${plinkdatatransposed} \
--noweb

#Get return code from last program call
returnCode=$?

if [ $returnCode -eq 0 ]
then
	

	echo -e "\nMoving temp files to final files\n\n"

	for tempFile in ${plinkdatatransposed}* ; do
		finalFile=`echo $tempFile | sed -e "s/~//g"`
		mv $tempFile $finalFile
	done
	
else
  
	echo -e "\nNon zero return code not making files final. Existing temp files are kept for debuging purposes\n\n"

	#Return non zero return code
	exit 1

fi