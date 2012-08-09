########## SETTINGS ############
# remove all existing R-objects
rm(list=ls())
gc()
#data.path       <- "D:/willem/statistical_genetics_large_files/arabidopsis_data/LFNdata/"
#script.path     <- "D:/willem/Dropbox/research/STATISTICAL_GENETICS/"
#setwd(data.path)
#source(paste(script.path,"functions.R",sep=""))

###

# The R-image containing the GWAS-object
#r.image.name      <- "LFN349acc_001.RData"


# The R-image in which we will save the new GWAS-object (i.e the old one, with the additional phenotypic data)
#new.r.image.name  <- "simulations.RData"

#n.rep       <- 3
#n.trait     <- 100

#load(r.image.name)
#pheno.frame <- data.frame(matrix(0,n.rep*GWAS.obj$n,n.trait+1))


#GWAS.obj <- AddPhenoData(gwas.obj=GWAS.obj,csv.file.name=csv.file.name,add.var.means=F,mean.cols=mean.cols,make.pheno.image=F,pheno.image.name="",add.normal.transform=add.normal.transform)
#save(GWAS.obj,file=new.r.image.name)


########################## START OF THE SCRIPT ################################



