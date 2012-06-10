#
# =====================================================
# $Id$
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/BwaSampe.ftl $
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=23:59:00

<#if seqType == "SR">
	#inputs "${leftfastqczip}"

<#else>
	#inputs "${leftfastqczip}"
	#inputs "${rightfastqczip}"

</#if>

inputs "${dedupmetrics}"
inputs "${mergedbam}"
inputs "${mergedbamindex}"
inputs "${samplealignmentmetrics}"
inputs "${sampleinsertsizemetrics}"
inputs "${samplehsmetrics}"
inputs "${snpsfinalvcf}"
inputs "${snpsfinalvcftable}"

if [ -f "${sample}.genotypeArray.updated.header.vcf" ]
then
	inputs "${sample}.genotypeArray.updated.header.vcf"
fi


alloutputsexist "${projectResultsDir}/${project}.zip"

# Change permissions

umask 0007


# Make result directories
mkdir -p ${projectResultsDir}/rawdata
mkdir -p ${projectResultsDir}/alignment
mkdir -p ${projectResultsDir}/snps
mkdir -p ${projectResultsDir}/qc


# Copy error, out and finished logs to project jobs directory

cp ${projectJobsDir}/*.out ${projectLogsDir}
cp ${projectJobsDir}/*.err ${projectLogsDir}
cp ${projectJobsDir}/*.log ${projectLogsDir}

# Copy project csv file to project results directory

cp ${projectJobsDir}/${project}.csv ${projectResultsDir}


# Create symlinks for all fastq files to the project results directory

cp -rs ${projectrawdatadir} ${projectResultsDir}/rawdata


# Copy fastQC output to results directory
cp ${leftfastqczip} ${projectResultsDir}/qc
cp ${rightfastqczip} ${projectResultsDir}/qc


# Copy dedup metrics to results directory
cp ${dedupmetrics} ${projectResultsDir}/qc

# Copy merged BAM plus index to results directory
cp ${mergedbam} ${projectResultsDir}/alignment
cp ${mergedbamindex} ${projectResultsDir}/alignment


# Copy alignment stats to results directory

cp ${samplealignmentmetrics} ${projectResultsDir}/qc
cp ${sampleinsertsizemetrics} ${projectResultsDir}/qc
cp ${samplehsmetrics} ${projectResultsDir}/qc


# Copy final vcf and vcf.table to results directory

cp ${snpsfinalvcf} ${projectResultsDir}/snps
cp ${snpsfinalvcftable} ${projectResultsDir}/snps


# Copy genotype array vcf to results directory

if [ -f "${sample}.genotypeArray.updated.header.vcf" ]
then
	cp ${sample}.genotypeArray.updated.header.vcf ${projectResultsDir}/qc
fi


# Copy QC report to results directory

cp ${qcstatisticstexreport} ${projectResultsDir}/qc


# Create README.txt containing link to documentation of zipfile on GCC wiki page
echo "http://www.blaat.nl" > ${projectResultsDir}/README.txt


# Create zip file for all "small text" files

zip -r ${projectResultsDir}/${project}.zip ${projectResultsDir}/snps
zip -gr ${projectResultsDir}/${project}.zip ${projectResultsDir}/qc
zip -g ${projectResultsDir}/${project}.zip ${projectResultsDir}/${project}.csv
zip -g ${projectResultsDir}/${project}.zip ${projectResultsDir}/README.txt