rm(list=ls())
gc()

#MAF <- c(0,0.01,0.05,0.10)

#maf.strings <- c("000","001","005","010")


# working directory, where all data files reside:
#data.path       <- "D:/willem/statistical_genetics_large_files/arabidopsis_data/LFNdata/"
# "script" directory, containing the R-scripts (recommended: equal to data.path)
#script.path     <- "D:/willem/Dropbox/research/STATISTICAL_GENETICS/"

#
#snp.file.name <- "hapmapsnpfile08022012_without_header.csv"#"hapmapsnpfile_without_header.csv"    # LFN
#snp.file.name <- "call_method_32_without_header.csv"   # atwell
#header.name   <- "hapmapsnpfile08022012_header.csv"#"hapmapsnpfile_header.csv"    # LFN
#header.name   <- "call_method_32_header.csv"  # atwell
#gffFileName  <- "TAIR10_GFF3_genes.gff"
# the reference genotype (typically columbia) is the ref.nr 'th genotype in the list of genotypes
ref.nr       <- 18  # LFN
#ref.nr       <- 1  # Atwell


#setwd(data.path)
#source(paste(script.path,"functions.R",sep=""))

#######################################################################


#for (maf in MAF) {

#gc()
#maf.string <- maf.strings[which(maf==MAF)]
#r.image.name  <- paste("LFN349acc_",maf.string,".RData",sep="") #"LFN349acc001.RData"   #LFN
#description   <- paste("LFN349acc_",maf.string,sep="")
#ReadMarkerAndGeneData2(gffFileName=gffFileName,geno.data=r.image.name,snp.file.name=snp.file.name,header.name=header.name,snp.start.col=3,ref.nr=ref.nr,maf=maf)
#MakeGwasObject2(geno.data=r.image.name,description=description,maf=maf)
#}

