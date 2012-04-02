scan.GLS  <- function(gwas.obj,input.pheno,varcomp.file,output.file,covariate.file="") {
# cov.string  is the string added to the scan.GLS command
#
cov.string      <- ""
if (covariate.file!="") {cov.string  <- paste("-fixed",covariate.file)}
if (file.exists(gwas.obj$external$bin.name)) {
  command.string      <- paste("scan_GLS",gwas.obj$external$bin.name,input.pheno,
                               gwas.obj$external$kinship.name,varcomp.file,output.file,cov.string)
  system(command.string, intern = TRUE, ignore.stderr = FALSE,wait = TRUE, input = NULL)
} else {
  gwas.obj$external$bin.name  <- paste(substr(gwas.obj$external$csv.name,1,nchar(gwas.obj$external$csv.name)-4),".bin",sep="")
  command.string      <- paste("scan_GLS",gwas.obj$external$csv.name,input.pheno,gwas.obj$external$kinship.name,
                               varcomp.file,output.file,cov.string,"-writebin",gwas.obj$external$bin.name)
  system(command.string, intern = TRUE, ignore.stderr = FALSE,wait = TRUE, input = NULL)
}

gwa.result <- ReadGwaResult(gwas.obj=gwas.obj,output.file=output.file)
gwa.result
}