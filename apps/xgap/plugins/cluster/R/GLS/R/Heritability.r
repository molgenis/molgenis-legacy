Heritability <- function(data.vector,geno.vector)   {
# INPUT :
# * data.vector :  vector of phenotypic values
# * geno.vector :  vector (character or factor) of genotypes
# OUTPUT :
# heritability, genetic - and residual variance
    her.frame   <- data.frame(data=data.vector,geno=geno.vector)
    her.frame   <- her.frame[!is.na(her.frame$data),]
    means       <- aggregate(her.frame$data, by=list(her.frame$geno),FUN=mean,na.rm =T)[,2]
    vars        <- aggregate(her.frame$data, by=list(her.frame$geno),FUN=var,na.rm =T)[,2]
    list(heritability=var(means,na.rm =T)/(mean(vars,na.rm =T)+var(means,na.rm =T)),gen.variance=var(means,na.rm =T),res.variance=mean(vars,na.rm =T))
}
