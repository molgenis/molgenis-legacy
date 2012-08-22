#
# GINV.R
# - Description: Generalized inverse of a matrix M, based on its single value decomposition
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

GINV    <- function(M) {
  if(missing(M)) stop("argument 'M' is missing, with no default")
  
  svdM    <- svd(M)
  svdM$v %*%diag(1/svdM$d)%*% t(svdM$u)
  svdM
}
