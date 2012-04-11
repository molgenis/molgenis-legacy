#
# =====================================================
# $Id$
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/Recalibrate.ftl $
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy: mdijkstra $
# =====================================================
#

#MOLGENIS walltime=45:59:00 mem=12 cores=8
#FOREACH run


# Add to parametercsv
# gafhome		/target/gpfs2/gaf
# gaftools		${gafhome}/tools
# gafscripts	${gaftools}/scripts

perl ${gafscripts}/run_GERALD.pl \
-run ${run} \
-samplecsv ${completeWorksheet}