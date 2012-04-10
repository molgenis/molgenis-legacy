#
# MakePhenoFile.R
# - Description: Create a phenotype file
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

MakePhenoFile <- function(pheno.object, file.name, col.number) {
  if(missing(pheno.object))  stop("argument 'pheno.object' is missing, with no default")
  if(missing(file.name))     stop("argument 'file.name' is missing, with no default")
  if(missing(col.number))    stop("argument 'col.number' is missing, with no default")

  # For a phenotypic data-frame containing at least a "genotype" column and at least one trait,
  # create a covariate file for scan_GLS, corresponding to the column numbers cov.cols
  pheno.frame <- data.frame(pheno.object$genotype,pheno.object[,col.number])
  names(pheno.frame)  <- c("genotype", names(pheno.object)[col.number])
  write.csv(pheno.frame,quote=F,file=file.name)
}
