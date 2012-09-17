#
# GetResiduals.R
# - Description: ???
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

# INPUT : a vector y and a design matrix or data.frame X, without intercept
#   family = 1 standard regression. family!=1 : logit
# OUTPUT : residuals of regression of y on to X

GetResiduals <- function(y,X,lm.family=1) {
  if(missing(y)) stop("argument 'y' is missing, with no default")
  if(missing(X)) stop("argument 'X' is missing, with no default")
  
  dta <- data.frame(cbind(y,X))
  lm.formula <- paste(names(dta)[1],"~",paste(names(dta)[-1],collapse="+")) 
  
  if(lm.family==1){ #TODO: This is just plain WRONG, unless explained very very well in the documentation !!!
    lm.fit     <- glm(formula=lm.formula,data=dta,family=gaussian(link = "identity"))  
  }else{
    lm.fit     <- glm(formula=lm.formula,data=dta,family=binomial(link = "logit"))
  }
  as.numeric(lm.fit$residuals)
}
