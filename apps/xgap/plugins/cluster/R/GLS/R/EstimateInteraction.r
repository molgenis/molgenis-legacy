#
# EstimateInteraction.R
# - Description: ???
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

# N.B another "global" object is used: iv, defined in emmax_win.r this is done to pass on the values of the variance components as they are in the scan for the main effects
# N.B. the input geno.vector is (supposed to be) character; for asreml, ordered factor is required

EstimateInteraction  <- function(geno.vector, marker1, marker2, trait.values, kinship.asreml.object, cov.values=data.frame(NULL)){
  if(missing(geno.vector)) stop("argument 'geno.vector' is missing, with no default")
  if(missing(marker1)) stop("argument 'marker1' is missing, with no default")
  if(missing(marker2)) stop("argument 'marker2' is missing, with no default")
  if(missing(trait.values)) stop("argument 'trait.values' is missing, with no default")
  if(missing(kinship.asreml.object)) stop("argument 'kinship.asreml.object' is missing, with no default")
  
  require(asreml)
 
  interaction.frame   <- data.frame(genotype=ordered(geno.vector,levels=unique(geno.vector)),phenotype=trait.values,marker1=marker1,marker2=marker2,epistasis=marker1*marker2)
  if(ncol(cov.values)>1){
    interaction.frame <- cbind(interaction.frame,cov.values)
  }
  if(ncol(cov.values)==1){  
    reml.formula.inter <- as.formula("phenotype ~ marker1 + marker2 + epistasis")
  }else{
    reml.formula.inter <- as.formula(paste("phenotype ~ marker1 + marker2 + epistasis +",paste(names(cov.values),collapse="+")))
  }
  reml.obj<- asreml(fixed= reml.formula.inter,data=interaction.frame, random = ~ giv(genotype,var=T),na.method.X="omit", ginverse = list(genotype=GWAS.obj$kinship.asreml),G.param=iv,R.param=iv,fixgammas=TRUE)  
  # N.B. in the preceding line, we should have kinship.asreml.object instead of GWAS.obj$kinship.asreml; but the former gives an error. WHY ??
  
  effect  <-  reml.obj$coefficients$fixed[["epistasis"]]
  pvalue  <- (wald(reml.obj))[[4]][4]
  list(pvalue,effect)
}
