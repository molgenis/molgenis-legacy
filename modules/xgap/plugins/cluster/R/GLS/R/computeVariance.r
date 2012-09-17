#
# computeVariance.R
# - Description: ???
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

computeVariance <- function(effectsFrame){
  if(missing(effectsFrame)) stop("argument 'effectsFrame' is missing, with no default")
  
  x <- matrix(effectsFrame$location,ncol=nrow(effectsFrame))
  a <- matrix(outer(t(x),x,FUN="-"),ncol=nrow(effectsFrame))
  y <- matrix(effectsFrame$size,ncol=1)
  return(t(y) %*% haldane(abs(a)) %*% y)
}
