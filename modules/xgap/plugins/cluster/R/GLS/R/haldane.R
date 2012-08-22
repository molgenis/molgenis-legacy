#
# haldane.R
# - Description: ???
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

haldane <- function(x){
  if(missing(x)) stop("argument 'x' is missing, with no default")
  exp(-2*x/100)
}