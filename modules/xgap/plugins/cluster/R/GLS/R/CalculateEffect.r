#
# CalculateEffect.R
# - Description: Compute False Discovery Proportion under the full (strong) null
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

CalculateEffect <- function(gwas.obj, snp.name, trait.number){
  if(missing(gwas.obj))     stop("argument 'gwas.obj' is missing, with no default")
  if(missing(snp.name))     stop("argument 'snp.name' is missing, with no default")
  if(missing(trait.number)) stop("argument 'trait.number' is missing, with no default")
  
  effect.size <- NA
  if (!is.na(mean(gwas.obj$pheno[gwas.obj$markers[snp.name,gwas.obj$pheno$genotype]==1,trait.number],na.rm=T)) & !is.na(mean(gwas.obj$pheno[gwas.obj$markers[snp.name,gwas.obj$pheno$genotype]==0,trait.number],na.rm=T))) {
    effect.size <- mean(gwas.obj$pheno[gwas.obj$markers[snp.name,gwas.obj$pheno$genotype]==1,trait.number],na.rm=T) - mean(gwas.obj$pheno[gwas.obj$markers[snp.name,gwas.obj$pheno$genotype]==0,trait.number],na.rm=T)
  }
  return(effect.size/2.0)
}