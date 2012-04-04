# Taken from : http://realizationsinbiostatistics.blogspot.com/2008/08/matrix-square-roots-in-r_18.html
MatrixRoot <- function(x) { # assumes that x is symmetric
  x.eig <- eigen(x,symmetric=TRUE)
  x.sqrt <- x.eig$vectors %*% diag(sqrt(x.eig$values)) %*% solve(x.eig$vectors)
  x.sqrt
  }