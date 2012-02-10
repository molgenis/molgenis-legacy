#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=08:00:00 nodes=1 cores=1 mem=1

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