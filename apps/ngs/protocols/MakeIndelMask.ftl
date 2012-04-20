#
# =====================================================
# $Id: MakeIndelMask.ftl 11668 2012-04-18 13:08:24Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/MakeIndelMask.ftl $
# $LastChangedDate: 2012-04-18 15:08:24 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11668 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=40:00:00
#FOREACH externalSampleID

inputs "${indelsfilteredbed}"
alloutputsexist "${indelsmaskbed}"

python ${makeIndelMaskpyton} \
${indelsfilteredbed} \
10 \
${indelsmaskbed}