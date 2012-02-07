#
# =====================================================
# $Id$
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/Zipfq.ftl $
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy: pneerincx $
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