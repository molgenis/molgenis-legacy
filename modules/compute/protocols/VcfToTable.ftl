#
# =====================================================
# $Id$
# $URL$
# $LastChangedDate$
# $LastChangedRevision$
# $LastChangedBy$
# =====================================================
#

#MOLGENIS walltime=00:40:00
#FOREACH externalSampleID

inputs "${snpsfinalvcf}"
alloutputsexist "${snpsfinalvcftable}" "${snpsfinalvcftabletype}" "${snpsfinalvcftableclass}" "${snpsfinalvcftableimpact}"

####Transform VCF file into tabular file####
python ${vcf2tablepy} \
${snpsfinalvcf} \
-f CHROM,POS,ID,REF,ALT,QUAL,FILTER,AB,AC,AF,ALTFWD,ALTREV,AN,\
BaseCounts,BaseQRankSum,DB,DP,DS,Dels,FS,GC,HRun,HW,HaplotypeScore,\
LowMQ,MQ,MQ0,MQ0Fraction,MQRankSum,PercentNBaseSolid,QD,ReadPosRankSum,REFFWD,REFREV,SB,SBD,\
SNPEFF_AMINO_ACID_CHANGE,SNPEFF_CODON_CHANGE,SNPEFF_EFFECT,SNPEFF_EXON_ID,SNPEFF_FUNCTIONAL_CLASS,\
SNPEFF_GENE_BIOTYPE,SNPEFF_GENE_NAME,SNPEFF_IMPACT,SNPEFF_TRANSCRIPT_ID,Samples,TDT,\
dbSNP132.AF,dbSNP132.ASP,dbSNP132.ASS,dbSNP132.CDA,dbSNP132.CFL,dbSNP132.CLN,dbSNP132.DSS,dbSNP132.G5,\
dbSNP132.G5A,dbSNP132.GCF,dbSNP132.GMAF,dbSNP132.GNO,dbSNP132.HD,dbSNP132.INT,dbSNP132.KGPROD,dbSNP132.KGPilot1,dbSNP132.KGPilot123,\
dbSNP132.KGVAL,dbSNP132.LSD,dbSNP132.MTP,dbSNP132.MUT,dbSNP132.NOC,dbSNP132.NOV,dbSNP132.NS,dbSNP132.NSF,dbSNP132.NSM,dbSNP132.OM,\
dbSNP132.OTH,dbSNP132.PH1,dbSNP132.PH2,dbSNP132.PH3,dbSNP132.PM,dbSNP132.PMC,dbSNP132.R3,dbSNP132.R5,dbSNP132.REF,dbSNP132.RSPOS,\
dbSNP132.RV,dbSNP132.S3D,dbSNP132.SAO,dbSNP132.SCS,dbSNP132.SLO,dbSNP132.SSR,dbSNP132.SYN,dbSNP132.TPA,dbSNP132.U3,dbSNP132.U5,dbSNP132.VC,\
dbSNP132.VLD,dbSNP132.VP,dbSNP132.WGT,dbSNP132.WTD,dbSNP132.dbSNPBuildID,\
sumGLbyD,\
FORMAT,${externalSampleID} \
-o ${snpsfinalvcftable}

# get SNP statistics
perl ${snpannotationstatspl} \
-vcf_table ${snpsfinalvcftable} \
-typefile ${snpsfinalvcftabletype} \
-classfile ${snpsfinalvcftableclass} \
-impactfile ${snpsfinalvcftableimpact} \
-snptypes DOWNSTREAM,INTERGENIC,INTRAGENIC,INTRON,NON_SYNONYMOUS_CODING,NON_SYNONYMOUS_START,SPLICE_SITE_ACCEPTOR,SPLICE_SITE_DONOR,START_GAINED,START_LOST,STOP_GAINED,STOP_LOST,SYNONYMOUS_CODING,SYNONYMOUS_STOP,UPSTREAM,UTR_3_PRIME,UTR_5_PRIME \
-snpclasses MISSENSE,NONSENSE,NONE,SILENT \
-snpimpacts HIGH,LOW,MODERATE,MODIFIER