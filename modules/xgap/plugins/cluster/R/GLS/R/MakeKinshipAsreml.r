#
# MakeKinshipAsreml.R
# - Description: ???
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

MakeKinshipAsreml  <- function(K, genotype.names){
  if(missing(K)) stop("argument 'K' is missing, with no default")
  if(missing(genotype.names)) stop("argument 'genotype.names' is missing, with no default")

  n                   <- nrow(K)
  vec1                <- rep(1:n,1:n)
  row.number.matrix   <- matrix(rep(1:n,n),ncol=n)
  vec2                <- row.number.matrix[upper.tri(row.number.matrix,diag=T)]
  matrix.indices      <- matrix(c(vec1,vec2),ncol=2)
  #
  Ainv        <- solve(K)
  AINV        <- data.frame(matrix(0,round(.5*n*(n+1)),3))
  names(AINV) <- c("Row","Column","Ainverse")
  AINV[,1]    <- vec1
  AINV[,2]    <- vec2
  AINV[,3]    <- Ainv[matrix.indices]
  attr(AINV,"rowNames") <-  genotype.names
  AINV 
}
