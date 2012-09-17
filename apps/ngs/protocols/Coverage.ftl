#
# =====================================================
# $Id: Coverage.ftl 11668 2012-04-18 13:08:24Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/Coverage.ftl $
# $LastChangedDate: 2012-04-18 15:08:24 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11668 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=65:59:00 mem=12 cores=1
#FOREACH externalSampleID

inputs "${mergedbam}"
alloutputsexist "${sample}.coverage.csv" \
"${samplecoverageplotpdf}" \
"${sample}.coverage.Rdata"

export R_HOME=${R_HOME}
export PATH=${R_HOME}/bin:<#noparse>${PATH}</#noparse>
export R_LIBS=${R_LIBS} 

${coveragescript} \
--bam ${mergedbam} \
--chromosome 1 \
--interval_list ${targetintervals} \
--csv ${sample}.coverage.csv \
--pdf ${samplecoverageplotpdf} \
--Rcovlist ${sample}.coverage.Rdata
