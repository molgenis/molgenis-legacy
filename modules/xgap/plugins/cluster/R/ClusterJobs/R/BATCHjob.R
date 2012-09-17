#####################################################################
#
# Minjob.R
#
# copyright (c) 2009-2010, Danny Arends
# last modified Jan, 2011
# first written Okt, 2010
# 
# Part of the XGAP - ClusterJobs package, a minimal userjob that can be batched out
# Contains: run_BATCH
#
######################################################################

######################################################################
#
# run_BATCH: Bach detection on phenotype data using correlations
#
######################################################################

run_BATCH <- function(dbpath = "", subjob, item, jobid, outname, myanalysisfile, jobparams=list(c("message","hello World")), investigationname="", libraryloc=NULL){
  message <- getparameter("message",jobparams)
  
  cat("report(2,\"",paste(message,"from item ", item," subjob", subjob," running job:", jobid," needs to store results in",outname),"\")\n",file=myanalysisfile,append=T)
  
  cat("Sys.sleep(100)\n",file=myanalysisfile,append=T)
  
  cat("report(3,\"JobFinished\")\n",file=myanalysisfile,append=T)
  
  cat("info: Quit the script so we don't waste resources\n")
  cat("q(\"no\")","\n",sep="",file=myanalysisfile,append=T)
}
