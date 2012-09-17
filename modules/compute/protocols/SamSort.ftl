#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=35:59:00 mem=4

#INPUTS bamfile
#OUTPUTS sortedbam,sortedbamindex
#LOGS log
#EXES sortsamjar,buildbamindexjar
#TARGETS

inputs "${bamfile}"
alloutputsexist \
 "${sortedbam}" \
 "${sortedbamindex}"

java -jar -Xmx3g ${sortsamjar} \
INPUT=${bamfile} \
OUTPUT=${sortedbam} \
SORT_ORDER=coordinate \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}

java -jar -Xmx3g ${buildbamindexjar} \
INPUT=${sortedbam} \
OUTPUT=${sortedbamindex} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=1000000 \
TMP_DIR=${tempdir}