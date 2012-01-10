#
# =====================================================
# $Id: fastqc.ftl 10198 2011-12-20 08:34:26Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/fastqc.ftl $
# $LastChangedDate: 2011-12-20 09:34:26 +0100 (Tue, 20 Dec 2011) $
# $LastChangedRevision: 10198 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=08:00:00 nodes=1 cores=1 mem=1
#INPUTS 
#OUTPUTS
#EXEC
#FOREACH

inputs "${leftfilegz}"
inputs "${rightfilegz}"

alloutputsexist \
 "${leftfastqczip}" \
 "${leftfastqcsummarytxt}" \
 "${leftfastqcsummarylog}" \
 "${rightfastqczip}" \
 "${rightfastqcsummarytxt}" \
 "${rightfastqcsummarylog}"

# first make logdir...
mkdir -p "${intermediatedir}"

# pair1
${fastqcjar} ${leftfilegz} \
-Dfastqc.output_dir=${intermediatedir} \
-Dfastqc.unzip=false

# pair2
${fastqcjar} ${rightfilegz} \
-Dfastqc.output_dir=${intermediatedir} \
-Dfastqc.unzip=false
<@end/>