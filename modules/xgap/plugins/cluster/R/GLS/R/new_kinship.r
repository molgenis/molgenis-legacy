############################
# load a GWAS-object

# remove all existing R-objects
rm(list=ls())
gc()
# working directory, where all data files reside:
#data.path       <- "D:/willem/statistical_genetics_large_files/arabidopsis_data/LFNdata/"

# "script" directory, containing the R-scripts (recommended: equal to data.path)
#script.path           <- "D:/willem/Dropbox/research/STATISTICAL_GENETICS/"

#source(paste(script.path,"functions.R",sep=""))
#setwd(data.path)
#load("LFN349acc_005.RData")

####################################
# compute the correlations
# test.size <- GWAS.obj$N-1
# test.cor <- abs(apply(matrix(1:test.size),1,function(x){cor(as.integer(GWAS.obj$markers[x,]),as.integer(GWAS.obj$markers[x+1,]))}))
# test.cor <- c(test.cor,1)
# save(test.cor,file="snpcorrelation.RData")
##############################

#load("snpcorrelations.RData")
# First, a quality check is done: only snps whose correlation with the 2 adjacent snps is at least cor.max, are retained
# From this set we then select snps that are at least LD.dist apart, and that have a correlation of at most cor.min
#cor.max     <- 0.05
#cor.min     <- 0.15   
#LD.dist     <- 20000 # in bp

################################

#test.cor <- c(test.cor,1)
#ind <- test.cor[1:(GWAS.obj$N)]>cor.max
#ind <- (c(T,ind[1:(GWAS.obj$N-1)]) & ind)

#first.selection <- which(ind)

#last.number <- first.selection[1]
#k           <-1
#final.selection <- first.selection[1]
#while (k<length(first.selection)) {
#  k <- k+1
#  if (((GWAS.obj$map$cum.position[first.selection[k]]-GWAS.obj$map$cum.position[last.number])>LD.dist) & (cor(as.numeric(GWAS.obj$markers[first.selection[k],]),as.numeric(GWAS.obj$markers[last.number,])) < cor.min)) {
#    last.number     <- first.selection[k]
#    final.selection <- c(final.selection,last.number)
#  }
#}
#check1 <- abs(apply(matrix(1:length(final.selection)),1,function(x){cor(as.integer(GWAS.obj$markers[final.selection,][x,]),as.integer(GWAS.obj$markers[final.selection,][x+1,]))}))
#check2 <- abs(apply(matrix(1:length(final.selection)),1,function(x){cor(as.integer(GWAS.obj$markers[final.selection,][x,]),as.integer(GWAS.obj$markers[final.selection,][x-1,]))}))

#M         <- length(check1)
#check1[M] <- 0
#ind2      <- check1<cor.min
#ind2      <- (c(T,ind[1:(M-1)]) & ind2)
#ms        <- sum(ind2)

##########################

#marker.names.3669.out.of.LFN349acc_005 <- row.names(GWAS.obj$markers)[final.selection[ind2]]
#marker.numbers.3669.out.of.LFN349acc_005 <- final.selection[ind2]
#save(marker.numbers.3669.out.of.LFN349acc_005,marker.names.3669.out.of.LFN349acc_005,file="kinship_selection.RData")

######################################

#new.kinship      <- IBS(GWAS.obj$markers[final.selection[ind2],])
#kin.file         <- paste("kinship_based_on_",sum(ind2),"_markers.csv",sep="")
#write.table(new.kinship,file=kin.file,quote=F,sep=",",row.names=FALSE,col.names=GWAS.obj$plant.names)

##################################
#GWAS.obj$kinship <- new.kinship
#write.table(GWAS.obj$kinship,file=GWAS.obj$external$kinship.name,quote=F,sep=",",row.names=FALSE,col.names=GWAS.obj$plant.names)
#save(GWAS.obj,file=r.image.name)
######################
#
# Based on the same selection, now look at correlations
#markers.maf.format <- SwitchToMinorAlleleEncoding(GWAS.obj$markers,block.size=5000,two.alleles=F)
#new.kinship        <- cor(markers.maf.format[final.selection[ind2],])
#new.kinship2        <- cor(GWAS.obj$markers[final.selection[ind2],])
#kin.file           <- paste("kinship_based_on_",sum(ind2),"_markers_correlations.csv",sep="")
#write.table(new.kinship,file=kin.file,quote=F,sep=",",row.names=FALSE,col.names=GWAS.obj$plant.names)


# Based on the same selection, now look at ... from synbreed package
#new.kinship        <- cor(markers.maf.format[final.selection[ind2],])
#kin.file           <- paste("kinship_based_on_",sum(ind2),"_markers_correlations.csv",sep="")
#write.table(new.kinship,file=kin.file,quote=F,sep=",",row.names=FALSE,col.names=GWAS.obj$plant.names)

# Based on the same selection, now look at correlations of residuals of pca
#res.matrix         <- markers.maf.format[final.selection[ind2],]
#
#n.pca              <- 7
#GWAS.obj$markers[final.selection[ind2],]
#for (j in 1:ms) {
#  res.matrix[j,]   <- GetResiduals(y=as.numeric(res.matrix[j,]),X=GWAS.obj$pca[,1:n.pca],lm.family=2)
#}
#new.kinship        <- cor(res.matrix)
#kin.file           <- paste("kinship_based_on_",sum(ind2),"_markers_residual_correlation_",n.pca,"pcas.csv",sep="")
#write.table(new.kinship,file=kin.file,quote=F,sep=",",row.names=FALSE,col.names=GWAS.obj$plant.names)
####################################

#heatmap(new.kinship,cexRow=0.1,cexCol=0.1) #file=paste("kinship_based_on_",sum(ind2),"_markers.pdf",sep="")


#heatmap(IBS(GWAS.obj$markers[GWAS.obj$map$chromosome==1,]),cexRow=0.1,cexCol=0.1)
