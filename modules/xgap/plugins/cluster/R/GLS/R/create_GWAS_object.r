#length(unique(GWAS.obj$map$gene1[GWAS.obj$map$chromosome==5]))
rm(list=ls())
gc()
# working directory, where all data files reside:
#data.path       <- "D:/willem/statistical_genetics_large_files/arabidopsis_data/LFNdata/"
#data.path       <- "D:/willem/statistical_genetics_large_files/arabidopsis_data/atwell_data/"
# "script" directory, containing the R-scripts (recommended: equal to data.path)
#script.path     <- "D:/willem/Dropbox/research/STATISTICAL_GENETICS/"
# N.B. DON'T FORGET THE LAST "/"
# N.B. no spaces in the folder or file names !

# The R-image in which the new objects will be saved
#r.image.name  <- "LFN349acc001_new.RData" #"LFN349acc001.RData"   #LFN
#r.image.name  <- "atwellData001.RData"  # atwell
# The minor allele frequency to which the markers should be restricted
#maf           <- 0.01
#
#snp.file.name <- "hapmapsnpfile08022012_without_header.csv"#"hapmapsnpfile_without_header.csv"    # LFN
#snp.file.name <- "call_method_32_without_header.csv"   # atwell
#header.name   <- "hapmapsnpfile08022012_header.csv"#"hapmapsnpfile_header.csv"    # LFN
#header.name   <- "call_method_32_header.csv"  # atwell
#gffFileName  <- "TAIR10_GFF3_genes.gff"
# the reference genotype (typically columbia) is the ref.nr 'th genotype in the list of genotypes
#ref.nr       <- 18  # LFN
#ref.nr       <- 1  # Atwell

#######################################################################

#setwd(data.path)
#source(paste(script.path,"functions.R",sep=""))

#ReadMarkerAndGeneData2(gffFileName=gffFileName,geno.data=r.image.name,snp.file.name=snp.file.name,header.name=header.name,snp.start.col=3,ref.nr=ref.nr,maf=maf)

#################
# close R-session and start again:
#data.path       <- "D:/willem/statistical_genetics_large_files/arabidopsis_data/LFNdata/"
# "script" directory, containing the R-scripts (recommended: equal to data.path)
#script.path     <- "D:/willem/Dropbox/research/STATISTICAL_GENETICS/"
#setwd(data.path)
#source(paste(script.path,"functions.R",sep=""))
##############

# description <- "atwell_geno"
#description   <- "LFN349acc001_new" #LFN
#description   <- "atwellData001" # atwell
#r.image.name  <- "LFN349acc001_new.RData" #LFN
#r.image.name  <- "atwellData001.RData"  # atwell

#MakeGwasObject2(geno.data=r.image.name,description=description,maf=maf)

