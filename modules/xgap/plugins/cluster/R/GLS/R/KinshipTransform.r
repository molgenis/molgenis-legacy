#
# KinshipTransform.R
# - Description: ???
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

KinshipTransform <- function(M){
  if(missing(M)) stop("argument 'M' is missing, with no default")
  
  nn <- ncol(M)
  result <- (sum(diag(M))-as.numeric(matrix(1,1,nn) %*% M %*% matrix(1,nn,1))/nn)/(nn-1)
  return(result)
}
