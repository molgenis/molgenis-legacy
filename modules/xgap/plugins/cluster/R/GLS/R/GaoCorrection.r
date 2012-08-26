GaoCorrection <- function(marker.frame,number.of.replicates=rep(1,ncol(marker.frame)),cut.off=0.995,
                               inv.cor.matrix=diag(rep(1,sum(number.of.replicates))),doubleGeno=TRUE) {
# INPUT :
# * marker.frame : markers in the rows; genotypes in the columns. Data from one chromosome !
# * number.of.replicates : a vector of length ncol(marker.frame).
#   For every genotype, it gives the number of observations (individuals of that genotype)
# * inv.cor.matrix : the inverse of the correlation matrix of the individual observations.
#   Should have dimension sum(number.of.replicates) x sum(number.of.replicates)
# * alpha : desired FWE-control
# OUTPUT :
# *
# *
# *
# USES the functions MatrixRoot and DefineBlocks
N         <- nrow(marker.frame)
n.geno    <- ncol(marker.frame)
INVROOT   <- MatrixRoot(inv.cor.matrix)
blocksize <- 2000
matrix.blocks <- DefineBlocks(indices=1:N,block.size=blocksize)
n.blocks      <- length(matrix.blocks)
new.matrix    <- matrix(0,sum(number.of.replicates),N)

for (b in 1:n.blocks) {
  X <- t(as.matrix(marker.frame[matrix.blocks[[b]],rep(1:n.geno,times=number.of.replicates)]))
  if (doubleGeno) {X <- 2*X}
  # premultiply X with the square root of inv.cor.matrix, and transpose
  X <- INVROOT %*% X
  new.matrix[,matrix.blocks[[b]]] <- as.matrix(as.data.frame(lapply(data.frame(X),function(x){(x-mean(x))/sd(x)})))
}
sin.vals   <- svd(new.matrix)$d
eigen.vals <-  sin.vals^2
which(cumsum(eigen.vals)/sum(eigen.vals) > 0.999)
Meff <- min(which(cumsum(eigen.vals)/sum(eigen.vals) > cut.off))

Meff
}

