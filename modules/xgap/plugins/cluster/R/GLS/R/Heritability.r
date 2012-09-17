#
# Heritability.R
# - Description: Calculate heritability
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

# INPUT :
# * data.vector :  vector of phenotypic values
# * geno.vector :  vector (character or factor) of genotypes
# OUTPUT :
# heritability, genetic - and residual variance

Heritability <- function(data.vector, geno.vector){
  if(missing(data.vector)) stop("argument 'data.vector' is missing, with no default")
  if(missing(geno.vector)) stop("argument 'geno.vector' is missing, with no default")
  
  her.frame   <- data.frame(data=data.vector,geno=geno.vector)
  her.frame   <- her.frame[!is.na(her.frame$data),]
  means       <- aggregate(her.frame$data, by=list(her.frame$geno),FUN=mean,na.rm =T)[,2]
  vars        <- aggregate(her.frame$data, by=list(her.frame$geno),FUN=var,na.rm =T)[,2]
  list(heritability=var(means,na.rm =T)/(mean(vars,na.rm =T)+var(means,na.rm =T)),gen.variance=var(means,na.rm =T),res.variance=mean(vars,na.rm =T))
}
