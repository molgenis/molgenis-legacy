KinshipTransform <- function(Matrix) {
  nn <- ncol(Matrix)
  (sum(diag(Matrix))-as.numeric(matrix(1,1,nn) %*% Matrix %*% matrix(1,nn,1))/nn)/(nn-1)
}
