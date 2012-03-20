DefineBlocks  <- function(indices,block.size=1000) {
    nsnp            <- length(indices)
    if (nsnp<=block.size) {
      blocks  <- list(indices)
    } else {
      blocks          <- NULL
      nbl             <- ceiling(nsnp/block.size)
      if (nbl== nsnp/block.size) {
          for (i in 1:nbl) {blocks[[i]]   <- indices[(i-1)*block.size + 1:block.size]}
      } else {
          for (i in 1:(nbl-1)) {blocks[[i]]   <- indices[(i-1)*block.size + 1:block.size]}
          blocks[[nbl]]   <- indices[-(1:((nbl-1)*block.size))]
      }   
    }
blocks
}