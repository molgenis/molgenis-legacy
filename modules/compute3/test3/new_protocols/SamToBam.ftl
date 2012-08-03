#
# =====================================================
# $Id: SamToBam.ftl 10962 2012-02-21 09:59:42Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/SamToBam.ftl $
# $LastChangedDate: 2012-02-21 10:59:42 +0100 (Tue, 21 Feb 2012) $
# $LastChangedRevision: 10962 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

#MOLGENIS walltime=35:59:00 mem=3

#INPUTS samfile
#OUTPUTS bamfile
#LOGS log
#EXES samtobamjar
#TARGETS

java -jar -Xmx3g ${samtobamjar} \
INPUT=${samfile} \
OUTPUT=${bamfile} \
VALIDATION_STRINGENCY=LENIENT \
MAX_RECORDS_IN_RAM=2000000 \
TMP_DIR=${tempdir}
