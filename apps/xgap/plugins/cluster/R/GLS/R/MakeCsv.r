MakeCsv  <- function(markers,plant.names,file.name) {
# given a matrix of marker-scores (markers in the rows), a vector of genotype names, write the matrix to a csv-file with the name file.name
    blocks <- DefineBlocks(1:nrow(markers),10000)
    cat("",plant.names,"\n",file=file.name,sep=",")
    for (i in 1:length(blocks)) {write.table(markers[blocks[[i]],],file=file.name,quote=F,append=T,col.names =F,sep=",")}
}