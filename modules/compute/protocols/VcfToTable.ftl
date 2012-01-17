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
#MOLGENIS walltime=00:40:00
inputs "${snpsgenomicannotatedfilteredvcf}"
inputs "${snpsgenomicannotatedvcf}"
alloutputsexist \
 "${snpsgenomicannotatedfilteredvcf}.table" \
 "${snpsgenomicannotatedvcf}.table"

#Convert filtered vcf to table
python ${tooldir}/GATK-1.0.5069/Sting/python/vcf2table.py \
${snpsgenomicannotatedfilteredvcf} \
-f CHROM,POS,ID,REF,ALT,QUAL,FILTER,AB,AC,AF,ALTFWD,ALTREV,AN,\
BaseCounts,BaseQRankSum,DB,DP,DS,Dels,FS,GC,HRun,HW,HaplotypeScore,\
LowMQ,MQ,MQ0,MQRankSum,QD,REFFWD,REFREV,SB,SBD,dbsnp.haplotypeReference,\
dbsnp.haplotypeAlternate,dbsnp.haplotypeStrand,dbsnp.chromStart,\
dbsnp.chromEnd,dbsnp.name,dbsnp.score,dbsnp.strand,dbsnp.refNCBI,\
dbsnp.refUCSC,dbsnp.observed,dbsnp.molType,dbsnp.class,dbsnp.valid,\
dbsnp.avHet,dbsnp.avHeSE,dbsnp.func,dbsnp.locType,dbsnp.weight,\
knowngene.transcriptStrand,knowngene.positionType,knowngene.spliceDist,knowngene.referenceCodon,\
knowngene.referenceAA,knowngene.variantCodon,knowngene.variantAA,\
knowngene.changesAA,knowngene.functionalClass,knowngene.codingCoordStr,\
knowngene.proteinCoordStr,knowngene.inCodingRegion,knowngene.spliceInfo,\
refseq.name,refseq.name2,refseq.transcriptStrand,refseq.positionType,\
refseq.frame,refseq.mrnaCoord,refseq.codonCoord,refseq.spliceDist,\
refseq.referenceCodon,refseq.referenceAA,refseq.variantCodon,refseq.variantAA,\
refseq.changesAA,refseq.functionalClass,refseq.codingCoordStr,\
refseq.proteinCoordStr,refseq.inCodingRegion,refseq.spliceInfo,\
refseq.uorfChange,FORMAT,${externalSampleID} \
-o ${snpsgenomicannotatedfilteredvcf}.table


#Convert unfiltered vcf to table
python ${tooldir}/GATK-1.0.5069/Sting/python/vcf2table.py \
${snpsgenomicannotatedvcf} \
-f CHROM,POS,ID,REF,ALT,QUAL,FILTER,AB,AC,AF,ALTFWD,ALTREV,AN,\
BaseCounts,BaseQRankSum,DB,DP,DS,Dels,FS,GC,HRun,HW,HaplotypeScore,\
LowMQ,MQ,MQ0,MQRankSum,QD,REFFWD,REFREV,SB,SBD,dbsnp.haplotypeReference,\
dbsnp.haplotypeAlternate,dbsnp.haplotypeStrand,dbsnp.chromStart,\
dbsnp.chromEnd,dbsnp.name,dbsnp.score,dbsnp.strand,dbsnp.refNCBI,\
dbsnp.refUCSC,dbsnp.observed,dbsnp.molType,dbsnp.class,dbsnp.valid,\
dbsnp.avHet,dbsnp.avHeSE,dbsnp.func,dbsnp.locType,dbsnp.weight,\
knowngene.transcriptStrand,knowngene.positionType,knowngene.spliceDist,knowngene.referenceCodon,\
knowngene.referenceAA,knowngene.variantCodon,knowngene.variantAA,\
knowngene.changesAA,knowngene.functionalClass,knowngene.codingCoordStr,\
knowngene.proteinCoordStr,knowngene.inCodingRegion,knowngene.spliceInfo,\
refseq.name,refseq.name2,refseq.transcriptStrand,refseq.positionType,\
refseq.frame,refseq.mrnaCoord,refseq.codonCoord,refseq.spliceDist,\
refseq.referenceCodon,refseq.referenceAA,refseq.variantCodon,refseq.variantAA,\
refseq.changesAA,refseq.functionalClass,refseq.codingCoordStr,\
refseq.proteinCoordStr,refseq.inCodingRegion,refseq.spliceInfo,\
refseq.uorfChange,FORMAT,${externalSampleID} \
-o ${snpsgenomicannotatedvcf}.table
<@end />