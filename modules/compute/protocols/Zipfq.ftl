#
# =====================================================
# $Id: zipfq.ftl 10334 2012-01-06 15:11:25Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/zipfq.ftl $
# $LastChangedDate: 2012-01-06 16:11:25 +0100 (Fri, 06 Jan 2012) $
# $LastChangedRevision: 10334 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=10:00:00 nodes=1 cores=1 mem=10 clusterQueue=cluster
#INPUTS 
#OUTPUTS
#EXEC
#FOREACH

alloutputsexist \
 ${leftfilegz} \
 ${rightfilegz}
inputs ${leftbarcodefq} ${rightbarcodefq}

# The following code gzips files and removes original file
# However, in the case of a symlink, the symlink is removed.
gzip -f ${leftbarcodefq}
gzip -f ${rightbarcodefq}
<@end/>