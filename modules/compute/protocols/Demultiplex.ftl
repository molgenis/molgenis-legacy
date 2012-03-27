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
#FOREACH run, lane

#
# For each lane demultiplex rawdata:
#
<#if seqType == "SR">
	
	<#if barcode[0] == "None">
		# Do nothing.
	<#else>
		${demultiplexscript} --bcs '${csv(barcode)}' \
		--mms 1 \
		--mpr1 ${fq} \
		--dmr1 '${csv(fq_barcode)}' \
		--ukr1 ${fq_discarded} \
		--log  ${runIntermediateDir}/demultiplex.log 
	</#if>
		
	<#list fq_barcode as file_to_check>
		# md5sum the demultiplexed file.
		md5sum ${file_to_check} > ${file_to_check}.md5
		
		# gzip the demultiplexed file.
		gzip -c ${file_to_check} > ${file_to_check}.gz 
	</#list>
	
<#elseif seqType == "PE">
	
	<#if barcode[0] == "None">
		# Do nothing.
	<#else>
		${demultiplexscript} --bcs '${csv(barcode)}' \
		--mms 1 \
		--mpr1 ${fq_1} \
		--mpr2 ${fq_2} \
		--dmr1 '${csv(fq_barcode_1)}' \
		--dmr2 '${csv(fq_barcode_2)}' \
		--ukr1 ${fq_discarded_1} \
		--ukr2 ${fq_discarded_2} \
		--log  ${runIntermediateDir}/demultiplex.log 
	</#if>
		
	<#list fq_barcode_1 as file_1_to_check>
		# md5sum the demultiplexed file.
		md5sum ${file_1_to_check} > ${file_1_to_check}.md5
		
		# gzip the demultiplexed file.
		gzip -c ${file_1_to_check} > ${file_1_to_check}.gz 
	</#list>
	
	<#list fq_barcode_2 as file_2_to_check>
		# md5sum the demultiplexed file.
		md5sum ${file_2_to_check} > ${file_2_to_check}.md5
		
		# gzip the demultiplexed file.
		gzip -c ${file_2_to_check} > ${file_2_to_check}.gz 
	</#list>
		
</#if>