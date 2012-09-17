#
# fdp.R
# - Description: Compute False Discovery Proportion under the full (strong) null
#
# Copyright (c) 2012 Willem Kruijer and Danny Arends

fdp <- function(pvals,ev=1,gamma=0.05) {
  if(missing(pvals)) stop("argument 'pvals' is missing, with no default")
  
  p <- length(pvals)
  if(gamma==0){
    lod.thr= -log(ev/p,base=10)
    R.thr= sum(pvals<= ev/p)
  }else{
    evtable = data.frame(pvalues=sort(pvals),test=rep(0,p))
    evtable$test  = as.numeric((p*evtable$pvalues) /(1:p)<= gamma)
    if(sum(evtable$test)==p){
      R.thr=p
      lod.thr=-log(evtable$pvalues[p],base=10)
    }else{
      ind = min(which(evtable$test==0))-1
      if (ind==0) {R.thr=0; lod.thr=Inf} else {R.thr=ind; lod.thr=-log(evtable$pvalues[ind],base=10)}
    } 
  }
  list(lod.thr,R.thr)
}
