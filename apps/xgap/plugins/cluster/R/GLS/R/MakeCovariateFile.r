MakeCovariateFile <- function(pheno.dataframe,cov.cols,file.name="") {
# For a phenotypic data-frame, create a covariate file for scan_GLS, corresponding to the column numbers cov.cols
        cov.frame               <- data.frame(mu=rep(1,nrow(pheno.dataframe)),row.names=row.names(pheno.dataframe))
        if (sum(cov.cols)!=0) {
            new.names <- c(names(cov.frame),names(pheno.dataframe)[cov.cols])
            cov.frame <- cbind(cov.frame,pheno.dataframe[,cov.cols])
            names(cov.frame)  <- new.names
            write.csv(cov.frame,quote=F,row.names=F,file=file.name)
        }
cov.frame
}