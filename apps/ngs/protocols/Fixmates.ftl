#
# =====================================================
# $Id: Fixmates.ftl 11668 2012-04-18 13:08:24Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/Fixmates.ftl $
# $LastChangedDate: 2012-04-18 15:08:24 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11668 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=35:59:00 mem=6

inputs "${realignedbam}"
alloutputsexist \
 "${matefixedbam}" \
 "${matefixedbamindex}"

java -jar -Xmx6g \
${fixmateinformationjar} \
INPUT=${realignedbam} \
OUTPUT=${matefixedbam} \
SORT_ORDER=coordinate \
VALIDATION_STRINGENCY=SILENT \
TMP_DIR=${tempdir}

java -jar -Xmx3g ${buildbamindexjar} \
INPUT=${matefixedbam} \
OUTPUT=${matefixedbamindex} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}