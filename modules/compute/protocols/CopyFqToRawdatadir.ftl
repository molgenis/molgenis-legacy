#
# =====================================================
# $Id$
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/Recalibrate.ftl $
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy: mdijkstra $
# =====================================================
#

#MOLGENIS walltime=47:59:00 mem=2 cores=1
#FOREACH run


${gafscripts}/copy_fq_to_rawdatadir.pl \
-rawdatadir ${rawdatadir} \
-run ${run} \
-samplecsv ${completeWorksheet}