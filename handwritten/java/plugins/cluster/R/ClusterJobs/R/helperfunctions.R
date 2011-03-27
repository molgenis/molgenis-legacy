#####################################################################
#
# helperfunction.R
#
# copyright (c) 2009, Danny Arends
# last modified Fep, 2009
# first written Feb, 2009
# 
# Part of the ClusterJobs package
# Contains: lodscorevectortoscanone, generateQTLfile, generateESTfile
#
######################################################################


#Change any list of lodscores into a scanone object (only pre-req: length(lodscores)==sum(nmar(cross))
lodscorevectortoscanone <- function(cross,lodscores,traitnames = NULL){
  n <- unlist(lapply(FUN=colnames,pull.map(cross)))
  chr <- NULL
  if(!is.null(ncol(pull.map(cross)[[1]]))){
    d <- as.numeric(unlist(lapply(pull.map(cross),FUN=function(x) {x[1,]})))
    for(i in 1:nchr(cross)){chr <- c(chr,rep(names(cross$geno)[i], ncol(pull.map(cross)[[i]])))}
  }else{
    d <- as.numeric(unlist(pull.map(cross)))
    for(i in 1:nchr(cross)){chr <- c(chr,rep(names(cross$geno)[i], length(pull.map(cross)[[i]])))}
  }
  qtlprofile <- cbind(chr,d,lodscores)
  qtlprofile <- as.data.frame(qtlprofile)
  qtlprofile[,1] <- as.factor(chr)
  qtlprofile[,2] <- as.numeric(d)
  if(!is.null(ncol(lodscores))){
    for(x in 1:ncol(lodscores)){
      qtlprofile[,2+x] <- as.numeric(lodscores[,x])
    }
    traitnames = Names("lod",ncol(lodscores))
  }else{
     qtlprofile[,3] <- as.numeric(lodscores)
     traitnames = "lod"
  }
  rownames(qtlprofile) <- n
  colnames(qtlprofile) <- c("chr","cM",traitnames)
  class(qtlprofile) <- c("scanone", "data.frame")
  qtlprofile
}


batcheffectcheck <- function(cross, cutoff=2){
  pheno <- t(pull.pheno(cross))
  correlation <- cor(pheno)
  tree <- heatmap(correlation, Colv=NA, scale="none", keep.dendro=T)
  branches <- cut(tree$Rowv,cutoff)
  returnlist <- vector("list", length(branches$lower))
  for(x in 1:length(branches$lower)){
    returnlist[[x]] <- unlist(branches$lower[[x]])
    names(returnlist[[x]]) <- colnames(pheno)[unlist(branches$lower[[x]])]
    cat("Group",x,":",paste(colnames(pheno)[unlist(branches$lower[[x]])],";"),"\n")
  }
  invisible(returnlist)
}

batcheffectcorrect <- function(cross, batchlist){
  pheno <- pull.pheno(cross)
  s <- proc.time()
  cormatrix <- NULL
  for(y in 1:ncol(pheno)){
    if(y %% 1000 == 0){
      e <- proc.time()
      cat("Done:",y,"/",ncol(pheno),"in",as.numeric(e[3]-s[3]),"secs\n")
      s <- e
    }
    oamean <- mean(pheno[,y])
    groupmeans <- lapply(lapply(batchlist, fun <- function(x){pheno[x,y]}),mean)
    diffmeans <- unlist(groupmeans) - oamean
    traitcorrection <- rep(0,nrow(pheno))
    for(x in 1:length(diffmeans)){
      traitcorrection[batchlist[[x]]] <- diffmeans[x]
    }
    cat(traitcorrection,"\n",sep="\t",file="tmpbatch.out",append=TRUE)
  }
  cormatrix <- t(read.table("tmpbatch.out",sep="\t"))
  cormatrix <- cormatrix[1:nrow(pheno),1:ncol(pheno)]
}