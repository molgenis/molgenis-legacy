#
# =====================================================
# $Id: CreateInhouseProjects.ftl 11654 2012-04-17 16:08:11Z mburger $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/CreateInhouseProjects.ftl $
# $LastChangedDate: 2012-04-17 18:08:11 +0200 (Tue, 17 Apr 2012) $
# $LastChangedRevision: 11654 $
# $LastChangedBy: mburger $
# =====================================================
#

#MOLGENIS walltime=00:10:00
#FOREACH project

#
# Change permissions.
#
umask 0007

#
# Create project dirs.
#
mkdir -p ${projectrawarraydatadir}
mkdir -p ${projectrawdatadir}
mkdir -p ${projectJobsDir}
mkdir -p ${projectLogsDir}
mkdir -p ${intermediatedir}
mkdir -p ${projectResultsDir}
mkdir -p ${qcdir}

#
# Create symlinks to the raw data required to analyse this project
#
# For each sequence file (could be multiple per sample):
#
<#list internalSampleID as sample>
	
	<#if seqType[sample_index] == "SR">
		
		<#if barcode[sample_index] == "None">
			ln -s ${fq[sample_index]} ${projectrawdatadir}/
		<#else>
			ln -s ${fq_barcode[sample_index]} ${projectrawdatadir}/
		</#if>
		
	<#elseif seqType[sample_index] == "PE">
		
		<#if barcode[sample_index] == "None">
			ln -s ${fq_1[sample_index]} ${projectrawdatadir}/
			ln -s ${fq_2[sample_index]} ${projectrawdatadir}/
		<#else>
			ln -s ${fq_barcode_1[sample_index]} ${projectrawdatadir}/
			ln -s ${fq_barcode_2[sample_index]} ${projectrawdatadir}/
		</#if>
		
	</#if>
	
</#list>

#
# TODO: array for each sample:
#

#
# Create subset of samples for this project.
#
<#--<#assign unfolded = unfoldParametersCSV(parameters) />
<#list unfolded as sampleSequenceDetails>
echo ${sampleSequenceDetails} >> ${projectJobsDir}/${project}.csv
</#list>-->
${tooldir}/scripts/extract_samples_from_GAF_list.pl --i ${worksheet} --o ${projectJobsDir}/${project}.csv --c project --q ${project}

#
# Execute MOLGENIS/compute to create job scripts to analyse this project.
#
sh ${molgenisComputeDir}/molgenis_compute.sh \
-worksheet=${projectJobsDir}/${project}.csv \
-outputscriptsdir=${projectJobsDir}/ \
-parametersfile=${parametersFile} \
-workflowfile=${workflowFile} \
-protocoldir=${protocolsDir} \
-cluster=dummy \
-templatesdir=dummy dummy
