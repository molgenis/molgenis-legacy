MakePhenoFile <- function(pheno.object,file.name,col.number=2) {
# For a phenotypic data-frame containing at least a "genotype" column and at least one trait,
# create a covariate file for scan_GLS, corresponding to the column numbers cov.cols
    pheno.frame <- data.frame(pheno.object$genotype,pheno.object[,col.number])
    names(pheno.frame)  <- c("genotype",names(pheno.object)[col.number])
    write.csv(pheno.frame,quote=F,file=file.name)
}
