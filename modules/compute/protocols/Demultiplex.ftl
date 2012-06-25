#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=48:00:00 nodes=1 cores=4 mem=1
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
		inputs "${allRawNgsDataDir}/${runPrefix}/${compressedFastqFilenameSR}"
		
		#
		# Read count of the input file.
		# Note: we actually count lines, which equals reads * 4 for FastQ files.
		#
		reads_in_1=$(gzip -cd ${allRawNgsDataDir}/${runPrefix}/${compressedFastqFilenameSR} | wc -l)
		
		#
		# Demultiplex the multiplexed, gzipped FastQ file.
		#
		${demultiplexscript} --bcs '${csv(barcode)}' \
		--mpr1 ${allRawNgsDataDir}/${runPrefix}/${compressedFastqFilenameSR} \
		--dmr1 '${csv(compressedDemultiplexedSampleFastqFilepathSR)}' \
		--ukr1 ${runIntermediateDir}/${compressedDemultiplexedDiscardedFastqFilenameSR} \
		--tm MP \
		> ${runIntermediateDir}/${filenamePrefix}.demultiplex.log
		
		#
		# Read count of the output file.
		#
		summed_reads_out_1=0
		
		#
		# For the demultiplexed, uncompressed FastQ files:
		# 1. Calculate MD5Sums.
		# 2. Count the amount of lines(, which equals to reads * 4) per file.
		# 3. Update the sum of lines of all files.
		#
		<#list demultiplexedSampleFastqFilenameSR as fileToCheck>
		this_read_count=0
		mkfifo ${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenameSR[fileToCheck_index]}.pipe
		md5sum <${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenameSR[fileToCheck_index]}.pipe | \
			sed 's/ -/ ${fileToCheck}/' > ${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenameSR[fileToCheck_index]} &
		this_read_count=$(gzip -cd ${runIntermediateDir}/${compressedDemultiplexedSampleFastqFilenameSR[fileToCheck_index]} | \
			tee ${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenameSR[fileToCheck_index]}.pipe | \
			wc -l)
		rm ${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenameSR[fileToCheck_index]}.pipe
		summed_reads_out_1=$(( $summed_reads_out_1 + $this_read_count ))
			
		</#list>
		
		#
		# Same for the discarded, uncompressed FastQ file.
		#
		this_read_count=0
		mkfifo ${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenameSR}.pipe
		md5sum <${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenameSR}.pipe | \
			sed 's/ -/ ${demultiplexedDiscardedFastqFilenameSR}/' > ${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenameSR} &
		this_read_count=$(gzip -cd ${runIntermediateDir}/${compressedDemultiplexedDiscardedFastqFilenameSR} | \
			tee ${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenameSR}.pipe | \
			wc -l)
		rm ${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenameSR}.pipe
		summed_reads_out_1=$(( $summed_reads_out_1 + $this_read_count ))
		
		#
		# Flush disk caches to disk to make sure we don't loose any demultiplexed data 
		# when a machine crashes and some of the "written" data was in a write buffer.
		#
		sync
		
		#
		# Read count sanity check.
		#
		if (( $reads_in_1 == $summed_reads_out_1 ))
		then touch ${runIntermediateDir}/${filenamePrefix}.demultiplex.read_count_check.passed
		else touch ${runIntermediateDir}/${filenamePrefix}.demultiplex.read_count_check.FAILED
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
		inputs "${allRawNgsDataDir}/${runPrefix}/${compressedFastqFilenamePE1}"
		inputs "${allRawNgsDataDir}/${runPrefix}/${compressedFastqFilenamePE2}"
	
		#
		# Read count of the input file.
		# Note: we actually count lines, which equals reads * 4 for FastQ files.
		#
		reads_in_1=$(gzip -cd ${allRawNgsDataDir}/${runPrefix}/${compressedFastqFilenamePE1} | wc -l)
		reads_in_2=$(gzip -cd ${allRawNgsDataDir}/${runPrefix}/${compressedFastqFilenamePE2} | wc -l)
		
		#
		# Read count sanity check for the inputs.
		# For PE data the amount of reads in both input files must be the same!
		#
		if (( $reads_in_1 != $reads_in_2))
		then touch ${runIntermediateDir}/${filenamePrefix}.demultiplex.read_count_check.FAILED
		echo "FATAL: cannot demultiplex ${filenamePrefix}. Number of reads in both specified PE FastQ input files not the same!"
		exit 1
		fi
		
		#
		# Demultiplex the multiplexed, gzipped FastQ file.
		#
		${demultiplexscript} --bcs '${csv(barcode)}' \
		--mpr1 ${allRawNgsDataDir}/${runPrefix}/${compressedFastqFilenamePE1} \
		--mpr2 ${allRawNgsDataDir}/${runPrefix}/${compressedFastqFilenamePE2} \
		--dmr1 '${csv(compressedDemultiplexedSampleFastqFilepathPE1)}' \
		--dmr2 '${csv(compressedDemultiplexedSampleFastqFilepathPE2)}' \
		--ukr1 ${runIntermediateDir}/${compressedDemultiplexedDiscardedFastqFilenamePE1} \
		--ukr2 ${runIntermediateDir}/${compressedDemultiplexedDiscardedFastqFilenamePE2} \
		--tm MP \
		> ${runIntermediateDir}/${filenamePrefix}.demultiplex.log
		
		#
		# Read count of the output file.
		#
		summed_reads_out_1=0
		summed_reads_out_2=0
		
		#
		# For the demultiplexed, uncompressed FastQ files:
		# 1. Calculate MD5Sums.
		# 2. Count the amount of lines(, which equals to reads * 4) per file.
		# 3. Update the sum of lines of all files.
		#
		<#list demultiplexedSampleFastqFilenamePE1 as fileToCheck>
		this_read_count=0
		mkfifo ${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenamePE1[fileToCheck_index]}.pipe
		md5sum <${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenamePE1[fileToCheck_index]}.pipe | \
			sed 's/ -/ ${fileToCheck}/' > ${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenamePE1[fileToCheck_index]} &
		this_read_count=$(gzip -cd ${runIntermediateDir}/${compressedDemultiplexedSampleFastqFilenamePE1[fileToCheck_index]} | \
			tee ${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenamePE1[fileToCheck_index]}.pipe | \
			wc -l)
		rm ${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenamePE1[fileToCheck_index]}.pipe
		summed_reads_out_1=$(( $summed_reads_out_1 + $this_read_count ))
		
		</#list>
		<#list demultiplexedSampleFastqFilenamePE2 as fileToCheck>
		this_read_count=0
		mkfifo ${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenamePE2[fileToCheck_index]}.pipe
		md5sum <${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenamePE2[fileToCheck_index]}.pipe | \
			sed 's/ -/ ${fileToCheck}/' > ${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenamePE2[fileToCheck_index]} &
		this_read_count=$(gzip -cd ${runIntermediateDir}/${compressedDemultiplexedSampleFastqFilenamePE2[fileToCheck_index]} | \
			tee ${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenamePE2[fileToCheck_index]}.pipe | \
			wc -l)
		rm ${runIntermediateDir}/${demultiplexedSampleFastqChecksumFilenamePE2[fileToCheck_index]}.pipe
		summed_reads_out_2=$(( $summed_reads_out_2 + $this_read_count ))
		
		</#list>
		#
		# Same for the discarded, uncompressed FastQ files.
		#
		this_read_count=0
		mkfifo ${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenamePE1}.pipe
		md5sum <${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenamePE1}.pipe | \
			sed 's/ -/ ${demultiplexedDiscardedFastqFilenamePE1}/' > ${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenamePE1} &
		this_read_count=$(gzip -cd ${runIntermediateDir}/${compressedDemultiplexedDiscardedFastqFilenamePE1} | \
			tee ${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenamePE1}.pipe | \
			wc -l)
		rm ${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenamePE1}.pipe
		summed_reads_out_1=$(( $summed_reads_out_1 + $this_read_count ))
		
		this_read_count=0
		mkfifo ${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenamePE2}.pipe
		md5sum <${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenamePE2}.pipe | \
			sed 's/ -/ ${demultiplexedDiscardedFastqFilenamePE2}/' > ${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenamePE2} &
		this_read_count=$(gzip -cd ${runIntermediateDir}/${compressedDemultiplexedDiscardedFastqFilenamePE2} | \
			tee ${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenamePE2}.pipe | \
			wc -l)
		rm ${runIntermediateDir}/${demultiplexedDiscardedFastqChecksumFilenamePE2}.pipe
		summed_reads_out_2=$(( $summed_reads_out_2 + $this_read_count ))
		
		#
		# Flush disk caches to disk to make sure we don't loose any demultiplexed data 
		# when a machine crashes and some of the "written" data was in a write buffer.
		#
		sync
		
		#
		# Read count sanity check.
		#
		if (( $reads_in_1 == $summed_reads_out_1 )) && (( $reads_in_2 == $summed_reads_out_2))
		then touch ${runIntermediateDir}/${filenamePrefix}.demultiplex.read_count_check.passed
		else touch ${runIntermediateDir}/${filenamePrefix}.demultiplex.read_count_check.FAILED
		fi
	</#if>
	
</#if>
