#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

<#include "macros.ftl"/>
<@begin/>
#MOLGENIS walltime=45:00:00 mem=10
inputs "${indexfile}"
inputs "${baitintervals}"
inputs "${snpsvariantannotatedvcf}"
inputs "${dbsnpSNPstxt}"
inputs "${refGeneTabletxt}"
inputs "${knownGeneTabletxt}"
inputs "${refLinktxt}"
alloutputsexist "${snpsgenomicannotatedvcf}"

java -Xmx10g -jar ${genomeAnalysisTKjar} \
-T GenomicAnnotator \
-l info \
-R ${indexfile} \
-B:variant,vcf ${snpsvariantannotatedvcf} \
-B:dbsnp,AnnotatorInputTable ${dbsnpSNPstxt} \
-B:refseq,AnnotatorInputTable ${refGeneTabletxt} \
-B:knowngene,AnnotatorInputTable ${knownGeneTabletxt} \
-J reflink,${refLinktxt},reflink.mrnaAcc=refseq.name \
-s dbsnp.haplotypeReference,dbsnp.haplotypeAlternate,dbsnp.haplotypeStrand,\
dbsnp.chromStart,dbsnp.chromEnd,dbsnp.name,dbsnp.score,dbsnp.strand,dbsnp.refNCBI,\
dbsnp.refUCSC,dbsnp.observed,dbsnp.molType,dbsnp.class,dbsnp.valid,dbsnp.avHet,\
dbsnp.avHeSE,dbsnp.func,dbsnp.locType,dbsnp.weight,refseq.name,refseq.name2,\
refseq.transcriptStrand,refseq.positionType,refseq.frame,refseq.mrnaCoord,\
refseq.codonCoord,refseq.spliceDist,refseq.referenceCodon,refseq.referenceAA,\
refseq.variantCodon,refseq.variantAA,refseq.changesAA,refseq.functionalClass,\
refseq.codingCoordStr,refseq.proteinCoordStr,refseq.inCodingRegion,refseq.spliceInfo,\
refseq.uorfChange,knowngene.transcriptStrand,knowngene.positionType,knowngene.mrnaCoord,\
knowngene.codonCoord,knowngene.spliceDist,knowngene.referenceCodon,knowngene.referenceAA,\
knowngene.variantCodon,knowngene.variantAA,knowngene.changesAA,knowngene.functionalClass,\
knowngene.codingCoordStr,knowngene.proteinCoordStr,knowngene.inCodingRegion,\
knowngene.spliceInfo,knowngene.uorfChange \
-o ${snpsgenomicannotatedvcf}
<@end />