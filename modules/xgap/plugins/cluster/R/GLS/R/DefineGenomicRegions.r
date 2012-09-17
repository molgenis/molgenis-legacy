#
# DefineGenomicRegions.R
# - Description: DefineGenomicRegions, DefineGenomicRegionsWithOverlap
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

DefineGenomicRegions <- function(chr.vector,block.size=100) {
  if(missing(chr.vector)) stop("argument 'chr.vector' is missing, with no default")
  
  n.chr  <- length(unique(chr.vector))
  chr.nrs<- sort(unique(chr.vector))
  output.list1 <- list()
  for (i in chr.nrs) {
    j <- which(chr.nrs==i)
    n.marker <- sum(chr.vector==i)
    k1 <- ceiling(n.marker/block.size)
    difference <- round(block.size/2)
    output.list1[[j]] <- data.frame(begin=1+(0:(k1-1))*block.size,end=(1:k1)*block.size)
    output.list1[[j]][nrow(output.list1[[j]]),2] <- n.marker
    output.list1[[j]] <- output.list1[[j]] + sum(chr.vector<i)
  }
  output.list1
}

DefineGenomicRegionsWithOverlap <- function(chr.vector,block.size=100){
  if(missing(chr.vector)) stop("argument 'chr.vector' is missing, with no default")

  if(ceiling(block.size/2)!=block.size/2){
    block.size <- block.size + 1
  }
  n.chr  <- length(unique(chr.vector))
  chr.nrs<- sort(unique(chr.vector))
  output.list1 <- data.frame(begin=integer(0),end=integer(0))
  for(i in chr.nrs){
    j <- which(chr.nrs==i)
    n.marker <- sum(chr.vector==i)
    k <- ceiling(2*n.marker/block.size)
    difference <- block.size/2
    new.output.list <- data.frame(begin=1+(0:(k-1))*(block.size/2),end=block.size+(0:(k-1))*(block.size/2))
    nr <- nrow(new.output.list)
    new.output.list[(nr-1):nr,2] <- n.marker
    new.output.list <- new.output.list + sum(chr.vector<i)
    output.list1 <- rbind(output.list1,new.output.list)
  }
  output.list1
}