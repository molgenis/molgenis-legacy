IBS     <- function(X,normalization=nrow(X)) {
# input: a p x n matrix of 0-1 marker-scores; markers in the rows; individuals in the columns
# output: a matrix K with elements K_{ij}, defined as the number of markers for which individuals i and j are identical by state, divided by the total number of markers
                X       <- as.matrix(X)
                Y       <- X
                Y[X==0] <- 1
                Y[X==1] <- 0
                (t(X) %*% X + t(Y) %*% Y)/normalization
                }