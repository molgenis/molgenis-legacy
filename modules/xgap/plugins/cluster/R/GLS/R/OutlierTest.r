#
# OutlierTest.R
# - Description: Test for outliers
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

OutlierTest <- function(x,n_SD=3){
  if(missing(x)) stop("argument 'x' is missing, with no default")
  
  st.dev  <- sd(x,na.rm=TRUE)
  avg     <- mean(x,na.rm=TRUE)
  outliers<- which(x < (avg - n_SD*st.dev) | x > (avg + n_SD*st.dev))
  list(outlier.values=x[outliers],outliers=outliers)
}
