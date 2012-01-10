#
# =====================================================
# $Id: demultiplex.ftl 10337 2012-01-06 16:55:50Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/demultiplex.ftl $
# $LastChangedDate: 2012-01-06 17:55:50 +0100 (Fri, 06 Jan 2012) $
# $LastChangedRevision: 10337 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#assign runtimelog=runtimelogdemultiplex />
<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=20:00:00 nodes=1 cores=1 mem=10
#INPUTS 
#OUTPUTS
#EXEC
#FOREACH flowcell, lane

inputs "${leftinputfile}"
inputs "${rightinputfile}"
<#include "helpers.ftl"/>
alloutputsexist ${ssvQuoted(leftbarcodefq)} ${ssvQuoted(rightbarcodefq)}


# create directories to put demultiplexed files in
<#list projectrawdatadir as thisoutputdir>
mkdir -p ${thisoutputdir}
</#list>
mkdir -p ${demultiplexinfodir}

# do the work
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
<@end/>