#
# =====================================================
# $Id: CopyFqToRawdatadir.ftl 11669 2012-04-18 13:14:23Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/CopyFqToRawdatadir.ftl $
# $LastChangedDate: 2012-04-18 15:14:23 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11669 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=47:59:00 mem=2 cores=1
#FOREACH run

umask 0007

${gafscripts}/copy_fq_to_rawdatadir.pl \
-rawdatadir ${runIntermediateDir} \
-run ${run} \
-samplecsv ${worksheet}