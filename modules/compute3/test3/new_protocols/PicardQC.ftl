#
# =====================================================
# $Id: PicardQC.ftl 11222 2012-03-13 15:04:44Z mdijkstra $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/PicardQC.ftl $
# $LastChangedDate: 2012-03-13 16:04:44 +0100 (Tue, 13 Mar 2012) $
# $LastChangedRevision: 11222 $
# $LastChangedBy: mdijkstra $
# =====================================================
#

#MOLGENIS walltime=35:59:00 mem=4

inputs "${sortedbam}"
inputs "${indexfile}"
<#if capturingKit != "None">
inputs ${baitintervals}
inputs ${targetintervals}
#INPUTS sortedbam.*,indexfile,baitintervals,targetintervals,picardjar
<#else>
#INPUTS sortedbam,indexfile
</#if>

#OUTPUTS alignmentmetrics,gcbiasmetrics,gcbiasmetricspdf,insertsizemetrics,insertsizemetricspdf,meanqualitybycycle,meanqualitybycyclepdf,qualityscoredistribution,qualityscoredistributionpdf,hsmetrics,bamindexstats
#LOGS log
#EXES alignmentmetricsjar,gcbiasmetricsjar,insertsizemetricsjar,meanqualitybycyclejar,qualityscoredistributionjar,hsmetricsjar,bamindexstatsjar
#TARGETS

java -jar -Xmx4g ${alignmentmetricsjar} \
I=${sortedbam} \
O=${alignmentmetrics} \
R=${indexfile} \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir}

java -jar ${gcbiasmetricsjar} \
R=${indexfile} \
I=${sortedbam} \
O=${gcbiasmetrics} \
CHART=${gcbiasmetricspdf} \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir}

java -jar ${insertsizemetricsjar} \
I=${sortedbam} \
O=${insertsizemetrics} \
H=${insertsizemetricspdf} \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir}

java -jar ${meanqualitybycyclejar} \
I=${sortedbam} \
O=${meanqualitybycycle} \
CHART=${meanqualitybycyclepdf} \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir}

java -jar ${qualityscoredistributionjar} \
I=${sortedbam} \
O=${qualityscoredistribution} \
CHART=${qualityscoredistributionpdf} \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir}

<#if capturingKit != "None">
	java -jar -Xmx4g ${hsmetricsjar} \
	INPUT=${sortedbam} \
	OUTPUT=${hsmetrics} \
	BAIT_INTERVALS=${baitintervals} \
	TARGET_INTERVALS=${targetintervals} \
	VALIDATION_STRINGENCY=LENIENT \
	TMP_DIR=${tempdir}
<#else>
	echo "## net.sf.picard.metrics.StringHeader" > ${hsmetrics}
	echo "#" >> ${hsmetrics}
	echo "## net.sf.picard.metrics.StringHeader" >> ${hsmetrics}
	echo "#" >> ${hsmetrics}
	echo "" >> ${hsmetrics}
	echo "## METRICS CLASS net.sf.picard.analysis.directed.HsMetrics" >> ${hsmetrics}
	echo "BAIT_SETCS CLASSGENOME_SIZE.sf.pBAIT_TERRITORY.dTARGET_TERRITORYs       BAIT_DESIGN_EFFICIENCY  TOTAL_READS     PF_READS	PF_UNIQUE_READS PCT_PF_READS    PCT_PF_UQ_READS	PF_UQ_READS_ALIGNED	PCT_PF_UQ_READS_ALIGNED	PF_UQ_BASES_ALIGNED	ON_BAIT_BASES	NEAR_BAIT_BASES	OFF_BAIT_BASES	ON_TARGET_BASES	PCT_SELECTED_BASES	PCT_OFF_BAIT	ON_BAIT_VS_SELECTED	MEAN_BAIT_COVERAGE	MEAN_TARGET_COVERAGE	PCT_USABLE_BASES_ON_BAIT	PCT_USABLE_BASES_ON_TARGET	FOLD_ENRICHMENT	ZERO_CVG_TARGETS_PCT	FOLD_80_BASE_PENALTY	PCT_TARGET_BASES_2X	PCT_TARGET_BASES_10X	PCT_TARGET_BASES_20X	PCT_TARGET_BASES_30X	HS_LIBRARY_SIZE	HS_PENALTY_1None    NA_PENALNA_20X	NA_PENALNA_30X	NA_DROPONA	NA_DROPONA	NAMPLE	NABRARY	NAAD_GRONA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA	NA" >> ${hsmetrics}
</#if>

java -jar ${bamindexstatsjar} \
INPUT=${sortedbam} \
VALIDATION_STRINGENCY=LENIENT \
TMP_DIR=${tempdir} \
> ${bamindexstats}
