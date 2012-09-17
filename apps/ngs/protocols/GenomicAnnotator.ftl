#
# =====================================================
# $Id: GenomicAnnotator.ftl 11668 2012-04-18 13:08:24Z pneerincx $
# $URL: http://www.molgenis.org/svn/molgenis_apps/trunk/modules/compute/protocols/GenomicAnnotator.ftl $
# $LastChangedDate: 2012-04-18 15:08:24 +0200 (Wed, 18 Apr 2012) $
# $LastChangedRevision: 11668 $
# $LastChangedBy: pneerincx $
# =====================================================
#

#MOLGENIS walltime=24:00:00 mem=10
#FOREACH externalSampleID

inputs "${indexfile}"
inputs "${baitsbed}"
inputs "${dbsnpSNPstxt}"
inputs "${snpsvcf}"
alloutputsexist "${snpsgenomicannotatedvcf}"

#####Annotate with dbSNP132 SNPs only#####
java -Xmx10g -jar ${genomeAnalysisTKjar} \
-T GenomicAnnotator \
-l info \
-R ${indexfile} \
-B:variant,vcf ${snpsvcf} \
-B:dbSNP132,AnnotatorInputTable ${dbsnpSNPstxt} \
-s dbSNP132.AF,dbSNP132.ASP,dbSNP132.ASS,dbSNP132.CDA,dbSNP132.CFL,dbSNP132.CLN,dbSNP132.DSS,dbSNP132.G5,\
dbSNP132.G5A,dbSNP132.GCF,dbSNP132.GMAF,dbSNP132.GNO,dbSNP132.HD,dbSNP132.INT,dbSNP132.KGPROD,dbSNP132.KGPilot1,dbSNP132.KGPilot123,\
dbSNP132.KGVAL,dbSNP132.LSD,dbSNP132.MTP,dbSNP132.MUT,dbSNP132.NOC,dbSNP132.NOV,dbSNP132.NS,dbSNP132.NSF,dbSNP132.NSM,dbSNP132.OM,\
dbSNP132.OTH,dbSNP132.PH1,dbSNP132.PH2,dbSNP132.PH3,dbSNP132.PM,dbSNP132.PMC,dbSNP132.R3,dbSNP132.R5,dbSNP132.REF,dbSNP132.RSPOS,\
dbSNP132.RV,dbSNP132.S3D,dbSNP132.SAO,dbSNP132.SCS,dbSNP132.SLO,dbSNP132.SSR,dbSNP132.SYN,dbSNP132.TPA,dbSNP132.U3,dbSNP132.U5,dbSNP132.VC,\
dbSNP132.VLD,dbSNP132.VP,dbSNP132.WGT,dbSNP132.WTD,dbSNP132.dbSNPBuildID \
-o ${snpsgenomicannotatedvcf} \
-L ${baitsbed}