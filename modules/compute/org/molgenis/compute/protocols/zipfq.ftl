#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=10:00:00 nodes=1 cores=1 mem=10 clusterQueue=cluster
#INPUTS 
#OUTPUTS
#EXEC
#FOREACH

# The following code gzips files and removes original file
# However, in the case of a symlink, the symlink is removed.
gzip -f ${leftbarcode}
gzip -f ${rightbarcode}