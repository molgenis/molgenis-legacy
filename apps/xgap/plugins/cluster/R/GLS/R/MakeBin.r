# to do: remove the temporary files after the bin-file has been created
MakeBin  <- function(markers,kinship.file,csv.file.name,bin.file.name) {
# markers : marker-scores (markers in the rows)
# kinship.file : csv.file containing a kinship matrix
# csv.file.name : csv-file containing the same data as the markers object
# OUTPUT:
# a binary file bin.file.name, containing the marker-data
    varcompfile <- "temp.varcomp.csv"
    MakeVarcompFile(file.name=varcompfile)
    temp.pheno.file <- "temp.pheno.csv"
    MakePhenoFile(pheno.object=data.frame(genotype=rep(names(markers),each=2),temp.trait=rnorm(2*ncol(markers))),col.number=2,file.name=temp.pheno.file)
    output.file <- "temp.output.csv"
    command.string      <- paste("scan_GLS",csv.file.name,temp.pheno.file,kinship.file,varcompfile,output.file,"-writebin",bin.file.name)
    system(command.string, intern = TRUE, ignore.stderr = FALSE,wait = TRUE, input = NULL)
}