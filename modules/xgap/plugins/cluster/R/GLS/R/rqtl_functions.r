#
# rqtl_functions.R
# - Description: ???
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

ExtractDesignMatrixFromCross <- function(cross){
  if(missing(cross)) stop("argument 'cross' is missing, with no default")

  regr.frame <- data.frame(simdata=cross$pheno)
  for(i in 1:nchr(cross)){
    n.temp  <- ncol(regr.frame)
    regr.frame <- cbind(regr.frame,as.data.frame(cross$geno[[i]]$data))
    names(regr.frame)[-(1:n.temp)] <- names(pull.map(cross)[[i]])
  }
  regr.frame
}

ExtractPiMassInputFilesFromCross <- function(cross, geno.file="geno_piMASS.txt", pheno.file="pheno_piMASS") {
  if(missing(cross)) stop("argument 'cross' is missing, with no default")
  regr.frame <- ExtractDesignMatrixFromCross(cross)
  geno.frame <- t(2*(regr.frame[,-1]-1)) 
  pheno.vector<- regr.frame[,1]
  pheno.frame<- qqnorm(pheno.vector,plot.it=F)$pheno.vector
  #TODO: This is just plain WRONG, NEVER overwrite files without warning !!!
  write.table(geno.frame,file=geno.file,quote=FALSE,row.names=T,col.names=F,sep=",")
  write.table(pheno.frame,file=pheno.file,quote=FALSE,row.names=F,col.names=F,sep=",")
}
