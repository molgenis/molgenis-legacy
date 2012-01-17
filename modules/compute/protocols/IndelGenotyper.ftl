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
#MOLGENIS walltime=33:00:00 mem=8

inputs "${sortedrecalbam}"
inputs "${indexfile}"
alloutputsexist \
 "${indelsvcf}" \
 "${indelsbed}" \
 "${indelsverboseoutput}"

java -Xmx8g -jar ${genomeAnalysisTKjar} \
-l INFO \
-T IndelGenotyperV2 \
-I ${sortedrecalbam} \
-o ${indelsvcf} \
--bedOutput ${indelsbed} \
-R ${indexfile} \
-verbose ${indelsverboseoutput} \
--window_size 300
<@end />