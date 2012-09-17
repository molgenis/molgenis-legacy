#
# MakeBin.R
# - Description: ???
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

# INPUT:
# * markers : marker-scores (markers in the rows)
# * kinship.file : csv.file containing a kinship matrix
# * csv.file.name : csv-file containing the same data as the markers object
# OUTPUT:
# * a binary file bin.file.name, containing the marker-data
# TODO: Remove the temporary files after the bin-file has been created

MakeBin <- function(markers, kinship.file, csv.file.name, bin.file.name){
  if(missing(markers))       stop("argument 'markers' is missing, with no default")
  if(missing(kinship.file))  stop("argument 'kinship.file' is missing, with no default")
  if(missing(csv.file.name)) stop("argument 'csv.file.name' is missing, with no default")
  if(missing(bin.file.name)) stop("argument 'bin.file.name' is missing, with no default")
  
  varcompfile <- "temp.varcomp.csv"
  MakeVarcompFile(file.name=varcompfile)
  temp.pheno.file <- "temp.pheno.csv"
  MakePhenoFile(pheno.object=data.frame(genotype=rep(names(markers),each=2),temp.trait=rnorm(2*ncol(markers))),col.number=2,file.name=temp.pheno.file)
  output.file <- "temp.output.csv"
  command.string      <- paste("scan_GLS",csv.file.name,temp.pheno.file,kinship.file,varcompfile,output.file,"-writebin",bin.file.name)
  system(command.string, intern = TRUE, ignore.stderr = FALSE,wait = TRUE, input = NULL)
  #?file.delete(temp.pheno.file)
  #?file.delete(varcompfile)
}
