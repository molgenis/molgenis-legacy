#
# is.installed.R
# - Description: Check for installed packages
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

is.installed <- function(mypkg){
  if(missing(mypkg)) stop("argument 'mypkg' is missing, with no default")
  is.element(mypkg, installed.packages()[,1])
}
