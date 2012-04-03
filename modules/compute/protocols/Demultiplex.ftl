#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=20:00:00 nodes=1 cores=1 mem=10
#FOREACH flowcell, lane

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
		# Demultiplex the multiplexed, gzipped FastQ file.
		#
		${demultiplexscript} --bcs '${csv(barcode)}' \
		--mms 1 \
		--mpr1 ${fq_gz} \
		--dmr1 '${csv(fq_gz_barcode)}' \
		--ukr1 ${fq_gz_discarded} \
		>> ${runIntermediateDir}/demultiplex.log
		
		#
		# Read count of the input file.
		#
		reads_in_1=$(gzip -cd ${fq_gz} | wc -l)
		
		#
		# Read count of the output file.
		#
		summed_reads_out_1=0
		
		<#list fq_barcode as file_to_check>
			#
			# Calculate MD5Sums for the demultiplexed, uncompressed FastQ files.
			#
			gzip -cd ${file_to_check}${gz_extension} | md5sum | sed 's/ -/ ${file_to_check}/' > ${file_to_check}${md5sum_extension}
			
			#
			# Update summed read count of output files.
			#
			summed_reads_out_1=$(( $summed_reads_out_1 + $(gzip -cd ${file_to_check}${gz_extension} | wc -l) ))
		</#list>
		
		#
		# Calculate MD5Sum for the discarded, uncompressed FastQ file.
		#
		gzip -cd ${fq_gz_discarded} | md5sum | sed 's/ -/ ${fq_discarded}/' > ${fq_discarded}${md5sum_extension}
		
		#
		# Update summed read count of output files with the # discarded reads.
		#		
		summed_reads_out_1=$(( $summed_reads_out_1 + $(gzip -cd ${fq_gz_discarded} | wc -l) ))
		
		#
		# Flush disk caches to disk to make sure we don't loose any demultiplexed data 
		# when a machine crashes and some of the "written" data was in a write buffer.
		#
		sync
		
		#
		# Read count sanity check.
		#
		if (( $reads_in_1 == $summed_reads_out_1 ))
		then touch ${runIntermediateDir}/demultiplex.read_count_check.passed
		else touch ${runIntermediateDir}/demultiplex.read_count_check.FAILED
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
		# Demultiplex the multiplexed, gzipped FastQ file.
		#
		${demultiplexscript} --bcs '${csv(barcode)}' \
		--mms 1 \
		--mpr1 ${fq_gz_1} \
		--mpr2 ${fq_gz_2} \
		--dmr1 '${csv(fq_gz_barcode_1)}' \
		--dmr2 '${csv(fq_gz_barcode_2)}' \
		--ukr1 ${fq_gz_discarded_1} \
		--ukr2 ${fq_gz_discarded_2} \
		>> ${runIntermediateDir}/demultiplex.log 
		
		#
		# Read count of the input files.
		#
		reads_in_1=$(gzip -cd ${fq_gz_1} | wc -l)
		reads_in_2=$(gzip -cd ${fq_gz_2} | wc -l)
		
		#
		# Read count of the output file.
		#
		summed_reads_out_1=0
		summed_reads_out_2=0
		
		<#list fq_barcode_1 as file_1_to_check>
			#
			# Calculate MD5Sums for the demultiplexed, uncompressed FastQ files.
			#
			gzip -cd ${file_1_to_check}${gz_extension} | md5sum | sed 's/ -/ ${file_1_to_check}/' > ${file_1_to_check}${md5sum_extension}
			#
			# Update summed read count of output files.
			#
			summed_reads_out_1=$(( $summed_reads_out_1 + $(gzip -cd ${file_1_to_check}${gz_extension} | wc -l) ))
		</#list>
		<#list fq_barcode_2 as file_2_to_check>
			#
			# Calculate MD5Sums for the demultiplexed, uncompressed FastQ files.
			#
			gzip -cd ${file_2_to_check}${gz_extension} | md5sum | sed 's/ -/ ${file_2_to_check}/' > ${file_2_to_check}${md5sum_extension}
			#
			# Update summed read count of output files.
			#
			summed_reads_out_2=$(( $summed_reads_out_2 + $(gzip -cd ${file_2_to_check}${gz_extension} | wc -l) ))
		</#list>
		
		#
		# Calculate MD5Sum for the discarded, uncompressed FastQ file.
		#
		gzip -cd ${fq_gz_discarded_1} | md5sum | sed 's/ -/ ${fq_discarded_1}/' > ${fq_discarded_1}${md5sum_extension}
		gzip -cd ${fq_gz_discarded_2} | md5sum | sed 's/ -/ ${fq_discarded_2}/' > ${fq_discarded_2}${md5sum_extension}
		
		#
		# Update summed read count of output files with the # discarded reads.
		#		
		summed_reads_out_1=$(( $summed_reads_out_1 + $(gzip -cd ${fq_gz_discarded_1} | wc -l) ))
		summed_reads_out_2=$(( $summed_reads_out_2 + $(gzip -cd ${fq_gz_discarded_2} | wc -l) ))
		
		#
		# Flush disk caches to disk to make sure we don't loose any demultiplexed data 
		# when a machine crashes and some of the "written" data was in a write buffer.
		#
		sync
		
		#
		# Read count sanity check.
		#
		if (( $reads_in_1 == $summed_reads_out_1 )) && (( $reads_in_2 == $summed_reads_out_2))
		then touch ${runIntermediateDir}/demultiplex.read_count_check.passed
		else touch ${runIntermediateDir}/demultiplex.read_count_check.FAILED
		fi
	</#if>
	
</#if>
