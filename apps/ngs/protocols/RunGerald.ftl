#
# =====================================================
# $Id: RunGerald.ftl 11669 2012-04-18 13:14:23Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/RunGerald.ftl $
# $LastChangedDate: 2012-04-18 15:14:23 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11669 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=45:59:00 mem=12 cores=8
#FOREACH run



perl ${gafscripts}/run_GERALD.pl \
-run ${run} \
-samplecsv ${worksheet}