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
#FOREACH run

#
# Check the reads of the input file.
#

#
# For each lane demultiplex rawdata.
#
<#if seqType == "SR">
	
	<#if barcode[0] == "None">
		# Do nothing.
	<#else>
		${demultiplexscript} --bcs '${csv(barcode)}' \
		--mms 1 \
		--mpr1 ${fq.gz} \
		--dmr1 '${csv(fq.gz_barcode)}' \
		--ukr1 ${fq.gz_discarded} \
		>> ${runIntermediateDir}/demultiplex.log
		
		reads_in_1 = gzip -cd ${fq.gz} | wc -l
		
	</#if>
		
	<#list fq_barcode as file_to_check>
		# md5sum the demultiplexed file.
		gzip -cd ${file_to_check}${gz_extension} | md5sum > ${file_to_check}${md5sum_extension}
	</#list>
	
<#elseif seqType == "PE">
	
	<#if barcode[0] == "None">
		# Do nothing.
	<#else>
		${demultiplexscript} --bcs '${csv(barcode)}' \
		--mms 1 \
		--mpr1 ${fq.gz_1} \
		--mpr2 ${fq.gz_2} \
		--dmr1 '${csv(fq.gz_barcode_1)}' \
		--dmr2 '${csv(fq.gz_barcode_2)}' \
		--ukr1 ${fq.gz_discarded_1} \
		--ukr2 ${fq.gz_discarded_2} \
		>> ${runIntermediateDir}/demultiplex.log 
	</#if>
		
	<#list fq_barcode_1 as file_1_to_check>
		# md5sum the demultiplexed file.
		gzip -cd ${file_1_to_check}${gz_extension} | md5sum > ${file_1_to_check}${md5sum_extension}
	</#list>
	
	<#list fq_barcode_2 as file_2_to_check>
		# md5sum the demultiplexed file.
		gzip -cd ${file_2_to_check}${gz_extension} | md5sum > ${file_2_to_check}${md5sum_extension}
	</#list>
		
</#if>

#
# Check the reads of all the output files.
#

#
# Compare input with total output and generate error if not the same.
#