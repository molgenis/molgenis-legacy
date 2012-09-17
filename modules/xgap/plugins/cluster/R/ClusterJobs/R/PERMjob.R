#####################################################################
#
# PERMUTATIONanalysis.R
#
# copyright (c) 2009-2011, Danny Arends
# last modified Jan, 2011
# first written Nov, 2009
# 
# Part of the ClusterJobs package
# Contains: run_PERM
#
######################################################################

######################################################################
#
# run_PERM: Generates a QTL file which is executed (R CMD BATCH <qtlfile>) by a cluster
#
######################################################################

run_PERM <- function(dbpath = "", item, b_size, totalitems,name,jobid,jobparams=list(c("map","scanall"),c("method","hk"),c("model","normal"),c("nperms","0")),investigationname="",libraryloc){
  cat("info: Generating start code to communicate and load libraries\n")
  qtlfile <- startcode(dbpath,jobid,item,libraryloc)
  
  cat("info: Start by sending a message (so we know we're running)\n")
  cat("report(2,\"LoadingCrossobject\")\n",file=qtlfile,append=T)
  
  cat("info: Get your parameters\n")
  map <- getparameter("map",jobparams)
  method <- getparameter("method",jobparams)
  model <- getparameter("model",jobparams)
  nperms <- getparameter("nperms",jobparams)
  
  cat(Generate_Statement(paste("load(\"./run",jobid,"/cross.RData\")","\n",sep="")),file=qtlfile,append=T)
  cat("report(2,\"FinishedLoading\")\n",file=qtlfile,append=T)
  
  cat("info: Throw away genotypes we don't need to save memory\n")
  cat(Generate_Statement(paste("cross$pheno <- cross$pheno[",((item-1)*b_size)+1,":",min(((item-1)*b_size)+b_size,totalitems),"]","\n",sep="")),file=qtlfile,append=T)
  
  cat("info: The actual analysis is performed here, Generate Code to do permutations using the parameters\n")
  cat("Thresholds <- NULL\n",file=qtlfile,append=T)
  cat("Names <- NULL\n",file=qtlfile,append=T)
  cat("for(num in 1:nphe(cross)){\n",file=qtlfile,append=T)
  cat(Generate_Statement(paste("bresults <- mqmpermutation(cross, n.perm=",nperms,", pheno.col=num,scanfunction=",map,",method='",method,"',model='",model,"',multicore=FALSE,verbose=TRUE)","\n",sep="")),file=qtlfile,append=T)
  cat(Generate_Statement(paste("results <- summary(mqmprocesspermutation(bresults))[,1]","\n",sep="")),file=qtlfile,append=T)
  cat(Generate_Statement(paste("Thresholds <- rbind(Thresholds,results)","\n",sep="")),file=qtlfile,append=T)
  cat(Generate_Statement(paste("Names <- c(Names,substr(colnames(bresults[[1]])[3],5,nchar(colnames(bresults[[1]])[3])))","\n",sep="")),file=qtlfile,append=T)
  cat("}\n",file=qtlfile,append=T)
  cat("rownames(Thresholds) <- Names\n",file=qtlfile,append=T)
  cat("Thresholds\n",file=qtlfile,append=T)
	
  cat("info: Store PERM results by using PermToMolgenis function or any other function provided in the molgenis R API\n")
  cat("report(2,\"StoringPERMresults\")\n",file=qtlfile,append=T)
  cat(Generate_Statement(paste("PermToMolgenis(investigationname='",investigationname,"',resultsname='",name,"',results=Thresholds,",(item-1)*b_size,",",b_size,")\n",sep="")),file=qtlfile,append=T)
  cat("report(3,\"JobFinished\")\n",file=qtlfile,append=T)
  #Quit
  cat("q(\"no\")","\n",sep="",file=qtlfile,append=T)
}
