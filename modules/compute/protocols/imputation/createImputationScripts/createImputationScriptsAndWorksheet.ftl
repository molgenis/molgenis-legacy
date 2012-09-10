#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=4

#INPUTS 
#OUTPUTS 
#EXES molgenisComputeBin
#LOGS log
#TARGETS

####TEMPLATE TO BE FINISHED SOON####

#Run Jar to create full worksheet





# Execute MOLGENIS/compute to create job scripts to analyse this project.
#
sh ${McDir}/molgenis_compute.sh \
-worksheet=${projectJobsDir}/${project}.csv \
-parameters=${McParameters} \
-workflow=${workflowFile} \
-protocols=${McProtocols}/ \
-templates=${McTemplates}/ \
-scripts=${projectJobsDir}/ \
-id=${McId}