#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=48:00:00 nodes=1 cores=1 mem=1
#FOREACH flowcell, lane, seqType, filenamePrefix

export PATH=${R_HOME}/bin:<#noparse>${PATH}</#noparse>
export R_HOME=${R_HOME}

#
# Check if we need to run this step or wether demultiplexing was already executed successfully in a previous run.
#
# Note: we don't check for presence of the actual demultiplexed reads, but for empty file indicating successfull demultipxing instead 
#       where success is based on a comparison of the amount of reads in the multiplexed input file and the total amount of reads in 
#       the demultiplexed output files: these counts should be the same.
#
alloutputsexist "${runIntermediateDir}/demultiplex.${filenamePrefix}.read_count_check.passed"

#
# For each lane demultiplex rawdata.
#
<#if seqType == "SR">
	
	<#if barcode[0] == "None">
		#
		# Do nothing.
		#
		touch ${runIntermediateDir}/demultiplex.read_count_check.skipped
	<#else>
		#
		# Check if the files required for demultiplexing are present.
		#
		inputs "${compressedFastqFilepathSR}"

		#
		# Demultiplex the multiplexed, gzipped FastQ file.
		#
		${demultiplexScript} --bcs '${csv(barcode)}' \
		--mpr1 ${compressedFastqFilepathSR} \
		--dmr1 '${csv(compressedDemultiplexedSampleFastqFilepathSR)}' \
		--ukr1 ${compressedDemultiplexedDiscardedFastqFilepathSR}
		
		#
		# Read count of the input file.
		#
		reads_in_1=$(gzip -cd ${compressedFastqFilepathSR} | wc -l)
		
		#
		# Read count of the output file.
		#
		summed_reads_out_1=0
		
		<#list demultiplexedSampleFastqFilenameSR as fileToCheck>
			#
			# Calculate MD5Sums for the demultiplexed, uncompressed FastQ files.
			#
			gzip -cd ${compressedDemultiplexedSampleFastqFilepathSR[fileToCheck_index]} | \
			md5sum | \
			sed 's/ -/ ${fileToCheck}/' > ${demultiplexedSampleFastqChecksumFilepathSR[fileToCheck_index]}
			
			#
			# Update summed read count of output files.
			#
			summed_reads_out_1=$(( $summed_reads_out_1 + $(gzip -cd ${compressedDemultiplexedSampleFastqFilepathSR[fileToCheck_index]} | wc -l) ))
		</#list>
		
		#
		# Calculate MD5Sum for the discarded, uncompressed FastQ file.
		#
		gzip -cd ${compressedDemultiplexedDiscardedFastqFilepathSR} | \
		md5sum | \
		sed 's/ -/ ${demultiplexedDiscardedFastqFilenameSR}/' > ${demultiplexedDiscardedFastqChecksumFilepathSR}
		
		#
		# Update summed read count of output files with the # discarded reads.
		#		
		summed_reads_out_1=$(( $summed_reads_out_1 + $(gzip -cd ${compressedDemultiplexedDiscardedFastqFilepathSR} | wc -l) ))
		
		#
		# Flush disk caches to disk to make sure we don't loose any demultiplexed data 
		# when a machine crashes and some of the "written" data was in a write buffer.
		#
		sync
		
		#
		# Read count sanity check.
		#
		if (( $reads_in_1 == $summed_reads_out_1 ))
		then touch ${runIntermediateDir}/demultiplex.${filenamePrefix}.read_count_check.passed
		else touch ${runIntermediateDir}/demultiplex.${filenamePrefix}.read_count_check.FAILED
		fi
		
	</#if>
	
<#elseif seqType == "PE">
	
	<#if barcode[0] == "None">
		#
		# Do nothing.
		#
		touch ${runIntermediateDir}/demultiplex.read_count_check.skipped
	<#else>
		#
		# Check if the files required for demultiplexing are present.
		#
		inputs "${compressedFastqFilepathPE1}"
		inputs "${compressedFastqFilepathPE2}"
	
		#
		# Demultiplex the multiplexed, gzipped FastQ file.
		#
		${demultiplexScript} --bcs '${csv(barcode)}' \
		--mpr1 ${compressedFastqFilepathPE1} \
		--mpr2 ${compressedFastqFilepathPE2} \
		--dmr1 '${csv(compressedDemultiplexedSampleFastqFilepathPE1)}' \
		--dmr2 '${csv(compressedDemultiplexedSampleFastqFilepathPE2)}' \
		--ukr1 ${compressedDemultiplexedDiscardedFastqFilepathPE1} \
		--ukr2 ${compressedDemultiplexedDiscardedFastqFilepathPE2}
		
		#
		# Read count of the input files.
		#
		reads_in_1=$(gzip -cd ${compressedFastqFilepathPE1} | wc -l)
		reads_in_2=$(gzip -cd ${compressedFastqFilepathPE2} | wc -l)
		
		#
		# Read count of the output file.
		#
		summed_reads_out_1=0
		summed_reads_out_2=0
		
		<#list demultiplexedSampleFastqFilenamePE1 as fileToCheck>
			#
			# Calculate MD5Sums for the demultiplexed, uncompressed FastQ files.
			#
			gzip -cd ${compressedDemultiplexedSampleFastqFilepathPE1[fileToCheck_index]} | \
			md5sum | \
			sed 's/ -/ ${fileToCheck}/' > ${demultiplexedSampleFastqChecksumFilepathPE1[fileToCheck_index]}
			#
			# Update summed read count of output files.
			#
			summed_reads_out_1=$(( $summed_reads_out_1 + $(gzip -cd ${compressedDemultiplexedSampleFastqFilepathPE1[fileToCheck_index]} | wc -l) ))
		</#list>
		<#list demultiplexedSampleFastqFilenamePE2 as fileToCheck>
			#
			# Calculate MD5Sums for the demultiplexed, uncompressed FastQ files.
			#
			gzip -cd ${compressedDemultiplexedSampleFastqFilepathPE2[fileToCheck_index]} | \
			md5sum | \
			sed 's/ -/ ${fileToCheck}/' > ${demultiplexedSampleFastqChecksumFilepathPE2[fileToCheck_index]}
			#
			# Update summed read count of output files.
			#
			summed_reads_out_1=$(( $summed_reads_out_1 + $(gzip -cd ${compressedDemultiplexedSampleFastqFilepathPE2[fileToCheck_index]} | wc -l) ))
		</#list>
		
		#
		# Calculate MD5Sum for the discarded, uncompressed FastQ file.
		#
		gzip -cd ${compressedDemultiplexedDiscardedFastqFilepathPE1} | \
		md5sum | \
		sed 's/ -/ ${demultiplexedDiscardedFastqFilenamePE1}/' > ${demultiplexedDiscardedFastqChecksumFilepathPE1}
		gzip -cd ${compressedDemultiplexedDiscardedFastqFilepathPE2} | \
		md5sum | \
		sed 's/ -/ ${demultiplexedDiscardedFastqFilenamePE2}/' > ${demultiplexedDiscardedFastqChecksumFilepathPE2}
		
		#
		# Update summed read count of output files with the # discarded reads.
		#		
		summed_reads_out_1=$(( $summed_reads_out_1 + $(gzip -cd ${compressedDemultiplexedDiscardedFastqFilepathPE1} | wc -l) ))
		summed_reads_out_2=$(( $summed_reads_out_2 + $(gzip -cd ${compressedDemultiplexedDiscardedFastqFilepathPE2} | wc -l) ))
		
		#
		# Flush disk caches to disk to make sure we don't loose any demultiplexed data 
		# when a machine crashes and some of the "written" data was in a write buffer.
		#
		sync
		
		#
		# Read count sanity check.
		#
		if (( $reads_in_1 == $summed_reads_out_1 )) && (( $reads_in_2 == $summed_reads_out_2))
		then touch ${runIntermediateDir}/demultiplex.${filenamePrefix}.read_count_check.passed
		else touch ${runIntermediateDir}/demultiplex.${filenamePrefix}.read_count_check.FAILED
		fi
	</#if>
	
</#if>
