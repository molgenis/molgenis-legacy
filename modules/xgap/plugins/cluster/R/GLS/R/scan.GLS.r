#
# scan.GLS.R
# - Description: Interface for scanning QTL using scan_GLS commanline executable
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

scan.GLS  <- function(gwas.obj, input.pheno, varcomp.file, output.file, covariate.file){
  #Check if the user supplied all the arguments we expect
  if(missing(gwas.obj))     stop("argument 'gwas.obj' is missing, with no default")
  if(missing(input.pheno))  stop("argument 'input.pheno' is missing, with no default")
  if(missing(varcomp.file)) stop("argument 'varcomp.file' is missing, with no default")
  if(missing(output.file))  stop("argument 'output.file' is missing, with no default")
  
  cov.string <- "" # cov.string is the string added to the scan.GLS command
  if(!missing(covariate.file)){
    cov.string  <- paste("-fixed",covariate.file)
  }
  if(file.exists(gwas.obj$external$bin.name)){
    command.string <- paste("scan_GLS",gwas.obj$external$bin.name,input.pheno, gwas.obj$external$kinship.name,varcomp.file,output.file,cov.string)
  }else{
    gwas.obj$external$bin.name  <- paste(substr(gwas.obj$external$csv.name,1,nchar(gwas.obj$external$csv.name)-4),".bin",sep="")
    command.string <- paste("scan_GLS",gwas.obj$external$csv.name,input.pheno,gwas.obj$external$kinship.name, varcomp.file,output.file,cov.string,"-writebin",gwas.obj$external$bin.name)
  }
  system(command.string, intern = TRUE, ignore.stderr = FALSE,wait = TRUE, input = NULL)
  gwa.result <- ReadGwaResult(gwas.obj=gwas.obj,output.file=output.file)
  gwa.result
}