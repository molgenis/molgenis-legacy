#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4


#EXES molgenisComputeBin
#LOGS log

####TEMPLATE TO BE FINISHED SOON####

mkdir -p ${projectdir}
mkdir -p ${projectTempDir}


#Run Jar to create full worksheet


<#if imputationPipeline == "impute2">


	java -jar ${expandWorksheetJar} ${McWorksheet} ${projectJobsDir}/${project}.csv ${chrBinsFile} 
	
	
	
	# Execute MOLGENIS/compute to create job scripts to analyse this project.
	#
	sh ${McDir}/molgenis_compute.sh \
	-worksheet=${projectJobsDir}/${project}.csv \
	-parameters=${McParameters} \
	-workflow=${McProtocols}/workflowImpute.csv \
	-protocols=${McProtocols}/ \
	-templates=${McTemplates}/ \
	-scripts=${projectJobsDir}/ \
	-id=${McId}
	
<#else>

	echo "imputationPipeline ${imputationPipeline} not supported"
	return 1

</#if>