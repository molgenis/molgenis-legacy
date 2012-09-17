#
# NormalQuantileTransform.R
# - Description: Normalize quantiles
# The normal quantile transform as in Guan and Stephens (2010)
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

NormalQuantileTransform <- function(x, plot.it=FALSE){
  if(missing(x))  stop("argument 'x' is missing, with no default")
  qqnorm(x, plot.it=plot.it)$x
}
