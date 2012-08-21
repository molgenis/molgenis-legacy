#
# DefineBlocks.R
# - Description: Compute False Discovery Proportion under the full (strong) null
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

DefineBlocks  <- function(indices, block.size=1000) {
  if(missing(indices)) stop("argument 'indices' is missing, with no default")
 
  nsnp            <- length(indices)
  if(nsnp<=block.size){
    blocks <- list(indices)
  }else{
    blocks <- NULL
    nbl <- ceiling(nsnp/block.size)
    if(nbl== nsnp/block.size){
      for(i in 1:nbl){
        blocks[[i]]   <- indices[(i-1)*block.size + 1:block.size]
      }
    }else{
      for(i in 1:(nbl-1)){
        blocks[[i]]   <- indices[(i-1)*block.size + 1:block.size]
      }
      blocks[[nbl]]   <- indices[-(1:((nbl-1)*block.size))]
    }   
  }
  blocks
}
