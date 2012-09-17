#
# =====================================================
# $Id: AnalyzeCovariates.ftl 11668 2012-04-18 13:08:24Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/AnalyzeCovariates.ftl $
# $LastChangedDate: 2012-04-18 15:08:24 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11668 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=00:45:00

inputs "${matefixedcovariatecsv}"
inputs "${sortedrecalcovariatecsv}"
alloutputsexist \
"${cyclecovariatebefore}" \
"${cyclecovariateafter}"

export PATH=${R_HOME}/bin:<#noparse>${PATH}</#noparse>
export R_HOME=${R_HOME}
export R_LIBS=${R_LIBS} 

java -jar -Xmx4g ${analyzecovariatesjar} -l INFO \
-resources ${indexfile} \
--recal_file ${matefixedcovariatecsv} \
-outputDir ${recalstatsbeforedir} \
-Rscript ${rscript} \
-ignoreQ 5

java -jar -Xmx4g ${analyzecovariatesjar} -l INFO \
-resources ${indexfile} \
--recal_file ${sortedrecalcovariatecsv} \
-outputDir ${recalstatsafterdir} \
-Rscript ${rscript} \
-ignoreQ 5