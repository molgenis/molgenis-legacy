#
# IbdCorrection.R
# - Description: ???
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

IbdCorrection  <- function(K, T=0){
  if(missing(K)) stop("argument 'K' is missing, with no default")
  
  return(apply(K-matrix(T,nrow(K),ncol(K)),c(1,2),function(x) max(x,0))/(1-T))
}
