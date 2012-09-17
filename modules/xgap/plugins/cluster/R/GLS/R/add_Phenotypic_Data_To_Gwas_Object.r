########## SETTINGS ############
# remove all existing R-objects
rm(list=ls())
gc()
#data.path       <- "D:/willem/statistical_genetics_large_files/arabidopsis_data/test_data/"
#script.path     <- "D:/willem/Dropbox/research/STATISTICAL_GENETICS/"
#setwd(data.path)
#source(paste(script.path,"functions.R",sep=""))

###

# The R-image containing the GWAS-object
#r.image.name      <- "LFN349acc_001.RData" #"atwellData001.RData"
# The R-image in which we will save the new GWAS-object (i.e the old one, with the additional phenotypic data)
#new.r.image.name  <- "Ronny_001.RData"#"myGWASdata.RData" # "atwellData001with_pheno.RData" #"myGWASdata.RData" #"Rik001.RData"

# name of the csv-file that contains the phenotypic data
#csv.file.name   <-  "ZscoretraitsRJ.csv"#"flowering_time_rKooke_reduced.csv"#"rik_full_data.csv"#"simulated_data.csv" #"rik_full_data.csv"
#csv.file.name  <-  "atwell_pheno.csv"   # atwell

########################## START OF THE SCRIPT ################################

#load(r.image.name)
#GWAS.obj <- AddPhenoData(gwas.obj=GWAS.obj,csv.file.name=csv.file.name,add.var.means=F,mean.cols=mean.cols,make.pheno.image=F,pheno.image.name="",add.normal.transform=add.normal.transform)
# atwell : 
#transformed.traits <- c(2:8,24,37:46,70:72)
#for (tr.n in transformed.traits) {
#  if (min(GWAS.obj$pheno[,tr.n],na.rm=T)<=0) {GWAS.obj$pheno[,tr.n] <-   GWAS.obj$pheno[,tr.n]  -  min(GWAS.obj$pheno[,tr.n],na.rm=T) + 1}
#  GWAS.obj$pheno[,tr.n] <- log10(GWAS.obj$pheno[,tr.n])
#  }
#save(GWAS.obj,file=new.r.image.name)

