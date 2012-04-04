GINV    <- function(M) {
# returns a generalized inverse of a matrix M, based on its svd
  svdM    <- svd(M)
svdM$v %*%diag(1/svdM$d)%*% t(svdM$u)
}