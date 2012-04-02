GenomicControlPvalues <- function(pvals,n.obs,n.cov=0) {
# OBJECTIVE : correction of p-values based on the genomic inflation factor, as in Devlin and Roeder (1999?)
# INPUT :
# * pvals : vector of p-values
# * n.obs : number of individuals
# * n.cov : number of covariables
# OUTPUT :
# * the new, corrected p-values
# * genomic inflation factor
#
# assumes p-values from an F-test with df=1 and df2=n.obs-n.cov-2
    F.stats     <- qf(pvals, df1=1, df2=n.obs-n.cov-2,lower.tail=F)
    inflation   <- median(F.stats)/qf(0.5, df1=1, df2=n.obs-n.cov-2,lower.tail=F)
    F.stats     <- F.stats /inflation
    new.pvals   <- pf(F.stats, df1=1, df2=n.obs-n.cov-2,lower.tail=F)
list(pvalues=new.pvals, inflation.factor=inflation)
}