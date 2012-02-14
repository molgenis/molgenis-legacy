#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

<#assign runtimelog=runtimelogdemultiplex />
<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=20:00:00 nodes=1 cores=1 mem=10
#FOREACH flowcell, lane

<#if seqType == "SR">
	# Make two symlinks in project rawdata folder
	# This is a temporary hack to cope with "SR" (single read) files
	<#assign leftinputfile  = leftinputfileSR />
	<#assign rightinputfile = rightinputfileSR />
	
	ln -s ${srinputfile} ${leftinputfile}
	ln -s ${srinputfile} ${rightinputfile}
</#if>

inputs "${leftinputfile}"
inputs "${rightinputfile}"
<#include "helpers.ftl"/>
alloutputsexist ${ssvQuoted(leftbarcodefq)} ${ssvQuoted(rightbarcodefq)}


# create directories to put demultiplexed files in
<#list projectrawdatadir as thisoutputdir>
mkdir -p ${thisoutputdir}
</#list>
mkdir -p ${demultiplexinfodir}

# IF barcodes exist THEN demultiplex, ELSE make symlinks
<#if barcode?size == 1 && barcode[0] == "None">
	ln -s ${leftinputfile} ${leftbarcodefq[0]}
	ln -s ${rightinputfile} ${rightbarcodefq[0]}
<#else>
	# demultiplex the lane
	${demultiplexscript} \
	--bc '${csv(barcode)}' \
	--mismatches 1 \
	--left  ${leftinputfile} \
	--right ${rightinputfile} \
	--check \
	--out '${csv(outputleftright)}' \
	--log ${logfile} \
	--discardleft  ${discardleft} \
	--discardright ${discardright}
</#if>

# Clean the mess in case of Single Read data
<#if seqType == "SR">
	<#-- #rename left end files -->
	<#--assign n = barcode?size /-->
	<#--list 0..(n-1) as i>mv ${leftbarcodefq[i]} ${srbarcodefq[i]}-->
	<#--/#list-->
	
	# remove right end files
	<#list rightbarcodefq as rfq>rm ${rfq}
	</#list>
</#if>



<@end/>