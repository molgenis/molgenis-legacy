#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=23:59:00

<#if seqType == "SR">
	#inputs "${leftfastqczip}"

<#else>
	#inputs "${leftfastqczip}"
	#inputs "${rightfastqczip}"

</#if>

inputs "${alignmentmetrics}"
inputs "${gcbiasmetrics}"
inputs "${gcbiasmetricspdf}"
inputs "${insertsizemetrics}"
inputs "${insertsizemetricspdf}"
inputs "${meanqualitybycycle}"
inputs "${meanqualitybycyclepdf}"
inputs "${qualityscoredistribution}"
inputs "${qualityscoredistributionpdf}"
inputs "${hsmetrics}"
inputs "${bamindexstats}"
inputs "${samplealignmentmetrics}"
inputs "${samplegcbiasmetrics}"
inputs "${samplegcbiasmetricspdf}"
inputs "${sampleinsertsizemetrics}"
inputs "${sampleinsertsizemetricspdf}"
inputs "${samplemeanqualitybycycle}"
inputs "${samplemeanqualitybycyclepdf}"
inputs "${samplequalityscoredistribution}"
inputs "${samplequalityscoredistributionpdf}"
inputs "${samplehsmetrics}"
inputs "${samplebamindexstats}"
inputs "${dedupmetrics}"
inputs "${mergedbam}"
inputs "${mergedbamindex}"
inputs "${sample}.coverage.csv"
inputs "${samplecoverageplotpdf}"
inputs "${sample}.coverage.Rdata"
inputs "${coveragegatk}"
inputs "${coveragegatk}.sample_cumulative_coverage_counts"
inputs "${coveragegatk}.sample_cumulative_coverage_proportions"
inputs "${coveragegatk}.sample_interval_statistics"
inputs "${coveragegatk}.sample_interval_summary"
inputs "${coveragegatk}.sample_statistics"
inputs "${coveragegatk}.sample_summary"
inputs "${coveragegatk}.cumulative_coverage.pdf"
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
mkdir -p ${projectResultsDir}/coverage
mkdir -p ${projectResultsDir}/snps
mkdir -p ${projectResultsDir}/qc/statistics


# Copy error, out and finished logs to project jobs directory

cp ${projectJobsDir}/*.out ${projectLogsDir}
cp ${projectJobsDir}/*.err ${projectLogsDir}
cp ${projectJobsDir}/*.log ${projectLogsDir}

# Copy project csv file to project results directory

cp ${projectJobsDir}/${project}.csv ${projectResultsDir}


# Create symlinks for all fastq and md5 files to the project results directory

cp -rs ${projectrawdatadir} ${projectResultsDir}/rawdata


# Copy fastQC output to results directory
cp ${intermediatedir}/*_fastqc.zip ${projectResultsDir}/qc


# Copy dedup metrics to results directory
cp ${dedupmetrics} ${projectResultsDir}/qc/statistics

# Copy merged BAM plus index to results directory
cp ${mergedbam} ${projectResultsDir}/alignment
cp ${mergedbamindex} ${projectResultsDir}/alignment


# Copy alignment stats (lane and sample) to results directory

cp ${intermediatedir}/*.alignmentmetrics ${projectResultsDir}/qc/statistics
cp ${intermediatedir}/*.gcbiasmetrics ${projectResultsDir}/qc/statistics
cp ${intermediatedir}/*.insertsizemetrics ${projectResultsDir}/qc/statistics
cp ${intermediatedir}/*.meanqualitybycycle ${projectResultsDir}/qc/statistics
cp ${intermediatedir}/*.qualityscoredistribution ${projectResultsDir}/qc/statistics
cp ${intermediatedir}/*.hsmetrics ${projectResultsDir}/qc/statistics
cp ${intermediatedir}/*.bamindexstats ${projectResultsDir}/qc/statistics
cp ${intermediatedir}/*.pdf ${projectResultsDir}/qc/statistics


# Copy coverage stats (for future reference) to results directory

cp ${intermediatedir}/*.coverage* ${projectResultsDir}/coverage


# Copy final vcf and vcf.table to results directory

cp ${snpsfinalvcf} ${projectResultsDir}/snps
cp ${snpsfinalvcftable} ${projectResultsDir}/snps


# Copy genotype array vcf to results directory

if [ -f "${sample}.genotypeArray.updated.header.vcf" ]
then
	cp ${sample}.genotypeArray.updated.header.vcf ${projectResultsDir}/qc
fi
cp ${sampleconcordancefile} ${projectResultsDir}/qc

# Copy QC report to results directory

cp ${qcdir}/${project}_QCReport.pdf ${projectResultsDir}


###########################################
##################TO DO####################
# Create README.txt containing link to documentation of zipfile on GCC wiki page
echo "http://www.blaat.nl" > ${projectResultsDir}/README.txt

# save latex README template in file
echo "<#include "CopyFqToRawdataDirREADMEtemplate.tex"/>" > ${projectResultsDir}/README.txt

pdflatex -output-directory=${projectResultsDir} ${projectResultsDir}/README.txt

###########################################
###########################################

# Create zip file for all "small text" files

cd ${projectResultsDir}

zip -r ${projectResultsDir}/${project}.zip snps
zip -gr ${projectResultsDir}/${project}.zip qc
zip -g ${projectResultsDir}/${project}.zip ${project}.csv
zip -g ${projectResultsDir}/${project}.zip README.pdf
zip -g ${projectResultsDir}/${project}.zip ${project}_QCReport.pdf


# Create md5sum for zip file
md5sum ${projectResultsDir}/${project}.zip ${projectResultsDir}/${project}.zip.md5