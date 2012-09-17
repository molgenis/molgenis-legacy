#
# MatrixRoot.R
# - Description: Calculate Matrix Square Roots in R
# Taken from : http://realizationsinbiostatistics.blogspot.com/2008/08/matrix-square-roots-in-r_18.html
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

MatrixRoot <- function(x) { # assumes that x is symmetric
  if(missing(x))     stop("argument 'x' is missing, with no default")
  x.eig <- eigen(x,symmetric=TRUE)
  x.sqrt <- x.eig$vectors %*% diag(sqrt(x.eig$values)) %*% solve(x.eig$vectors)
  x.sqrt
}
