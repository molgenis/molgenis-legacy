#
# =====================================================
# $Id: MergeBam.ftl 11669 2012-04-18 13:14:23Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/MergeBam.ftl $
# $LastChangedDate: 2012-04-18 15:14:23 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11669 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=23:59:00 mem=6 cores=2
#FOREACH externalSampleID

<#if sortedrecalbam?size == 1>
	#cp ${sortedrecalbam[0]} ${mergedbam}
	#cp ${sortedrecalbam[0]}.bai ${mergedbamindex}
	ln -s ${sortedrecalbam[0]} ${mergedbam}
	ln -s ${sortedrecalbam[0]}.bai ${mergedbamindex}
<#else>
	java -jar -Xmx6g ${mergesamfilesjar} \
	<#list sortedrecalbam as srb>INPUT=${srb} \
	</#list>
	ASSUME_SORTED=true USE_THREADING=true \
	TMP_DIR=${tempdir} MAX_RECORDS_IN_RAM=6000000 \
	OUTPUT=${mergedbam} \
	SORT_ORDER=coordinate \
	VALIDATION_STRINGENCY=SILENT
	
	java -jar -Xmx3g ${buildbamindexjar} \
	INPUT=${mergedbam} \
	OUTPUT=${mergedbamindex} \
	VALIDATION_STRINGENCY=LENIENT \
	MAX_RECORDS_IN_RAM=1000000 \
	TMP_DIR=${tempdir}
</#if>