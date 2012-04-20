#
# =====================================================
# $Id: SamToBam.ftl 11668 2012-04-18 13:08:24Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/SamToBam.ftl $
# $LastChangedDate: 2012-04-18 15:08:24 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11668 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=35:59:00 mem=3

inputs "${samfile}"
alloutputsexist "${bamfile}"

java -jar -Xmx3g ${samtobamjar} \
INPUT=${samfile} \
OUTPUT=${bamfile} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=2000000 \
TMP_DIR=${tempdir}