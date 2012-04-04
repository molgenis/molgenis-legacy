CalculateEffect <- function(gwas.obj,snp.name,trait.number) {
  effect.size <- NA
  if (!is.na(mean(gwas.obj$pheno[gwas.obj$markers[snp.name,gwas.obj$pheno$genotype]==1,trait.number],na.rm=T)) & !is.na(mean(gwas.obj$pheno[gwas.obj$markers[snp.name,gwas.obj$pheno$genotype]==0,trait.number],na.rm=T))) {
    effect.size <- mean(gwas.obj$pheno[gwas.obj$markers[snp.name,gwas.obj$pheno$genotype]==1,trait.number],na.rm=T) - mean(gwas.obj$pheno[gwas.obj$markers[snp.name,gwas.obj$pheno$genotype]==0,trait.number],na.rm=T)
  }
effect.size/2
}