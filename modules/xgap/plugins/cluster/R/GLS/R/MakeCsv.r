#
# MakeCsv.R
# - Description: Given a matrix of marker-scores (markers in the rows), a vector of genotype names, write the matrix to a csv-file with the name file.name
# 
# Copyright (c) 2012 Willem Kruijer and Danny Arends

MakeCsv  <- function(markers,plant.names,file.name){
  if(missing(markers))      stop("argument 'markers' is missing, with no default")
  if(missing(plant.names))  stop("argument 'plant.names' is missing, with no default")
  if(missing(file.name))    stop("argument 'file.name' is missing, with no default")
  
  blocks <- DefineBlocks(1:nrow(markers),10000)
  cat("",plant.names,"\n",file=file.name,sep=",")
  for (i in 1:length(blocks)){
    write.table(markers[blocks[[i]],],file=file.name, quote=FALSE, append=TRUE, col.names=FALSE, sep=",")
  }
}
