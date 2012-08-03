#
# =====================================================
# $Id: SamSort.ftl 10962 2012-02-21 09:59:42Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/SamSort.ftl $
# $LastChangedDate: 2012-02-21 10:59:42 +0100 (Tue, 21 Feb 2012) $
# $LastChangedRevision: 10962 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

#MOLGENIS walltime=35:59:00 mem=4

#INPUTS bamfile
#OUTPUTS sortedbam.*
#LOGS log
#EXES sortsamjar,buildbamindexjar
#TARGETS

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
