#
# =====================================================
# $Id: CreateFinalReport.ftl 11664 2012-04-18 12:17:06Z freerkvandijk $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/Recalibrate.ftl $
# $LastChangedDate: 2012-04-18 14:17:06 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11664 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

#MOLGENIS walltime=47:59:00 mem=2 cores=1
#FOREACH run

umask 0007



perl ${scriptdir}/create_per_sample_finalreport.pl \
-inputdir ${arraydir} \
-outputdir ${runIntermediateDir} \
-run ${run} \
-samplecsv ${worksheet}